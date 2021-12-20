public class FunctionT extends Type {

    private final Type returnType;
    private FieldList paramList;

    public FunctionT(Type t) {
        super(Kind.FUNCTION);
        this.returnType = t;
        this.paramList = null;
    }

    public FunctionT(Type t, FieldList f) {
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

    public void setParamList(FieldList paramList) {
        this.paramList = paramList;
    }

    public boolean isParamsMatched(FieldList p) {
        if (this.paramList == null) return p == null;
        return this.paramList.equals(p);
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        FunctionT curT = (FunctionT) t;
        if (!this.returnType.isEquivalentType(curT.getReturnType())) return false;
        if (this.paramList == null) return curT.getParamList() == null;
        return this.paramList.equals(curT.getParamList());
    }

    public static boolean isFunction(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.FUNCTION;
    }

}
