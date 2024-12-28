package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker liDarWorkerTracker;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService");
        this.liDarWorkerTracker = liDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // Subscribe to DetectObjectsEvents
        subscribeEvent(DetectObjectsEvent.class, (DetectObjectsEvent event) -> {
            System.out.println("LiDarService received DetectObjectsEvent for object: " + event.toString());

            // Process the object using LiDarWorkerTracker
            String[] trackedObjects = liDarWorkerTracker.processObject(event.getObjectDetails());

            // Use a normal for loop to process tracked objects
            for (int i = 0; i < trackedObjects.length; i++) {
                String trackedObject = trackedObjects[i];
                TrackedObjectsEvent trackedEvent = new TrackedObjectsEvent(trackedObject);
                sendEvent(trackedEvent);
            }
        });

        // Subscribe to TickBroadcasts
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            System.out.println("LiDarService received TickBroadcast: " + tick.getTick());

            // Perform periodic maintenance or processing if necessary
            liDarWorkerTracker.performPeriodicUpdate();
        });

        // Log initialization completion
        System.out.println("LiDarService initialized and ready to process LiDAR data.");
    }
    }
}
