package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import finalforeach.cosmicreach.rendering.meshes.IntIndexData;
import finalforeach.cosmicreach.rendering.meshes.IntIndexedMesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(IntIndexedMesh.class)
public interface IntIndexedMeshAccessor {
    @Accessor
    IntIndexData getIndexData();
}
