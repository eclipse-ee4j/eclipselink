/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement
@XmlType(propOrder = {"name"})
public class Child {
    public String name;
    
    @XmlInverseReference(mappedBy="children")
    public Parent parent;

    public boolean equals(Object obj) {
        Child child = (Child)obj;
        if(name == null){
            if(child.name!=null){
                return false;
            }            
        }else if(!name.equals(child.name)){
            return false;
        }
        return this.parent != null && child.parent != null;
    }    
}
