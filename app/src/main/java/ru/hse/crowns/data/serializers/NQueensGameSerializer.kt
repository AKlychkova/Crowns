package ru.hse.crowns.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import ru.hse.crowns.proto.NQueensGameDTO
import java.io.InputStream
import java.io.OutputStream

object NQueensGameSerializer: Serializer<NQueensGameDTO?> {
    override val defaultValue: NQueensGameDTO?
        get() = null

    override suspend fun readFrom(input: InputStream): NQueensGameDTO? {
        try {
            return NQueensGameDTO.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: NQueensGameDTO?, output: OutputStream) {
        t?.writeTo(output)
    }
}