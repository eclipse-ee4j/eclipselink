/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This class represents a XMLConversionPair. It is used to 
 * persist a collection of XMLConversionPair to Deployment XML.  An 
 * XMLConversionPair has a QName representing a schema type and a String 
 * representing a class name.</p>
 */

public class XMLConversionPair {
    protected QName xmlType;
    protected String javaType;

    public XMLConversionPair() {
    }

    public XMLConversionPair(QName xmlType, String javaType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public QName getXmlType() {
        return xmlType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaType() {
        return javaType;
    }
}
