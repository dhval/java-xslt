<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:j="http://www.it.ojp.gov/jxdm/3.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <!-- These variables are passed as input parameters -->
    <xsl:param name='MessageID'/>
    <xsl:param name='Action'/>
    <xsl:param name='CaseTypeText'/>
    <xsl:param name='CourtTypeCode'/>
    <xsl:param name='DocketNumberText'/>
    <xsl:param name='JurisdictionCode'/>
    <xsl:param name='CountyCode'/>
    <xsl:param name='MessageTimestampDateTime'/>

    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="j:ID[local-name(..)='CaseDocketID']">
              <xsl:copy>
                  <xsl:value-of select="$DocketNumberText"/>
              </xsl:copy>
    </xsl:template>

    <xsl:template match="/">
		<Envelope xmlns="http://www.w3.org/2003/05/soap-envelope"
				  xmlns:aopc="http://jnet.state.pa.us/jxdm/aopc"
				  xmlns:wsa="http://www.w3.org/2005/08/addressing"
                  xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xmlns:ref="http://jnet.state.pa.us/jxdm/aopc/wsa-ref">
			<Header>
				<wsa:To>jms:topic://jnet.aopc.CourtNotice</wsa:To>
				<wsa:MessageID>
                    <xsl:value-of select="$MessageID"/>
                </wsa:MessageID>
				<wsa:Action>
                    <xsl:value-of select="$Action"/>
                </wsa:Action>
				<wsa:ReferenceParameters>
					<ref:CaseTypeText>
                        <xsl:value-of select="$CaseTypeText"/>
                    </ref:CaseTypeText>
					<ref:CourtTypeCode>
                        <xsl:value-of select="$CourtTypeCode"/>
                    </ref:CourtTypeCode>
					<ref:CountyCode>
                        <xsl:value-of select="$CountyCode"/>
                    </ref:CountyCode>
					<ref:JurisdictionCode>
                        <xsl:value-of select="$JurisdictionCode"/>
                    </ref:JurisdictionCode>
					<ref:DocketTypeText>CRIMINAL</ref:DocketTypeText>
					<ref:DocketNumberText>
                        <xsl:value-of select="$DocketNumberText"/>
                    </ref:DocketNumberText>
					<ref:ServiceTypeText>MESSAGE-XML</ref:ServiceTypeText>
					<ref:ActionTypeText>CRIMINAL COMPLAINT FILED</ref:ActionTypeText>
					<ref:MessageTimestampDateTime>
                        <xsl:value-of select="$MessageTimestampDateTime"/>
                    </ref:MessageTimestampDateTime></wsa:ReferenceParameters>
			</Header>
			<Body>
                <xsl:apply-templates select="//*[local-name()='Message']/*[1]"/>
			</Body>
		</Envelope>
	</xsl:template>
	
	
	
</xsl:stylesheet>
