import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FpDriver {
    private static Scanner reader = new Scanner(System.in);
    private static FPGrowth fp;
    private static ArrayList<TreeSet<String>> transactions = new ArrayList<>();

    public static void main(String[] kune) throws FileNotFoundException {
        println("Enter the path of transactions file: ");
//        String filePath = reader.nextLine();
        Scanner scan = new Scanner(new File("tr3.csv"));
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
                    print("Enter Minimum Support Count: ");
                    int minCount = reader.nextInt();
                    fp = new FPGrowth(transactions, minCount);
                    fp.startFPGrowth();
                    println("Doing great\n");
                    break;

                case 2:
                    println("All Transactions so Far..");
                    printAllTransactions();
                    break;

                case 3:
                    fp.DisplayL1();
                    break;

                case 4:
                    fp.ListAllLinks();
                    break;

                case 5:
//                    ListAllPatternBases();
                    break;

                default:
                    println("Good bye, Folks!\n");
                    isRunning = false;
                    break;
            }
        }
    }

    private static void printAllTransactions() {
        for (TreeSet<String> transaction : transactions) {
            for (String item : transaction) {
                print(item);
            }
            println("");
        }
    }

    private static void print(String s) {
        System.out.print(s);
    }

    private static void println(String s) {
        System.out.println(s);
    }
}
