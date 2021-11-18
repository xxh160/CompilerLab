import org.antlr.v4.runtime.ANTLRInputStream;

import java.io.FileInputStream;
import java.text.DecimalFormat;

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
        if (lexer.getFaults() > 0) return;
        for (var i : tokenList) {
            int type = i.getType();
            String output = lexer.getToken(type);
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
            System.out.println(output);
        }
    }

}
