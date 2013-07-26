<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR"
exclude-result-prefixes="ocr">

<xsl:output method="xml"/>

   <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>
   
   
   
      <xsl:template match="ocr:document">
		<TEI xmlns="bla" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.loc.gov/standards/alto/ns-v2# http://www.loc.gov/standards/alto/alto-v2.0.xsd">
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
			metadata
		</teiHeader>
      </xsl:template>
      
      <xsl:template match="ocr:page">
      	Page | |
           <xsl:apply-templates />
        <pb type="page"/>
      </xsl:template>
      
      
      <xsl:template match="ocr:paragraphs">
      	<p>
      		paragraph
      	</p>
      </xsl:template>
   
</xsl:stylesheet>