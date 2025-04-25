package ru.hse.crowns.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import ru.hse.crowns.proto.QueensGameDTO
import java.io.InputStream
import java.io.OutputStream

object QueensGameSerializer: Serializer<QueensGameDTO> {
    override val defaultValue: QueensGameDTO
        get() = QueensGameDTO.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): QueensGameDTO {
        try {
            return QueensGameDTO.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: QueensGameDTO, output: OutputStream) {
        t.writeTo(output)
    }

}