public class StructureT extends Type {

    private String name;
    private FieldList memberList;

    public StructureT() {
        super(Kind.STRUCTURE);
        this.name = null;
        this.memberList = null;
    }

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
        while (cur.hasNext()) {
            if (cur.getName().equals(name)) return true;
            cur = cur.getNext();
        }
        return cur.getName().equals(name);
    }
    
    public FieldList getMember(String name) {
        if (name == null) return null;
        FieldList cur = this.memberList;
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

}
