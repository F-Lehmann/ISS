JFLAGS = -g -cp $(CP) -d $(OUTDIR)
JC = javac
CP = ITB2/ITB2.jar
OUTDIR = ITB2/Filter/
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

# ADD NEW CLASSES HERE
CLASSES = \
	ITB2/ITB2/examples/Grayfilter.java \
	ITB2/ITB2/examples/PropertyExample.java \
	ITB2/ITB2/examples/SelectionExample.java \
	*/SelektiveFarbverbesserung_LKO_FL_JM.java \
	*/CopyImageFilter_KB.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(OUTDIR)*.class
	
run:
	java -jar ITB2/ITB2.jar
