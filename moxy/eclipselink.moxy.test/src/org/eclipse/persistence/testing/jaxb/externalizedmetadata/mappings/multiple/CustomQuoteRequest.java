/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 22/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.oxm.annotations.XmlProperties;
import org.eclipse.persistence.oxm.annotations.XmlProperty;

public class CustomQuoteRequest {
    public String requestId;
    
    // the annotations will be overridden by the first occurrance 
    // of JavaAttribute 'currencyPairCode' in XML
    @XmlElement
    @XmlPath("QuoteReq/@Sym")
    @XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple.DateAdapter.class)
    @XmlProperties({
        @XmlProperty(name="5", value="Q"),
        @XmlProperty(name="6", value="s6", valueType=java.lang.String.class)
    })
    public String currencyPairCode;
    
    // the annotations will be overridden by the first occurrance 
    // of JavaAttribute 'date' in XML
    @XmlElement(name="OrderDatez")
    @XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple.LegAdapter.class)
    public Calendar date;
    
    public boolean equals(Object obj) {
        CustomQuoteRequest cObj;
        try {
            cObj = (CustomQuoteRequest) obj;
        } catch (ClassCastException e) {
            return false;
        }
        return requestId.equals(cObj.requestId) && currencyPairCode.equals(cObj.currencyPairCode);
    }
    
    public static class MyCQRInnerClass {
        public String foo;
        public MyCQRInnerClass() {};
    }
}
