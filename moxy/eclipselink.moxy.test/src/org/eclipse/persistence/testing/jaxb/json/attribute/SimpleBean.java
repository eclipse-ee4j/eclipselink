/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - September 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public  class SimpleBean {
    @XmlAttribute
    private String attr;

    @XmlAttribute
    private String attr2;
    
    public String elem1;
   
    public String elem2;
    
    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }
    
    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }
    
    public boolean equals(Object obj){
        if(obj instanceof SimpleBean){
            return (attr == null && ((SimpleBean)obj).attr ==null)  || (attr.equals(((SimpleBean)obj).attr)) &&
            (attr2 == null && ((SimpleBean)obj).attr2 ==null)  || (attr2.equals(((SimpleBean)obj).attr2)) &&
            (elem1 == null && ((SimpleBean)obj).elem1 ==null)  || (elem1.equals(((SimpleBean)obj).elem1)) &&
            (elem2 == null && ((SimpleBean)obj).elem2 ==null)  || (elem2.equals(((SimpleBean)obj).elem2));
        }
        return false;
    }
} 