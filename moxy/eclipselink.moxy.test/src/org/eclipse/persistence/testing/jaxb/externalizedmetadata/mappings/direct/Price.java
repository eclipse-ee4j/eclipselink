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
 * dmccann - February 17/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct;

public class Price {
    public java.math.BigDecimal price;
    public String currency;
    
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @javax.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;
       
    java.math.BigDecimal getPrice() { 
        wasGetCalled = true;
        return price; 
    }
    
    void setPrice(java.math.BigDecimal price) {
        wasSetCalled = true;
        this.price = price; 
    }

    
    public boolean equals(Object obj) {
        Price pObj;
        try {
            pObj = (Price) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        
        if (price == null) {
            if (pObj.price != null) {
                return false;
            }
            return this.currency.equals(pObj.currency);
        } else {
            if (pObj.price == null) {
                return false;
            }
        }
        return this.currency.equals(pObj.currency) && this.price.equals(pObj.price);
    }
}