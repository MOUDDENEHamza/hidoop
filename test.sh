#!/bin/bash

ssh hmoudden@$1 "cd nosave/hadoop && java -cp src ordo.WorkerImpl 800$(("$2" + 1)) $(("$2" + 1))" &
kill -INT 888
