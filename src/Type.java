public abstract class Type {

    protected final Kind selfKind;

    public Type(Kind selfK) {
        this.selfKind = selfK;
    }

    public Kind getSelfKind() {
        return this.selfKind;
    }

    public abstract boolean isEquivalentType(Type t);

}
