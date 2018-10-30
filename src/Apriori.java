import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Apriori {
    private static Scanner reader = new Scanner(System.in);
    private static int minCount;
    private static ArrayList<TreeSet<String>> transactions = new ArrayList<>();
    private static ArrayList<TreeMap<String, Integer>> L = new ArrayList<>();
    private static ArrayList<TreeMap<String, Integer>> C = new ArrayList<>();

    public static void main(String[] kune) throws FileNotFoundException {
        // read transactions from csv file
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
        println("`````````APRIORI ALGORITHM``````````");
        boolean isRunning = true;
        while (isRunning) {
            println("");
            println("1. Determine Frequent Item Data set");
            println("2. List all Transactions");
            println("3. C[index]");
            println("4. L[index]");
            println("Press any key to exit");
            int choice = reader.nextInt();
            switch (choice) {
                case 1:
                    L.clear();
                    C.clear();
                    print("Enter Minimum Support Count: ");
                    minCount = reader.nextInt();
                    startAprioriAlgorithm();
                    println("Doing great\n");
                    break;

                case 2:
                    println("All Transactions so Far..");
                    printAllTransactions();
                    break;

                case 3:
                    print("Enter index: ");
                    printMap(C.get(reader.nextInt()));
                    break;

                case 4:
                    print("Enter index: ");
                    printMap(L.get(reader.nextInt()));
                    break;

                default:
                    println("Good bye, Folks!\n");
                    isRunning = false;
                    break;
            }
        }
    }

    private static void startAprioriAlgorithm() {
        // L[0] and C[0] is dummy
        L.add(new TreeMap<>());
        C.add(new TreeMap<>());

        // find L1 = frequent 1-item-data-sets
        L.add(findL1());
        printMap(L.get(L.size() - 1));

        for (int k = 2; !L.get(k - 1).isEmpty(); ++k) {
            C.add(AprioriGen(L.get(k - 1)));
            printMap(C.get(C.size() - 1));
            TreeMap<String, Integer> l = new TreeMap<>();
            for (Map.Entry<String, Integer> c : C.get(C.size() - 1).entrySet()) {
                if (c.getValue() >= minCount) {
                    l.put(c.getKey(), c.getValue());
                }
            }
            L.add(l);
            printMap(L.get(L.size() - 1));
        }

        println("Frequents Item Sets are as Below -->");
        printMap(L.get(L.size() - 2));
    }

    private static TreeMap<String, Integer> AprioriGen(TreeMap<String, Integer> l) {
        TreeMap<String, Integer> c = new TreeMap<>();

        // since the l1 and l2 will be of form 'ABC' 'DCE'
        for (Map.Entry<String, Integer> l1 : l.entrySet()) {
            for (Map.Entry<String, Integer> l2 : l.entrySet()) {
                println(l1.getKey() + "-" + l2.getKey());
                if (checkJoinCondition(l1, l2)) {
                    String l1Jl2 = getl1Jl2(l1, l2);
                    if (hasInfrequentSubsets(l1Jl2, l)) {
                        // Don't add l1Jl2 (remove them)
                    } else {
                        // compute count of l1Jl2 and add to TreeMap c in the form of {'ABC' : 3}
                        for (TreeSet<String> transaction : transactions) {
                            StringBuilder transactionStringBuilder = new StringBuilder();
                            for (String item : transaction) transactionStringBuilder.append(item);
                            String transactionString = transactionStringBuilder.toString();

                            if (itemsPresentInTransaction(l1Jl2, transactionString)) {
                                if (c.keySet().contains(l1Jl2)) {
                                    c.replace(l1Jl2, c.get(l1Jl2) + 1);
                                } else {
                                    c.put(l1Jl2, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
        return c;
    }

    private static boolean itemsPresentInTransaction(String items, String transactionString) {
        String[] itemsList = items.split("");
        String[] transactionItemsList = transactionString.split("");

        return Arrays.asList(transactionItemsList).containsAll(Arrays.asList(itemsList));
    }

    private static boolean hasInfrequentSubsets(String l1Jl2, TreeMap<String, Integer> l) {
        // get all combinations of length L.indexOf(l) in l1Jl2
        ArrayList<String> allSubsets = new ArrayList<>();
        for (int i = 0; i < l1Jl2.length(); ++i) {
            if (i + L.indexOf(l) <= l1Jl2.length()) {
                String subString = l1Jl2.substring(i, i + L.indexOf(l));
                allSubsets.add(subString);
            }
        }

        // compare each subset is present in l or not
        for (String subSet : allSubsets) {
            if (!l.keySet().contains(subSet)) {
                return true;
            }
        }
        return false;
    }

    private static String getl1Jl2(Map.Entry<String, Integer> l1, Map.Entry<String, Integer> l2) {
        TreeSet<String> set = new TreeSet<>();
        // since the l1 and l2 will be of form 'ABC' 'DCE'
        set.addAll(Arrays.asList(l1.getKey().split("")));
        set.addAll(Arrays.asList(l2.getKey().split("")));
        StringBuilder sb = new StringBuilder();
        for (String item : set) sb.append(item);
        return sb.toString();
    }

    private static boolean checkJoinCondition(Map.Entry<String, Integer> l1, Map.Entry<String, Integer> l2) {
        String[] l1Entires = l1.getKey().split("");
        String[] l2Entires = l2.getKey().split("");

        int n = l1Entires.length;
        for (int i = 0; i <= n-2; ++i) {
            if(!l1Entires[i].equals(l2Entires[i])) {
                return false;
            }
        }
        return l1Entires[n - 1].compareTo(l2Entires[n - 1]) < 0;
    }

    private static TreeMap<String, Integer> findL1() {
        TreeMap<String, Integer> C1 = new TreeMap<>();
        for (TreeSet<String> transaction : transactions) {
            for (String item : transaction) {
                if (!C1.containsKey(item)) {
                    C1.put(item, 1);
                } else {
                    C1.put(item, C1.get(item) + 1);
                }
            }
        }
        C.add(C1);
        // Take min support into consideration and remove others
        TreeMap<String, Integer> L1 = new TreeMap<>();
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

    private static void printMap(TreeMap<String, Integer> treeMap) {
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
