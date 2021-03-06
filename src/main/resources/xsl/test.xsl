<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:param name='value'/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/">
        <value>
            <xsl:copy-of select="$value"/>
        </value>
        <data>
            <xsl:value-of select="//*[local-name()='BinarySecurityToken']/text()"/>
        </data>
	</xsl:template>
	
</xsl:stylesheet>
