public class FloatT extends Type {

    public FloatT() {
        super(Kind.FLOAT);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        return this.selfKind == t.getSelfKind();
    }

    public static boolean isFloat(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.FLOAT;
    }

}
