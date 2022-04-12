ANTLR=/usr/local/lib/antlr-4.10-complete.jar

all: generate compile test

generate:
	java -jar $(ANTLR) -o output SLang.g4

compile:
	javac -cp $(ANTLR):output:. Main.java

test:
	java -cp $(ANTLR):output:. Main test.sl > test.ll
	lli test.ll

clean:
	rm -f test.ll
	rm -f *.class
	rm -rf output

