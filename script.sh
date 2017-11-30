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

xml_tools() {
 	echo "Arguments: "
 	echo $(cat config.json)
 	SPRING_APPLICATION_JSON=$(cat config.json) mvn test -Dtest=ConfigTest
}

publish() {
 	echo "JSON Config: "$@
    mvn spring-boot:run -f="$MVN_DIR" -Drun.arguments="--task=$1" -Ddata.config="$2" -Drun.jvmArguments="-Xmx1024m"
}


