<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
			xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
			 xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
	<xsl:output method="xml" indent="yes"/>
	<xsl:strip-space elements="*"/>
	
	<xsl:variable name="schemas" select="document('../../../../schemas.xml')/schemas"/>
	
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="xsd:complexType[@name='DateType']">
		<xsd:complexType name="DateType">
			<xsd:complexContent>
				<xsd:extension base="s:ComplexObjectType">
					<xsd:sequence>
						<xsd:element ref="nc:Date" minOccurs="0" maxOccurs="1"/>
					</xsd:sequence>
				</xsd:extension>
			</xsd:complexContent>
		</xsd:complexType>
		<xsd:complexType name="DateTimeType">
			<xsd:complexContent>
				<xsd:extension base="s:ComplexObjectType">
					<xsd:sequence>
						<xsd:element ref="nc:DateTime" minOccurs="0" maxOccurs="1"/>
					</xsd:sequence>
				</xsd:extension>
			</xsd:complexContent>
		</xsd:complexType>
	</xsl:template>
	
	<xsl:template match="xsd:element[@name='BinaryCaptureDate']">
		<xsd:element name="BinaryCaptureDate" type="nc:DateTimeType"/>
	</xsl:template>
	
	<xsl:template match="wsdl:types">
		<xsl:copy>
			<xsl:for-each select="$schemas/file">
				<xsl:apply-templates select="document(.)"/>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="comment()" priority="1"/>
	<xsl:template match="@schemaLocation"/>
	<!--<xsl:template match="xsd:import"/>-->
	<xsl:template match="@nillable"/>
	<xsl:template match="xsd:annotation"/>
	<xsl:template match="xsd:attributeGroup/xsd:attribute"/>	
	
	<!-- Add '**' to state code list -->
	<xsl:template match="xsd:simpleType[@name='USStateCodeSimpleType']/xsd:restriction/xsd:enumeration[1]">
		<xsl:copy>
			<xsl:attribute name="value">**</xsl:attribute>
		</xsl:copy>
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
