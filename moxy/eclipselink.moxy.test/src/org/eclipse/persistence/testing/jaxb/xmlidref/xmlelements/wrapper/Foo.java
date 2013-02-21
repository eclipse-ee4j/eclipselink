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
 *     Denise Smith - February 20, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.wrapper;

import java.util.*;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class Foo {

    @XmlElements({
        @XmlElement(name="Attribute1", type=AttributeImpl.class),
        @XmlElement(name="Attribute2", type=AttributeImpl2.class)
    })
    List<Attribute> attributes = new ArrayList<Attribute>();;

    @XmlElementWrapper(name="AttributeRefs")
    @XmlElements({
      @XmlElement(name="AttributeRef1", type=AttributeImpl.class),
      @XmlElement(name="AttributeRef2", type=AttributeImpl2.class)
    })
    @XmlIDREF
    List<Attribute> attributeRefs = new ArrayList<Attribute>();
    
    @XmlElementWrapper(name="AttributeImplsWrapper")  
    @XmlIDREF
    List<AttributeImpl> attributeImplRefs = new ArrayList<AttributeImpl>();

    public boolean equals(Object obj){
    	if(obj instanceof Foo){
    		Foo compare = (Foo)obj;
    		if(!attributes.equals(compare.attributes)){
    			return false;
    		}
    		if(!attributeRefs.equals(compare.attributeRefs)){
    			return false;
    		}
    		if(!attributeImplRefs.equals(compare.attributeImplRefs)){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
    
}