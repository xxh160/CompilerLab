import org.antlr.runtime.ANTLRInputStream;

import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {
        try {
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(args[0]));
            CmmLexer cmmLexer = new CmmLexer(input);
        } catch (Exception e) {
        }
    }

}
