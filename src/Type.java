public abstract class Type {

    protected final Kind selfKind;
    protected boolean isRightVal;

    public Type(Kind selfK) {
        this.selfKind = selfK;
        this.isRightVal = false;
    }

    public Kind getSelfKind() {
        return this.selfKind;
    }

    public boolean isRightVal() {
        return this.isRightVal;
    }

    public void setRightVal(boolean rightVal) {
        isRightVal = rightVal;
    }

    public abstract boolean isEquivalentType(Type t);

}
