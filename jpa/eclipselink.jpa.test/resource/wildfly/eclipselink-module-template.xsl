<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0 which is available at
    http://www.eclipse.org/legal/epl-2.0.

    SPDX-License-Identifier: EPL-2.0

-->

<!--
 Contributors:
     Lukas - initial API and implementation
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="urn:jboss:module:1.5">
    <xsl:output method="xml" indent="yes" xalan:indent-amount="4" xmlns:xalan="http://xml.apache.org/xalan" />
    <xsl:param name="separator" select="':'"/>
    <!-- '$separator' delimited list of resources/jars -->
    <xsl:param name="resources" select="''"/>
    <xsl:param name="dependencies" select="''"/>

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
                <xsl:with-param name="elName" select="'resource-root'"/>
                <xsl:with-param name="attrName" select="'path'"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*[local-name() = 'dependencies']">
        <xsl:element name="dependencies">
            <xsl:for-each select="*[local-name() = 'module']">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:call-template name="tokenize">
                <xsl:with-param name="text" select="normalize-space($dependencies)"/>
                <xsl:with-param name="elName" select="'module'"/>
                <xsl:with-param name="attrName" select="'name'"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <xsl:template name="tokenize">
        <xsl:param name="text"/>
        <xsl:param name="elName"/>
        <xsl:param name="attrName"/>
        <xsl:param name="s" select="$separator"/>
        <xsl:if test="$text != ''">
            <xsl:choose>
                <xsl:when test="not(contains($text, $s))">
                    <xsl:if test="not(//*[@path=$text])">
                        <xsl:element name="{$elName}">
                            <xsl:attribute name="{$attrName}">
                                <xsl:value-of select="normalize-space($text)"/>
                            </xsl:attribute>
                            <xsl:if test="$elName = 'module'">
                                <xsl:attribute name="export">true</xsl:attribute>
                            </xsl:if>
                        </xsl:element>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="norm" select="normalize-space(substring-before($text, $s))"/>
                    <xsl:if test="not(//*[@path=$norm])">
                        <xsl:element name="{$elName}">
                            <xsl:attribute name="{$attrName}">
                                <xsl:value-of select="$norm"/>
                            </xsl:attribute>
                            <xsl:if test="$elName = 'module'">
                                <xsl:attribute name="export">true</xsl:attribute>
                            </xsl:if>
                        </xsl:element>
                    </xsl:if>
                    <xsl:call-template name="tokenize">
                        <xsl:with-param name="text" select="normalize-space(substring-after($text, $s))"/>
                        <xsl:with-param name="elName" select="$elName"/>
                        <xsl:with-param name="attrName" select="$attrName"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
