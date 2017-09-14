<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		 xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="xsd:schema">
		<xsl:copy>
			<xsl:apply-templates select="xsd:import">
				<xsl:sort select="@namespace"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="xsd:attribute">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="xsd:attributeGroup">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="xsd:simpleType">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="xsd:complexType">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="xsd:element">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	
</xsl:stylesheet>
