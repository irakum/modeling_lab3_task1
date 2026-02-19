package Bank;

import java.util.ArrayList;

public class Model {
    private ArrayList<Element> list = new ArrayList<>();
    double tnext, tcurr;
    int event;

    public Model(ArrayList<Element> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }

    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            Element eventElement = null;

            for (Element e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    eventElement = e;
                }
            }

            if (eventElement == null || tnext == Double.MAX_VALUE) {
                System.out.println("No more events to process. Simulation ends.");
                break;
            }

            System.out.println("\nIt's time for event in " +
                    eventElement.getName() + ", time = " + tnext);

            for (Element e : list) {
                e.doStatistics(tnext - tcurr);
                e.setTcurr(tnext);
            }

            tcurr = tnext;

            if (eventElement != null) {
                eventElement.outAct();
                handleQueueSwitching(eventElement);
            }

            for (Element e : list) {
                if (e.getTnext() == tcurr && e != eventElement) {
                    System.out.println("Additional synchronous event in " + e.getName() + ", time = " + tcurr);
                    e.outAct();
                    handleQueueSwitching(eventElement);
                }
            }
            printInfo();
        }
        printResult();
    }

    private void handleQueueSwitching(Element eventElement) {
        if (eventElement instanceof Process) {
            Process p1 = null;
            Process p2 = null;

            for (Element e : list) {
                if (e.getName().equals("Cashier1")) p1 = (Process) e;
                if (e.getName().equals("Cashier2")) p2 = (Process) e;
            }

            if (p1 != null && p2 != null) {
                if (eventElement == p1) {
                    p2.trySwitchTo(p1);
                }
                else if (eventElement == p2) {
                    p1.trySwitchTo(p2);
                }
            }
        }
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        double avgTotalInBank = 0;
        int globalArrivals = 0;
        int failures = 0;

        for (Element e : list) {
            e.printResult();

            if (e instanceof Process) {
                Process p = (Process) e;
                double meanLoad = p.getBusyTime() / tcurr;
                double avgQueue = p.getMeanQueue() / tcurr;

                System.out.println("--- Stats for " + p.getName() + " ---");
                System.out.println("1) Середнє завантаження: " + meanLoad);
                System.out.println("5) Середня довжина черги: " + avgQueue);
                System.out.println("3) Сер. інтервал між від'їздами: " + p.getAverageInterval());
                System.out.println("7) Число змін смуг: " + p.getSwitchCount());

                avgTotalInBank += (avgQueue + meanLoad);
                failures += p.getFailure();
            }
            if (e instanceof Create) {
                globalArrivals = e.getQuantity();
            }
        }

        System.out.println("\n--- Global Bank Stats ---");
        System.out.println("2) Середнє число клієнтів у банку: " + avgTotalInBank);
        System.out.println("6) Відсоток відмов: " + (failures / (double)(globalArrivals) * 100) + "%");

        double lambda = (globalArrivals - failures) / tcurr; //закон Літтла, середній темп прибування
        System.out.println("4) Середній час перебування клієнта в банку: " + (avgTotalInBank / lambda));
    }
}
