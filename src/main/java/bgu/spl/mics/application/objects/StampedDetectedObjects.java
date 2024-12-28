package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final int time;
    private final List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public int getTime() {
        return time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(time).append(", Detected Objects: [");
        for (int i = 0; i < detectedObjects.size(); i++) {
            sb.append(detectedObjects.get(i).toString());
            if (i < detectedObjects.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}