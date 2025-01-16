package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import finalforeach.cosmicreach.rendering.ChunkBatch;
import io.github.startsmercury.ao_anisotropy_fix.impl.client.IndexedMeshDataMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ChunkBatch.MeshDataMetadata.class)
public class ChunkBatch$MeshDataMetadataMixin implements IndexedMeshDataMetadata {
    @Unique
    private int indexPosition;

    @Unique
    private int numIndices;

    @Override
    public int ao_anisotropy_fix$getIndexPosition() {
        return this.indexPosition;
    }

    @Override
    public void ao_anisotropy_fix$setIndexPosition(final int indexPosition) {
        this.indexPosition = indexPosition;
    }

    @Override
    public int ao_anisotropy_fix$getNumIndices() {
        return this.numIndices;
    }

    @Override
    public void ao_anisotropy_fix$setNumIndices(final int numIndices) {
        this.numIndices = numIndices;
    }
}
