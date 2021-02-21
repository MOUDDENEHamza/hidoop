#!/bin/bash

# Copy filesample.txt in data.txt.
cp filesample.txt data.txt

# Generate 2^n times 1Ko file.
echo "This script aims to create a text file of variant sizes."
echo "You should input an integer n to generate 2^n times 1Ko file"
echo ""
echo "For 1Go type 20"
echo "For 2Go type 21"
echo "For 4Go type 22"
echo "For 8Go type 23"

# Check if n is an integer.
while [ 1 = 1 ]; do
  read -p "Please type an integer n : " n
  if [[ $n ]] && [ $n -eq $n ] 2>/dev/null; then
    for ((i = 0; i < $n; i++)); do
      cat data.txt data.txt >temp
      mv temp data.txt
    done
    break
  else
    echo "$n is not an integer or not defined"
    echo "Please, restart again"
    echo ""
  fi
done
