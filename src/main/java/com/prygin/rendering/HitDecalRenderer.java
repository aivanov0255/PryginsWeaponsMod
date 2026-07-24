package com.prygin.rendering;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import com.prygin.Guns;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class HitDecalRenderer implements ClientModInitializer {
    private static HitDecalRenderer instance;
    public static HitDecalRenderer getInstance() { return instance; }

    private static final long LIFETIME_MS = 2000L;
    private static final int MAX_DECALS = 200;
    private static final float HALF_SIZE = 0.25f;
    private static final float SURFACE_OFFSET = 0.01f;

    private record Decal(Vec3 hitPos, Direction face, long spawnTimeMs, Identifier texture) {
        float ageFraction(long now) {
            float t = (now - spawnTimeMs) / (float) LIFETIME_MS;
            return Math.max(0f, Math.min(1f, t));
        }
        boolean isExpired(long now) { return now - spawnTimeMs >= LIFETIME_MS; }
    }

    private final List<Decal> decals = new ArrayList<>();
    private BlockHitResult lastSpawnedHit = null;

    // Separate allocators so that the sort-scratch ByteBufferBuilder never
    // aliases the vertex-build ByteBufferBuilder. Both are small and reused
    // across frames; they are closed in close().
    private static final ByteBufferBuilder VERTEX_ALLOCATOR = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private static final ByteBufferBuilder INDEX_SORT_ALLOCATOR = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private MappableRingBuffer vertexBuffer;
    private MappableRingBuffer indexBuffer;

    @Override
    public void onInitializeClient() {
        instance = this;
        LevelRenderEvents.END_EXTRACTION.register(this::extractDecals);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(this::drawDecals);
    }

    private void extractDecals(LevelExtractionContext context) {
        long now = System.currentTimeMillis();
        Iterator<Decal> it = decals.iterator();
        while (it.hasNext()) {
            if (it.next().isExpired(now)) it.remove();
        }
    }

    public void spawnDecalFromRaycast(BlockHitResult blockHit, Identifier texture) {
        long now = System.currentTimeMillis();
        if (decals.size() >= MAX_DECALS) decals.remove(0);
        decals.add(new Decal(blockHit.getLocation(), blockHit.getDirection(), now, texture));
        lastSpawnedHit = blockHit;
    }

    private void drawDecals(LevelRenderContext context) {
        if (decals.isEmpty()) return;

        long now = System.currentTimeMillis();
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        RenderPipeline pipeline = RenderPipelines.ENTITY_TRANSLUCENT;
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);
        Matrix4fc positionMatrix = matrices.last().pose();

        // Group decals by texture so we can draw one GPU pass per unique
        // texture. A single RenderPass can only have one Sampler0 bound at
        // a time, so mixing textures in one batch isn't possible.
        // LinkedHashMap preserves insertion order, keeping older decals
        // (drawn first) correctly behind newer ones depth-wise.
        Map<Identifier, List<Decal>> byTexture = new LinkedHashMap<>();
        for (Decal decal : decals) {
            byTexture.computeIfAbsent(decal.texture(), k -> new ArrayList<>()).add(decal);
        }

        for (Map.Entry<Identifier, List<Decal>> entry : byTexture.entrySet()) {
            AbstractTexture gpuTexture = textureManager.getTexture(entry.getKey());

            // 26.2: RenderPipeline#getVertexFormatMode/getVertexFormat were replaced by
            // getPrimitiveTopology()/getVertexFormatBinding(index), since a pipeline can
            // now bind up to sixteen vertex buffers/formats.
            BufferBuilder buffer = new BufferBuilder(VERTEX_ALLOCATOR, pipeline.getPrimitiveTopology(), pipeline.getVertexFormatBinding(0));

            for (Decal decal : entry.getValue()) {
                float alpha = 1.0f - decal.ageFraction(now);
                addDecalQuad(positionMatrix, buffer, decal.hitPos(), decal.face(), alpha);
            }

            MeshData mesh = buffer.buildOrThrow();
            GpuBufferSlice vertices = upload(mesh);
            draw(Minecraft.getInstance(), pipeline, mesh, vertices, gpuTexture);
            vertexBuffer.rotate();
        }

        matrices.popPose();
    }

    private void addDecalQuad(Matrix4fc positionMatrix, BufferBuilder buffer, Vec3 hitPos, Direction face, float alpha) {
        Vector3f normal = new Vector3f(face.getStepX(), face.getStepY(), face.getStepZ());

        Vector3f worldUp = (face.getAxis() == Direction.Axis.Y)
                ? new Vector3f(0, 0, 1)
                : new Vector3f(0, 1, 0);

        Vector3f right = new Vector3f();
        worldUp.cross(normal, right);
        if (right.lengthSquared() < 1.0e-6f) right.set(1, 0, 0);
        right.normalize();

        Vector3f up = new Vector3f();
        normal.cross(right, up);
        up.normalize();

        right.mul(HALF_SIZE);
        up.mul(HALF_SIZE);

        float cx = (float) (hitPos.x + normal.x() * SURFACE_OFFSET);
        float cy = (float) (hitPos.y + normal.y() * SURFACE_OFFSET);
        float cz = (float) (hitPos.z + normal.z() * SURFACE_OFFSET);

        float x0 = cx - right.x() - up.x(), y0 = cy - right.y() - up.y(), z0 = cz - right.z() - up.z();
        float x1 = cx + right.x() - up.x(), y1 = cy + right.y() - up.y(), z1 = cz + right.z() - up.z();
        float x2 = cx + right.x() + up.x(), y2 = cy + right.y() + up.y(), z2 = cz + right.z() + up.z();
        float x3 = cx - right.x() + up.x(), y3 = cy - right.y() + up.y(), z3 = cz - right.z() + up.z();

        int overlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
        int light = 0xF000F0;

        buffer.addVertex(positionMatrix, x0, y0, z0).setColor(1f, 1f, 1f, alpha).setUv(0f, 1f).setOverlay(overlay).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
        buffer.addVertex(positionMatrix, x1, y1, z1).setColor(1f, 1f, 1f, alpha).setUv(1f, 1f).setOverlay(overlay).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
        buffer.addVertex(positionMatrix, x2, y2, z2).setColor(1f, 1f, 1f, alpha).setUv(1f, 0f).setOverlay(overlay).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
        buffer.addVertex(positionMatrix, x3, y3, z3).setColor(1f, 1f, 1f, alpha).setUv(0f, 0f).setOverlay(overlay).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
    }

    // 26.2: CommandEncoder#mapBuffer was removed in favor of GpuBuffer#map / GpuBufferSlice#map,
    // and RenderPass#setVertexBuffer now takes a GpuBufferSlice rather than a raw GpuBuffer,
    // so this now both maps the data in AND returns the slice the draw call needs.
    private GpuBufferSlice upload(MeshData builtBuffer) {
        int vertexBufferSize = builtBuffer.drawState().vertexCount() * builtBuffer.drawState().format().getVertexSize();

        if (this.vertexBuffer == null || this.vertexBuffer.size() < vertexBufferSize) {
            if (this.vertexBuffer != null) this.vertexBuffer.close();
            this.vertexBuffer = new MappableRingBuffer(
                    () -> Guns.MOD_ID + " hit decal buffer",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE,
                    vertexBufferSize);
        }

        GpuBufferSlice destSlice = this.vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining());
        try (GpuBufferSlice.MappedView mappedView = destSlice.map(false, true)) {
            int srcPos = builtBuffer.vertexBuffer().position();
            mappedView.data().put(builtBuffer.vertexBuffer());
            builtBuffer.vertexBuffer().position(srcPos);
        }

        return destSlice;
    }

    // Uploads the sorted translucency index buffer for the QUADS path and returns
    // a GpuBufferSlice over the written data for RenderPass#setIndexBuffer.
    //
    // After MeshData#sortQuads runs, the sorted index data is available via
    // MeshData#indexBuffer() as a plain ByteBuffer (non-null for sorted-quad
    // MeshData; null when only auto-sequential indices are needed). We copy
    // that ByteBuffer into a MappableRingBuffer slot using the same
    // GpuBufferSlice#map pattern as upload() uses for vertices.
    //
    // VertexFormat#uploadImmediateIndexBuffer was removed in 26.2 with no
    // documented replacement; this is the equivalent manual upload.
    private GpuBufferSlice uploadSortedQuadIndexBuffer(MeshData builtBuffer) {
        ByteBuffer indexData = builtBuffer.indexBuffer(); // non-null after sortQuads
        int indexBufferSize = indexData.remaining();

        if (this.indexBuffer == null || this.indexBuffer.size() < indexBufferSize) {
            if (this.indexBuffer != null) this.indexBuffer.close();
            this.indexBuffer = new MappableRingBuffer(
                    () -> Guns.MOD_ID + " hit decal index buffer",
                    GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_MAP_WRITE,
                    indexBufferSize);
        }

        GpuBufferSlice destSlice = this.indexBuffer.currentBuffer().slice(0, indexBufferSize);
        try (GpuBufferSlice.MappedView mappedView = destSlice.map(false, true)) {
            int srcPos = indexData.position();
            mappedView.data().put(indexData);
            indexData.position(srcPos);
        }

        this.indexBuffer.rotate();
        return destSlice;
    }

    private static void draw(Minecraft client, RenderPipeline pipeline, MeshData builtBuffer,
                             GpuBufferSlice vertices, AbstractTexture texture) {
        MeshData.DrawState drawParameters = builtBuffer.drawState();

        // 26.2: RenderPass#setIndexBuffer takes a GpuBufferSlice, so both branches
        // produce a GpuBufferSlice rather than a raw GpuBuffer.
        GpuBufferSlice indices;
        IndexType indexType;

        if (pipeline.getPrimitiveTopology() == PrimitiveTopology.QUADS) {
            // INDEX_SORT_ALLOCATOR is separate from VERTEX_ALLOCATOR so the
            // sort-scratch buffer never aliases a live vertex segment.
            builtBuffer.sortQuads(INDEX_SORT_ALLOCATOR, RenderSystem.getProjectionType().vertexSorting());
            indices = HitDecalRenderer.getInstance().uploadSortedQuadIndexBuffer(builtBuffer);
            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getPrimitiveTopology());
            // AutoStorageIndexBuffer#getBuffer returns a GpuBuffer; wrap in a full
            // slice so setIndexBuffer receives the expected GpuBufferSlice.
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount()).slice();
            indexType = shapeIndexBuffer.type();
        }

        // 26.2: getModelViewMatrixCopy() was removed; use getModelViewMatrix().
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        // 26.2: Minecraft#getMainRenderTarget moved to GameRenderer#mainRenderTarget,
        // and createRenderPass's clear-color parameter changed from OptionalInt to
        // Optional<Vector4fc>.
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> Guns.MOD_ID + " hit decal rendering",
                        client.gameRenderer.mainRenderTarget().getColorTextureView(), Optional.<Vector4fc>empty(),
                        client.gameRenderer.mainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {

            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());

            renderPass.setVertexBuffer(0, vertices);
            // 26.2: setIndexBuffer takes a GpuBufferSlice directly — not .buffer().
            renderPass.setIndexBuffer(indices.buffer(), indexType);
            // 26.2: drawIndexed signature is
            // (indexCount, instanceCount, firstIndex, baseVertex, firstInstance).
            renderPass.drawIndexed(drawParameters.indexCount(), 1, 0, 0, 0);
        }

        builtBuffer.close();
    }

    public void close() {
        VERTEX_ALLOCATOR.close();
        INDEX_SORT_ALLOCATOR.close();
        if (this.vertexBuffer != null) {
            this.vertexBuffer.close();
            this.vertexBuffer = null;
        }
        if (this.indexBuffer != null) {
            this.indexBuffer.close();
            this.indexBuffer = null;
        }
    }
}