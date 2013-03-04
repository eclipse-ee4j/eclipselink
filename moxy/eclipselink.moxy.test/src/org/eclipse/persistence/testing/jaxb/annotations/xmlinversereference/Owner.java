/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
 ******************************************************************************/
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
