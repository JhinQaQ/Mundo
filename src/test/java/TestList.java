import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TestList {
    public static void main(String[] args) {


        List<String> arr = new ArrayList<String>();
        long startTime = System.currentTimeMillis();
        Random random = new Random();

        for (int i = 1; i < 50000; i++) {
            arr.add(random.nextInt(i), "a");
        }
        long endTime = System.currentTimeMillis();
        System.out.println(startTime-endTime);

        List<String> lis = new LinkedList<String>();
        long start = System.currentTimeMillis();

        for (int i = 1; i < 50000; i++) {
            arr.add(random.nextInt(i), "a");
        }
        long end = System.currentTimeMillis();
        System.out.println(start-end);

    }
}
