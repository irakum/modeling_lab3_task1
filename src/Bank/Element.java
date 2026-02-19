package Bank;//import FunRand;

import java.util.ArrayList;
import java.util.List;

public class Element {

    private String name;
    private double tnext;
    private double delayMean, delayDev;
    private String distribution;
    private int quantity;
    private double tcurr;
    private int state;
    protected List<Element> nextElements = new ArrayList<>();
    private static int nextId=0;
    private int id;
    private double busyTime;

    public Element(){
        busyTime = 0.0;
        tnext = Double.MAX_VALUE;
        delayMean = 1.0;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
    }
    public Element(double delay){
        tnext = Double.MAX_VALUE;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
        busyTime = 0.0;
    }

    public Element(double delay, int devices){
        tnext = Double.MAX_VALUE;
        delayMean = delay;
        distribution = "";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
        busyTime = 0.0;
    }

    public Element(String nameOfElement, double delay){
        name = nameOfElement;
        tnext = Double.MAX_VALUE;
        delayMean = delay;
        distribution = "exp";
        tcurr = tnext;
        state=0;
        id = nextId;
        nextId++;
        name = "element"+id;
        busyTime = 0.0;
    }

    public double getDelay() {
        double delay = getDelayMean();
        if ("exp".equalsIgnoreCase(getDistribution())) {
            delay = FunRand.Exp(getDelayMean());
        } else {
            if ("norm".equalsIgnoreCase(getDistribution())) {
                delay = FunRand.Norm(getDelayMean(),
                        getDelayDev());
            } else {
                if ("unif".equalsIgnoreCase(getDistribution())) {
                    delay = FunRand.Unif(getDelayMean(),
                            getDelayDev());
                } else {
                    if("".equalsIgnoreCase(getDistribution()))
                        delay = getDelayMean();
                }
            }
        }
        return delay;
    }
    public double getDelayDev() {
        return delayDev;
    }
    public void setDelayDev(double delayDev) {
        this.delayDev = delayDev;
    }
    public String getDistribution() {
        return distribution;
    }
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getTcurr() {
        return tcurr;
    }
    public void setTcurr(double tcurr) {
        this.tcurr = tcurr;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void inAct() {

    }
    public void outAct(){
        quantity++;
    }

    public void addNextElement(Element element) {
        this.nextElements.add(element);
    }

    protected Element selectNextElement() {
        if (nextElements.isEmpty()) return null;

        return nextElements.getFirst();
    }

    public double getTnext() {
        return tnext;
    }
    public void setTnext(double tnext) {
        this.tnext = tnext;
    }
    public double getDelayMean() {
        return delayMean;
    }
    public void setDelayMean(double delayMean) {
        this.delayMean = delayMean;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void printResult(){
        System.out.println(getName()+ " quantity = "+ quantity);
    }

    public void printInfo(){
        System.out.println(getName()+ " state= " +state+
                " quantity = "+ quantity+
                " tnext= "+tnext);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void doStatistics(double delta){
        /*
        if (state == 1) {
            busyTime += delta; // Оновлення часу завантаження
        }

         */
    }

    public void updateBusyTime() {
        busyTime += (tcurr - tnext);
    }

    public double getBusyTime() {
        return busyTime;
    }

    public void setBusyTime(double time) {
        busyTime = time;
    }

    public double getMeanLoad(double totalTime) {
        return busyTime / totalTime;
    }
}
