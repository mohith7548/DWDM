public class Header {
    private String item;
    private int count;
    private FpNode linkNode;

    public Header(String item, int count, FpNode linkNode) {
        this.item = item;
        this.count = count;
        this.linkNode = linkNode;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FpNode getLinkNode() {
        return linkNode;
    }

    public void setLinkNode(FpNode linkNode) {
        this.linkNode = linkNode;
    }
}
