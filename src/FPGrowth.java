import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FPGrowth {
    private static Scanner reader = new Scanner(System.in);
    private static HashMap<String, Integer> Fmap = new HashMap<>();
    private static ArrayList<String> F = new ArrayList<>();
    private static ArrayList<TreeSet<String>> transactions = new ArrayList<>();
    private static int minCount;
    private static TreeMap<String, Integer> Rules;
    private static FpNode HEAD;

    private static Comparator<String> valComparator = (t1, t2) -> {
        Integer i1 = Fmap.get(t1);
        Integer i2 = Fmap.get(t2);
        return -i1.compareTo(i2); // -ve because to get descending order
    };

    public static void main(String[] kune) throws FileNotFoundException {
        println("Enter the path of transactions file: ");
        String filePath = reader.nextLine();
        Scanner scan = new Scanner(new File(filePath));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] lineArray = line.split(",");
            TreeSet<String> set = new TreeSet<>(Arrays.asList(lineArray));
            transactions.add(set);
        }

        // While loop to give options to user
        println("`````````FP-GROWTH ALGORITHM``````````");
        boolean isRunning = true;
        while (isRunning) {
            println("");
            println("1. Determine Frequent Item Data set");
            println("2. List all Transactions");
            println("3. Frequent 1-item sets");
            println("Press any key to exit");
            int choice = reader.nextInt();
            switch (choice) {
                case 1:
                    F.clear();
                    Fmap.clear();
                    print("Enter Minimum Support Count: ");
                    minCount = reader.nextInt();
                    startFPGrowth();
                    println("Doing great\n");
                    break;

                case 2:
                    println("All Transactions so Far..");
                    printAllTransactions();
                    break;

                case 3:
                    printMap(Fmap);
                    break;

                default:
                    println("Good bye, Folks!\n");
                    isRunning = false;
                    break;
            }
        }
    }

    private static void startFPGrowth() {
        // Find all 1-Freq item sets
        Fmap = findL1();
        printMap(Fmap);

        // Sort F in (descending) order
        sortF();
        printArrayList(F);

        println("");
        ConstructFpTree();
        println("Finished Construction of Fp-Tree");

        // test tree by printing a node's children and their count
        testTree();

        // Compute the Association rules from the Tree

    }

    private static void testTree() {
        for (FpNode node : HEAD.getChildren().get(0).getChildren()) {
            print(node.getName() + ":" + node.getCount() + " ");
        }
        println("");
    }

    private static void ConstructFpTree() {
        // Construct tree
        HEAD = new FpNode("NULL", 0, new ArrayList<>());

        for (TreeSet<String> transaction : transactions) {
            ArrayList<String> t = new ArrayList<>(transaction);
            t.sort(valComparator);
            printArrayList(t);

            InsertItem(t, HEAD);
        }
    }

    private static void InsertItem(ArrayList<String> t, FpNode T) {
        if (!T.getChildren().isEmpty()) {
            for (FpNode child : T.getChildren()) {
                if (child.getName().equals(t.get(0))) {
                    child.setCount(child.getCount() + 1);
                    t.remove(t.get(0));
                    if (!t.isEmpty()) {
                        InsertItem(t, child);
                    }
                    return;
                }
            }
            FpNode newNode = new FpNode(t.get(0), 1, new ArrayList<>());
            t.remove(t.get(0));
            T.getChildren().add(newNode);
            if (!t.isEmpty()) {
                InsertItem(t, newNode);
            }
        } else {
            FpNode newNode = new FpNode(t.get(0), 1, new ArrayList<>());
            T.getChildren().add(newNode);
            t.remove(t.get(0));
            if (!t.isEmpty()) {
                InsertItem(t, newNode);
            }
        }
    }

    private static void sortF() {
        ArrayList<String> k = new ArrayList<>(Fmap.keySet());
        printArrayList(k);
        // comparator is mae static at first itself
        k.sort(valComparator);
        F = k;
    }

    private static HashMap<String, Integer> findL1() {
        HashMap<String, Integer> C1 = new HashMap<>();
        for (TreeSet<String> transaction : transactions) {
            for (String item : transaction) {
                if (!C1.containsKey(item)) {
                    C1.put(item, 1);
                } else {
                    C1.put(item, C1.get(item) + 1);
                }
            }
        }

        // Take min support into consideration and remove others
        HashMap<String, Integer> L1 = new HashMap<>();
        for (Map.Entry<String, Integer> entry : C1.entrySet()) {
            if (entry.getValue() >= minCount) {
                L1.put(entry.getKey(), entry.getValue());
            }
        }
        return L1;
    }

    private static void printAllTransactions() {
        for (TreeSet<String> transaction : transactions) {
            for (String item : transaction) {
                print(item);
            }
            println("");
        }
    }

    private static void printArrayList(ArrayList<String> comb) {
        for (String a : comb) {
            print(a + " ");
        }
        println("");
    }

    private static void printIntArrayList(ArrayList<Integer> comb) {
        for (Integer a : comb) {
            print(a + " ");
        }
        println("");
    }

    private static void printMap(HashMap<String, Integer> treeMap) {
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        println("");
    }

    private static void print(String s) {
        System.out.print(s);
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
