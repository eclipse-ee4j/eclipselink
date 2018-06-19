<?xml version="1.0"?>
<!--

    Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0 which is available at
    http://www.eclipse.org/legal/epl-2.0.

    SPDX-License-Identifier: EPL-2.0

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="employee">
        <e>
            <fn>
                <xsl:value-of select="first-name"/>
            </fn>
            <ln>
                <xsl:value-of select="last-name"/>
            </ln>
        </e>
    </xsl:template>

</xsl:stylesheet>
