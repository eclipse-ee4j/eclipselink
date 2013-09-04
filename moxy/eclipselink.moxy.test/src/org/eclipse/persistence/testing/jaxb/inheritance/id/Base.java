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
 *     Denise Smith - August 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.inheritance.id;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Base {

    @XmlAttribute
    @XmlID
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public boolean equals(Object obj){
        if(obj instanceof Base){
            return (id == null && ((Base)obj).id == null) || (id !=null && id.equals(((Base)obj).id));
        }
        return false;
    }
}