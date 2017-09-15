#!/usr/bin/env bash

MVN_DIR=~/projects/github/xsl-tool

gen_schemas_xml() {
 	echo "Directory root: "$@
 	echo "Generating file: schemas.xml"
 	mvn clean spring-boot:run -Drun.arguments="schemas,--src=$@" -f="$MVN_DIR"
}

gen_flatten_wsdl() {
 	echo "WSDL File: "$@
 	echo "Generating file: $@_Full.wsdl"
 	mvn clean spring-boot:run -Drun.arguments="flatten,--src=$@" -f="$MVN_DIR"
}

