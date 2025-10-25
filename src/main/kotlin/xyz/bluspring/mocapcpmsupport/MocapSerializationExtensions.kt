package xyz.bluspring.mocapcpmsupport

import net.mt1006.mocap.mocap.files.RecordingFiles
import java.util.*

fun RecordingFiles.Reader.readFixedBitSet(size: Int): BitSet {
    val size = -Math.floorDiv(-size, 8)
    val bytes = ByteArray(size)
    for (i in 0 until size) {
        bytes[i] = this.readByte()
    }
    return BitSet.valueOf(bytes)
}

fun RecordingFiles.Writer.writeFixedBitSet(bitSet: BitSet, size: Int) {
    if (bitSet.length() > size) {
        throw IndexOutOfBoundsException("BitSet is larger than expected size (${bitSet.length()} > $size)")
    }

    val bytes = bitSet.toByteArray()
    val size = -Math.floorDiv(-size, 8)
    for (b in bytes.copyOf(size)) {
        this.addByte(b)
    }
}

fun <E : Enum<E>> RecordingFiles.Writer.writeEnumSet(enumSet: EnumSet<E>, enumClass: Class<E>) {
    val enums = enumClass.enumConstants
    val bitSet = BitSet(enums.size)

    for ((i, e) in enums.withIndex()) {
        bitSet.set(i, enumSet.contains(e))
    }

    this.writeFixedBitSet(bitSet, enums.size)
}

fun <E : Enum<E>> RecordingFiles.Reader.readEnumSet(enumClass: Class<E>): EnumSet<E> {
    val enums = enumClass.enumConstants
    val bitSet = this.readFixedBitSet(enums.size)
    val enumSet = EnumSet.noneOf(enumClass)

    for ((i, e) in enums.withIndex()) {
        if (bitSet.get(i)) {
            enumSet.add(e)
        }
    }

    return enumSet
}

fun RecordingFiles.Writer.writeByteArray(byteArray: ByteArray) {
    this.addInt(byteArray.size)
    for (b in byteArray) {
        this.addByte(b)
    }
}

fun RecordingFiles.Reader.readByteArray(): ByteArray {
    val size = this.readInt()
    val byteArray = ByteArray(size)
    for (i in 0 until size) {
        byteArray[i] = this.readByte()
    }

    return byteArray
}