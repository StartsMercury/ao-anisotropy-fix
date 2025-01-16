package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import finalforeach.cosmicreach.ClientSingletons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ClientSingletons.class)
public abstract class ClientSingletonsMixin {
    /**
     * @author StartsMercury
     * @reason It ignores any anisotropy fix provided.
     */
    @Overwrite
    public static boolean usesSharedindices() {
        return false;
    }
}
