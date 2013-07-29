<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR"
exclude-result-prefixes="ocr">

<xsl:output method="xml"/>

   <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>
   
   
   
      <xsl:template match="ocr:document">
		<TEI xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:apply-templates select="ocr:metadata"/>
			<text>
				<body>
					<xsl:apply-templates select="ocr:page"/>
				</body>
			</text>
		</TEI>
      </xsl:template>
      
      <xsl:template match="ocr:metadata">
      	<teiHeader>
      		<profileDesc>
      			<xsl:apply-templates select="ocr:ocrSoftwareName"/>
      			<langUsage>
      				<xsl:apply-templates select="ocr:languages"/>
      			</langUsage>
      		</profileDesc>
		</teiHeader>
      </xsl:template>
      
      <xsl:template match="ocr:ocrSoftwareName">
      	<creation>
      		<xsl:value-of select="text()"/><xsl:text> </xsl:text><xsl:value-of select="following-sibling::ocr:ocrSoftwareVersion"/>
      	</creation>
      </xsl:template>
      
      <xsl:template match="ocr:languages">
      	<language>
      	<xsl:if test="@langId">
      		<xsl:attribute name="ident">
      			<xsl:value-of select="@langId"/>
      		</xsl:attribute>
      	</xsl:if>
      	<xsl:value-of select="text()"/></language>
      </xsl:template>
      
      <xsl:template match="ocr:page">
           <xsl:apply-templates />
        <milestone n="{@physicalNumber}" type="page"/>
        <pb/>
      </xsl:template>
      
      
      <xsl:template match="ocr:paragraphs">
      	<p>
      		paragraph
      	</p>
      </xsl:template>
   
</xsl:stylesheet>