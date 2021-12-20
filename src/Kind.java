public enum Kind {
    INT,
    FLOAT,
    ARRAY,
    STRUCTURE,
    FUNCTION;

    public static Type getType(Kind k) {
        Type res;
        switch (k) {
            case INT:
                res = new IntT();
                break;
            case FLOAT:
                res = new FloatT();
                break;
            case ARRAY:
                res = new ArrayT();
                break;
            case FUNCTION:
                res = new FunctionT();
                break;
            default:
                res = new StructureT();
        }
        return res;
    }

}
