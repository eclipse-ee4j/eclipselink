/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr.sxf;

// Javse imports

// Java extension imports

// EclipseLink imports

public class SimpleXMLFormat {

    public static final String DEFAULT_SIMPLE_XML_FORMAT_TAG =
      "simple-xml-format";
    public static final String DEFAULT_SIMPLE_XML_TAG =
      "simple-xml";

    private String simpleXMLFormatTag;
    private String xmlTag;

    public String getSimpleXMLFormatTag() {
        return simpleXMLFormatTag;
    }

    public void setSimpleXMLFormatTag(String simpleXMLFormatTag) {
        this.simpleXMLFormatTag = simpleXMLFormatTag;
    }

    public String getXMLTag() {
        return xmlTag;
    }

    public void setXMLTag(String xmlTag) {
        this.xmlTag = xmlTag;
    }
}
