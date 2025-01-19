package io.github.startsmercury.ao_anisotropy_fix.mixin.client;

import com.badlogic.gdx.utils.IntArray;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import finalforeach.cosmicreach.rendering.ChunkBatch;
import finalforeach.cosmicreach.rendering.meshes.IGameMesh;
import finalforeach.cosmicreach.rendering.meshes.IntIndexedMesh;
import finalforeach.cosmicreach.rendering.meshes.MeshData;
import io.github.startsmercury.ao_anisotropy_fix.impl.client.IndexedMeshDataMetadata;
import java.nio.IntBuffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class.
 *
 * @see Mixin
 */
@Mixin(ChunkBatch.class)
public abstract class ChunkBatchMixin {
    @Shadow
    IGameMesh mesh;

    @Inject(
        method = {
            "dispose(Z)V",
            "rebuildMeshReadFromOldMesh(Lfinalforeach/cosmicreach/rendering/meshes/MeshData;)V",
        },
        at = @At(
            value = "INVOKE_ASSIGN",
            target =
                "Lcom/badlogic/gdx/graphics/glutils/VertexData;getBuffer(Z)Ljava/nio/FloatBuffer;"
        )
    )
    private void createMeshIndxes(
        final CallbackInfo callback,
        final @Share("meshIdxes") LocalRef<IntBuffer> meshIdxesRef
    ) {
        if (this.mesh instanceof final IntIndexedMesh mesh) {
            meshIdxesRef.set(((IntIndexedMeshAccessor) mesh).getIndexData().indexBuffer);
        }
    }

