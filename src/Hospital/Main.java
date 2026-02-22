package Hospital;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Create creator = new Create(15);
        creator.setName("CREATOR");
        creator.setDistribution("exp");

        Process reception = new Process(1, 2);
        reception.setName("RECEPTION");
        reception.setDistribution("exp");

        Process room = new Process(3,8, 3);
        room.setName("ROOM");
        room.setDistribution("unif");

        Process wayToLab = new Process(2, 5, 1);
        wayToLab.setName("WAY_TO_LAB");
        wayToLab.setDistribution("unif");

        Process labReg = new Process(4.5,3, 1);
        labReg.setName("LAB_REGISTRATION");
        labReg.setDistribution("erl");
        
        Process labAnalysis = new Process(4, 2, 2);
        labAnalysis.setName("LAB_ANALYSIS");
        labAnalysis.setDistribution("erl");

        Process wayBack = new Process(2, 5, 1);
        wayBack.setName("WAY_BACK");
        wayBack.setDistribution("unif");

        Dispose dispose = new Dispose();
        dispose.setName("DISPOSE");

        creator.addNextElement(reception);
        reception.addNextElement(room);
        reception.addNextElement(wayToLab);
        room.addNextElement(dispose);
        wayToLab.addNextElement(labReg);
        labReg.addNextElement(labAnalysis);
        labAnalysis.addNextElement(wayBack);
        labAnalysis.addNextElement(dispose);
        wayBack.addNextElement(reception);

        ArrayList<Element> list = new ArrayList<>();
        list.add(creator);
        list.add(reception);
        list.add(room);
        list.add(wayToLab);
        list.add(labReg);
        list.add(labAnalysis);
        list.add(wayBack);
        list.add(dispose);

        Model model = new Model(list);
        model.simulate(100.0);
    }
}