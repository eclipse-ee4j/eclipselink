/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelements;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlRootElement(name="employee-data")
public class XmlElementsListOfElement 
{
 
    @XmlElements({@XmlElement(name="A", type=Integer.class), @XmlElement(name="B", type=Float.class)})
    public List items;
/*
    public String toString()
    {
        return "EMPLOYEE: " + id + "\n choice=" + choice;
    }
*/
    public boolean equals(Object object) {
        XmlElementsListOfElement  example = ((XmlElementsListOfElement )object);
        return example.items.size() == this.items.size() && example.items.get(0).equals(this.items.get(0));        
    }
}

