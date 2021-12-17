import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CmmSemanticListener implements CmmParserListener {

    private final ParseTreeProperty<Type> values;
    private final SymbolTable st;

    public CmmSemanticListener() {
        this.values = new ParseTreeProperty<>();
        this.st = new SymbolTable();
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

    }

    @Override
    public void exitExtDecList(CmmParser.ExtDecListContext ctx) {

    }

    @Override
    public void enterSpecifier(CmmParser.SpecifierContext ctx) {

    }

    @Override
    public void exitSpecifier(CmmParser.SpecifierContext ctx) {

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
    public void enterOr(CmmParser.OrContext ctx) {

    }

    @Override
    public void exitOr(CmmParser.OrContext ctx) {

    }

    @Override
    public void enterMulOrDiv(CmmParser.MulOrDivContext ctx) {

    }

    @Override
    public void exitMulOrDiv(CmmParser.MulOrDivContext ctx) {

    }

    @Override
    public void enterInt(CmmParser.IntContext ctx) {

    }

    @Override
    public void exitInt(CmmParser.IntContext ctx) {

    }

    @Override
    public void enterArrayRef(CmmParser.ArrayRefContext ctx) {

    }

    @Override
    public void exitArrayRef(CmmParser.ArrayRefContext ctx) {

    }

    @Override
    public void enterStructRef(CmmParser.StructRefContext ctx) {

    }

    @Override
    public void exitStructRef(CmmParser.StructRefContext ctx) {

    }

    @Override
    public void enterFuncCall(CmmParser.FuncCallContext ctx) {

    }

    @Override
    public void exitFuncCall(CmmParser.FuncCallContext ctx) {

    }

    @Override
    public void enterFloat(CmmParser.FloatContext ctx) {

    }

    @Override
    public void exitFloat(CmmParser.FloatContext ctx) {

    }

    @Override
    public void enterNot(CmmParser.NotContext ctx) {

    }

    @Override
    public void exitNot(CmmParser.NotContext ctx) {

    }

    @Override
    public void enterParenthesis(CmmParser.ParenthesisContext ctx) {

    }

    @Override
    public void exitParenthesis(CmmParser.ParenthesisContext ctx) {

    }

    @Override
    public void enterNegative(CmmParser.NegativeContext ctx) {

    }

    @Override
    public void exitNegative(CmmParser.NegativeContext ctx) {

    }

    @Override
    public void enterAnd(CmmParser.AndContext ctx) {

    }

    @Override
    public void exitAnd(CmmParser.AndContext ctx) {

    }

    @Override
    public void enterPlusOrMinus(CmmParser.PlusOrMinusContext ctx) {

    }

    @Override
    public void exitPlusOrMinus(CmmParser.PlusOrMinusContext ctx) {

    }

    @Override
    public void enterCompare(CmmParser.CompareContext ctx) {

    }

    @Override
    public void exitCompare(CmmParser.CompareContext ctx) {

    }

    @Override
    public void enterAssign(CmmParser.AssignContext ctx) {

    }

    @Override
    public void exitAssign(CmmParser.AssignContext ctx) {

    }

    @Override
    public void enterId(CmmParser.IdContext ctx) {

    }

    @Override
    public void exitId(CmmParser.IdContext ctx) {

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

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
