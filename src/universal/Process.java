package universal;

import java.util.ArrayList;
import java.util.List;

public class Process extends Element {
    private int queue, maxqueue, failure;
    private double meanLoad;
    private double meanQueue;

    private int maxTransitions = 3000;
    private int currentTransitions = 0;

    public Process(double delay, int numDevices) {
        super(delay, numDevices);
        this.queue = 0;
        this.maxqueue = Integer.MAX_VALUE;
        this.meanQueue = 0.0;
    }

    @Override
    public void inAct() {
        if (currentTransitions >= maxTransitions) {
            failure++;
            return;
        }
        currentTransitions++;

        if (busyDevices < numDevices) {
            busyDevices++;
            super.setState(busyDevices);
            double arrivalTime = super.getTcurr() + super.getDelay();

            if (busyDevices == 1) {
                super.setTnext(arrivalTime);
            } else {
                if (arrivalTime < super.getTnext()) {
                    super.setTnext(arrivalTime);
                }
            }
        } else {
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        busyDevices--;

        if (queue > 0) {
            queue--;
            busyDevices++;
            super.setTnext(super.getTcurr() + super.getDelay());
        } else if (busyDevices > 0) {
            double nextTime = Double.MAX_VALUE;
            for (int i = 0; i < busyDevices; i++) {
                nextTime = Math.min(nextTime, super.getTcurr() + super.getDelay());
            }
            super.setTnext(nextTime);
        } else {
            super.setState(0);
            super.setTnext(Double.MAX_VALUE);
        }

        if (!super.nextElements.isEmpty()) {
            Element next = selectNextElement();
            System.out.println(getName() + " sends request to " + next.getName());
            if (next != null) {
                next.inAct();
            }
        }

        super.setState(busyDevices);
    }

    public int getFailure() {
        return failure;
    }
    public int getQueue() {
        return queue;
    }
    public void setQueue(int queue) {
        this.queue = queue;
    }
    public int getMaxqueue() {
        return maxqueue;
    }
    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
    }
    @Override
    public void doStatistics(double delta) {
        meanQueue = getMeanQueue() + queue * delta;
        super.setBusyTime(getBusyTime() + delta * busyDevices);
    }
    public double getMeanQueue() {
        return meanQueue;
    }
    public double getMeanLoad() {
        return this.meanLoad;
    }
    public int getNumDevices() {
        return numDevices;
    }

    public void setNumDevices(int numDevices) {
        this.numDevices = numDevices;
    }
}
