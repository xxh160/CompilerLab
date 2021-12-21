public class ParseInfo {

    private static ParseInfo blankInfo = null;
    private static ParseInfo errorInfo = null;

    public static ParseInfo getBlankInfo() {
        if (blankInfo == null) blankInfo = new ParseInfo();
        return blankInfo;
    }

    public static ParseInfo getErrorInfo() {
        if (errorInfo == null) {
            errorInfo = new ParseInfo();
            errorInfo.setT(new ErrorT());
        }
        return errorInfo;
    }

    // param fields
    private FieldList f;
    // type
    private Type t;
    // var name
    private String s;
    // is in structScope
    private boolean structScope;
    // right val
    private boolean isRightVal;

    public ParseInfo() {
        this.f = null;
        this.t = null;
        this.s = null;
        this.structScope = false;
    }

    public ParseInfo(FieldList f, Type t) {
        this.f = f;
        this.t = t;
        this.s = null;
    }

    public FieldList getF() {
        return f;
    }

    public void setF(FieldList f) {
        this.f = f;
    }

    public Type getT() {
        return t;
    }

    public void setT(Type t) {
        this.t = t;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isError() {
        return ErrorT.isError(this.t);
    }

    public boolean isStructScope() {
        return structScope;
    }

    public void setStructScope(boolean structScope) {
        this.structScope = structScope;
    }

    public boolean isRightVal() {
        return isRightVal;
    }

    public void setRightVal(boolean rightVal) {
        isRightVal = rightVal;
    }

}
