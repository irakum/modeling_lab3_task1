package Hospital;

public class Create extends Element {
    public Create(double delay) {
        super(delay);
        super.setTnext(0.0);
    }
    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(super.getTcurr() + super.getDelay());

        int type = generatePatientType();
        Patient p = new Patient(type, super.getTcurr());
        System.out.println(super.getTcurr() + ": Створено нового " + "Patient" + p.getId());

        Element reception = super.getNextElements().getFirst();
        ((Process) reception).inAct(p);
    }

    private int generatePatientType() {
        double rand = Math.random();
        if (rand < 0.5) {
            return 1;
        } else if (rand < 0.6) {
            return 2;
        } else {
            return 3;
        }
    }
}
