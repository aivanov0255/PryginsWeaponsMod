package com.prygin.rope;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PORTING NOTE (26.2): as of the sampler/render-pipeline rework (landed for
 * 1.21.11/26.1 and unchanged in 26.2), textures no longer own GL wrap-mode
 * state that a mod can flip. Address-mode (clamp/repeat) is a property of a
 * {@link GpuSampler}, which is only applied at draw time when a RenderType's
 * pipeline binds a texture with that sampler (RenderPass#bindTexture).
 *
 * So "make this texture repeat" is no longer a one-shot mutation on the
 * texture; it's "use a repeating sampler when you draw with this texture".
 * This class now just builds/caches the GpuSampler. The actual "use it"
 * half of the old contract moved to {@link RopeRenderTypes}, which supplies
 * a RenderType that binds the rope texture with this sampler -- see that
 * class for the part that replaces `RenderType.entityTranslucent(texture)`
 * in RopeRenderer.
 *
 * Samplers are cheap, immutable, and deduplicated by the engine's own
 * SamplerCache (only 32 possible combinations exist), so there's no need to
 * key this off the texture identity the way the old CONFIGURED set did --
 * we only cache here to avoid re-querying the cache every frame.
 */
public final class RopeTextureUtil {

    private static final Map<Identifier, GpuSampler> REPEATING_SAMPLERS = new ConcurrentHashMap<>();

    private RopeTextureUtil() {
    }

    /**
     * Returns a sampler that tiles (GL_REPEAT-equivalent) on both axes,
     * matching the filtering the old code left untouched (nearest, no
     * mipmaps) rather than picking new defaults.
     *
     * NOTE: the boolean param below selects between a max LOD of 0 vs 1000
     * (i.e. whether mipmaps are considered at all) per the 26.x
     * SamplerCache#getSampler signature -- pass true here if your rope
     * texture actually has mipmaps and you want them sampled.
     */
    public static GpuSampler repeatingSampler(Identifier texture) {
        return REPEATING_SAMPLERS.computeIfAbsent(texture, id ->
                RenderSystem.getSamplerCache().getSampler(
                        AddressMode.REPEAT,   // U
                        AddressMode.REPEAT,   // V
                        FilterMode.NEAREST,   // minification
                        FilterMode.NEAREST,   // magnification
                        false                 // no mipmapping, matches old behavior
                )
        );
    }

    /**
     * Kept for call-site compatibility with the old API shape, but it no
     * longer needs to "configure" anything up front -- computeIfAbsent in
     * repeatingSampler() already handles the once-per-texture work. This
     * just warms the cache eagerly if you want to call it from the same
     * place the old ensureRepeating() call was.
     */
    public static void ensureRepeating(Identifier texture) {
        repeatingSampler(texture);
    }
}