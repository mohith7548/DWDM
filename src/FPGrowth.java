import java.util.*;

class FPGrowth {
    private Header[] table;

    private HashMap<String, Integer> Fmap;
    private ArrayList<String> F;
    private LinkedHashMap<ArrayList<String>, Integer> frequentItems;

    private ArrayList<TreeMap<String, Integer>> patternBase;
    private ArrayList<TreeSet<String>> transactions;
    private int supCount;
    private FpNode HEAD;

    private Comparator<String> valComparator = (t1, t2) -> {
        Integer i1 = Fmap.get(t1);
        Integer i2 = Fmap.get(t2);
        return -i1.compareTo(i2); // -ve because to get descending order
    };

    FPGrowth(ArrayList<TreeSet<String>> transactions, int minCount) {
        this.transactions = transactions;
        this.supCount = minCount;
        frequentItems = new LinkedHashMap<>();
        patternBase = new ArrayList<>();
        HEAD = new FpNode("NULL", 0, new ArrayList<>(), null, null);
    }

    void startFPGrowth() {
        // clear all variables

        // Find all 1-Freq item sets
        findL1();
        printMap(Fmap);

        // Sort F in (descending) order
        sortF();
        print("F = ");
        printArrayList(F);

        table = new Header[F.size()];
        constructHeader();

        println("");
        ConstructFpTree();
        println("Finished Construction of Fp-Tree");

        // test tree by printing a node's children and their count
        testTree();

        /// Compute the Association rules from the Tree

        ListAllLinks();

        // create Conditional Fp tree
        Fp_growth(HEAD, table, null, frequentItems, supCount);
//        printFrequentItems(frequentItems);

    }

    private void Fp_growth(FpNode tree, Header[] table, ArrayList<String> a,
                           LinkedHashMap<ArrayList<String>, Integer> frequentItems, int supCount) {

        TreeMap<Set<String>, Integer> cond_pat_base = new TreeMap<>();

        if (TreeHasSinglePath(tree)) {
            FpNode temp = tree;
            TreeMap<String, Integer> itemsMap = new TreeMap<>();
            // generate all combinations 'b' in path P
            while (temp.getChildren().size() != 0) {
                temp = temp.getChildren().get(0);
                itemsMap.put(temp.getName(), temp.getCount());
                println(temp.getName());
            }
            // for each pattern add it into ans list with count = min_sup count of nodes in b
            LinkedHashMap<ArrayList<String>, Integer> comb = findSubList(itemsMap);

            for (ArrayList<String> l : comb.keySet()) {
                int count = comb.get(l);
                if (l.size() == 0) {
                    if (a != null) {
                        l.addAll(a);
                        count = supCount;
                    }
                    if (l.size() != 0) {
                        frequentItems.put(l, count);
                        printArrayList(l);
                    }
                } else {
                    if (a != null) {
                        l.addAll(a);
                    }
                    frequentItems.put(l, count);
                    printArrayList(l);
                }

            }


        } else {
            println("Nice Job!");
        }
    }

    /*find sublists for a given list*/
    private LinkedHashMap<ArrayList<String>, Integer> findSubList(TreeMap<String, Integer> li1) {
        int n = li1.size();
        ArrayList<String> li = new ArrayList<>(li1.keySet());
        printArrayList(li);
        LinkedHashMap<ArrayList<String>, Integer> lst = new LinkedHashMap<>();

        ArrayList<String> m = new ArrayList<>();
        m.add("k");m.add("u");m.add("n");m.add("e");
        lst.put(m, 12);

        for (int i = 0; i < (1 << n); i++) {
            ArrayList<String> l = new ArrayList<>();
            int c = 0;
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    l.add(li.get(j));
                    c = li1.get(li.get(j));
                    printArrayList(l);
                    println(c + "");
                    println("");
                }
            }
            lst.put(l, c);
        }
        return lst;
    }

    private boolean TreeHasSinglePath(FpNode head) {
        if (head == null) {
            return false;
        }
        while (head.getChildren().size() != 0) {
            if (head.getChildren().size() > 1) {
                println("Hi");
                return false;
            }
            head = head.getChildren().get(0);
        }
        return true;
    }

    private void constructHeader() {
        for (int i = 0; i < table.length; ++i) {
            table[i] = new Header(F.get(i), Fmap.get(F.get(i)), null);
        }
    }

    private void ConstructFpTree() {
        // Construct tree

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


    private void InsertItem(ArrayList<String> t, FpNode T) {
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

    private void testTree() {
        for (FpNode node : HEAD.getChildren().get(0).getChildren()) {
            print(node.getName() + ":" + node.getCount() + " ");
        }
        println("");
    }

    /*print frequent item lists*/
    private void printFrequentItems(TreeMap<ArrayList<String>, Integer> freqitems) {
        for (ArrayList<String> ll : freqitems.keySet()) {
            System.out.println(ll + "\t:" + freqitems.get(ll));
        }
    }


    void ListAllLinks() {
        println("Links size is: " + table.length);
        for (Header h : table) {
            FpNode fpNode = h.getLinkNode();
            while (fpNode != null) {
                print(fpNode.getName() + "(" + fpNode.getCount() + ")" + "-->");
                fpNode = fpNode.getSiblingNode();
            }
            println("");
        }
    }

    private void updateLinks(FpNode newNode) {
        FpNode fpNode = table[F.indexOf(newNode.getName())].getLinkNode();
        if (fpNode == null) {
//            links[F.indexOf(newNode.getName())] = newNode;
            table[F.indexOf(newNode.getName())].setLinkNode(newNode);
        } else {
            // traverse
            while (fpNode.getSiblingNode() != null) {
                // go connecting nodes
                fpNode = fpNode.getSiblingNode();
            }
            fpNode.setSiblingNode(newNode);

        }
    }

    private void sortF() {
        ArrayList<String> k = new ArrayList<>(Fmap.keySet());
        // comparator is mae at first itself
        k.sort(valComparator);
        F = k;
    }

    private void findL1() {
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
            if (entry.getValue() >= supCount) {
                L1.put(entry.getKey(), entry.getValue());
            }
        }
        Fmap = L1;
    }

    void DisplayL1() {
        printMap(Fmap);
    }

    private void printArrayList(ArrayList<String> comb) {
        for (String a : comb) {
            print(a + " ");
        }
        println("");
    }

    private void printMap(TreeMap<String, Integer> treeMap) {
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        println("");
    }

    private void printMap(HashMap<String, Integer> treeMap) {
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        println("");
    }

    private void print(String s) {
        System.out.print(s);
    }

    private void println(String s) {
        System.out.println(s);
    }
}
