<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
exclude-result-prefixes="ocr xsi">

	<xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>
   
      <xsl:template match="ocr:document">
		<root>
			<!-- no metadata -->
			<xsl:apply-templates select="ocr:page"/>
		</root>
      </xsl:template>
   
      <xsl:template match="ocr:page">
		<child>
			<xsl:value-of select="upper-case('some text')"/>
		</child>
      </xsl:template>
   
   
</xsl:stylesheet>