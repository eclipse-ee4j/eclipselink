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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="root")
@XmlType(propOrder={"name", "nameList"})
public class QNameRoot {

    private QName name;
    private List<QName> nameList;

    public QNameRoot() {
        nameList = new ArrayList<QName>(2);
    }

    @XmlPath("qname[@classifier='single']/text()")
    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    @XmlPath("qname[@classifier='many']/text()")
    public List<QName> getNameList() {
        return nameList;
    }

    public void setNameList(List<QName> qNameList) {
        this.nameList = qNameList;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        QNameRoot test = (QNameRoot) obj;

        if(null == name && null != test.getName()) {
            return false;
        } else if(!name.equals(test.getName())) {
            return false;
        }

        List testNameList = test.getNameList();
        int itemsSize = nameList.size();
        if(itemsSize != testNameList.size()) {
            return false;
        }
        for(int x=0; x<itemsSize; x++) {
            if(!nameList.get(x).equals(testNameList.get(x))) {
                return false;
            }
        }
        return true;
    }

}