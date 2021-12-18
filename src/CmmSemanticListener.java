import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CmmSemanticListener implements CmmParserListener {

    private final ParseTreeProperty<Type> values;
    private final SymbolTable st;

    public CmmSemanticListener() {
        this.values = new ParseTreeProperty<>();
        this.st = new SymbolTable();
    }

    private Type popType(ParseTree node) {
        Type type = this.values.get(node);
        this.values.removeFrom(node);
        return type;
    }

    private void pushType(ParseTree node, Type type) {
        this.values.put(node, type);
    }

    @Override
    public void enterProgram(CmmParser.ProgramContext ctx) {

    }

    @Override
    public void exitProgram(CmmParser.ProgramContext ctx) {
    }

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
            Type type = this.popType(f);
            this.pushType(ctx, type);
        }
    }

    @Override
    public void exitExtDecList(CmmParser.ExtDecListContext ctx) {
    }

    @Override
    public void enterSpecifier(CmmParser.SpecifierContext ctx) {

    }

    @Override
    public void exitSpecifier(CmmParser.SpecifierContext ctx) {
        System.out.println(ctx.TYPE());
        System.out.println(ctx.structSpecifier());
    }

    @Override
    public void enterStructSpecifier(CmmParser.StructSpecifierContext ctx) {

    }

    @Override
    public void exitStructSpecifier(CmmParser.StructSpecifierContext ctx) {

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

    @Override
    public void enterExp(CmmParser.ExpContext ctx) {

    }

    @Override
    public void exitExp(CmmParser.ExpContext ctx) {

    }

    @Override
    public void enterArgs(CmmParser.ArgsContext ctx) {

    }

    @Override
    public void exitArgs(CmmParser.ArgsContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        int t = terminalNode.getSymbol().getType();
        if (t == CmmParser.TYPE) {
            Type type;
            if (terminalNode.getSymbol().getText().equals("int")) type = new Int();
            else type = new Float();
            this.pushType(terminalNode, type);
        }
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
