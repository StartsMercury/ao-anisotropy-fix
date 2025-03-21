package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import finalforeach.cosmicreach.rendering.IMeshData;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJson;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJsonCuboidFace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
        final @Local(ordinal = 0, argsOnly = true) short[] blockLightLevels,
        final @Local(ordinal = 0, argsOnly = true) int[] skyLightLevels,
        final @Local(ordinal = 0) BlockModelJsonCuboidFace face,
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

        final boolean triangles_abc_cda_else_bcd_abd;

        final var aoDarkened = aoIdA < 3 || aoIdB < 3 || aoIdC < 3 || aoIdD < 3;
        if (aoDarkened) {
            // Prioritize AO: opposing dark corners must not connect.
            triangles_abc_cda_else_bcd_abd = aoIdA + aoIdC > aoIdB + aoIdD;
        } else {
            final int a = createKey(blockLightLevels, skyLightLevels, face.vertexIndexA);
            final int b = createKey(blockLightLevels, skyLightLevels, face.vertexIndexB);
            final int c = createKey(blockLightLevels, skyLightLevels, face.vertexIndexC);
            final int d = createKey(blockLightLevels, skyLightLevels, face.vertexIndexD);
            // Opposing darker lighting must connect.
            triangles_abc_cda_else_bcd_abd = b > a || b > c || d > a || d > c;
        }

        if (triangles_abc_cda_else_bcd_abd) {
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

    @Unique
    private static int createKey(
        final short[] blockLightLevels,
        final int[] skyLightLevels,
        final int index
    ) {
        final var rgb = blockLightLevels[index];
        final var a = skyLightLevels[index];
        final var r = rgb >>> 16 & 0xFF;
        final var g = rgb >>> 8 & 0xFF;
        final var b = rgb & 0xFF;

        return a + r + g + b;
    }
}
