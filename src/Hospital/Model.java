package Hospital;

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
            }

            for (Element e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
            printInfo();
        }
        printResult();
    }

    public void printInfo() {
        for (Element e : list) {
            e.printInfo();
        }
    }
    public void printResult() {
        System.out.println("\n------------- FINAL RESULTS -------------");

        Dispose finalDispose = null;
        Process labReg = null;

        for (Element e : list) {
            e.printResult(); // Стандартний вивід (черги, завантаження)

            if (e instanceof Dispose) {
                finalDispose = (Dispose) e;
            }
            if (e.getName().equalsIgnoreCase("LAB_REGISTRATION")) {
                labReg = (Process) e;
            }
        }

        System.out.println("\n--- Аналіз часу в системі ---");
        if (finalDispose != null) {
            for (int i = 1; i <= 3; i++) {
                System.out.printf("Середній час у системі для типу %d: %.2f\n",
                        i, finalDispose.getAvgTime(i));
            }
        }

        System.out.println("\n--- Аналіз лабораторії ---");
        if (labReg != null) {
            System.out.printf("Середній інтервал прибуття в лабораторію: %.2f\n",
                    labReg.getAverageInterval());
        }
    }
}
