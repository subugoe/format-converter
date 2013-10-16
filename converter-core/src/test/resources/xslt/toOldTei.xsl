<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:ocr="http://www.sub.uni-goettingen.de/ent/OCR" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
exclude-result-prefixes="ocr xsi">

<xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>
   
   
   
      <xsl:template match="ocr:document">
		<TEI.2>
			<teiHeader/>
			<text>
				<body>
					<xsl:apply-templates select="ocr:page"/>
				</body>
			</text>
		</TEI.2>
      </xsl:template>
      
      <xsl:template match="ocr:page">
           <xsl:apply-templates />
        <pb/>
        <milestone n="{@physicalNumber}" type="page"/>
      </xsl:template>
      
      <xsl:template match="ocr:pageItems[@xsi:type!='TextBlock']">
      	<xsl:variable name="pageNumber" select="ancestor::ocr:page/@physicalNumber"/>
      	<figure>
      	<xsl:attribute name="id">ID<xsl:value-of select="$pageNumber"/>_<xsl:value-of select="count(preceding::ocr:pageItems[@xsi:type='Image'])+1"/></xsl:attribute>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	</figure>
      </xsl:template>
  
      <xsl:template match="ocr:paragraphs">
      	<xsl:variable name="pageNumber" select="ancestor::ocr:page/@physicalNumber"/>
      	<p>
      		<xsl:attribute name="id">ID<xsl:value-of select="$pageNumber"/>_<xsl:value-of select="count(preceding::ocr:paragraphs)+1"/></xsl:attribute>
      		<xsl:apply-templates />
      	</p>
      </xsl:template>
      
      <xsl:template match="ocr:lines[ocr:lineItems[last()] = '¬']">
      	<xsl:apply-templates select="ocr:lineItems[not(preceding::ocr:lineItems) or preceding::ocr:lineItems[1] != '¬' and . != '¬']"/>
      </xsl:template>
      
      <xsl:template match="ocr:lines[not(ocr:lineItems) or ocr:lineItems[last()]!='¬']">
      	<xsl:apply-templates select="ocr:lineItems[not(preceding::ocr:lineItems) or preceding::ocr:lineItems[1] != '¬']"/>
      	<xsl:text> </xsl:text>
      </xsl:template>
      
      <xsl:template match="ocr:lineItems[@xsi:type='Word' and following::ocr:lineItems[1]='¬']">
      <seq>
      	<xsl:call-template name="wordSegment">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	<xsl:text> </xsl:text>
      	<xsl:call-template name="wordSegment">
      		<xsl:with-param name="contextNode" select="following::ocr:lineItems[2]"/>
      	</xsl:call-template>
      </seq>
      </xsl:template>
      
      <xsl:template match="ocr:lineItems[@xsi:type='Word' and (following::ocr:lineItems[1]!='¬' or not(following::ocr:lineItems))]">
      	<w>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="."/>
      	</xsl:call-template>
      	<xsl:apply-templates/></w>
      </xsl:template>
      
      <xsl:template name="wordSegment">
      	<xsl:param name="contextNode"/>
      	<w>
      	<xsl:call-template name="printCoordinates">
      		<xsl:with-param name="contextNode" select="$contextNode"/>
      	</xsl:call-template>
      	<xsl:apply-templates select="$contextNode/*"/>
      	</w>
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
      	<xsl:choose>
      		<xsl:when test=". = ' '">
      			<xsl:text> </xsl:text>
      		</xsl:when>
      		<xsl:otherwise>
		     	<w>
		      	<xsl:call-template name="printCoordinates">
		      		<xsl:with-param name="contextNode" select="."/>
		      	</xsl:call-template>
		      	<xsl:apply-templates/>
		      	</w>
      		</xsl:otherwise>
      	</xsl:choose>
      </xsl:template>
         
</xsl:stylesheet>