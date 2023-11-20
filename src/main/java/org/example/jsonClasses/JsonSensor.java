package org.example.jsonClasses;

public class JsonSensor {
    public String type;
    public byte address;
    public float[] position;
    public float[] rotation;
    public int fov;
    public int resolution_width;
    public int resolution_height;
    public int framerate;
    public String color_format;
    public boolean flip_image_y;
    public float min_distance;
    public float max_distance;
    public int sampling_rate;
    public float rotations_per_second;

    public int sample_count_fov;
    public float fov_distance_sensor;
}
