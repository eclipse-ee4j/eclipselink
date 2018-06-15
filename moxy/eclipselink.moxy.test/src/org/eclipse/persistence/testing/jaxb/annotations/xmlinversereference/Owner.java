/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class Owner {

    @XmlElementRef
    @XmlJavaTypeAdapter(OwnedAdapter.class)
    public List<Owned> owned;

    public boolean equals(Object obj) {
        Owner owner = (Owner)obj;
        if(owned.size() != owner.owned.size()) {
            return false;
        }
        for(int i = 0; i < owned.size(); i++) {
            if(!owned.get(i).equals(owner.owned.get(i))) {
                return false;
            }
        }
        return true;
    }

}
