import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CmmLexer lexer = null;
        try {
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(args[0]));
            lexer = new CmmLexer(input) {

                private int fault;

                @Override
                public List<? extends Token> getAllTokens() {
                    fault = 0;
                    var res = super.getAllTokens();
                    if (fault == 0) return res;
                    return null;
                }

                @Override
                public void notifyListeners(LexerNoViableAltException e) {
                    String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
                    System.err.printf("Error type A at Line %d: '%s'.\n", this._tokenStartLine, text.trim());
                    ++this.fault;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert lexer != null;
        var tokenList = lexer.getAllTokens();
        if (tokenList == null) return;
        for (var i : tokenList) {
            int type = i.getType();
            String output = lexer.getRuleNames()[type - 1];
            output += " ";
            // float
            if (type == 1) {
                float f = Float.parseFloat(i.getText());
                output += new DecimalFormat("0.000000").format(f);
            } else if (type == 2) {
                String si = i.getText();
                int res;
                if (si.startsWith("0x") || si.startsWith("0X")) {
                    res = Integer.parseInt(si.substring(2), 16);
                } else if (si.startsWith("0")) {
                    res = Integer.parseInt(si.substring(1), 8);
                } else {
                    res = Integer.parseInt(si);
                }
                output += String.valueOf(res);
            } else {
                output += i.getText();
            }
            output += " at Line ";
            output += String.valueOf(i.getLine());
            output += ".";
            System.err.println(output);
        }
    }

}
