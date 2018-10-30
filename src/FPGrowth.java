import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

public class FPGrowth {
    private static Scanner reader = new Scanner(System.in);
    private static Header[] header;

    private static HashMap<String, Integer> Fmap = new HashMap<>();
    private static ArrayList<String> F = new ArrayList<>(); // Links to hold the items as a linked list
//    private static FpNode[] links;

    private static ArrayList<ArrayList<TreeMap<String, Integer>>> patternBase;
    private static ArrayList<TreeSet<String>> transactions = new ArrayList<>();
    private static int minCount;
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
            println("4. List all links");
            println("5. Show Conditional Pattern bases");
            println("Press any key to exit");
            int choice = reader.nextInt();
            switch (choice) {
                case 1:
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

                case 4:
                    ListAllLinks();
                    break;

                case 5:
                    ListAllPatternBases();
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
        print("F = ");
        printArrayList(F);

        // Fill links with null node initially
//        links = new FpNode[F.size()];
        header = new Header[F.size()];
        constructHeader();
        patternBase = new ArrayList<>(); // do null entries for the same size

        println("");
        ConstructFpTree();
        println("Finished Construction of Fp-Tree");

        // test tree by printing a node's children and their count
        testTree();

        /// Compute the Association rules from the Tree

        ListAllLinks();
        // Get pattern base
        generateConditionalPatternBase();
        ListAllPatternBases();

        // create Conditional Fp tree
        Fp_growth(HEAD, null);
    }

    private static void Fp_growth(FpNode head, Object o) {
        if(TreeHasSinglePath(head)) {
            // get path nodes
            FpNode temp = head;
            ArrayList<String> comb = new ArrayList<>();
            while(temp!=null) {
                comb.add(temp.getName());
                temp = temp.getChildren().get(0);
            }
        } else {
            for (int i = header.length - 1; i >= 0; --i) {
                FpNode fpNode = header[i].getLinkNode();
                TreeMap<String, Integer> b = new TreeMap<>();
                b.put(Union(fpNode.getName(), head.getName()), fpNode.getCount());
                // gen cond pattern base
                // construct
            }
        }
    }

    private static String Union(String name, String name1) {
        TreeSet<String> set = new TreeSet<>();
        if (!name.equals("NULL"))
            set.addAll(Arrays.asList(name.split("")));
        if (!name1.equals("NULL"))
            set.addAll(Arrays.asList(name1.split("")));
        StringBuilder sb = new StringBuilder();
        for(String item: set) {
            sb.append(item);
        }
        return sb.toString();
    }

    private static boolean TreeHasSinglePath(FpNode head) {
        while(head != null) {
            if(head.getChildren().size() > 1) {
                return false;
            }
            head = head.getChildren().get(0);
        }
        return true;
    }

    private static void constructHeader() {
        for(int i=0; i<header.length; ++i) {
            header[i] = new Header(F.get(i), Fmap.get(F.get(i)), null);
        }
    }

    private static void generateConditionalPatternBase() {
        for (int i = header.length - 1; i >= 0; --i) {
            FpNode fpNode = header[i].getLinkNode();
            ArrayList<TreeMap<String, Integer>> maps = new ArrayList<>();
            while (fpNode != null) {
                StringBuilder sb = new StringBuilder();
                TreeMap<String, Integer> treeMap = new TreeMap<>();
                int count = fpNode.getCount();
                FpNode fpNode2 = fpNode;
                while (!fpNode2.getName().equals("NULL")) {
                    sb.append(fpNode2.getName());
                    fpNode2 = fpNode2.getParentNode();
                }
                sb = sb.deleteCharAt(0);
                if (sb.compareTo(new StringBuilder()) != 0) {
                    sb.reverse();
//                    println(sb.toString() + "--" + "count: " + count);
                    treeMap.put(sb.toString(), count);
                    maps.add(treeMap);
                }
                fpNode = fpNode.getSiblingNode();
            }
            patternBase.add(maps);
        }
    }

    private static void ConstructFpTree() {
        // Construct tree
        HEAD = new FpNode("NULL", 0, new ArrayList<>(), null, null);

        for (TreeSet<String> transaction : transactions) {
            ArrayList<String> t = new ArrayList<>(transaction);

            // Remove non-frequent items (which are not present in F) from the transaction
            ArrayList<String> nonFreqItems = new ArrayList<>(t);
            nonFreqItems.removeAll(F);
            t.removeAll(nonFreqItems);

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
            FpNode newNode = new FpNode(t.get(0), 1, new ArrayList<>(), T, null);
            T.getChildren().add(newNode);
            t.remove(t.get(0));
            // update the links
            updateLinks(newNode);
            if (!t.isEmpty()) {
                InsertItem(t, newNode);
            }
        } else {
            FpNode newNode = new FpNode(t.get(0), 1, new ArrayList<>(), T, null);
            T.getChildren().add(newNode);
            t.remove(t.get(0));
            // update the links
            updateLinks(newNode);
            if (!t.isEmpty()) {
                InsertItem(t, newNode);
            }
        }
    }

    private static void testTree() {
        for (FpNode node : HEAD.getChildren().get(0).getChildren()) {
            print(node.getName() + ":" + node.getCount() + " ");
        }
        println("");
    }

    private static void ListAllPatternBases() {
        int index = F.size() -1;
        for (ArrayList<TreeMap<String, Integer>> itemPatternBases : patternBase) {
            for (TreeMap<String, Integer> itempb : itemPatternBases) {
                print(F.get(index) + "->");
                printMap(itempb);
            }
            --index;
        }
    }

    private static void ListAllLinks() {
        println("Links size is: " + header.length);
        for (Header h: header) {
            FpNode fpNode = h.getLinkNode();
            while (fpNode != null) {
                print(fpNode.getName() + "(" + fpNode.getCount() + ")" + "-->");
                fpNode = fpNode.getSiblingNode();
            }
            println("");
        }
    }

    private static void updateLinks(FpNode newNode) {
        FpNode fpNode = header[F.indexOf(newNode.getName())].getLinkNode();
        if (fpNode == null) {
//            links[F.indexOf(newNode.getName())] = newNode;
            header[F.indexOf(newNode.getName())].setLinkNode(newNode);
        } else {
            // traverse
            while (fpNode.getSiblingNode() != null) {
                // go connecting nodes
                fpNode = fpNode.getSiblingNode();
            }
            fpNode.setSiblingNode(newNode);

        }
    }

    private static void sortF() {
        ArrayList<String> k = new ArrayList<>(Fmap.keySet());
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

    private static void printMap(TreeMap<String, Integer> treeMap) {
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
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
