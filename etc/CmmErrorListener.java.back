import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class CmmErrorListener implements ANTLRErrorListener {

    public static CmmErrorListener INSTANCE;

    public static void init(CmmParser parser) {
        INSTANCE = new CmmErrorListener(parser);
    }

    private boolean error;
    private final CmmParser parser;

    public boolean hasError() {
        return this.error;
    }

    private CmmErrorListener(CmmParser parser) {
        this.error = false;
        this.parser = parser;
    }

    private boolean isEnd(TokenStream stream, int cur) {
        if (cur == 0) return true;
        return stream.get(cur).getType() == CmmParser.LB && stream.get(cur - 1).getType() == CmmParser.ID;
    }

    // 用作 array index 检测, 没有 errorLine 的限制
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        this.error = true;
        // maybe many errors
        if (msg.startsWith("Index must be an integer")) {
            Token token = (CommonToken) offendingSymbol;
            int s = token.getTokenIndex() - 1;
            TokenStream stream = this.parser.getTokenStream();
            List<Integer> errors = new ArrayList<>();
            while (!isEnd(stream, s)) {
                token = stream.get(s--);
                if (token.getType() == CmmParser.ID || token.getType() == CmmParser.FLOAT) {
                    int l = token.getLine();
                    errors.add(0, l);
                }
            }
            for (int l : errors) System.err.println("Error type 12 at Line " + l + ": " + msg + ".");
            return;
        }
        System.err.println("Error type 12 at Line " + line + ": " + msg + ".");
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
