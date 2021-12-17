public class Array extends Type {

    // int[2][3] -> type = int[2], size = 3
    // int[2] -> type = int, size = 2
    private Type type;
    private int size;

    public Array() {
        super(Kind.ARRAY);
    }

}
