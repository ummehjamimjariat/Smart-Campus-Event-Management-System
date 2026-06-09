#!/bin/bash
echo "Smart Campus Event Management System"
echo "====================================="
find src -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt && echo "Compiled OK" && java -cp out com.smartcampus.Main
