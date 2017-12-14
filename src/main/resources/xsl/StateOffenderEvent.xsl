<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <!-- These variables are passed as input parameters -->
    <xsl:param name='Agency'/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/">
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" xmlns:u="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
            <s:Header>
                <o:Security xmlns:o="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                    <saml2:Assertion Version="2.0" xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion">
                        <saml2:Issuer/>
                        <saml2:Subject>
                            <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">DOC</saml2:NameID>
                            <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:sender-vouches"/>
                        </saml2:Subject>
                        <saml2:AttributeStatement>
                            <saml2:Attribute Name="gfipm:2.0:entity:OwnerAgencyName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                                <saml2:AttributeValue>
                                    <xsl:value-of select="$Agency"/>
                                </saml2:AttributeValue>
                            </saml2:Attribute>
                            <saml2:Attribute Name="gfipm:2.0:user:AssignmentAgencyName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                                <saml2:AttributeValue>
                                    <xsl:value-of select="$Agency"/>
                                </saml2:AttributeValue>
                            </saml2:Attribute>
                        </saml2:AttributeStatement>
                    </saml2:Assertion>
                </o:Security>
            </s:Header>
            <s:Body u:Id="_1" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <Notify xmlns="http://jnet.state.pa.us/message/jnet/EventMessageConsumer/1">
                    <EventMessage xmlns="http://www.jnet.state.pa.us/niem/JNET/jnet-core/1">
                        <DocumentCategoryText xsi:nil="true" xmlns="http://niem.gov/niem/niem-core/2.0"/>
                        <DocumentCreationDate xsi:nil="true" xmlns="http://niem.gov/niem/niem-core/2.0"/>
                        <DocumentEffectiveDate xsi:nil="true" xmlns="http://niem.gov/niem/niem-core/2.0"/>
                        <DocumentIdentification xmlns="http://niem.gov/niem/niem-core/2.0">
                            <IdentificationID>test-ae076b42-1e43-4158-8ef0-01a8a05cd848</IdentificationID>
                        </DocumentIdentification>
                        <DocumentPostDate xmlns="http://niem.gov/niem/niem-core/2.0">
                            <DateTime>2017-12-10T19:00:48.8480238-05:00</DateTime>
                        </DocumentPostDate>
                        <DocumentSource xsi:nil="true" xmlns="http://niem.gov/niem/niem-core/2.0"/>
                        <DocumentSubjectText xmlns="http://niem.gov/niem/niem-core/2.0">StateOffenderEvent</DocumentSubjectText>
                        <Message>
                            <xsl:copy-of select="//*[local-name()='OffenderEvent']"/>
                        </Message>
                    </EventMessage>
                </Notify>
            </s:Body>
        </s:Envelope>
    </xsl:template>
	
	
	
</xsl:stylesheet>
