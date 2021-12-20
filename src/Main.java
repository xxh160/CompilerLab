import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {
        try {
            CmmLexer lexer = new CmmLexerImpl(new ANTLRInputStream(new FileInputStream(args[0])));
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            CmmParser parser = new CmmParser(tokens);
            CmmParser.ProgramContext programContext = parser.program();

            CmmSemanticVisitor visitor = new CmmSemanticVisitor();
            visitor.visit(programContext);
        } catch (Exception e) {
        }
    }

}
