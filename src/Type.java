public abstract class Type {

    private final Kind selfKind;

    public Type(Kind selfK) {
        this.selfKind = selfK;
    }

    public Kind getSelfKind() {
        return this.selfKind;
    }
    
}
