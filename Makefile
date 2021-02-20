MAKE = make
SILENT = -s
JAVAC = javac
JAVA = java
JAR = -cp "lib/snakeyaml-1.5.jar"
CLASSPATH = -cp src
FLAG = -Xlint:unchecked
SRC = src/formats/*.java src/map/*.java src/hdfs/*.java src/ordo/*.java src/application/*.java
READ = read -p
CLASS = src/formats/*.class src/map/*.class src/hdfs/*.class src/ordo/*.class src/application/*.class

all :
	$(MAKE) compile $(SILENT)

compile :
	$(JAVAC) $(JAR) $(SRC)

run_worker :
	$(JAVA) $(CLASSPATH) ordo.WorkerImpl

run_NameProvider :
	$(JAVA) $(CLASSPATH) hdfs.NameProvider

run_HdfsServer :
	@$(READ) "Enter the server name : " ServerName; \
	$(JAVA) $(CLASSPATH):lib/snakeyaml-1.5.jar hdfs.HdfsServer $$ServerName

run_HdfsClient :
	@$(READ) "args : " args; \
        $(JAVA) $(CLASSPATH):lib/snakeyaml-1.5.jar hdfs.HdfsClient $$args

clean :
	rm $(CLASS)
