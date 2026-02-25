package Hospital;

import java.util.ArrayList;

public class Process extends Element {
    private ArrayList<Patient> queue = new ArrayList<>();
    private ArrayList<Double> tNextDevices = new ArrayList<>();
    private ArrayList<Patient> patientsInService = new ArrayList<>();

    private double lastArrivalTime = 0;
    private double totalIntervals = 0;
    private int arrivalCount = 0;

    public Process(double delay, int numDevices) {
        super(delay, numDevices);
        for (int i = 0; i < numDevices; i++) {
            tNextDevices.add(Double.MAX_VALUE);
            patientsInService.add(null);
        }
    }

    public Process(double delayMean, double delayDev, int numDevices) {
        super(delayMean, delayDev, numDevices);
        for (int i = 0; i < numDevices; i++) {
            tNextDevices.add(Double.MAX_VALUE);
            patientsInService.add(null);
        }
    }

    public void inAct(Patient p) {
        if (this.getName().equalsIgnoreCase("LAB_REGISTRATION")) {
            if (arrivalCount > 0) {
                totalIntervals += (super.getTcurr() - lastArrivalTime);
            }
            lastArrivalTime = super.getTcurr();
            arrivalCount++;
        }

        int freeDeviceIndex = -1;
        for (int i = 0; i < numDevices; i++) {
            if (tNextDevices.get(i) == Double.MAX_VALUE) {
                freeDeviceIndex = i;
                break;
            }
        }
        if (freeDeviceIndex != -1) {
            double delay = getDelayForPatient(p);
            tNextDevices.set(freeDeviceIndex, super.getTcurr() + delay);
            patientsInService.set(freeDeviceIndex, p);

            updateTNext();
        } else {
            queue.add(p);
            System.out.println(super.getTcurr() + ": Patient" + p.getId() + " став у чергу до " + getName());
        }
    }

    @Override
    public void outAct() {
        super.outAct();

        int deviceIndex = -1;
        for (int i = 0; i < numDevices; i++) {
            if (tNextDevices.get(i) == super.getTcurr()) {
                deviceIndex = i;
                break;
            }
        }

        Patient finishedPatient = patientsInService.get(deviceIndex);
        System.out.println(super.getTcurr() + ": Patient" + finishedPatient.getId() + " закінчив діяльність в " + getName());

        tNextDevices.set(deviceIndex, Double.MAX_VALUE);
        patientsInService.set(deviceIndex, null);

        handlePatientTransition(finishedPatient);
        checkQueue(deviceIndex);

        updateTNext();
    }

    private void handlePatientTransition(Patient p) {
        String name = this.getName().toUpperCase();

        switch (name) {
            case "RECEPTION" -> {
                if (p.getType() == 1) {
                    sendTo("ROOM", p);
                } else {
                    sendTo("WAY_TO_LAB", p);
                }
            }
            case "LAB_REGISTRATION" -> sendTo("LAB_ANALYSIS", p);
            case "LAB_ANALYSIS" -> {
                if (p.getType() == 2) {
                    p.setType(1);
                    sendTo("WAY_BACK", p);
                } else {
                    ((Dispose) findNextByName("DISPOSE")).inAct(p);
                }
            }
            case "ROOM" -> ((Dispose) findNextByName("DISPOSE")).inAct(p);
            case "WAY_BACK" -> sendTo("RECEPTION", p);
            case "WAY_TO_LAB" -> sendTo("LAB_REGISTRATION", p);
        }
    }

    private void sendTo(String nextName, Patient p) {
        Element next = findNextByName(nextName);
        if (next != null) {
            ((Process) next).inAct(p);
        }
    }

    private void checkQueue(int deviceIndex) {
        if (queue.isEmpty()) return;

        Patient nextPatient = null;

        if (this.getName().equalsIgnoreCase("RECEPTION")) {
            for (Patient candidate : queue) {
                if (candidate.getType() == 1) {
                    nextPatient = candidate;
                    break;
                }
            }
        }

        if (nextPatient == null) {
            nextPatient = queue.getFirst();
        }

        queue.remove(nextPatient);

        double delay = getDelayForPatient(nextPatient);
        tNextDevices.set(deviceIndex, super.getTcurr() + delay);
        patientsInService.set(deviceIndex, nextPatient);
    }

    private double getDelayForPatient(Patient p) {
        if (this.getName().equals("RECEPTION")) {
            switch (p.getType()) {
                case 1: return FunRand.Exp(15.0);
                case 2: return FunRand.Exp(40.0);
                case 3: return FunRand.Exp(30.0);
            }
        }

        return super.getDelay();
    }

    public double getAverageInterval() {
        return arrivalCount > 1 ? totalIntervals / (arrivalCount - 1) : 0;
    }

    private Element findNextByName(String name) {
        for (Element e : nextElements) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }

    private void updateTNext() {
        double minTNext = Double.MAX_VALUE;
        for (double t : tNextDevices) {
            if (t < minTNext) {
                minTNext = t;
            }
        }
        super.setTnext(minTNext);
    }

    @Override
    public void printInfo() {
        super.printInfo();
    }
    @Override
    public void doStatistics(double delta) {
        super.setBusyTime(getBusyTime() + delta * busyDevices);
    }

    public int getNumDevices() {
        return numDevices;
    }
    public void setNumDevices(int numDevices) {
        this.numDevices = numDevices;
    }
}
