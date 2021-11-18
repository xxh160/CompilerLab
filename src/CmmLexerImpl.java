import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.misc.Interval;

public class CmmLexerImpl extends CmmLexer {

    public CmmLexerImpl(CharStream input) {
        super(input);
    }

    @Override
    public void notifyListeners(LexerNoViableAltException e) {
        String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
        System.err.printf("Error type A at Line %d: %s.", this._tokenStartLine, this.getErrorDisplay(text));
    }

}
