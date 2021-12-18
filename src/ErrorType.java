public enum ErrorType {

    // 变量在使用时未经定义
    UndefinedVar(1, "Undefined variable"),
    // 函数在调用时未经定义
    UndefinedFun(2, "Undefined function"),
    // 变量出现重复定义 或变量与前面定义过的结构体名字重复
    RedefinedVar(3, "Redefined variable"),
    // 函数出现重复定义 ( 即同样的函数名出现了不止一次定义 )
    RedefinedFun(4, "Redefined function"),
    // 赋值号两边的表达式类型不匹配
    // 注意 struct 类型等价机制是 **结构等价**, 字段必须类型和顺序相同
    // 注意数组只检查基类型和维数
    TypeMismatchAssign(5, "Type mismatched for assignment"),
    // 赋值号左边出现一个只有右值的表达式
    RValAssign(6, "The left-hand side of an assignment must be a variable"),
    // 操作数类型不匹配或操作数类型与操作符不匹配
    // 例如整型变量与数组变量相加减 或数组 ( 或结构体 ) 变量与数组 ( 或结构体 ) 变量相加减
    TypeMismatchOperand(7, "Type mismatched for operands"),
    // return 语句的返回类型与函数定义的返回类型不匹配
    TypeMismatchReturn(8, "Type mismatched for return"),
    // 函数调用时实参与形参的数目或类型不匹配
    FunArgMismatch(9, "Function is not applicable for arguments"),
    // 对非数组型变量使用 "[...]" ( 数组访问 ) 操作符
    IllegalArrayRef(10, "Not an array"),
    // 对普通变量使用 "(...)" 或 "()" ( 函数调用 ) 操作符
    IllegalFunCall(11, "Not a function"),
    // 数组访问操作符 "[...]" 中出现非整数
    // 例如 a[1.5]
    IllegalArrayIndex(12, "Index must be an integer"),
    // 对非结构体型变量使用 "." 操作符
    IllegalStructRef(13, "Illegal use of \".\""),
    // 访问结构体中未定义过的域
    UndefinedStructField(14, "Non-existent field"),
    // 结构体中域名重复定义 ( 指同一结构体中 ) 或在定义时对域进行初始化
    // 例如 struct A { int a = 0; }
    IllegalStruct(15, "Refined field or illegally initialized field"),
    // 结构体的名字与前面定义过的结构体或变量的名字重复
    DuplicatedStructName(16, "Duplicated structure name"),
    // 直接使用未定义过的结构体来定义变量
    UndefinedStruct(17, "Undefined structure");

    private final int val;
    private final String msg;

    ErrorType(int val, String msg) {
        this.val = val;
        this.msg = msg;
    }

    public int getVal() {
        return this.val;
    }

    public String getMsg() {
        return this.msg;
    }

}
