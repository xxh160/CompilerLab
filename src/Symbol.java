public class Symbol {

    private final String name;
    private final Type type;
    // 散列冲突
    private Symbol next;

    public Symbol(String name, Type type) {
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

    public Symbol getNext() {
        return this.next;
    }

    public void add(Symbol next) {
        Symbol cur = this.next;
        if (cur == null) {
            this.next = next;
            return;
        }
        while (cur.hasNext()) {
            cur = cur.getNext();
        }
        cur.next = next;
    }

}
