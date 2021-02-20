#!/bin/bash

HOST[0]=hmoudden@ader.enseeiht.fr
HOST[1]=hmoudden@boole.enseeiht.fr
HOST[2]=hmoudden@iode.enseeiht.fr
HOST[3]=hmoudden@dragon.enseeiht.fr
HOST[4]=hmoudden@aston.enseeiht.fr

ssh ${HOST[0]} "pkill java"
