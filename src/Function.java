public class Function extends Type {

    private final Type returnType;
    private final FieldList paramList;

    public Function(Type t, FieldList f) {
        super(Kind.FUNCTION);
        this.returnType = t;
        this.paramList = f;
    }

    public Type getReturnType() {
        return this.returnType;
    }

    public FieldList getParamList() {
        return this.paramList;
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        Function curT = (Function) t;
        if (!this.returnType.isEquivalentType(curT.getReturnType())) return false;
        if (this.paramList == null) return curT.getParamList() == null;
        return this.paramList.equals(curT.getParamList());
    }
}
