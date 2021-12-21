public class ErrorT extends Type {

    public ErrorT() {
        super(Kind.ERROR);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.ERROR;
    }

    public static boolean isError(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.ERROR;
    }

}
