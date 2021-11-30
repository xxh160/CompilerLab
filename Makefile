include Makefile.git

WORK_DIR      = $(shell pwd)
SUBMIT        = submit.sh
ANTLR_GEN     = src
DOMAINNAME    = 139.224.195.50:3000

# ANTLR       = java -jar /usr/local/lib/antlr-*-complete.jar -listener -visitor -long-messages
ANTLR         = java -jar /usr/share/java/antlr-*-complete.jar -listener -visitor -long-messages
JAVAC         = javac -g -cp /usr/share/java/antlr-*-complete.jar
JAVA          = java

PFILE         = $(shell find . -name "CmmParser.g4")
LFILE         = $(shell find . -name "CmmLexer.g4")
JAVAFILE      = $(shell find . -name "*.java")
# ANTLRPATH   = $(shell find /usr/local/lib -name "antlr-*-complete.jar")
ANTLRPATH     = $(shell find /usr/share/java -name "antlr-*-complete.jar")

compile: antlr
	$(call git_commit, "make")
	mkdir -p classes
	$(JAVAC) $(JAVAFILE) -d classes

run: compile
	java -classpath ./classes:$(ANTLRPATH) Main $(FILEPATH)

antlr: $(LFILE) $(PFILE) 
	$(ANTLR) $(PFILE) $(LFILE)

test: compile
	$(call git_commit, "test")
	cd classes && $(JAVA) -classpath .:$(ANTLRPATH) Main ../tests/test3.cmm 2> ../tests/actual.txt
	diff tests/expected.txt tests/actual.txt > tests/out.txt
	echo "$$?"

clean:
	rm -f $(ANTLR_GEN)/*.tokens
	rm -f $(ANTLR_GEN)/*.interp
	rm -f $(ANTLR_GEN)/CmmLexer.java $(ANTLR_GEN)/CmmParser.java $(ANTLR_GEN)/CmmParserBaseListener.java $(ANTLR_GEN)/CmmParserBaseVisitor.java $(ANTLR_GEN)/CmmParserListener.java $(ANTLR_GEN)/CmmParserVisitor.java
	rm -rf classes
	rm -rf out

submit: clean
	git gc
	bash $(WORK_DIR)/$(SUBMIT)

.PHONY: compile antlr test run clean submit
