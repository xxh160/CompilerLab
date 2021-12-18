public class Float extends Type {

    public Float() {
        super(Kind.FLOAT);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        return this.selfKind == t.getSelfKind();
    }
}
