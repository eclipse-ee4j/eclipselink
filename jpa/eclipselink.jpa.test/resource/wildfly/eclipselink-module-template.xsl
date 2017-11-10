<?xml version="1.0" encoding="UTF-8"?>
<!--/*******************************************************************************
* Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Lukas - initial API and implementation
#******************************************************************************/-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="urn:jboss:module:1.5">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="4" xmlns:xalan="http://xml.apache.org/xalan" />
    <xsl:param name="separator" select="':'"/>
    <!-- '$separator' delimited list of resources/jars -->
    <xsl:param name="resources"/>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[local-name() = 'resources']">
        <xsl:element name="resources">
            <xsl:for-each select="*[local-name() = 'resource-root']">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:call-template name="tokenize">
                <xsl:with-param name="text" select="normalize-space($resources)"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <xsl:template name="tokenize">
        <xsl:param name="text"/>
        <xsl:param name="s" select="$separator"/>
        <xsl:if test="$text != ''">
            <xsl:choose>
                <xsl:when test="not(contains($text, $s))">
                    <xsl:if test="not(//*[@path=$text])">
                        <xsl:element name="resource-root">
                            <xsl:attribute name="path">
                                <xsl:value-of select="normalize-space($text)"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="norm" select="normalize-space(substring-before($text, $s))"/>
                    <xsl:if test="not(//*[@path=$norm])">
                        <xsl:element name="resource-root">
                            <xsl:attribute name="path">
                                <xsl:value-of select="$norm"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:if>
                    <xsl:call-template name="tokenize">
                        <xsl:with-param name="text" select="normalize-space(substring-after($text, $s))"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>