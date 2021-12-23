public class StructureT extends Type {

    private String name;
    private FieldList memberList;

    // 匿名 struct 的 name 可能为空
    // 匿名 struct 不在符号表中
    public StructureT(String name, FieldList f) {
        super(Kind.STRUCTURE);
        this.name = name;
        this.memberList = f;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldList getMemberList() {
        return this.memberList;
    }

    public void setMemberList(FieldList memberList) {
        this.memberList = memberList;
    }

    public boolean hasMember(String name) {
        if (name == null) return false;
        FieldList cur = this.memberList;
        if (cur == null) return false;
        while (cur.hasNext()) {
            if (cur.getName().equals(name)) return true;
            cur = cur.getNext();
        }
        return cur.getName().equals(name);
    }

    public FieldList getMember(String name) {
        if (name == null) return null;
        FieldList cur = this.memberList;
        if (cur == null) return null;
        while (cur.hasNext()) {
            if (cur.getName().equals(name)) return cur;
            cur = cur.getNext();
        }
        if (cur.getName().equals(name)) return cur;
        return null;
    }

    @Override
    public boolean isEquivalentType(Type t) {
        if (t == this) return true;
        if (t == null) return false;
        if (this.selfKind != t.getSelfKind()) return false;
        // 保证顺序和类型相同: 结构等价
        // 不看名字
        FieldList curS = this.memberList;
        FieldList curT = ((StructureT) t).getMemberList();
        if (curS == null) return curT == null;
        return curS.equals(curT);
    }

    public static boolean isStructure(Type t) {
        if (t == null) return false;
        return t.getSelfKind() == Kind.STRUCTURE;
    }

    // 判断是不是 struct 类型的变量
    // 和 struct 类型本身分开来
    public boolean isStructureVar(String s) {
        String stName = this.name;
        // struct 是匿名 struct, 不存在 struct 类型本身的 symbol
        if (stName == null) return true;
        // struct 类型名和 s 相同的话, 说明是 struct 类型本身
        return !stName.equals(s);
    }

}
