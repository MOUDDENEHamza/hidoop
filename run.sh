#!/bin/bash

n7_computers=(iode.enseeiht.fr carbone.enseeiht.fr oxygene.enseeiht.fr sodium.enseeiht.fr neon.enseeiht.fr azote.enseeiht.fr fluor.enseeiht.fr yoda.enseeiht.fr solo.enseeiht.fr hmoudden@vador.enseeiht.fr hmoudden@dragon.enseeiht.fr hmoudden@aston.enseeiht.fr hmoudden@cyclope.enseeiht.fr hmoudden@fermat.enseeiht.fr)

run_remote_hdfs() {
    terminator --title NameProvider -e 'ssh hmoudden@ader.enseeiht.fr "cd nosave/hidoop && make compile && java -cp src hdfs.NameProvider"; exec bash'
    sleep 5
    terminator --title server1 -e 'ssh hmoudden@boole.enseeiht.fr "cd nosave/hidoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server1 147.127.135.160"; exec bash'
    sleep 2
    terminator --title server2 -e 'ssh hmoudden@iode.enseeiht.fr "cd nosave/hidoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server2 147.127.135.160"; exec bash'
    sleep 2
    terminator --title server3 -e 'ssh hmoudden@dragon.enseeiht.fr "cd nosave/hidoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server3 147.127.135.160"; exec bash'
    sleep 2
    terminator --title server4 -e 'ssh hmoudden@aston.enseeiht.fr "cd nosave/hidoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server4 147.127.135.160"; exec bash'
}

run_remote_hidoop() {
    terminator --title worker1 -e 'ssh hmoudden@minotaure.enseeiht.fr "cd nosave/hidoop && java -cp src ordo.WorkerImpl 8001 1"; exec bash'
    sleep 2
    terminator --title worker2 -e 'ssh hmoudden@sodium.enseeiht.fr "cd nosave/hidoop && java -cp src ordo.WorkerImpl 8002 2"; exec bash'
    sleep 2
    terminator --title worker3 -e 'ssh hmoudden@cyclope.enseeiht.fr "cd nosave/hidoop && java -cp src ordo.WorkerImpl 8003 3"; exec bash'
    sleep 2
    terminator --title worker4 -e 'ssh hmoudden@fermat.enseeiht.fr "cd nosave/hidoop && java -cp src ordo.WorkerImpl 8004 4"; exec bash'

   sleep 5
   terminator --title user -e 'ssh hmoudden@carbone.enseeiht.fr "cd nosave/hidoop && java -cp src application.MyMapReduce data/huge_file.txt remote && diff data/count-res data/huge_file.txt-res"; exec bash'
}

./clean.sh
run_remote_hdfs
run_remote_hidoop
