import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class CmmParserListenerImpl implements CmmParserListener {

    private int space;
    private final CmmParser parser;

    private void incSpace() {
        this.space += 2;
    }

    private void decSpace() {
        this.space -= 2;
    }

    private void printSpace() {
        for (int i = 0; i < this.space; ++i) System.err.print(" ");
    }

    private String parseDouble(String i) {
        double f = Double.parseDouble(i);
        return new DecimalFormat("0.000000").format(f);
    }

    private String parseInteger(String si) {
        long res;
        if (si.startsWith("0x") || si.startsWith("0X")) {
            res = new BigInteger(si.substring(2), 16).longValue();
        } else if (si.startsWith("0") && si.length() > 1) {
            res = new BigInteger(si.substring(1), 8).longValue();
        } else {
            res = new BigInteger(si).longValue();
        }
        return String.valueOf(res);
    }

    public CmmParserListenerImpl(CmmParser parser) {
        this.space = 0;
        this.parser = parser;
    }

    @Override
    public void enterProgram(CmmParser.ProgramContext ctx) {
        System.err.println("Program (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitProgram(CmmParser.ProgramContext ctx) {
    }

    @Override
    public void enterExtDef(CmmParser.ExtDefContext ctx) {
        System.err.println("ExtDef (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitExtDef(CmmParser.ExtDefContext ctx) {
    }

    @Override
    public void enterExtDecList(CmmParser.ExtDecListContext ctx) {
        System.err.println("ExtDecList (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitExtDecList(CmmParser.ExtDecListContext ctx) {
    }

    @Override
    public void enterSpecifier(CmmParser.SpecifierContext ctx) {
        System.err.println("Specifier (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitSpecifier(CmmParser.SpecifierContext ctx) {
    }

    @Override
    public void enterStructSpecifier(CmmParser.StructSpecifierContext ctx) {
        System.err.println("StructSpecifier (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitStructSpecifier(CmmParser.StructSpecifierContext ctx) {
    }

    @Override
    public void enterVarDec(CmmParser.VarDecContext ctx) {
        System.err.println("VarDec (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitVarDec(CmmParser.VarDecContext ctx) {
    }

    @Override
    public void enterFunDec(CmmParser.FunDecContext ctx) {
        System.err.println("FunDec (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitFunDec(CmmParser.FunDecContext ctx) {
    }

    @Override
    public void enterVarList(CmmParser.VarListContext ctx) {
        System.err.println("VarList (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitVarList(CmmParser.VarListContext ctx) {
    }

    @Override
    public void enterParamDec(CmmParser.ParamDecContext ctx) {
        System.err.println("ParamDec (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitParamDec(CmmParser.ParamDecContext ctx) {
    }

    @Override
    public void enterCompSt(CmmParser.CompStContext ctx) {
        System.err.println("CompSt (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitCompSt(CmmParser.CompStContext ctx) {
    }

    @Override
    public void enterStmt(CmmParser.StmtContext ctx) {
        System.err.println("Stmt (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitStmt(CmmParser.StmtContext ctx) {
    }

    @Override
    public void enterDef(CmmParser.DefContext ctx) {
        System.err.println("Def (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitDef(CmmParser.DefContext ctx) {
    }

    @Override
    public void enterDecList(CmmParser.DecListContext ctx) {
        System.err.println("DecList (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitDecList(CmmParser.DecListContext ctx) {
    }

    @Override
    public void enterDec(CmmParser.DecContext ctx) {
        System.err.println("Dec (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitDec(CmmParser.DecContext ctx) {
    }

    @Override
    public void enterExp(CmmParser.ExpContext ctx) {
        System.err.println("Exp (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitExp(CmmParser.ExpContext ctx) {
    }

    @Override
    public void enterArgs(CmmParser.ArgsContext ctx) {
        System.err.println("Args (" + ctx.start.getLine() + ")");
    }

    @Override
    public void exitArgs(CmmParser.ArgsContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        int type = terminalNode.getSymbol().getType();
        if (type == CmmParser.EOF) return;
        this.printSpace();
        System.err.print(this.parser.getVocabulary().getSymbolicName(type));
        String text = terminalNode.getSymbol().getText();
        if (type == CmmParser.TYPE || type == CmmParser.ID) {
            System.err.print(": ");
            System.err.print(text);
        } else if (type == CmmParser.INT) {
            System.err.print(": ");
            System.err.print(this.parseInteger(text));
        } else if (type == CmmParser.FLOAT) {
            System.err.print(": ");
            System.err.print(this.parseDouble(text));
        }
        System.err.println();
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
        this.printSpace();
        this.incSpace();
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        this.decSpace();
    }
}
