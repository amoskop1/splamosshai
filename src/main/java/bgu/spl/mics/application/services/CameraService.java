package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.broadcasts.TickBroadcast;

import java.util.List;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService_" + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcasts to process updates based on system ticks
        subscribeBroadcast(TickBroadcast.class, tick -> {
            System.out.println("CameraService received TickBroadcast: Tick " + tick.getTick());

            // Detect objects at the current tick using the Camera instance
            List<StampedDetectedObjects> detectedObjects = camera.getDetectedObjectsAtTick(tick.getTick());

            // Create and send DetectObjectsEvent with the entire list
            if (!detectedObjects.isEmpty()) {
                DetectObjectsEvent event = new DetectObjectsEvent(detectedObjects);
                sendEvent(event);
            }
        });

        // Log initialization completion
        System.out.println("CameraService initialized for Camera ID: " + camera.getId());
    }
}
