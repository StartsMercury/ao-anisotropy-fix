package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.badlogic.gdx.utils.IntArray;
import finalforeach.cosmicreach.rendering.ChunkMeshGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ChunkMeshGroup.class)
public class ChunkMeshGroupMixin {
    @ModifyArg(
        method = "getMeshData",
        at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/rendering/meshes/MeshData;<init>(Lcom/badlogic/gdx/utils/FloatArray;Lcom/badlogic/gdx/utils/IntArray;Lfinalforeach/cosmicreach/rendering/shaders/GameShader;Lfinalforeach/cosmicreach/rendering/RenderOrder;)V"),
        index = 1
    )
    private static IntArray initIndices(IntArray original) {
        return original != null ? original : new IntArray();
    }
}
