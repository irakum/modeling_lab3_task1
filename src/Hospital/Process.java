package Hospital;

import java.util.ArrayList;

public class Process extends Element {
    private ArrayList<Patient> queue = new ArrayList<>();
    private ArrayList<Double> tNextDevices = new ArrayList<>();
    private ArrayList<Patient> patientsInService = new ArrayList<>();

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

        processNextStep(finishedPatient);

        if (!queue.isEmpty()) {
            Patient nextPatient = null;

            if (this.getName().equals("RECEPTION")){
                for (Patient candidate : queue) {
                    if (candidate.getType() == 1) {
                        nextPatient = candidate;
                        break; // Знайшли найпріоритетнішого
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

        updateTNext();
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

    private void processNextStep(Patient p) {
        String name = this.getName().toUpperCase();

        if (name.equals("RECEPTION")) {
            if (p.getType() == 1) {
                Element room = findNextByName("ROOM");
                if (room != null) {
                    ((Process) room).inAct(p);
                }
            } else {
                Element toLabWay = findNextByName("WAY_TO_LAB");
                if (toLabWay != null) {
                    ((Process) toLabWay).inAct(p);
                }
            }
        }

        else if (name.equals("LAB_REGISTRATION")) {
            System.out.println(super.getTcurr() + ": Patient" + p.getId() + " пройшов реєстрацію в лабі та очікує лаборанта.");
            Element labAnalysis = findNextByName("LAB_ANALYSIS");
            if (labAnalysis != null) {
                ((Process) labAnalysis).inAct(p);
            }
        }

        else if (name.equals("LAB_ANALYSIS")) {
            if (p.getType() == 2) {
                p.setType(1);
                System.out.println("Patient" + p.getId() + " здав аналізи і повертається в чергу до лікарів як тип 1");

                Element backWay = findNextByName("WAY_BACK");
                if (backWay != null) {
                    ((Process) backWay).inAct(p);
                }
            } else {
                System.out.println("Patient" + p.getId() + " залишає лікарню після обстеження.");
                Element dispose = findNextByName("DISPOSE");
                if (dispose != null) {
                    dispose.inAct();
                }
            }
        }

        else if (name.equals("ROOM")) {
            System.out.println("Patient" + p.getId() + " доставлений у палату.");
            Element dispose = findNextByName("DISPOSE");
            if (dispose != null) {
                dispose.inAct();
            }
        }

        else if (name.startsWith("WAY")) {
            if (!nextElements.isEmpty()) {
                ((Process)nextElements.getFirst()).inAct(p);
            }
        }
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
