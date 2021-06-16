/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.qname;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlRootElement
@XmlType(propOrder={"element", "list"})
public class Root {

    private QName attribute;
    private QName element;
    private List<QName> list;

    public Root() {
        list = new ArrayList<QName>();
    }

    @XmlAttribute
    public QName getAttribute() {
        return attribute;
    }

    public void setAttribute(QName qNameAttribute) {
        this.attribute = qNameAttribute;
    }

    public QName getElement() {
        return element;
    }

    public void setElement(QName qNameElement) {
        this.element = qNameElement;
    }

    public List<QName> getList() {
        return list;
    }

    public void setList(List<QName> qNameList) {
        this.list = qNameList;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != getClass()) {
            return false;
        }
        Root test = (Root) obj;
        if(!equals(attribute, test.getAttribute())) {
            return false;
        }
        if(!equals(element, test.getElement())) {
            return false;
        }
        if(list.size() != test.getList().size()) {
            return false;
        }
        for(int x=0; x<list.size(); x++) {
            if(!equals(list.get(x), test.getList().get(x))) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(QName control, QName test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }

}
