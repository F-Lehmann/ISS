JC = javac
CP = ITB2/ITB2.jar
JFLAGS = -g -cp $(CP) -d $(OUTDIR)
OUTDIR = ITB2/Filter/

default: clean
	$(JC) $(JFLAGS) **/*.java

clean:
	$(RM) $(OUTDIR)*.class

run: default
	java -jar ITB2/ITB2.jar
