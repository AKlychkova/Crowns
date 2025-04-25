package ru.hse.crowns.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import ru.hse.crowns.proto.KillerSudokuGameDTO
import java.io.InputStream
import java.io.OutputStream

object KillerSudokuGameSerializer : Serializer<KillerSudokuGameDTO> {
    override val defaultValue: KillerSudokuGameDTO
        get() = KillerSudokuGameDTO.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): KillerSudokuGameDTO {
        try {
            return KillerSudokuGameDTO.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: KillerSudokuGameDTO, output: OutputStream) {
        t.writeTo(output)
    }
}