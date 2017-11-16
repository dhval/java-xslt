<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:j="http://www.it.ojp.gov/jxdm/3.0">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <!-- These variables are passed as input parameters -->
    <xsl:param name='DOB'/>
    <xsl:param name='FirstName'/>
    <xsl:param name='LastName'/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="main">
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
                           xmlns:jnet-m="http://www.jnet.state.pa.us/niem/jnet/metadata/1"
                           xmlns:jc="http://www.jnet.state.pa.us/niem/JNET/jnet-core/1"
                           xmlns:nc="http://niem.gov/niem/niem-core/2.0"
                           xmlns:m3="http://niem.gov/niem/structures/2.0"
                           xmlns:j="http://niem.gov/niem/domains/jxdm/4.0">
            <SOAP-ENV:Body>
                <m:SendLEMSRequest xmlns:m="http://jnet.state.pa.us/message/jnet/LEMSRequestProxy/1">
                    <jnet-m:RequestMetadata>
                        <jnet-m:UserDefinedTrackingID>MFI0000007</jnet-m:UserDefinedTrackingID>
                        <jnet-m:RequestAgencyORIID>PA022035Y</jnet-m:RequestAgencyORIID>
                        <jnet-m:RequestAttentionName>Dudley Dorite</jnet-m:RequestAttentionName>
                        <jnet-m:RequestUserID>H02248185</jnet-m:RequestUserID>
                        <jnet-m:RequestDataSourceID>CWQ</jnet-m:RequestDataSourceID>
                        <jnet-m:RequestDestinationID>PA</jnet-m:RequestDestinationID>
                    </jnet-m:RequestMetadata>
                    <jc:PersonSearchCriteria>
                        <nc:PersonName>
                            <nc:PersonGivenName>
                                <xsl:value-of select="$FirstName"/>
                            </nc:PersonGivenName>
                            <nc:PersonSurName>
                                <xsl:value-of select="$LastName"/>
                            </nc:PersonSurName>
                        </nc:PersonName>
                        <nc:PersonBirthDate>
                            <nc:Date>
                                <xsl:value-of select="$DOB"/>
                            </nc:Date>
                        </nc:PersonBirthDate>
                    </jc:PersonSearchCriteria>
                </m:SendLEMSRequest>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
    </xsl:template>


</xsl:stylesheet>
