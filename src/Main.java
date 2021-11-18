import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.List;

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
        List<? extends Token> tokenList = lexer.getAllTokens();
        if (lexer.getFaults() > 0) return;
        for (Token i : tokenList) {
            int type = i.getType();
            String output = lexer.getToken(type);
            output += " ";
            // float
            if (type == 1) {
//                String si = i.getText().toLowerCase();
//                if (si.contains("e")) {
//                    String base = si.substring(0, si.indexOf('e'));
//                    String exponent = si.substring(si.indexOf('e') + 1);
//                }
                double f = Double.parseDouble(i.getText());
                output += new DecimalFormat("0.000000").format(f);
            } else if (type == 2) {
                String si = i.getText();
                int res;
                if (si.startsWith("0x") || si.startsWith("0X")) {
                    res = Integer.parseInt(si.substring(2), 16);
                } else if (si.startsWith("0") && si.length() > 1) {
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
