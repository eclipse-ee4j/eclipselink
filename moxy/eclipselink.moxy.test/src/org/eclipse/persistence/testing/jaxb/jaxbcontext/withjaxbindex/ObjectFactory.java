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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.xmlmodel.XmlEnumValue;

@XmlRegistry
public class ObjectFactory {
	   
    public ClassA createClassA() {
        return new ClassA();
    }
    
    public ClassAWithElementRef createClassAWithElementRef() {
        return new ClassAWithElementRef();
    }
    
    public ClassB createClassB() {
        return new ClassB();
    }
	    
    @XmlElementDecl(name = "a")
    public JAXBElement<String> createFooA(String i) {
        return new JAXBElement(new QName("a"), String.class, i);
    }
   
}
