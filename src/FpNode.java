import java.util.ArrayList;

public class FpNode {
    private String name;
    private int count;
    private ArrayList<FpNode> children;
    private FpNode parentNode;
    private FpNode siblingNode;

    public FpNode(String name, int count, ArrayList<FpNode> children, FpNode parentNode, FpNode siblingNode) {
        this.name = name;
        this.count = count;
        this.children = children;
        this.parentNode = parentNode;
        this.siblingNode = siblingNode;
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

    public FpNode getSiblingNode() {
        return siblingNode;
    }

    public void setSiblingNode(FpNode siblingNode) {
        this.siblingNode = siblingNode;
    }

    public FpNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(FpNode parentNode) {
        this.parentNode = parentNode;
    }
}
