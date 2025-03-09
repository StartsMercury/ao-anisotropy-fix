package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.rendering.items.ItemModelBlock;
import finalforeach.cosmicreach.rendering.meshes.IGameMesh;
import finalforeach.cosmicreach.rendering.meshes.MeshData;
import finalforeach.cosmicreach.rendering.meshes.SharedIndexMesh;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ItemModelBlock.class)
public class ItemModelBlockMixin {
    @Shadow
    IGameMesh mesh;

    @WrapOperation(
        method = "<init>",
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

    @Inject(
        method = "<init>",
        at = @At(
            value = "FIELD",
            shift = At.Shift.AFTER,
            target = "Lfinalforeach/cosmicreach/rendering/items/ItemModelBlock;mesh:Lfinalforeach/cosmicreach/rendering/meshes/IGameMesh;",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void updateCombinedMesh(
        final CallbackInfo callback,
        final @Local(ordinal = 0) MeshData meshData
    ) {
        this.mesh = meshData.toIntIndexedMesh(true);
    }

    @WrapOperation(
        method = "<init>",
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
