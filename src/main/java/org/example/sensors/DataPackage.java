package org.example.sensors;

import org.example.ConstValues;

public class DataPackage {

    public static final byte TYPE_CAR = 0;
    public static final byte TYPE_2D_CAM = 1;
    public static final byte TYPE_3D_CAM = 2;
    public static final byte TYPE_DISTANCE_SENSOR = 3;
    public static final byte TYPE_LIDAR = 4;

    public static final byte COLOR_FORMAT_BW = 0;
    public static final byte COLOR_FORMAT_RGB = 1;
    public static final byte COLOR_FORMAT_BGR = 2;
    public static final byte COLOR_FORMAT_D8 = 3;
    public static final byte COLOR_FORMAT_D16 = 4;

    public byte[] header;
    public byte[] customData;

    private int customDataLength;

    public DataPackage(int customDataLength)
    {
        header = new byte[6];
        customData = new byte[customDataLength];
        this.customDataLength = customDataLength;
    }

    public DataPackage(DataPackage other)
    {
        header = other.header.clone();
        customData = other.customData.clone();
        customDataLength = other.customDataLength;
    }

    public void setHeader(byte type, byte address)
    {
        header[0] = type;
        header[1] = address;
        ConstValues.intToByteArray(customDataLength, 2, header);
    }
}
