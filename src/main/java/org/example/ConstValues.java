package org.example;

public class ConstValues {
    public static final float DEGREES_TO_RADIANS = 0.017453292519943295f;
    public static final float RADIANS_TO_DEGREES = 57.29577951308232f;

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static void intToByteArray(int value, int offset, byte[] array) {
        array[offset] = (byte) (value >>> 24);
        array[offset + 1] = (byte) (value >>> 16);
        array[offset + 2] = (byte) (value >>> 8);
        array[offset + 3] = (byte) value;
    }

    public static int byteArrayToInt(int offset, byte[] array) {
        int val = 0;
        val |= ((int)array[offset] << 24) & 0xff000000;
        val |= ((int)array[offset + 1] << 16) & 0xff0000;
        val |= ((int)array[offset + 2] << 8) & 0xff00;
        val |= ((int)array[offset + 3]) & 0xff;
        return val;
    }

    public static void floatToByteArray(float value, int offset, byte[] array) {
        ConstValues.intToByteArray(Float.floatToIntBits(value), 0, array);
    }

    public static float byteArrayToFloat(int offset, byte[] array) {
        int val = 0;
        val |= ((int)array[offset] << 24) & 0xff000000;
        val |= ((int)array[offset + 1] << 16) & 0xff0000;
        val |= ((int)array[offset + 2] << 8) & 0xff00;
        val |= ((int)array[offset + 3]) & 0xff;
        return Float.intBitsToFloat(val);
    }
}
