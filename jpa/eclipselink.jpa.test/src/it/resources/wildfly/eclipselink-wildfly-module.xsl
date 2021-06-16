<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License v. 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0.

    This Source Code may also be made available under the following Secondary
    Licenses when the conditions for such availability set forth in the
    Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
    version 2 with the GNU Classpath Exception, which is available at
    https://www.gnu.org/software/classpath/license.html.

    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0

-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output omit-xml-declaration="no" indent="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <!--<xsl:template match="resources">-->
    <xsl:template match="*[local-name(.)='resources']">
        <xsl:element name="{local-name()}">
            <!--<xsl:apply-templates select="*[1]"/>-->
            <xsl:apply-templates select="*[@path = 'jipijapa-eclipselink-${jee.client.version}.jar']"/>
            <xsl:copy-of select="document('elResources.xml')/resources/*"/>
        </xsl:element>
    </xsl:template>


</xsl:stylesheet>
