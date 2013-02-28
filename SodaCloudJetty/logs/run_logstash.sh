#!/bin/sh

echo "Running logstash using the default options"

java -jar logstash-1.1.9-monolithic.jar agent -f logstash.conf
