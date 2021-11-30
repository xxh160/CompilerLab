import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        CmmLexer lexer = new CmmLexerImpl(new ANTLRInputStream(new FileInputStream(args[0])));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CmmParser parser = new CmmParser(tokens);
        CmmErrorListener.init(parser);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(CmmErrorListener.INSTANCE);

        ParseTreeWalker walker = new ParseTreeWalker();
        CmmParserListener baseListener = new CmmParserListenerImpl(parser);
        CmmParser.ProgramContext programContext = parser.program();
        if (CmmErrorListener.INSTANCE.hasError()) return;
        walker.walk(baseListener, programContext);
    }

}
