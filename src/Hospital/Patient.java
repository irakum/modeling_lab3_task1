package Hospital;

public class Patient {
    private int type;
    private double tArrival;
    private int id;
    private static int nextId = 1;

    public Patient(int type, double tArrival) {
        this.type = type;
        this.tArrival = tArrival;
        this.id = nextId++;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public double getTArrival() {
        return tArrival;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Patient #" + id + " [Type " + type + "]";
    }
}
