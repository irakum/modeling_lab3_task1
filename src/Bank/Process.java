package Bank;

public class Process extends Element {
    private int queue, maxqueue, failure;

    private double meanQueue; //пункт 5
    private int switchCount = 0; // пункт 7
    private double lastDepartureTime = 0; // пункт 3
    private double sumIntervals = 0; //пункт 3 (сума для середнього)

    public Process(double delay) {
        super(delay);
        this.queue = 0;
        this.maxqueue = Integer.MAX_VALUE;
        this.meanQueue = 0.0;
    }

    @Override
    public void inAct() {
        if (super.getState() == 0){
            super.setState(1);
            super.setTnext(super.getTcurr() + super.getDelay());
        }
        else {
            if (getQueue() < 3) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setState(0);

        if (super.getQuantity() > 0) {
            sumIntervals += (super.getTcurr() - lastDepartureTime);
        }
        lastDepartureTime = super.getTcurr();

        if (queue > 0) {
            queue--;
            super.setState(1);
            super.setTnext(super.getTcurr() + super.getDelay());
        } else {
            super.setTnext(Double.MAX_VALUE);
        }

        if (!super.nextElements.isEmpty()) {
            Element next = selectNextElement();
            System.out.println(getName() + " sends request to " + next.getName());
            if (next != null) {
                next.inAct();
            }
        }
    }

    public void trySwitchTo(Process otherCashier) {
        if (this.getQueue() - otherCashier.getQueue() >= 2) {
            this.setQueue(this.getQueue() - 1);
            otherCashier.setQueue(otherCashier.getQueue() + 1);

            this.switchCount++;

            System.out.println(super.getTcurr() + ": Клієнт перейшов з " +
                    this.getName() + " до " + otherCashier.getName());
        }
    }

    @Override
    public void doStatistics(double delta) {
        meanQueue = getMeanQueue() + queue * delta;
        if (super.getState() == 1) {
            super.setBusyTime(super.getBusyTime() + delta);
        }
    }

    // Геттери для результатів
    public double getAverageInterval() {
        return super.getQuantity() > 1 ? sumIntervals / (super.getQuantity() - 1) : 0;
    }

    public int getSwitchCount() {
        return switchCount;
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

    public double getMeanQueue() {
        return meanQueue;
    }
}
