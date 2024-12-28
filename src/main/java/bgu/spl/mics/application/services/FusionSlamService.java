package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.FusionSlam;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    private final FusionSlam fusionSlam;
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
// Subscribe to TrackedObjectsEvents
        subscribeEvent(TrackedObjectsEvent.class, (TrackedObjectsEvent event) -> {
            System.out.println("FusionSlamService received TrackedObjectsEvent: " + event.getTrackedObjects());

            // Update the map with new tracked objects
            fusionSlam.updateWithTrackedObjects(event.getTrackedObjects());
        });

        // Subscribe to PoseEvents
        subscribeEvent(PoseEvent.class, (PoseEvent event) -> {
            System.out.println("FusionSlamService received PoseEvent: " + event.getPose());

            // Update the map with the new pose
            fusionSlam.updateWithPose(event.getPose());
        });

        // Subscribe to TickBroadcasts
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            System.out.println("FusionSlamService received TickBroadcast: " + tick.getTick());

            // Perform periodic maintenance or processing if necessary
            fusionSlam.performPeriodicUpdate();
        });

        // Log initialization completion
        System.out.println("FusionSlamService initialized and ready to integrate sensor data.");
    }
    }
}
