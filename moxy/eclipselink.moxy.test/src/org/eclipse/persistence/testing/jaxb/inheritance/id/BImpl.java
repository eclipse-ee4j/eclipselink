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
public class BImpl extends Base implements B {

    public BImpl() {
    }

    public BImpl(String id) {
        super.setId(id);
    }

    @XmlIDREF
    @XmlElement(type = AImpl.class)
    private A a;


    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }
    
    public boolean equals(Object obj){
        return (super.equals(obj));
    }     

}
