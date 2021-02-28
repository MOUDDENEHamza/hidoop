#!/bin/bash

HOSTS=(
  ader.enseeiht.fr iode.enseeiht.fr carbone.enseeiht.fr oxygene.enseeiht.fr sodium.enseeiht.fr neon.enseeiht.fr
  azote.enseeiht.fr fluor.enseeiht.fr yoda.enseeiht.fr solo.enseeiht.fr vador.enseeiht.fr dragon.enseeiht.fr
  aston.enseeiht.fr cyclope.enseeiht.fr fermat.enseeiht.fr luke.enseeiht.fr
)

run_hdfs() {
  terminator --title NameProvider -e "ssh $login@${HOSTS[0]} 'cd nosave/hadoop && make compile && java -cp src hdfs.NameProvider'; exec bash"
  sleep 5
  for ((i = 1; i <= $nb_servers; i++)); do
    terminator --title Server"$i" -e "ssh $login@${HOSTS[$i]} 'cd nosave/hadoop && java -cp src:lib/snakeyaml-1.5.jar hdfs.HdfsServer server$i 147.127.135.160'; exec bash"
    sleep 2
  done
}

run_hidoop() {
  for ((i = 1; i <= $nb_servers; i++)); do
    terminator --title Worker"$i" -e "ssh $login@${HOSTS[$nb_servers + i]} 'cd nosave/hadoop && java -cp src ordo.WorkerImpl 800$i $i'; exec bash"
    sleep 2
  done
}

# Get user login
clear
read -rp "Please type your login : " login
echo

# Kill name provider process running
ssh "$login@${HOSTS[0]}" "pkill java"

# Get the number of servers
echo "We have $(($((${#HOSTS[@]} - 2)) / 2)) servers, please choose at least 1 server and at most "
echo "$(($((${#HOSTS[@]} - 2)) / 2)) servers."
read -rp "Please type the number of servers : " nb_servers

# Run the name provider and the servers
run_hdfs

# Run the workers
run_hidoop

# Run the application
terminator --title User -e "ssh $login@${HOSTS[${#n7_computers[@]} - 1]} 'cd nosave/hadoop && ./user.sh'; exec bash"
