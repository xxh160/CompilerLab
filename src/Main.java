import org.antlr.v4.runtime.ANTLRInputStream;

import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {
        CmmLexerImpl lexer = null;
        try {
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(args[0]));
            lexer = new CmmLexerImpl(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert lexer != null;
        var tokenList = lexer.getAllTokens();
//        if (lexer.getFaults() > 0) return;
        for (var i : tokenList) {
            System.out.println(lexer.getToken(i.getTokenIndex()));
        }
    }

}
