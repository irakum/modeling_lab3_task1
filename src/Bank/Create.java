package Bank;

public class Create extends Element {
    public Create(double delay) {
        super(delay);
        super.setTnext(0.0);
    }
    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());
        selectNextElement().inAct();
    }

    @Override
    protected Element selectNextElement(){
        Process cashier1 = (Process) nextElements.get(0);
        Process cashier2 = (Process) nextElements.get(1);

        if (cashier1.getQueue() <= cashier2.getQueue()) {
            return cashier1;
        } else {
            return cashier2;
        }
    }
}
