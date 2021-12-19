import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmmSemanticVisitor extends AbstractParseTreeVisitor<ParseInfo> implements CmmParserVisitor<ParseInfo> {

    private static final ParseInfo nullInfo = ParseInfo.getBlankInfo();
    private static final ParseInfo errorInfo = ParseInfo.getErrorInfo();

    private final SymbolTable st;
    // 只用于调用参数传递
    private final ParseTreeProperty<ParseInfo> values;

    public CmmSemanticVisitor() {
        this.st = new SymbolTable();
        this.values = new ParseTreeProperty<>();
    }

    private void putInfo(ParserRuleContext ctx, ParseInfo info) {
        this.values.put(ctx, info);
    }

    private ParseInfo getCallParam(ParserRuleContext ctx) {
        return this.values.get(ctx.getParent());
    }

    private void passCallParam(ParserRuleContext ctx) {
        ParseInfo i = this.getCallParam(ctx);
        this.putInfo(ctx, i);
    }

    private void notifyError(ErrorType et, int line) {
        String s = "Error type " +
                et.getVal() +
                " at Line " +
                line +
                ": " +
                et.getMsg();
        System.err.println(s);
    }

    private long parseInteger(String si) {
        long res;
        if (si.startsWith("0x") || si.startsWith("0X")) {
            res = new BigInteger(si.substring(2), 16).longValue();
        } else if (si.startsWith("0") && si.length() > 1) {
            res = new BigInteger(si.substring(1), 8).longValue();
        } else {
            res = new BigInteger(si).longValue();
        }
        return res;
    }

    private ParseInfo concatInfoFields(List<ParseInfo> children) {
        ParseInfo res = new ParseInfo();
        for (ParseInfo cur : children) {
            if (cur.isError()) continue;
            if (cur.getF() == null) continue;
            if (res.getF() == null) {
                res.setF(cur.getF());
                continue;
            }
            res.getF().add(cur.getF());
        }
        return res;
    }

    @Override
    public ParseInfo visitProgram(CmmParser.ProgramContext ctx) {
        this.visitChildren(ctx);
        // 该节点成功与失败与否对其他 parse 没有影响, 故直接返回 nullInfo
        return nullInfo;
    }

    @Override
    public ParseInfo visitExtDefVar(CmmParser.ExtDefVarContext ctx) {
        // extDef: specifier extDecList? SEMI
        // 不确定是哪个分支的时候直接用 visit
        ParseInfo i = this.visit(ctx.specifier());
        this.putInfo(ctx, i);
        if (ctx.extDecList() != null) this.visit(ctx.extDecList());
        return nullInfo;
    }

    @Override
    public ParseInfo visitExtDefFun(CmmParser.ExtDefFunContext ctx) {
        // extDef: specifier funDec compSt
        ParseInfo si = this.visit(ctx.specifier());
        this.putInfo(ctx, si);
        ParseInfo fi = this.visit(ctx.funDec());
        // function 重名, 直接跳过整个函数体
        if (!fi.isError()) this.visit(ctx.compSt());
        return nullInfo;
    }

    @Override
    public ParseInfo visitExtDecList(CmmParser.ExtDecListContext ctx) {
        // 传递 specifier
        this.passCallParam(ctx);
        this.visitChildren(ctx);
        return nullInfo;
    }

    @Override
    public ParseInfo visitSpecifierType(CmmParser.SpecifierTypeContext ctx) {
        TerminalNode node = ctx.TYPE();
        Type type;
        if (node.getText().equals("int")) type = new IntT();
        else type = new FloatT();
        return new ParseInfo(null, type);
    }

    @Override
    public ParseInfo visitSpecifierStruct(CmmParser.SpecifierStructContext ctx) {
        // specifier: structSpecifier
        return this.visit(ctx.structSpecifier());
    }

    @Override
    public ParseInfo visitStructWithBody(CmmParser.StructWithBodyContext ctx) {
        // structSpecifier: STRUCT optTag LC defList RC
        String name = this.visit(ctx.optTag()).getS();
        ParseInfo i = new ParseInfo();
        StructureT s;
        // 重名 struct, 忽略 defList 结构体
        if (this.st.contains(name)) {
            this.notifyError(ErrorType.DuplicatedStructName, ctx.optTag().getStart().getLine());
            // 错误恢复, 构造空结构
            s = new StructureT(name, null);
            i.setT(s);
            i.setError(true);
            return i;
        }
        // 通知子定义, 当前在 struct scope 内
        i.setStructScope(true);
        this.putInfo(ctx, i);
        ParseInfo di = this.visit(ctx.defList());
        i.setStructScope(false);
        s = new StructureT(name, di.getF());
        if (name != null) {
            Symbol symbol = new Symbol(name, s);
            this.st.put(symbol);
        }
        i.setT(s);
        return i;
    }

    @Override
    public ParseInfo visitStructWithoutBody(CmmParser.StructWithoutBodyContext ctx) {
        // structSpecifier: STRUCT tag
        String name = this.visit(ctx.tag()).getS();
        ParseInfo i = new ParseInfo();
        // 未定义结构体
        if (!this.st.contains(name)) {
            this.notifyError(ErrorType.UndefinedStruct, ctx.tag().getStart().getLine());
            // 错误恢复, 假设有这个 struct
            StructureT s = new StructureT(name, null);
            i.setT(s);
            i.setError(true);
            return i;
        }
        i.setT(this.st.get(name).getType());
        return i;
    }

    @Override
    public ParseInfo visitOptTag(CmmParser.OptTagContext ctx) {
        if (ctx.ID() == null) return nullInfo;
        ParseInfo i = new ParseInfo();
        i.setS(ctx.ID().getText());
        return i;
    }

    @Override
    public ParseInfo visitTag(CmmParser.TagContext ctx) {
        ParseInfo i = new ParseInfo();
        i.setS(ctx.ID().getText());
        return i;
    }

    @Override
    public ParseInfo visitVarDec(CmmParser.VarDecContext ctx) {
        // varDec: ID (LB (INT|FLOAT|ID) RB)*
        // extDecList: varDec (COMMA varDec)*
        // dec: varDec (ASSIGNOP exp)*
        // paramDec: specifier varDec
        // 需要注意当前 scope 和 type
        ParseInfo fi = this.getCallParam(ctx);
        String name = ctx.ID(0).getText();
        Type type = fi.getT();
        ArrayT base = null;
        // ArrayT 类型
        for (int i = ctx.getChildCount() - 1; i > 0; --i) {
            TerminalNode cur = (TerminalNode) ctx.getChild(i);
            int size = 0;
            int t = cur.getSymbol().getType();
            if (t == CmmParser.LB || t == CmmParser.RB) continue;
            if (t == CmmParser.FLOAT || t == CmmParser.ID) {
                this.notifyError(ErrorType.IllegalArrayIndex, cur.getSymbol().getLine());
            } else size = (int) this.parseInteger(cur.getText()); // CmmParser.INT
            if (base == null) base = new ArrayT(type, size);
            else base = new ArrayT(base, size);
        }
        Symbol s;
        FieldList f;
        ParseInfo i = new ParseInfo();
        s = new Symbol(name, ((base == null) ? type : base));
        f = new FieldList(name, ((base == null) ? type : base));
        if (this.st.contains(name)) {
            ErrorType et = ((fi.isStructScope()) ? ErrorType.IllegalStruct : ErrorType.RedefinedVar);
            this.notifyError(et, ctx.ID(0).getSymbol().getLine());
            // 出错返回空 info
            f.setNull();
            i.setError(true);
        } else this.st.put(s); // 放入符号表
        i.setF(f);
        return i;
    }

    @Override
    public ParseInfo visitFunDec(CmmParser.FunDecContext ctx) {
        // funDec: ID LP varList? RP
        // extDef: specifier funDec compSt SEMI
        String name = ctx.ID().getText();
        if (this.st.contains(name)) {
            this.notifyError(ErrorType.RedefinedFun, ctx.ID().getSymbol().getLine());
            return errorInfo;
        }
        ParseInfo fi = this.getCallParam(ctx);
        Type returnType = fi.getT();
        FieldList params = null;
        if (ctx.varList() != null) params = this.visit(ctx.varList()).getF();
        FunctionT f = new FunctionT(returnType, params);
        Symbol s = new Symbol(name, f);
        this.st.put(s);
        return nullInfo;
    }

    @Override
    public ParseInfo visitVarList(CmmParser.VarListContext ctx) {
        // varList: paramDec (COMMA paramDec)*
        // 需要返回字段链表 按照顺序拼接
        List<ParseInfo> list = ctx.paramDec()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return this.concatInfoFields(list);
    }

    @Override
    public ParseInfo visitParamDec(CmmParser.ParamDecContext ctx) {
        // paramDec: specifier varDec
        // 返回值原封不动传回去就行
        ParseInfo i = this.visit(ctx.specifier());
        this.putInfo(ctx, i);
        return this.visit(ctx.varDec());
    }

    @Override
    public ParseInfo visitCompSt(CmmParser.CompStContext ctx) {
        // compSt: LC defList stmtList RC
        // extDef: specifier funDec compSt
        // stmt: compSt
        // 主要注意传入的 specifier
        this.passCallParam(ctx);
        this.visit(ctx.defList());
        this.visit(ctx.stmtList());
        return nullInfo;
    }

    @Override
    public ParseInfo visitStmtList(CmmParser.StmtListContext ctx) {
        this.passCallParam(ctx);
        this.visitChildren(ctx);
        return nullInfo;
    }

    @Override
    public ParseInfo visitStmtChildScope(CmmParser.StmtChildScopeContext ctx) {
        this.passCallParam(ctx);
        this.visit(ctx.compSt());
        return nullInfo;
    }

    @Override
    public ParseInfo visitStmtNormal(CmmParser.StmtNormalContext ctx) {
        // stmt: RETURN? exp SEMI
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return errorInfo;
        // 检测 return type
        Type returnType = fi.getT();
        if (ctx.RETURN() != null) {
            Type extType = i.getT();
            if (!returnType.isEquivalentType(extType)) {
                this.notifyError(ErrorType.TypeMismatchReturn, ctx.exp().getStart().getLine());
                return errorInfo;
            }
        }
        return nullInfo;
    }

    @Override
    public ParseInfo visitStmtIf(CmmParser.StmtIfContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        Type returnType = fi.getT();
        ParseInfo i = this.visit(ctx.exp());
        if (!i.isError()) {
            Type extType = i.getT();
            if (!returnType.isEquivalentType(extType)) {
                this.notifyError(ErrorType.TypeMismatchReturn, ctx.exp().getStart().getLine());
            }
        }
        this.putInfo(ctx, fi);
        for (ParserRuleContext cur : ctx.stmt()) this.visit(cur);
        return nullInfo;
    }

    @Override
    public ParseInfo visitStmtWhile(CmmParser.StmtWhileContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        Type returnType = fi.getT();
        ParseInfo i = this.visit(ctx.exp());
        if (!i.isError()) {
            Type extType = i.getT();
            if (!returnType.isEquivalentType(extType)) {
                this.notifyError(ErrorType.TypeMismatchReturn, ctx.exp().getStart().getLine());
            }
        }
        this.putInfo(ctx, fi);
        this.visit(ctx.stmt());
        return nullInfo;
    }

    @Override
    public ParseInfo visitDefList(CmmParser.DefListContext ctx) {
        // defList: def*
        // 返回字段链表 按照顺序拼接
        this.passCallParam(ctx);
        List<ParseInfo> list = ctx.def()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return this.concatInfoFields(list);
    }

    @Override
    public ParseInfo visitDef(CmmParser.DefContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.specifier());
        i.setStructScope(fi.isStructScope());
        this.putInfo(ctx, i);
        return this.visit(ctx.decList());
    }

    @Override
    public ParseInfo visitDecList(CmmParser.DecListContext ctx) {
        // decList: dec (COMMA dec)*
        // 返回字段链表 按照顺序拼接
        this.passCallParam(ctx);
        List<ParseInfo> list = ctx.dec()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return this.concatInfoFields(list);
    }

    @Override
    public ParseInfo visitDec(CmmParser.DecContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        this.passCallParam(ctx);
        ParseInfo vi = this.visit(ctx.varDec());
        // 检查赋值语句
        // 虽然有 (ASSIGNOP exp)*, 但是最多只会扩展出一次
        if (ctx.ASSIGNOP().equals(Collections.emptyList())) return vi;
        // structure 内不允许赋值 error type 15
        if (fi.isStructScope()) {
            this.notifyError(ErrorType.IllegalStruct, ctx.getStart().getLine());
            return errorInfo;
        }
        ParseInfo ei = this.visit(ctx.exp(0));
        // 底层错则直接忽略
        if (ei.isError()) return vi;
        Type expType = ei.getT();
        Type varType = vi.getT();
        // 赋值两边类型必须相等
        if (!expType.isEquivalentType(varType)) {
            this.notifyError(ErrorType.TypeMismatchAssign, ctx.ASSIGNOP(0).getSymbol().getLine());
            return errorInfo;
        }
        return vi;
    }

    @Override
    public ParseInfo visitExpAssign(CmmParser.ExpAssignContext ctx) {
        // exp: exp ASSIGNOP exp
        ParseInfo leftInfo = this.visit(ctx.exp(0));
        ParseInfo rightInfo = this.visit(ctx.exp(1));
        ParseInfo res = new ParseInfo();
        // 底层错直接忽略
        if (leftInfo.isError() || rightInfo.isError()) return errorInfo;
        // 检查左值 只有不是左值才会检测类型
        if (leftInfo.getT().isRightVal()) {
            this.notifyError(ErrorType.RValAssign, ctx.exp(0).getStart().getLine());
            return errorInfo;
        }
        if (!leftInfo.getT().isEquivalentType(rightInfo.getT())) {
            this.notifyError(ErrorType.TypeMismatchAssign, ctx.ASSIGNOP().getSymbol().getLine());
            return errorInfo;
        }
        res.setT(leftInfo.getT());
        return res;
    }

    @Override
    public ParseInfo visitExpStructRef(CmmParser.ExpStructRefContext ctx) {
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return errorInfo;
        // 判断是不是 structure
        if (!StructureT.isStructure(i.getT())) {
            this.notifyError(ErrorType.IllegalStructRef, ctx.exp().getStart().getLine());
            return errorInfo;
        }
        // 寻找成员
        StructureT s = (StructureT) i.getT();
        // 没有这个成员
        if (!s.hasMember(ctx.ID().getText())) {
            this.notifyError(ErrorType.UndefinedStructField, ctx.ID().getSymbol().getLine());
            return errorInfo;
        }
        FieldList f = s.getMember(ctx.ID().getText());
        ParseInfo res = new ParseInfo();
        res.setT(f.getType());
        return res;
    }

    @Override
    public ParseInfo visitExpFunCall(CmmParser.ExpFunCallContext ctx) {
        // 是否有这个 function
        String name = ctx.ID().getText();
        if (!this.st.contains(name)) {
            this.notifyError(ErrorType.RedefinedFun, ctx.ID().getSymbol().getLine());
            return errorInfo;
        }
        Type t = this.st.get(name).getType();
        if (!FunctionT.isFunction(t)) {
            this.notifyError(ErrorType.IllegalFunCall, ctx.ID().getSymbol().getLine());
            return errorInfo;
        }
        FunctionT ft = (FunctionT) t;
        // 判断参数是否匹配
        FieldList params = null;
        if (ctx.args() != null) {
            ParseInfo i = this.visit(ctx.args());
            if (i.isError()) return errorInfo;
            params = i.getF();
        }
        // 不匹配
        if (!ft.isParamsMatched(params)) {
            this.notifyError(ErrorType.FunArgMismatch, ctx.args().getStart().getLine());
            return errorInfo;
        }
        ParseInfo res = new ParseInfo();
        res.setT(ft.getReturnType());
        return res;
    }

    @Override
    public ParseInfo visitExpParenthesis(CmmParser.ExpParenthesisContext ctx) {
        return this.visit(ctx.exp());
    }

    @Override
    public ParseInfo visitExpFloat(CmmParser.ExpFloatContext ctx) {
        ParseInfo i = new ParseInfo();
        FloatT t = new FloatT();
        t.setRightVal(true);
        i.setT(t);
        return i;
    }

    @Override
    public ParseInfo visitExpArrayRef(CmmParser.ExpArrayRefContext ctx) {
        ParseInfo i = this.visit(ctx.exp(0));
        if (i.isError()) return errorInfo;
        Type t = i.getT();
        // 不是 array
        if (!ArrayT.isArray(t)) {
            this.notifyError(ErrorType.IllegalArrayRef, ctx.exp(0).getStart().getLine());
            return errorInfo;
        }
        ParseInfo ei = this.visit(ctx.exp(1));
        if (ei.isError()) return errorInfo;
        // index 不是 int
        if (!IntT.isInt(ei.getT())) {
            this.notifyError(ErrorType.IllegalArrayIndex, ctx.exp(1).getStart().getLine());
            return errorInfo;
        }
        ArrayT a = (ArrayT) t;
        ParseInfo res = new ParseInfo();
        res.setT(a.getType());
        return res;
    }

    @Override
    public ParseInfo visitExpId(CmmParser.ExpIdContext ctx) {
        String name = ctx.ID().getText();
        if (!this.st.contains(name)) {
            this.notifyError(ErrorType.UndefinedVar, ctx.ID().getSymbol().getLine());
            return errorInfo;
        }
        Symbol s = this.st.get(name);
        ParseInfo res = new ParseInfo();
        res.setT(s.getType());
        return res;
    }

    @Override
    public ParseInfo visitExpBinary(CmmParser.ExpBinaryContext ctx) {
        // 仅有 int 型和 float 型变量才能参与算术运算
        // 且相互运算的类型必须相同
        ParseInfo leftInfo = this.visit(ctx.exp(0));
        ParseInfo rightInfo = this.visit(ctx.exp(1));
        // 底层错直接忽略
        if (leftInfo.isError() || rightInfo.isError()) return errorInfo;
        ParseInfo res = new ParseInfo();
        boolean callable = true;
        if (!IntT.isInt(leftInfo.getT()) && !FloatT.isFloat(leftInfo.getT())) {
            callable = false;
            notifyError(ErrorType.TypeMismatchOperand, ctx.exp(0).getStart().getLine());
        }
        if (!IntT.isInt(rightInfo.getT()) && !FloatT.isFloat(rightInfo.getT())) {
            callable = false;
            notifyError(ErrorType.TypeMismatchOperand, ctx.exp(1).getStart().getLine());
        }
        if (!callable) return errorInfo;
        if (!leftInfo.getT().isEquivalentType(rightInfo.getT())) {
            this.notifyError(ErrorType.TypeMismatchOperand, ctx.getStart().getLine());
        }
        res.setT(leftInfo.getT());
        return res;
    }

    @Override
    public ParseInfo visitExpUnary(CmmParser.ExpUnaryContext ctx) {
        // 仅有 int 型和 float 型变量才能参与算术运算
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return errorInfo;
        if (!IntT.isInt(i.getT()) && !FloatT.isFloat(i.getT())) {
            notifyError(ErrorType.TypeMismatchOperand, ctx.exp().getStart().getLine());
            return errorInfo;
        }
        return i;
    }

    @Override
    public ParseInfo visitExpInt(CmmParser.ExpIntContext ctx) {
        ParseInfo i = new ParseInfo();
        IntT t = new IntT();
        t.setRightVal(true);
        i.setT(t);
        return i;
    }

    @Override
    public ParseInfo visitArgs(CmmParser.ArgsContext ctx) {
        // exp (COMMA exp)*
        // 返回一个 fieldList 作为 params
        List<ParseInfo> list = ctx.exp()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        for (ParseInfo i : list) {
            if (i.isError()) return errorInfo;
        }
        return this.concatInfoFields(list);
    }

}
