package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.badlogic.gdx.utils.IntArray;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJson;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(BlockModelJson.class)
public abstract class BlockModelJsonMixin {
    @ModifyExpressionValue(method = "addVertices", at = @At(value = "FIELD", target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJson;useIndices:Z"))
    private boolean reorderVertices(
        final boolean original,
        final @Local(ordinal = 0) IntArray indices,
        final @Local(ordinal = 6) int aoIdA,
        final @Local(ordinal = 7) int aoIdB,
        final @Local(ordinal = 8) int aoIdC,
        final @Local(ordinal = 9) int aoIdD,
        final @Local(ordinal = 15) int i1,
        final @Local(ordinal = 16) int i2,
        final @Local(ordinal = 17) int i3,
        final @Local(ordinal = 18) int i4
    ) {
        if (!original || aoIdA + aoIdC > aoIdB + aoIdD) {
            return true;
        } else {
            // NOTE: only affects NAIVE renderer
            indices.add(i2);
            indices.add(i3);
            indices.add(i4);
            indices.add(i1);
            indices.add(i2);
            indices.add(i4);
            return false;
        }
    }
}