    @Inject(
        method = "dispose(Z)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = """
                Lfinalforeach/cosmicreach/rendering/ChunkBatch; \
                addFloatBufferToArray (                         \
                    Ljava/nio/FloatBuffer;                      \
                    Lcom/badlogic/gdx/utils/FloatArray;         \
                )V                                              \
            """,
            ordinal = 0
        )
    )
    private void addIdxToMDataIdxesOnDispose(
        final CallbackInfo callback,
        final @Share("meshIdxes") LocalRef<IntBuffer> meshIdxesRef,
        final @Local(ordinal = 0) MeshData mData,
        final @Local(ordinal = 0) ChunkBatch.MeshDataMetadata meta
    ) {
        final var meshIdxes = meshIdxesRef.get();

        if (meshIdxes == null) {
            return;
        }

        final var indexedMeta = (IndexedMeshDataMetadata) meta;
        final var idxBuffer = meshIdxes.slice(
            indexedMeta.ao_anisotropy_fix$getIndexPosition(),
            indexedMeta.ao_anisotropy_fix$getNumIndices()
        );
        final var mDataIdxes = mData.getIndices();
        this.addIntBufferToArray(idxBuffer, mDataIdxes, true);
    }

    @Inject(
        method = "rebuildMeshReadFromOldMesh(Lfinalforeach/cosmicreach/rendering/meshes/MeshData;)V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = """
                Lfinalforeach/cosmicreach/rendering/ChunkBatch; \
                addFloatBufferToArray (                         \
                    Ljava/nio/FloatBuffer;                      \
                    Lcom/badlogic/gdx/utils/FloatArray;         \
                )V                                              \
            """,
            ordinal = 0
        )
    )
    private void addIdxToMDataIdxesOnRebuildFromOld(
        final CallbackInfo callback,
        final @Share("meshIdxes") LocalRef<IntBuffer> meshIdxesRef,
        final @Local(ordinal = 1) MeshData mData,
        final @Local(ordinal = 0) ChunkBatch.MeshDataMetadata meta
    ) {
        final var meshIdxes = meshIdxesRef.get();

        if (meshIdxes == null) {
            return;
        }

        final var indexedMeta = (IndexedMeshDataMetadata) meta;
        final var idxBuffer = meshIdxes.slice(
            indexedMeta.ao_anisotropy_fix$getIndexPosition(),
            indexedMeta.ao_anisotropy_fix$getNumIndices()
        );
        final var mDataIdxes = mData.getIndices();
        this.addIntBufferToArray(idxBuffer, mDataIdxes, false);
    }

    @Inject(
        method = "rebuildMeshReadFromOldMesh(Lfinalforeach/cosmicreach/rendering/meshes/MeshData;)V",
        at = @At(
            value = "FIELD",
            shift = At.Shift.AFTER,
            target =
                "Lfinalforeach/cosmicreach/rendering/ChunkBatch$MeshDataMetadata;vertexPosition:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void addIdxToCombinedIdxes(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) MeshData combined,
        final @Share("meshIdxes") LocalRef<IntBuffer> meshIdxesRef,
        final @Local(ordinal = 1) MeshData mData,
        final @Local(ordinal = 0) ChunkBatch.MeshDataMetadata meta
    ) {
        final var meshIdxes = meshIdxesRef.get();

        if (meshIdxes == null) {
            return;
        }

        final var indexedMeta = (IndexedMeshDataMetadata) meta;
        final var combinedIdxes = combined.getIndices();
        final var idxPos = combinedIdxes.size;
        final var idxBuffer = meshIdxes.slice(
            indexedMeta.ao_anisotropy_fix$getIndexPosition(),
            indexedMeta.ao_anisotropy_fix$getNumIndices()
        );
        this.addIntBufferToArray(idxBuffer, combinedIdxes, false);
        indexedMeta.ao_anisotropy_fix$setIndexPosition(idxPos);
    }

    @Unique
    private void addIntBufferToArray(
        final IntBuffer bufferSrc,
        final IntArray arrayDst,
        final boolean normalize
    ) {
        final var numInts = bufferSrc.remaining();
        arrayDst.ensureCapacity(numInts);

        final var items = arrayDst.items;
        final var size = arrayDst.size;

        bufferSrc.get(items, size, numInts);
        arrayDst.size = size + numInts;

        // 0th is the first least index for normal quads
        // 3rd is the same for flipped quads
        if (normalize) {
            final var idxOff = Math.min(items[0], items[3]);

            for (var i = arrayDst.size - 1; i >= size; i--) {
                items[i] -= idxOff;
            }
        }
    }

    @ModifyArg(
        method = "rebuildMesh(Lfinalforeach/cosmicreach/rendering/meshes/MeshData;)V",
        at = @At(
            value = "INVOKE",
            target =
                "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        index = 1
    )
    private Object combineIndices(
        final Object value,
        final @Local(ordinal = 0, argsOnly = true) MeshData combined,
        final @Local(ordinal = 1) MeshData mData
    ) {
        final var meta = (IndexedMeshDataMetadata) value;
        final var idxOff = ((ChunkBatch.MeshDataMetadata) value).vertexPosition / 3;

        final var mDataIdxes = mData.getIndices();
        final var cIdxes = combined.getIndices();
        final var idxPos = cIdxes.size;
        cIdxes.addAll(mDataIdxes);

        for (var i = cIdxes.size - 1; i >= idxPos; i--) {
            cIdxes.items[i] += idxOff;
        }

        meta.ao_anisotropy_fix$setIndexPosition(idxPos);
        meta.ao_anisotropy_fix$setNumIndices(mDataIdxes.size);

        return meta;
    }

    @Inject(
        method = "setMeshFromCombined",
        at = @At(
            value = "INVOKE",
            target = """
                Lfinalforeach/cosmicreach/rendering/meshes/IGameMesh; \
                setVertices (                                         \
                    Lcom/badlogic/gdx/utils/FloatArray;               \
                )V                                                    \
            """
        )
    )
    private void setMeshIndicesFromCombined(
        final CallbackInfo callback,
        final @Local(ordinal = 0, argsOnly = true) MeshData combined
    ) {
        if (this.mesh instanceof final IntIndexedMesh mesh) {
            mesh.setIndices(combined.getIndices());
        }
    }
}
