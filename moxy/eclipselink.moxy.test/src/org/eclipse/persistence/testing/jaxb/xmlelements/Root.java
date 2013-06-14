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
 *     Denise Smith - June 14, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlRootElement(name = "root")
public class Root {
    
    @XmlElements({        
        @XmlElement(name = "string", type = String.class),
        @XmlElement(name = "root", type = Root.class)
    })
    protected List<Object> children;
    
    @XmlAttribute(name = "theName")
    protected String name;

    public boolean equals(Object obj){
    	if(obj instanceof Root){
    		Root compareObj = (Root)obj;
    		return (name.equals(compareObj.name) && (( children==null && compareObj.children==null) || children.equals(compareObj.children)));
    		
    	}
    	return false;
    }
}
