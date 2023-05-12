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
}
