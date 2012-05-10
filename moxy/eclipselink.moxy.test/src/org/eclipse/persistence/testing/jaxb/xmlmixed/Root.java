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
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmixed;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {
    @XmlAttribute
    private String attr;
    
    @XmlMixed
    private ArrayList<Object> objects;

    @XmlElement
    private String elem;
    

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String string) {
        this.attr = string;
    }
    
    public String getElem() {
        return elem;
    }

    public void setElem(String elem) {
        this.elem = elem;
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof Root)) {
            return false;
        }
        
        Root root = (Root)obj;
        return root.getAttr().equals(getAttr()) && root.getElem().equals(getElem()) && root.getObjects().equals(getObjects());
    }
    
}
