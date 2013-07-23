<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR"
exclude-result-prefixes="ocr">

<xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>
   
   
   
      <xsl:template match="ocr:document">
		<TEI xmlns="bla">
			<xsl:apply-templates select="ocr:Metadata"/>
			<text>
				<body>
					<xsl:apply-templates select="ocr:Page"/>
				</body>
			</text>
		</TEI>
      </xsl:template>
      
      <xsl:template match="ocr:Metadata">
      	<teiHeader>
			metadata
		</teiHeader>
      </xsl:template>
      
      <xsl:template match="ocr:Page">
      	Page
           <xsl:apply-templates />
        <pb type="page"/>
      </xsl:template>
      
      
      <xsl:template match="ocr:paragraphs">
      	<p>
      		paragraph
      	</p>
      </xsl:template>
   
</xsl:stylesheet>