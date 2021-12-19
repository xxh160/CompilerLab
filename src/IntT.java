public class IntT extends Type {

    public IntT() {
        super(Kind.INT);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        return this.selfKind == t.getSelfKind();
    }

    public static boolean isInt(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.INT;
    }

}
