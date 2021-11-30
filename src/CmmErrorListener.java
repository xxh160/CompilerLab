import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class CmmErrorListener implements ANTLRErrorListener {

    public static CmmErrorListener INSTANCE;

    public static void init(CmmParser parser){
        INSTANCE = new CmmErrorListener(parser);
    }

    private boolean error;
    private final ArrayList<Integer> errorLines;
    private final CmmParser parser;

    public boolean hasError() {
        return this.error;
    }

    private CmmErrorListener(CmmParser parser) {
        this.error = false;
        this.errorLines = new ArrayList<>();
        this.parser = parser;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        this.error = true;
        if (msg.startsWith("array size must be an integer constant")){
            Token token = (CommonToken) offendingSymbol;
            int s = token.getTokenIndex() - 1;
            TokenStream stream = this.parser.getTokenStream();
            while (token.getType() != CmmParser.ID && token.getType() != CmmParser.FLOAT) {
                token = stream.get(s--);
            }
            line = token.getLine();
        }
        if (this.errorLines.contains(line)) return;
        this.errorLines.add(line);
        System.err.println("Error type B at Line " + line + ": " + msg + ".");
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
    }

}
