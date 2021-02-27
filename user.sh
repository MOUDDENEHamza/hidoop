#!/bin/bash

echo "Hello $HOSTNAME, if it is the first time you run this application, please mind that before you launch this script"
echo " you should write a file in the servers you will use."
echo "Tips : To generate a text file you can use generate_data.sh script in data folder before you run the application."
echo

sleep 5
java -cp src application.MyMapReduce data/data.txt
