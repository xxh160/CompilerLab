public class Array extends Type {

    private final Type type;
    private final int size;

    public Array(Type type, int size) {
        super(Kind.ARRAY);
        this.type = type;
        this.size = size;
    }

    public Type getType() {
        return this.type;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        return this.type.isEquivalentType(((Array) t).getType());
    }
}
