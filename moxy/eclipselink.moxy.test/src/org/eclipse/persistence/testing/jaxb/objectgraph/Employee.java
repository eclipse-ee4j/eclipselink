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
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlNamedAttributeNode;
import org.eclipse.persistence.oxm.annotations.XmlNamedObjectGraph;

@XmlRootElement
@XmlNamedObjectGraph(
        name = "simple",
        attributeNodes = { 
            @XmlNamedAttributeNode(value="contactInfo", subgraph="simple") 
        }
        
    )
public class Employee {
    private List<ContactInfo> contactInfo = new ArrayList<ContactInfo>();
    
    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }
 
    public void setContactInfo(List<ContactInfo> contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public boolean equals(Object obj) {
        Employee e = (Employee)obj;
        return e.getContactInfo().equals(getContactInfo());
    }
}
