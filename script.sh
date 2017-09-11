#!/usr/bin/env bash

MVN_DIR=~/projects/github/xsl-tool

gen_schemas_xml() {
 	echo "Directory root: "$@
 	echo "Generating file: schemas.xml"
 	mvn clean spring-boot:run -Drun.arguments="schemas,--src=$@" -f="$MVN_DIR"
}

gen_flatten_wsdl() {
	WSDL_PATH="/Users/dhval/projects/jnet/dp/PDTD_SSP_v1.0.7/schema/SIP WS 1.1/PennDOTDriver.wsdl"
	xalan -o PennDOTDriver-Full.wsdl $WSDL_PATH flatten-wsdl.xsl 
	xalan -o PennDOTDriver-UI.wsdl PennDOTDriver-Full.wsdl RemoveSubstitutionGroups.xslt
	xalan -o PennDOTDriver-UI.wsdl PennDOTDriver-Full.wsdl RemoveDummyAttributes.xslt
	xalan -o PennDOTDriver-WM-Client.wsdl PennDOTDriver-Full.wsdl make-wm-client-wsdl.xslt
	xalan -o PennDOTDriver-WM.wsdl PennDOTDriver-Full.wsdl make-wm-wsdl.xslt  
}

