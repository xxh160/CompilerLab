public class Int extends Type {

    public Int() {
        super(Kind.INT);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        return this.selfKind == t.getSelfKind();
    }
}
