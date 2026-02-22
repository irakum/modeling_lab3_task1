package Hospital;

public class Dispose extends Element {
    private double totalTimeInSystem = 0;

    private double[] timeByType = new double[4];
    private int[] countByType = new int[4];

    public Dispose() {
        super();
    }

    public void inAct(Patient p) {
        if (super.getState() == 1) {
            this.updateBusyTime();
        }

        double timeSpent = getTcurr() - p.getTArrival();

        totalTimeInSystem += timeSpent;

        timeByType[p.getType()] += timeSpent;
        countByType[p.getType()]++;

        System.out.println(getTcurr() + ": Patient" + p.getId() +
                " покинув систему. Час у системі: " + timeSpent);

        super.outAct();
    }

    @Override
    public void outAct() {

    }

    @Override
    public void printResult() {
        System.out.println(getName() + " quantity = " + getQuantity());
    }
}
