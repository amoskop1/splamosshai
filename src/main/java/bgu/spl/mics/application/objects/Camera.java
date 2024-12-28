package bgu.spl.mics.application.objects;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Camera {
    private final int id;
    private final int frequency; // Time interval at which the camera operates
    private final List<StampedDetectedObjects> detectedObjectsList;

    /**
     * Constructor for Camera.
     *
     * @param id                  the unique identifier for the camera
     * @param frequency           the frequency (in ticks) at which the camera sends new data
     * @param detectedObjectsList the list of detected objects with timestamps
     */
    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.detectedObjectsList = new ArrayList<>(detectedObjectsList);
    }

    /**
     * Retrieves the detected objects for a specific tick.
     *
     * @param currentTick the current simulation tick
     * @return a list of StampedDetectedObjects detected at the given tick
     */
    public List<StampedDetectedObjects> getDetectedObjectsAtTick(int currentTick) {
        List<StampedDetectedObjects> result = new ArrayList<>();
        for (int i = 0; i < detectedObjectsList.size(); i++) {
            StampedDetectedObjects stamped = detectedObjectsList.get(i);
            if (stamped.getTime() == currentTick) {
                result.add(stamped);
            }
        }
        return result;
    }
    /**
     * Adds a new set of detected objects to the camera's internal list.
     *
     * @param stampedDetectedObjects The StampedDetectedObjects to add.
     */
    public void addDetectedObjects(StampedDetectedObjects stampedDetectedObjects) {
        detectedObjectsList.add(stampedDetectedObjects);
    }
    /**
     * Gets the unique identifier of the camera.
     *
     * @return the camera ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the frequency of the camera's operation.
     *
     * @return the frequency in ticks
     */
    public int getFrequency() {
        return frequency;
    }
}
