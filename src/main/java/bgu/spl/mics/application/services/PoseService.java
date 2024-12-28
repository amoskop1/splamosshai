package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPSIMU;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcasts
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            System.out.println("PoseService received TickBroadcast: " + tick.getTick());

            // Retrieve the current pose from GPSIMU
            String currentPose = gpsimu.getPose();

            // Send a PoseEvent with the current pose
            PoseEvent poseEvent = new PoseEvent(currentPose);
            sendEvent(poseEvent);
        });

        // Log initialization completion
        System.out.println("PoseService initialized and ready to broadcast poses.");
    }
}