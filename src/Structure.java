public class Structure extends Type {

    private final String name;
    private final FieldList memberList;

    // 匿名 struct 的 name 可能为空
    // 匿名 struct 不在符号表中
    public Structure(String name, FieldList f) {
        super(Kind.STRUCTURE);
        this.name = name;
        this.memberList = f;
    }

    public String getName() {
        return this.name;
    }

    public FieldList getMemberList() {
        return this.memberList;
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        // 保证顺序和类型相同: 结构等价
        // 不看名字
        FieldList curS = this.memberList;
        FieldList curT = ((Structure) t).getMemberList();
        if (curS == null) return curT == null;
        return curS.equals(curT);
    }
}
