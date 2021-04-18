#!/bin/bash

ssh hmoudden@succube.enseeiht.fr "cd nosave/hadoop && java -cp src ordo.WorkerImpl 8001 1"