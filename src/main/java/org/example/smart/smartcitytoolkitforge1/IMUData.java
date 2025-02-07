package org.example.smart.smartcitytoolkitforge1;

public class IMUData {
    public float rotationX;
    public float rotationY;
    public float rotationZ;
    public float positionX;
    public float positionY;
    public float positionZ;
    public int light_UP;
    public int light_DOWN;
    public int light_FORWARD;
    public int light_BACK;
    public int light_LEFT;
    public int light_RIGHT;
    public IMUData() {
        rotationX = 0;
        rotationY = 0;
        rotationZ = 0;
        positionX = 0;
        positionY = 0;
        positionZ = 0;
        light_UP = 255;
        light_DOWN = 255;
        light_FORWARD = 255;
        light_BACK = 255;
        light_LEFT = 255;
        light_RIGHT = 255;
    }
}
