#!/bin/bash

NAMEPROVIDER=( ader.enseeiht.fr)
SERVERS=(
        iode.enseeiht.fr carbone.enseeiht.fr minotaure.enseeiht.fr sodium.enseeiht.fr neon.enseeiht.fr
        azote.enseeiht.fr fluor.enseeiht.fr
        )
SENSOR=( ablette.enseeiht.fr )
WORKERS=(
        succube.enseeiht.fr hippogriffe.enseeiht.fr manticore.enseeiht.fr dragon.enseeiht.fr
        aston.enseeiht.fr cyclope.enseeiht.fr fermat.enseeiht.fr
        )
JOB=( behemot.enseeiht.fr )
USER=( luke.enseeiht.fr )

BREAKDONW_WORKERS=( oxygene.enseeiht.fr yoda.enseeiht.fr solo.enseeiht.fr vador.enseeiht.fr )

run_hdfs() {
  terminator --title NameProvider -e "ssh $login@${NAMEPROVIDER[0]} 'cd nosave/hadoop && make compile && java -cp src hdfs.NameProvider'; exec bash"
  sleep 5
  for ((i = 0; i < "$nb_servers"; i++)); do
    terminator --title Server"$(("$i" + 1))" -e "ssh $login@${SERVERS[$i]} 'cd nosave/hadoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server$(("$i" + 1)) 147.127.135.160'; exec bash"
  done
}

run_hidoop() {
  for ((i = 0; i < "$nb_servers"; i++)); do
    terminator --title Worker"$(("$i" + 1))" -e "ssh $login@${WORKERS[$i]} 'cd nosave/hadoop && java -cp src ordo.WorkerImpl 800$(("$i" + 1)) $(("$i" + 1))'; exec bash"
  done
  terminator --title Job -e "ssh $login@${JOB[0]} 'cd nosave/hadoop && java -cp src ordo.Job'; exec bash"
}

# Greeting message
clear
echo "HADOOP"
echo

# Get user login

read -rp "Please type your login : " login
echo

# Kill name provider process running
ssh "$login@${NAMEPROVIDER[0]}" "pkill java"

# Get the number of servers
echo "We have ${#SERVERS[@]} servers, please choose at least 1 server and at most ${#SERVERS[@]} servers."
while true; do
  read -rp "Please type the number of servers : " nb_servers
  if [[ $nb_servers ]] && [ "$nb_servers" -eq "$nb_servers" ] 2>/dev/null; then
    break
  else
    echo $'\e[31;1mERROR :\e[0m\eT '"$nb_servers"' is not an integer or not defined'
    echo "Please try again"
  fi
done

# Run the name provider and the servers
run_hdfs

# Run the workers
run_hidoop

# Run the heartbeat sensor
terminator --title Sensor -e "make compile && java -cp src ordo.HeartBeatSensor $nb_servers; exec bash"

# Run the application
terminator --title User -e "ssh $login@${USER[0]} 'cd nosave/hadoop && ./user.sh'; exec bash"
