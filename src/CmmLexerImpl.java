import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;

public class CmmLexerImpl extends CmmLexer {

    private int faults;

    public CmmLexerImpl(CharStream input) {
        super(input);
        this.faults = 0;
    }

    public int getFaults() {
        return this.faults;
    }

    public String getToken(int i) {
        if (i - 1 >= ruleNames.length || i - 1 < 0) return null;
        return ruleNames[i - 1];
    }

    @Override
    public List<? extends Token> getAllTokens() {
        this.faults = 0;
        return super.getAllTokens();
    }

    @Override
    public void notifyListeners(LexerNoViableAltException e) {
        String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
        System.err.printf("Error type A at Line %d: '%s'.\n", this._tokenStartLine, text);
        ++this.faults;
    }

}
