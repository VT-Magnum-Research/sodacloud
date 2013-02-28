#!/bin/sh

if [ -z "$1" ]
then
	echo "Usage: $0 <logstash.conf>"
	exit
fi

echo "Using configuration at $1"
echo "   If errors occur, check that $1 points to the absolute path of the log files on *your* filesystem"
echo "Starting logstash monolithic...this could take a while" 
java -jar logstash-1.1.10.dev-monolithic.jar agent -f $1 -- web --backend elasticsearch:///?local
#java -jar logstash-1.1.9-monolithic.jar agent -f $1
