// 本来只是个链表节点的
// 这里却是代替了链表自己的功能了
public class FieldList {

    private String name;
    private Type type;
    private FieldList next;

    public FieldList(String name, Type type) {
        this.name = name;
        this.type = type;
        this.next = null;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    public FieldList getNext() {
        return this.next;
    }

    public void add(FieldList next) {
        FieldList cur = this.next;
        if (cur == null) {
            this.next = next;
            return;
        }
        while (cur.hasNext()) {
            cur = cur.getNext();
        }
        cur.next = next;
    }

    public boolean equals(FieldList f) {
        if (f == this) return true;
        if (f == null) return false;
        if (!this.type.isEquivalentType(f.getType())) return false;
        FieldList curS = next;
        FieldList curF = f.getNext();
        if (curS == null) return curF == null;
        return curS.equals(curF);
    }

    public void setNull() {
        this.name = null;
        this.type = null;
        this.next = null;
    }

}
