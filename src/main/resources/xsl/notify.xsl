<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <!-- These variables are passed as input parameters -->
    <xsl:param name='Agency'/>
    <xsl:param name='Target'/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/">
        <SOAP-ENV:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
                           xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
            <SOAP-ENV:Header>
                <wsa:To xmlns:wsa="http://www.w3.org/2005/08/addressing">
                    <xsl:value-of select="$Target"/>
                </wsa:To>
                <wsa:Action xmlns:wsa="http://www.w3.org/2005/08/addressing">
                    http://jnet.state.pa.us/service/jnet/EventMessageConsumer/1/EventMessageConsumerInterface/NotifyRequest
                </wsa:Action>
                <wsa:MessageID xmlns:wsa="http://www.w3.org/2005/08/addressing">
                    d426-0377-d272-ce9a-13c7-90c5e60
                </wsa:MessageID>
                <wsa:ReplyTo xmlns:wsa="http://www.w3.org/2005/08/addressing">
                    <wsa:Address>
                        http://www.w3.org/2005/08/addressing/anonymous
                    </wsa:Address>
                </wsa:ReplyTo>
                <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                    <saml2:Assertion ID="ff642e32-21fb-4e89-b269-3e963a8ba567" IssueInstant="2017-08-25T15:11:40-04:00"
                                     Version="2.0" xmlns:saml2="urn:oasis:names:tc:saml2:2.0:assertion">
                        <saml2:AttributeStatement>
                            <saml2:Attribute Name="gfipm:2.0:entity:OwnerAgencyName" NameFormat="urn:oasis:names:tc:saml2:2.0:attrname-format:uri">
                                <saml2:AttributeValue>
                                    <xsl:value-of select="$Agency"/>
                                </saml2:AttributeValue>
                            </saml2:Attribute>
                        </saml2:AttributeStatement>
                    </saml2:Assertion>
                </wsse:Security>
            </SOAP-ENV:Header>
            <SOAP-ENV:Body>
                <xsl:copy-of select="//*[local-name()='Notify']"/>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
	</xsl:template>
	
	
	
</xsl:stylesheet>
