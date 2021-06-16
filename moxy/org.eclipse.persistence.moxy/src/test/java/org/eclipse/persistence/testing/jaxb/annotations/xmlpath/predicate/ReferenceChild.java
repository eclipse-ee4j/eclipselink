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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;
import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlType(propOrder={"id", "parent", "children"})
public class ReferenceChild {

    private String id;
    private ReferenceChild parent;
    private List<ReferenceChild> children = new ArrayList<ReferenceChild>(2);

    @XmlKey
    @XmlPath("group[@class='id']/@id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlJoinNode(xmlPath="group[@class='parent']/parent[@class='node']/text()", referencedXmlPath="group[@class='id']/@id")
    public ReferenceChild getParent() {
        return parent;
    }

    public void setParent(ReferenceChild parent) {
        this.parent = parent;
    }

    @XmlJoinNode(xmlPath="group[@class='child']/child[@class='node']/text()", referencedXmlPath="group[@class='id']/@id")
    public List<ReferenceChild> getChildren() {
        return children;
    }

    public void setChildren(List<ReferenceChild> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ReferenceChild test = (ReferenceChild) obj;
        if(!equals(id, test.getId())) {
            return false;
        }
        if(null != parent && !equals(parent.getId(), test.getParent().getId())) {
            return false;
        }
        for(int x=0; x<children.size(); x++) {
            if(!equals(children.get(x).getId(), test.getChildren().get(x).getId())) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
