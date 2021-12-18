import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CmmSemanticListener implements CmmParserListener {

    private final ParseTreeProperty<Type> types;
    private final ParseTreeProperty<FieldList> fields;
    private final SymbolTable st;

    public CmmSemanticListener() {
        this.types = new ParseTreeProperty<>();
        this.fields = new ParseTreeProperty<>();
        this.st = new SymbolTable();
    }

    private Type popType(ParseTree node) {
        Type type = this.types.get(node);
        this.types.removeFrom(node);
        return type;
    }

    private void pushType(ParseTree node, Type type) {
        this.types.put(node, type);
    }

    private FieldList popField(ParseTree node) {
        FieldList field = this.fields.get(node);
        this.fields.removeFrom(node);
        return field;
    }

    private void pushField(ParseTree node, FieldList field) {
        this.fields.put(node, field);
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

    @Override
    public void enterProgram(CmmParser.ProgramContext ctx) {

    }

    @Override
    public void exitProgram(CmmParser.ProgramContext ctx) {
    }

    // 全局变量的定义
    @Override
    public void enterExtDef(CmmParser.ExtDefContext ctx) {

    }

    @Override
    public void exitExtDef(CmmParser.ExtDefContext ctx) {

    }

    @Override
    public void enterExtDecList(CmmParser.ExtDecListContext ctx) {
        ParserRuleContext father = ctx.getParent();
        // extDef: specifier extDecList? SEMI
        if (father.getRuleIndex() == CmmParser.RULE_extDef) {
            CmmParser.ExtDefContext f = (CmmParser.ExtDefContext) father;
            Type type = this.popType(f.specifier());
            // inh 传参
            this.pushType(ctx, type);
        }
    }

    @Override
    public void exitExtDecList(CmmParser.ExtDecListContext ctx) {
    }

    @Override
    public void enterSpecifier(CmmParser.SpecifierContext ctx) {
    }

    // 可以通过 ctx.XXX() 是否为 null 判断进入哪条分支
    @Override
    public void exitSpecifier(CmmParser.SpecifierContext ctx) {
        Type type;
        if (ctx.TYPE() != null) {
            // TYPE, 直接处理
            if (ctx.TYPE().getText().equals("int")) type = new Int();
            else type = new Float();
        } else type = this.popType(ctx.structSpecifier()); // structSpecifier
        this.pushType(ctx, type);
    }

    @Override
    public void enterStructSpecifier(CmmParser.StructSpecifierContext ctx) {
    }

    @Override
    public void exitStructSpecifier(CmmParser.StructSpecifierContext ctx) {
        // structSpecifier with body
        if (ctx.optTag() != null) {
            // 获取 defList 的 def
            // 构造 struct, 看 optTag 是否有名字来判断加入符号表与否
        }
        // without body
        // structSpecifier: STRUCT tag
        // 查符号表, 如果没有直接报错, type 17
        String structName = ctx.tag().getText();
        if (!this.st.contains(structName)) {
            this.notifyError(ErrorType.UndefinedStruct, ctx.getStart().getLine());
            // 错误恢复, 假设有这个 struct
            Structure s = new Structure(ctx.tag().getText(), null);
            this.pushType(ctx, s);
            return;
        }
        Symbol symbol = this.st.get(structName);
        this.pushType(ctx, symbol.getType());
    }

    @Override
    public void enterOptTag(CmmParser.OptTagContext ctx) {

    }

    @Override
    public void exitOptTag(CmmParser.OptTagContext ctx) {

    }

    @Override
    public void enterTag(CmmParser.TagContext ctx) {

    }

    @Override
    public void exitTag(CmmParser.TagContext ctx) {

    }

    @Override
    public void enterVarDec(CmmParser.VarDecContext ctx) {
        // extDecList: varDec (COMMA varDec)*
        // 多个 varDec 会多次 enter
        ParserRuleContext father = ctx.getParent();
        if (ctx.getParent().getRuleIndex() == CmmParser.RULE_extDecList) {
            CmmParser.ExtDecListContext f = (CmmParser.ExtDecListContext) father;
            Type type = this.popType(f);
            this.pushType(ctx, type);
        }
    }

    @Override
    public void exitVarDec(CmmParser.VarDecContext ctx) {
        
    }

    @Override
    public void enterFunDec(CmmParser.FunDecContext ctx) {

    }

    @Override
    public void exitFunDec(CmmParser.FunDecContext ctx) {

    }

    @Override
    public void enterVarList(CmmParser.VarListContext ctx) {

    }

    @Override
    public void exitVarList(CmmParser.VarListContext ctx) {

    }

    @Override
    public void enterParamDec(CmmParser.ParamDecContext ctx) {

    }

    @Override
    public void exitParamDec(CmmParser.ParamDecContext ctx) {

    }

    @Override
    public void enterCompSt(CmmParser.CompStContext ctx) {

    }

    @Override
    public void exitCompSt(CmmParser.CompStContext ctx) {

    }

    @Override
    public void enterStmtList(CmmParser.StmtListContext ctx) {

    }

    @Override
    public void exitStmtList(CmmParser.StmtListContext ctx) {

    }

    @Override
    public void enterStmt(CmmParser.StmtContext ctx) {

    }

    @Override
    public void exitStmt(CmmParser.StmtContext ctx) {

    }

    @Override
    public void enterDefList(CmmParser.DefListContext ctx) {

    }

    @Override
    public void exitDefList(CmmParser.DefListContext ctx) {

    }

    @Override
    public void enterDef(CmmParser.DefContext ctx) {

    }

    @Override
    public void exitDef(CmmParser.DefContext ctx) {

    }

    @Override
    public void enterDecList(CmmParser.DecListContext ctx) {

    }

    @Override
    public void exitDecList(CmmParser.DecListContext ctx) {

    }

    @Override
    public void enterDec(CmmParser.DecContext ctx) {

    }

    @Override
    public void exitDec(CmmParser.DecContext ctx) {

    }

    // 以下是 exp 备选分支
    @Override
    public void enterExpAssign(CmmParser.ExpAssignContext ctx) {

    }

    @Override
    public void exitExpAssign(CmmParser.ExpAssignContext ctx) {

    }

    @Override
    public void enterExpOr(CmmParser.ExpOrContext ctx) {

    }

    @Override
    public void exitExpOr(CmmParser.ExpOrContext ctx) {

    }

    @Override
    public void enterExpStructRef(CmmParser.ExpStructRefContext ctx) {

    }

    @Override
    public void exitExpStructRef(CmmParser.ExpStructRefContext ctx) {

    }

    @Override
    public void enterExpMulOrDiv(CmmParser.ExpMulOrDivContext ctx) {

    }

    @Override
    public void exitExpMulOrDiv(CmmParser.ExpMulOrDivContext ctx) {

    }

    @Override
    public void enterExpFuncCall(CmmParser.ExpFuncCallContext ctx) {

    }

    @Override
    public void exitExpFuncCall(CmmParser.ExpFuncCallContext ctx) {

    }

    @Override
    public void enterExpCompare(CmmParser.ExpCompareContext ctx) {

    }

    @Override
    public void exitExpCompare(CmmParser.ExpCompareContext ctx) {

    }

    @Override
    public void enterExpNegative(CmmParser.ExpNegativeContext ctx) {

    }

    @Override
    public void exitExpNegative(CmmParser.ExpNegativeContext ctx) {

    }

    @Override
    public void enterExpParenthesis(CmmParser.ExpParenthesisContext ctx) {

    }

    @Override
    public void exitExpParenthesis(CmmParser.ExpParenthesisContext ctx) {

    }

    @Override
    public void enterExpFloat(CmmParser.ExpFloatContext ctx) {

    }

    @Override
    public void exitExpFloat(CmmParser.ExpFloatContext ctx) {

    }

    @Override
    public void enterExpAnd(CmmParser.ExpAndContext ctx) {

    }

    @Override
    public void exitExpAnd(CmmParser.ExpAndContext ctx) {

    }

    @Override
    public void enterExpPlusOrMinus(CmmParser.ExpPlusOrMinusContext ctx) {

    }

    @Override
    public void exitExpPlusOrMinus(CmmParser.ExpPlusOrMinusContext ctx) {

    }

    @Override
    public void enterExpNot(CmmParser.ExpNotContext ctx) {

    }

    @Override
    public void exitExpNot(CmmParser.ExpNotContext ctx) {

    }

    @Override
    public void enterExpArrayRef(CmmParser.ExpArrayRefContext ctx) {

    }

    @Override
    public void exitExpArrayRef(CmmParser.ExpArrayRefContext ctx) {

    }

    @Override
    public void enterExpId(CmmParser.ExpIdContext ctx) {

    }

    @Override
    public void exitExpId(CmmParser.ExpIdContext ctx) {

    }

    @Override
    public void enterExpInt(CmmParser.ExpIntContext ctx) {

    }

    @Override
    public void exitExpInt(CmmParser.ExpIntContext ctx) {

    }

    @Override
    public void enterArgs(CmmParser.ArgsContext ctx) {

    }

    @Override
    public void exitArgs(CmmParser.ArgsContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {


    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    // 先于具体的 enterRule 调用
    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
    }

    // 后于具体的 exitRule 调用
    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
    }

}
