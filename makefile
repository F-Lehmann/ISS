JFLAGS = -g -cp $(CP) -d $(OUTDIR)
JC = javac
CP = ITB2/ITB2.jar
OUTDIR = ITB2/Filter/
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = $(shell find . -name '*.java')

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(OUTDIR)*.class

run:
	java -jar ITB2/ITB2.jar
