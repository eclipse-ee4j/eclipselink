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
 *     Matt MacIvor - October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.xmlpath;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="myclass")
public class MyClass {
    public String name;
    
    @XmlPath("email/text()")
    public String email;
    
    @XmlPath("email/@confirmed")
    public boolean confirmed;
    
    @XmlPath("phones/phone[@type='home']")
    public Phone homePhone;
    
    @XmlPath("phones/phone[@type='work']")
    public Phone workPhone;
    
    @XmlPath(".")
    public CanadianAddress address;
    
    public boolean equals(Object obj) {
        MyClass myClass = (MyClass)obj;
        return email.equals(myClass.email) && name.equals(myClass.name)&& confirmed == myClass.confirmed && homePhone.equals(myClass.homePhone) 
                && workPhone.equals(myClass.workPhone) && address.equals(myClass.address); 
    }

}