package org.example.smart.smartcitytoolkitforge1;
import org.example.smart.smartcitytoolkitforge1.IMUData;
public class Azure {
    private final IMUData Data;
    public Azure() {
        Data = new IMUData();
    }
    public void update() {
        simulateData();
    }
    public void simulateData() {
        Data.positionY += 0.01f;
        Data.positionX += 0.01f;
        Data.positionZ += 0.01f;

        Data.rotationX += 0.1f;
        Data.rotationY += 0.1f;
        Data.rotationZ += 0.1f;

        Data.light_UP = 135;
        Data.light_DOWN = 55;
        Data.light_FORWARD = 75;
        Data.light_BACK = 255;
        Data.light_LEFT = 98;
        Data.light_RIGHT = 33;

        if(Data.positionX >= 10) {resetSimulatedData();}
    }

    public void resetSimulatedData() {
        Data.positionY = 0f;
        Data.positionX = 0f;
        Data.positionZ = 0f;

        Data.rotationX = 0f;
        Data.rotationY = 0f;
        Data.rotationZ = 0f;

        Data.light_UP = 255;
        Data.light_DOWN = 255;
        Data.light_FORWARD = 255;
        Data.light_BACK = 255;
        Data.light_LEFT = 255;
        Data.light_RIGHT = 255;
    }
    public IMUData getData() {
        return Data;
    }
}
