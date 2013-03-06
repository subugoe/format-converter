<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:TEI="http://www.tei-c.org/ns/1.0"
    xmlns:abbyy="http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="xd xsi abbyy" version="1.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Dec 9, 2011</xd:p>
            <xd:p><xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <!-- 
    <xsl:strip-space elements="*"/>
    <xsl:include href="entityConverter.xsl"/>
    -->
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="abbyy:document">
        <TEI:TEI xmlns="http://www.tei-c.org/ns/1.0">
            <TEI:teiHeader>
                <TEI:fileDesc>
                    <TEI:titleStmt>
                        <TEI:title>Abbyy Finereader Document</TEI:title>
                    </TEI:titleStmt>
                    <TEI:publicationStmt>
                        <TEI:p/>
                    </TEI:publicationStmt>
                    <TEI:sourceDesc>
                        <TEI:p/>
                    </TEI:sourceDesc>
                </TEI:fileDesc>
            </TEI:teiHeader>
            <TEI:text>
                <TEI:body>
                    <xsl:apply-templates/>
                </TEI:body>
            </TEI:text>
        </TEI:TEI>
    </xsl:template>
    <xsl:template match="abbyy:charParams">
        <xsl:value-of select="./text()"/>
    </xsl:template>
    <xsl:template match="abbyy:line">
        <xsl:apply-templates/>
        <TEI:lb/>
    </xsl:template>
    <xsl:template match="abbyy:page">
        <xsl:apply-templates/>
        <TEI:pb/>
    </xsl:template>
    <xsl:template match="abbyy:par">
        <TEI:p>
            <xsl:apply-templates/>
        </TEI:p>
    </xsl:template>
    <xsl:template match="abbyy:block">
        <TEI:div>
            <xsl:apply-templates/>
        </TEI:div>
    </xsl:template>
    <xsl:template match="abbyy:formatting|abbyy:text|abbyy:region|abbyy:rect">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="abbyy:documentData"/>
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="comment()|processing-instruction()">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>
