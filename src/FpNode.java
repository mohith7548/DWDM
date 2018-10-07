import java.util.ArrayList;

public class FpNode {
    private String name;
    private int count;
    private ArrayList<FpNode> children;

    public FpNode(String name, int count, ArrayList<FpNode> children) {
        this.name = name;
        this.count = count;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<FpNode> getChildren() {
        return children;
    }
}
