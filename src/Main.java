import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        CmmLexer lexer = new CmmLexerImpl(new ANTLRInputStream(new FileInputStream(args[0])));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CmmParser parser = new CmmParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        CmmParserListener baseListener = new CmmParserListenerImpl(parser);
        walker.walk(baseListener, parser.program());
    }

}
