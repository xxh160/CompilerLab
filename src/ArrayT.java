public class ArrayT extends Type {

    private Type type;
    private int size;

    public ArrayT() {
        super(Kind.ARRAY);
        this.type = null;
        this.size = 0;
    }

    public ArrayT(Type type, int size) {
        super(Kind.ARRAY);
        this.type = type;
        this.size = size;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type t) {
        this.type = t;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        return this.type.isEquivalentType(((ArrayT) t).getType());
    }

    public static boolean isArray(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.ARRAY;
    }

}
