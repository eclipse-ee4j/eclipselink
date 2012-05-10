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
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.javadoc.xmlschematype;

import javax.xml.bind.annotation.*;

import java.util.Calendar; 

@XmlRootElement
public class USPrice {
    
    @XmlElement(name="start-date")
    @XmlSchemaType(name = "date")
    public Calendar date;
    
    public boolean equals(Object o) {
        if(!(o instanceof USPrice) || o == null) {
            return false;
        } else {
            return ((USPrice)o).date.equals(this.date);
        }
    }
    
    public String toString() {
        return "USPRICE(" + date + ")";
    }
}
