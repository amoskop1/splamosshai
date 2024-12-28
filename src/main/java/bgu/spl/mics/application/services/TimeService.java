package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    private final int tickTime;
    private final int duration;

    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.tickTime = tickTime;
        this.duration = duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        new Thread(() -> {
            for (int currentTick = 1; currentTick <= duration; currentTick++) {
                try {
                    // Broadcast the TickBroadcast
                    sendBroadcast(new TickBroadcast(currentTick));

                    // Sleep for the duration of a tick
                    Thread.sleep(tickTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Terminate the service after the specified duration
            terminate();
        }).start();

        // Log initialization completion
        System.out.println("TimeService initialized and ticking.");
    }
    }
}
