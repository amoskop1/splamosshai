package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectObjectsEvent implements Event<Boolean> {
    private final List<StampedDetectedObjects> stampedDetectedObjects;

    public DetectObjectsEvent(List<StampedDetectedObjects> stampedDetectedObjects) {
        this.stampedDetectedObjects = stampedDetectedObjects;
    }


    public List<StampedDetectedObjects> getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }

    @Override
    public String toString() {
        return getStampedDetectedObjects().toString();
    }
}
