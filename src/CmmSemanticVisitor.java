import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmmSemanticVisitor extends AbstractParseTreeVisitor<ParseInfo> implements CmmParserVisitor<ParseInfo> {

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
        FieldList f = null;
        for (ParseInfo cur : children) {
            if (cur.isError()) continue;
            FieldList curF = cur.getF();
            if (cur.getF() == null) continue;
            if (f == null) {
                f = curF;
                continue;
            }
            f.add(curF);
        }
        res.setF(f);
        return res;
    }

    @Override
    public ParseInfo visitProgram(CmmParser.ProgramContext ctx) {
        this.visitChildren(ctx);
        // 该节点成功与失败与否对其他 parse 没有影响, 故直接返回 ParseInfo.nullInfo()
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitExtDefVar(CmmParser.ExtDefVarContext ctx) {
        // extDef: specifier extDecList? SEMI
        // 不确定是哪个分支的时候直接用 visit
        ParseInfo i = this.visit(ctx.specifier());
        // 若 specifier 出错, 则定义列表无效
        if (i.isError()) return ParseInfo.nullInfo();
        this.putInfo(ctx, i);
        if (ctx.extDecList() != null) this.visit(ctx.extDecList());
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitExtDefFun(CmmParser.ExtDefFunContext ctx) {
        // extDef: specifier funDec compSt
        ParseInfo si = this.visit(ctx.specifier());
        // 如果 specifier 出错, 函数定义直接丢弃?
        // no
        this.putInfo(ctx, si);
        ParseInfo fi = this.visit(ctx.funDec());
        // function 重名, 直接跳过整个函数体
        if (!fi.isError()) this.visit(ctx.compSt());
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitExtDecList(CmmParser.ExtDecListContext ctx) {
        // 传递 specifier
        this.passCallParam(ctx);
        this.visitChildren(ctx);
        return ParseInfo.nullInfo();
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
            // 返回错误 info
            return ParseInfo.errorInfo();
        }
        // 通知子定义, 当前在 struct scope 内
        i.setStructScope(true);
        this.putInfo(ctx, i);
        ParseInfo di = this.visit(ctx.defList());
        // defList 错了也无妨
        i.setStructScope(false);
        // 如果是 struct {...} 那 name 是 null
        s = new StructureT(name, di.getF());
        // 看完定义后才加入符号表
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
            return ParseInfo.errorInfo();
        }
        Type t = this.st.get(name).getType();
        if (!StructureT.isStructure(t)) {
            this.notifyError(ErrorType.UndefinedStruct, ctx.tag().getStart().getLine());
            return ParseInfo.errorInfo();
        }
        // 还有可能是 struct {} x
        // struct x y;
        StructureT st = (StructureT) t;
        if (st.getName() == null) {
            this.notifyError(ErrorType.UndefinedStruct, ctx.tag().getStart().getLine());
            return ParseInfo.errorInfo();
        }
        i.setT(t);
        return i;
    }

    @Override
    public ParseInfo visitOptTag(CmmParser.OptTagContext ctx) {
        if (ctx.ID() == null) return ParseInfo.nullInfo();
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
        String name = ctx.ID().getText();
        Type type = fi.getT();
        ArrayT base = null;
        // ArrayT 类型
        for (int i = ctx.getChildCount() - 1; i > 0; --i) {
            TerminalNode cur = (TerminalNode) ctx.getChild(i);
            int t = cur.getSymbol().getType();
            if (t == CmmParser.LB || t == CmmParser.RB) continue;
            int size = (int) this.parseInteger(cur.getText()); // CmmParser.INT
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
            this.notifyError(et, ctx.ID().getSymbol().getLine());
            return ParseInfo.errorInfo();
        } else this.st.put(s); // 放入符号表
        i.setF(f);
        i.setT((base == null) ? type : base);
        return i;
    }

    @Override
    public ParseInfo visitFunDec(CmmParser.FunDecContext ctx) {
        // funDec: ID LP varList? RP
        // extDef: specifier funDec compSt SEMI
        String name = ctx.ID().getText();
        if (this.st.contains(name)) {
            this.notifyError(ErrorType.RedefinedFun, ctx.ID().getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        ParseInfo fi = this.getCallParam(ctx);
        Type returnType = fi.getT();
        FieldList params = null;
        if (ctx.varList() != null) {
            ParseInfo vi = this.visit(ctx.varList());
            // 这个应该不会有 error
            // 或者说 出错也无妨
            params = vi.getF();
        }
        FunctionT f = new FunctionT(returnType, params);
        Symbol s = new Symbol(name, f);
        this.st.put(s);
        return ParseInfo.nullInfo();
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
        // 出错返回错误 info 即可
        if (i.isError()) return ParseInfo.errorInfo();
        this.putInfo(ctx, i);
        return this.visit(ctx.varDec());
    }

    @Override
    public ParseInfo visitCompSt(CmmParser.CompStContext ctx) {
        // compSt: LC defList stmtList RC
        // extDef: specifier funDec compSt
        // stmt: compSt
        // 主要注意传入的 specifier, return type
        this.passCallParam(ctx);
        this.visit(ctx.defList());
        this.visit(ctx.stmtList());
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitStmtList(CmmParser.StmtListContext ctx) {
        // return type
        this.passCallParam(ctx);
        this.visitChildren(ctx);
        // 出错了和别的语句关系也不大
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitStmtChildScope(CmmParser.StmtChildScopeContext ctx) {
        // return type
        this.passCallParam(ctx);
        this.visit(ctx.compSt());
        // 出错了和别的语句关系也不大
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitStmtNormal(CmmParser.StmtNormalContext ctx) {
        // stmt: RETURN? exp SEMI
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return ParseInfo.errorInfo();
        // 检测 return type
        Type returnType = fi.getT();
        if (ctx.RETURN() != null) {
            Type extType = i.getT();
            if (!returnType.isEquivalentType(extType)) {
                this.notifyError(ErrorType.TypeMismatchReturn, ctx.exp().getStart().getLine());
                return ParseInfo.errorInfo();
            }
        }
        // 错了和别的语句关系不大
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitStmtIf(CmmParser.StmtIfContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.exp());
        // 如果 exp 错那只是跳过 exp
        if (!i.isError()) {
            // ??????? 为什么我在这里一直判断的是 return type
            // 检测是不是 int
            Type extType = i.getT();
            if (!IntT.isInt(extType)) {
                this.notifyError(ErrorType.TypeMismatchOperand, ctx.exp().getStart().getLine());
            }
        }
        this.putInfo(ctx, fi);
        // 包括 else 里边的 stmt
        for (ParserRuleContext cur : ctx.stmt()) this.visit(cur);
        // 错了和别的语句关系不大
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitStmtWhile(CmmParser.StmtWhileContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.exp());
        if (!i.isError()) {
            Type extType = i.getT();
            if (!IntT.isInt(extType)) {
                this.notifyError(ErrorType.TypeMismatchOperand, ctx.exp().getStart().getLine());
            }
        }
        this.putInfo(ctx, fi);
        this.visit(ctx.stmt());
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitDefList(CmmParser.DefListContext ctx) {
        // defList: def*
        // 返回字段链表 按照顺序拼接
        // 传入是否在 struct 内
        this.passCallParam(ctx);
        // 每个 visit 是否成立和别的 visit 无关
        List<ParseInfo> list = ctx.def()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return this.concatInfoFields(list);
    }

    @Override
    public ParseInfo visitDef(CmmParser.DefContext ctx) {
        // 是否在 struct 内
        ParseInfo fi = this.getCallParam(ctx);
        ParseInfo i = this.visit(ctx.specifier());
        if (i.isError()) return ParseInfo.errorInfo();
        i.setStructScope(fi.isStructScope());
        this.putInfo(ctx, i);
        // 一定不是错的 最多空 info
        return this.visit(ctx.decList());
    }

    @Override
    public ParseInfo visitDecList(CmmParser.DecListContext ctx) {
        // decList: dec (COMMA dec)*
        // 返回字段链表 按照顺序拼接
        // 是否在 struct 和 specifier
        this.passCallParam(ctx);
        // 每个 visit 是否成立和别的 visit 无关
        List<ParseInfo> list = ctx.dec()
                .stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return this.concatInfoFields(list);
    }

    @Override
    public ParseInfo visitDec(CmmParser.DecContext ctx) {
        ParseInfo fi = this.getCallParam(ctx);
        // specifier
        this.passCallParam(ctx);
        // type 和 field
        ParseInfo vi = this.visit(ctx.varDec());
        // 艹 找了好久
        if (vi.isError()) return ParseInfo.errorInfo();
        // 检查赋值语句
        if (ctx.ASSIGNOP() == null) return vi;
        // structure 内不允许赋值 error type 15
        if (fi.isStructScope()) {
            this.notifyError(ErrorType.IllegalStruct, ctx.getStart().getLine());
            // 将加入符号表中的符号删除
            // 要删吗 ?
            // this.st.remove(ctx.varDec().ID().getText());
            // 好像删不删无所谓, 但不删就必须要返回 vi
            return vi;
        }
        ParseInfo ei = this.visit(ctx.exp());
        // 底层错则直接忽略
        // 如果是 struct 到不了这里, 如果是 compSt 返回什么都无所谓 -> 反正已经在符号表里了
        if (ei.isError()) return ParseInfo.errorInfo();
        Type expType = ei.getT();
        Type varType = vi.getT();
        // 赋值两边类型必须相等
        if (!expType.isEquivalentType(varType)) {
            this.notifyError(ErrorType.TypeMismatchAssign, ctx.ASSIGNOP().getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        // 返回 null 原因同上
        return ParseInfo.nullInfo();
    }

    @Override
    public ParseInfo visitExpAssign(CmmParser.ExpAssignContext ctx) {
        // exp: exp ASSIGNOP exp
        ParseInfo leftInfo = this.visit(ctx.exp(0));
        ParseInfo rightInfo = this.visit(ctx.exp(1));
        ParseInfo res = new ParseInfo();
        // 底层错直接忽略
        if (leftInfo.isError() || rightInfo.isError()) return ParseInfo.errorInfo();
        // 检查左值 只有不是左值才会检测类型
        if (leftInfo.isRightVal()) {
            this.notifyError(ErrorType.RValAssign, ctx.exp(0).getStart().getLine());
            return ParseInfo.errorInfo();
        }
        if (!leftInfo.getT().isEquivalentType(rightInfo.getT())) {
            this.notifyError(ErrorType.TypeMismatchAssign, ctx.ASSIGNOP().getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        res.setT(leftInfo.getT());
        // 返回左值
        // oj 上应该是判定左值 ?
        res.setRightVal(false);
        return res;
    }

    @Override
    public ParseInfo visitExpStructRef(CmmParser.ExpStructRefContext ctx) {
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return ParseInfo.errorInfo();
        // 判断是不是 structure
        if (!StructureT.isStructure(i.getT())) {
            this.notifyError(ErrorType.IllegalStructRef, ctx.exp().getStart().getLine());
            return ParseInfo.errorInfo();
        }
        // 寻找成员
        StructureT s = (StructureT) i.getT();
        // 没有这个成员
        if (!s.hasMember(ctx.ID().getText())) {
            this.notifyError(ErrorType.UndefinedStructField, ctx.ID().getSymbol().getLine());
            return ParseInfo.errorInfo();
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
            this.notifyError(ErrorType.UndefinedFun, ctx.ID().getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        Type t = this.st.get(name).getType();
        if (!FunctionT.isFunction(t)) {
            this.notifyError(ErrorType.IllegalFunCall, ctx.ID().getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        FunctionT ft = (FunctionT) t;
        // 判断参数是否匹配
        FieldList params = null;
        if (ctx.args() != null) {
            ParseInfo i = this.visit(ctx.args());
            // 如果 args 出错 那直接返回错误
            if (i.isError()) return ParseInfo.errorInfo();
            params = i.getF();
        }
        // 不匹配
        if (!ft.isParamsMatched(params)) {
            this.notifyError(ErrorType.FunArgMismatch, ctx.getStart().getLine());
            return ParseInfo.errorInfo();
        }
        ParseInfo res = new ParseInfo();
        res.setT(ft.getReturnType());
        res.setRightVal(true);
        return res;
    }

    @Override
    public ParseInfo visitExpParenthesis(CmmParser.ExpParenthesisContext ctx) {
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return ParseInfo.errorInfo();
        return i;
    }

    @Override
    public ParseInfo visitExpFloat(CmmParser.ExpFloatContext ctx) {
        ParseInfo i = new ParseInfo();
        FloatT t = new FloatT();
        i.setRightVal(true);
        i.setT(t);
        return i;
    }

    @Override
    public ParseInfo visitExpArrayRef(CmmParser.ExpArrayRefContext ctx) {
        ParseInfo i = this.visit(ctx.exp(0));
        // 前边出错直接返回
        if (i.isError()) return ParseInfo.errorInfo();
        Type t = i.getT();
        // 不是 array
        if (!ArrayT.isArray(t)) {
            this.notifyError(ErrorType.IllegalArrayRef, ctx.exp(0).getStart().getLine());
            return ParseInfo.errorInfo();
        }
        ParseInfo ei = this.visit(ctx.exp(1));
        // 有一个 index 错就判全部错
        if (ei.isError()) return ParseInfo.errorInfo();
        // index 不是 int
        if (!IntT.isInt(ei.getT())) {
            this.notifyError(ErrorType.IllegalArrayIndex, ctx.exp(1).getStart().getLine());
            return ParseInfo.errorInfo();
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
            return ParseInfo.errorInfo();
        }
        Symbol s = this.st.get(name);
        ParseInfo res = new ParseInfo();
        res.setT(s.getType());
        if (FunctionT.isFunction(s.getType())) res.setRightVal(true);
        return res;
    }

    @Override
    public ParseInfo visitExpBinary(CmmParser.ExpBinaryContext ctx) {
        // 仅有 int 型和 float 型变量才能参与算术运算
        // 且相互运算的类型必须相同
        ParseInfo leftInfo = this.visit(ctx.exp(0));
        ParseInfo rightInfo = this.visit(ctx.exp(1));
        // 底层错直接忽略
        if (leftInfo.isError() || rightInfo.isError()) return ParseInfo.errorInfo();
        ParseInfo res = new ParseInfo();
        // 左操作数不是可运算类型
        if (!IntT.isInt(leftInfo.getT()) && !FloatT.isFloat(leftInfo.getT())) {
            this.notifyError(ErrorType.TypeMismatchOperand, ctx.exp(0).getStart().getLine());
            return ParseInfo.errorInfo();
        }
        // 右操作数不是可运算类型
        if (!IntT.isInt(rightInfo.getT()) && !FloatT.isFloat(rightInfo.getT())) {
            this.notifyError(ErrorType.TypeMismatchOperand, ctx.exp(1).getStart().getLine());
            return ParseInfo.errorInfo();
        }
        if (!leftInfo.getT().isEquivalentType(rightInfo.getT())) {
            TerminalNode t = (TerminalNode) ctx.getChild(1);
            this.notifyError(ErrorType.TypeMismatchOperand, t.getSymbol().getLine());
            return ParseInfo.errorInfo();
        }
        res.setT(leftInfo.getT());
        if (ctx.RELOP() != null) res.setT(new IntT());
        res.setRightVal(true);
        return res;
    }

    @Override
    public ParseInfo visitExpUnary(CmmParser.ExpUnaryContext ctx) {
        // 仅有 int 型和 float 型变量才能参与算术运算
        ParseInfo i = this.visit(ctx.exp());
        if (i.isError()) return ParseInfo.errorInfo();
        if (!IntT.isInt(i.getT()) && !FloatT.isFloat(i.getT())) {
            notifyError(ErrorType.TypeMismatchOperand, ctx.exp().getStart().getLine());
            return ParseInfo.errorInfo();
        }
        ParseInfo res = new ParseInfo();
        res.setT(i.getT());
        res.setRightVal(true);
        return res;
    }

    @Override
    public ParseInfo visitExpInt(CmmParser.ExpIntContext ctx) {
        ParseInfo i = new ParseInfo();
        IntT t = new IntT();
        i.setRightVal(true);
        i.setT(t);
        return i;
    }

    @Override
    public ParseInfo visitArgs(CmmParser.ArgsContext ctx) {
        // exp (COMMA exp)*
        // 返回一个 fieldList 作为 params
        // 每个 visit 是否成立好像和别的 visit 相关
        List<ParseInfo> list = new ArrayList<>();
        for (CmmParser.ExpContext i : ctx.exp()) {
            ParseInfo cur = this.visit(i);
            if (cur.isError()) return ParseInfo.errorInfo();
            cur.setF(new FieldList(null, cur.getT()));
            list.add(cur);
        }
        return this.concatInfoFields(list);
    }

}
