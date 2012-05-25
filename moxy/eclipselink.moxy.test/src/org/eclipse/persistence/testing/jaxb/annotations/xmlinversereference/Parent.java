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
 * Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Parent {
    
    @XmlElementRef
    public List<Child> children;
    
    public boolean equals(Object obj) {
        Parent parent = (Parent)obj;
        if(children.size() != parent.children.size()) {
            return false;
        }
        for(int i = 0; i < children.size(); i++) {
            Child child1 = children.get(i);
            Child child2 = parent.children.get(i);
            
            if(!(child1.equals(child2))) {
                return false;
            }
        }
        return true;
    }

}
