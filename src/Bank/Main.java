package Bank;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Create creator = new Create(0.5);
        creator.setName("CREATOR");
        creator.setDistribution("exp");

        Process process1 = new Process(0.3);
        process1.setName("Cashier1");
        process1.setDistribution("exp");
        process1.setMaxqueue(3);

        Process process2 = new Process(0.3);
        process2.setName("Cashier2");
        process2.setDistribution("exp");
        process2.setMaxqueue(3);

        Dispose dispose = new Dispose();
        dispose.setName("DISPOSE");

        creator.addNextElement(process1);
        creator.addNextElement(process2);

        process1.addNextElement(dispose);
        process2.addNextElement(dispose);

        ArrayList<Element> list = new ArrayList<>();
        list.add(creator);
        list.add(process1);
        list.add(process2);
        list.add(dispose);

        // задаємо початкові умови
        process1.setState(1);
        process2.setState(1);
        process1.setQueue(2);
        process2.setQueue(2);
        process1.setTnext(FunRand.Norm(1.0, 0.3));
        process2.setTnext(FunRand.Norm(1.0, 0.3));
        creator.setTnext(0.1);

        Model model = new Model(list);
        model.simulate(1000.0);
    }
}