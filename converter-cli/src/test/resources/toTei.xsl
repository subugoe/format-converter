<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
exclude-result-prefixes="ocr xsi">

<xsl:output method="xml" indent="yes"/>

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
      
      <xsl:template match="ocr:pageItems[@xsi:type='Image']">
      	<xsl:variable name="pageNumber" select="ancestor::ocr:page/@physicalNumber"/>
      	<figure>
      	<xsl:attribute name="id">ID<xsl:value-of select="$pageNumber"/>_<xsl:value-of select="count(preceding::ocr:pageItems[@xsi:type='Image'])+1"/></xsl:attribute>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	</figure>
      </xsl:template>
  
      <xsl:template match="ocr:pageItems[@xsi:type='Table']">
      	<table>
      	<xsl:attribute name="rows"><xsl:value-of select="count(ocr:rows)"/></xsl:attribute>
      	<xsl:attribute name="cols"><xsl:value-of select="count(ocr:rows[1]/ocr:cells)"/></xsl:attribute>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	<xsl:apply-templates/>
      	</table>
      </xsl:template>
  
      <xsl:template match="ocr:rows">
      	<row>
      		<xsl:apply-templates/>
      	</row>
      </xsl:template>

      <xsl:template match="ocr:cells">
      	<cell>
      		<xsl:apply-templates/>
      	</cell>
      </xsl:template>

      <xsl:template match="ocr:paragraphs">
      	<xsl:variable name="pageNumber" select="ancestor::ocr:page/@physicalNumber"/>
      	<p>
      		<xsl:attribute name="id">ID<xsl:value-of select="$pageNumber"/>_<xsl:value-of select="count(preceding::ocr:paragraphs)+1"/></xsl:attribute>
      		<xsl:apply-templates />
      	</p>
      </xsl:template>
      
      <xsl:template match="ocr:lines">
      	<xsl:apply-templates/>
      	<lb/>
      </xsl:template>
      
      <xsl:template match="ocr:lineItems[@xsi:type='Word']">
      	<w>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	<xsl:apply-templates/></w>
      </xsl:template>
      
      <xsl:template name="printCoordinates">
      <xsl:param name="contextNode"/>
      	<xsl:if test="$contextNode/@left">
      		<xsl:attribute name="function">
      			<xsl:value-of select="$contextNode/@left"/><xsl:text>,</xsl:text>
      			<xsl:value-of select="$contextNode/@top"/><xsl:text>,</xsl:text>
      			<xsl:value-of select="$contextNode/@right"/><xsl:text>,</xsl:text>
      			<xsl:value-of select="$contextNode/@bottom"/>
      		</xsl:attribute>
      	</xsl:if>
      </xsl:template>
      
      <xsl:template match="ocr:lineItems[@xsi:type='NonWord']">
     	<pc>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	<xsl:apply-templates/></pc>
      </xsl:template>
      
      <xsl:template match="ocr:characters">
      	<xsl:value-of select="text()"/>      	
      </xsl:template>
   
</xsl:stylesheet>