package com.prygin.rope;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PORTING NOTE (26.2): this class didn't exist before -- it's the missing
 * half of what RopeTextureUtil.ensureRepeating() used to do by itself. In
 * the new renderer, "bind this texture with a repeating sampler" only
 * happens through a RenderType's pipeline/RenderSetup, not through mutable
 * texture state. So instead of calling
 *   RenderType.entityTranslucent(rope.getTexture())
 * in RopeRenderer, get a RenderType from here that's wired to
 * RopeTextureUtil's repeating sampler for that specific texture.
 *
 * CAVEAT: the exact builder chain below follows the pattern documented for
 * the 1.21.11/26.x RenderSetup rework (RenderSetup.builder(pipeline)
 * .withTexture("Sampler0", id, sampler).createRenderSetup(), with
 * RenderType now being "just a named RenderSetup"). I don't have a compiled
 * 26.2 environment to verify the final method names against
 * (RenderType.create(...) vs a RenderSetup-accepting overload, and whether
 * cloning RenderPipelines.ENTITY_TRANSLUCENT already carries over its
 * "Sampler0" declaration or needs re-declaring via withSampler(...)).
 * Treat this file as "verify against your local mappings before shipping",
 * not "compiles as-is."
 */
public final class RopeRenderTypes {

    private static final Map<Identifier, RenderType> ROPE_TYPES = new ConcurrentHashMap<>();

    private RopeRenderTypes() {
    }

    public static RenderType ropeTranslucent(Identifier texture) {
        return ROPE_TYPES.computeIfAbsent(texture, id -> {
            GpuSampler repeatSampler = RopeTextureUtil.repeatingSampler(id);

            // Base off vanilla's entity-translucent pipeline so blending,
            // depth testing, culling, etc. all match what
            // RenderType.entityTranslucent used to give us for free.
            RenderPipeline pipeline = RenderPipelines.ENTITY_TRANSLUCENT;

            RenderSetup setup = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", id, () -> repeatSampler)
                    .createRenderSetup();

            // If RenderType in your mappings doesn't expose a
            // RenderSetup-accepting factory directly, the fallback is to
            // check whatever overload of RenderType.create(...) 26.2 kept
            // for "wrap an existing RenderSetup as a named RenderType" --
            // the migration primer explicitly calls RenderType "simply a
            // named RenderSetup", so there should be a thin wrapper here.
            return RenderType.create(
                    "rope_translucent",
                    setup
            );
        });
    }
}