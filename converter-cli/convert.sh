#!/bin/bash

for abbyyfile in `ls $1`
do
  java -jar converter-cli-0.0.1-SNAPSHOT.jar -informat abbyyxml -outformat tei -infile "$1/$abbyyfile" -outfile "$2/$abbyyfile.tei.xml"
done
