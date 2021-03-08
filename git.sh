#!/bin/sh

# Add the new files
git add .

# Make a commit
read -rp "The message to commit : " message
git commit -m "#$message"

# Push to remote repository
git push

# Update repository on n7 computers
ssh hmoudden@ader.enseeiht.fr 'cd nosave/hadoop && git pull'; exec bash
