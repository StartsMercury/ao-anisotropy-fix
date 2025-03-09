package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import finalforeach.cosmicreach.rendering.ExperimentalNaiveZoneRenderer;
import finalforeach.cosmicreach.rendering.meshes.IGameMesh;
import finalforeach.cosmicreach.rendering.meshes.MeshData;
import finalforeach.cosmicreach.rendering.meshes.SharedIndexMesh;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ExperimentalNaiveZoneRenderer.class)
public class ExperimentalNaiveZoneRendererMixin {
    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = """
                Lfinalforeach/cosmicreach/rendering/meshes/MeshData;          \
                toSharedIndexMesh(                                            \
                    Z                                                         \
                ) Lfinalforeach/cosmicreach/rendering/meshes/SharedIndexMesh; \
            """
        )
    )
    private SharedIndexMesh unshareIndices(
        final MeshData instance,
        final boolean isStatic,
        final Operation<SharedIndexMesh> original
    ) {
        return null;
    }

    @WrapOperation(
        method = "render",
        at = @At(
            value = "FIELD",
            target = "Lfinalforeach/cosmicreach/rendering/meshes/MeshData;mesh:Lfinalforeach/cosmicreach/rendering/meshes/IGameMesh;",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void updateCombinedMesh(
        final MeshData instance,
        final IGameMesh value,
        final Operation<Void> original
    ) {
        original.call(instance, instance.toIntIndexedMesh(true));
    }

    @WrapOperation(
        method = "render",
        at = @At(
            value = "INVOKE",
            target =
                "Lfinalforeach/cosmicreach/rendering/SharedQuadIndexData;allowForNumIndices(IZ)V"
        )
    )
    private void skipIndexDataSynthesis(
        final int numIndices,
        final boolean bind,
        final Operation<Void> original
    ) {
        // DO NOTHING
    }
}
