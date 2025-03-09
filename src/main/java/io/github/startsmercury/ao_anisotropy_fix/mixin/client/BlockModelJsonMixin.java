package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.badlogic.gdx.utils.IntArray;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import finalforeach.cosmicreach.rendering.IMeshData;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJson;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(BlockModelJson.class)
public abstract class BlockModelJsonMixin {
    @ModifyExpressionValue(
        method = "addVertices",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;addVert(Lfinalforeach/cosmicreach/rendering/IMeshData;IFFISII)I",
            ordinal = 0
        )
    )
    private int captureVertexIndex1(final int original, final @Share("i1") LocalIntRef i1Ref) {
        i1Ref.set(original);
        return original;
    }

    @ModifyExpressionValue(
        method = "addVertices",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;addVert(Lfinalforeach/cosmicreach/rendering/IMeshData;IFFISII)I",
            ordinal = 1
        )
    )
    private int captureVertexIndex2(final int original, final @Share("i2") LocalIntRef i2Ref) {
        i2Ref.set(original);
        return original;
    }

    @ModifyExpressionValue(
        method = "addVertices",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;addVert(Lfinalforeach/cosmicreach/rendering/IMeshData;IFFISII)I",
            ordinal = 2
        )
    )
    private int captureVertexIndex3(final int original, final @Share("i3") LocalIntRef i3) {
        i3.set(original);
        return original;
    }

    @ModifyExpressionValue(
        method = "addVertices",
        at = @At(
            value = "INVOKE",
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;addVert(Lfinalforeach/cosmicreach/rendering/IMeshData;IFFISII)I",
            ordinal = 3
        )
    )
    private int captureVertexIndex4(final int original, final @Share("i4") LocalIntRef i4Ref) {
        i4Ref.set(original);
        return original;
    }

    @Inject(
        method = "addVertices",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;addVert(Lfinalforeach/cosmicreach/rendering/IMeshData;IFFISII)I",
            ordinal = 3
        )
    )
    private void reorderVertices(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) IMeshData meshData,
        final @Local(ordinal = 6) int aoIdA,
        final @Local(ordinal = 7) int aoIdB,
        final @Local(ordinal = 8) int aoIdC,
        final @Local(ordinal = 9) int aoIdD,
        final @Share("i1") LocalIntRef i1Ref,
        final @Share("i2") LocalIntRef i2Ref,
        final @Share("i3") LocalIntRef i3Ref,
        final @Share("i4") LocalIntRef i4Ref
    ) {
        final var indices = meshData.getIndices();

        final int i1 = i1Ref.get();
        final int i2 = i2Ref.get();
        final int i3 = i3Ref.get();
        final int i4 = i4Ref.get();

        if (aoIdA + aoIdC > aoIdB + aoIdD) {
            indices.add(i1);
            indices.add(i2);
            indices.add(i3);
            indices.add(i3);
            indices.add(i4);
            indices.add(i1);
        } else {
            indices.add(i2);
            indices.add(i3);
            indices.add(i4);
            indices.add(i1);
            indices.add(i2);
            indices.add(i4);
        }
    }
}
