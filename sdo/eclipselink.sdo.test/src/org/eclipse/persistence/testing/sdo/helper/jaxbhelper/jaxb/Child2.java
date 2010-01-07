/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="")
public class Child2 {

    private int id;
    private Child1 child1;

    public Child2() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Child1 getChild1() {
        return child1;
    }

    public void setChild1(Child1 child1) {
        this.child1 = child1;
    }

}
