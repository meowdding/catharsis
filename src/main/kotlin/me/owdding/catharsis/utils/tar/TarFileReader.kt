package me.owdding.catharsis.utils.tar

import java.io.InputStream

object TarFileReader {
    fun readTar(inputStream: InputStream): Map<String, ByteArray> {
        val entries = mutableMapOf<String, ByteArray>()
        inputStream.use {
            val prefix = "./"
            var nextName: String? = null
            while (true) {
                val array = ByteArray(512)
                var readBytes = it.read(array) // Should always be 512
                while (readBytes != 512) {
                    val thisReadBytes = it.read(array, readBytes, 512 - readBytes)
                    if (thisReadBytes == -1) return@use
                    readBytes += thisReadBytes
                }
                val name = readString(array, 0, 100)
                if (name.isEmpty()) continue

                val nameSuffix = readString(array, 345, 155)
                val length = readOctalNumber(array, 124, 12)
                val fileType = array[156].toInt()
                val magic = readString(array, 257, 5)

                if (
                    magic != "ustar" ||
                    length == 0 ||
                    fileType.toChar() != '0'
                ) continue

                val data = ByteArray(length)
                var totalDataBytesRead = it.read(data)

                while (totalDataBytesRead < length) {
                    val bytesReadThisRead = it.read(data, totalDataBytesRead, length - totalDataBytesRead)
                    if (bytesReadThisRead == -1) return@use

                    totalDataBytesRead += bytesReadThisRead
                }

                // Having 2 %s is important I promise
                it.skip(((512 - (totalDataBytesRead % 512)) % 512).toLong())

                if (name == "./@LongLink") {
                    nextName = data.toString(Charsets.US_ASCII).dropLast(1)
                } else if (nextName != null) {
                    entries[nextName.removePrefix(prefix)] = data
                    nextName = null
                } else {
                    entries[name.removePrefix(prefix) + nameSuffix] = data
                }
            }
        }
        return entries
    }

    @Suppress("SameParameterValue")
    private fun readOctalNumber(byteArray: ByteArray, offset: Int, maxLength: Int): Int {
        var accumulated = 0
        for (i in offset..<offset + maxLength) {
            val byte = byteArray[i].toInt()
            if (byte == 0 || byte == 32) return accumulated
            val number = byte.toChar() - '0'
            accumulated *= 8
            accumulated += number
        }
        return accumulated
    }

    private fun readString(byteArray: ByteArray, offset: Int, maxLength: Int): String {
        return buildString {
            for (i in offset..<offset + maxLength) {
                val byte = byteArray[i].toInt()
                if (byte == 0) return@buildString
                this.append(byte.toChar())
            }
        }
    }
}
