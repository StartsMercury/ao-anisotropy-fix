package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJson;
import finalforeach.cosmicreach.rendering.blockmodels.BlockModelJsonCuboidFace;
import org.objectweb.asm.Opcodes;
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
    @Inject(
        method = "addVertices",
        at = @At(
            value = "FIELD",
            target = "Lfinalforeach/cosmicreach/rendering/blockmodels/BlockModelJsonCuboidFace;vertexIndexA:I"
        )
    )
    private void changeQuadFlipCondition(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) short[] blockLightLevels,
        final @Local(ordinal = 0, argsOnly = true) int[] skyLightLevels,
        final @Local(ordinal = 0) BlockModelJsonCuboidFace face,
        final @Local(ordinal = 6) LocalIntRef aoIdARef,
        final @Local(ordinal = 7) int aoIdB,
        final @Local(ordinal = 8) int aoIdC,
        final @Local(ordinal = 9) int aoIdD,
        final @Share("realAoIdA") LocalIntRef realAoIdARef
    ) {
        final var aoIdA = aoIdARef.get();
        final boolean triangles_abc_cda_else_bcd_abd;
        realAoIdARef.set(aoIdA);

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
            // x + min > max + max
            // x + 0 > 3 + 3
            // x > 6
            // x = 7
            aoIdARef.set(7);
        } else {
            // NOT(x + max > min + min)
            // x + max <= min + min
            // x +  3  <=  0  +  0
            // x + 3 <= 0
            // x <= -3
            // x == -3
            aoIdARef.set(-3);
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

    @Inject(
        method = "addVertices",
        at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPGE, ordinal = 1)
    )
    private void unchangeQuadFlipCondition(
        final CallbackInfo callback,
        final @Local(ordinal = 6) LocalIntRef aoIdARef,
        final @Share("realAoIdA") LocalIntRef realAoIdARef
    ) {
        aoIdARef.set(realAoIdARef.get());
    }
}
