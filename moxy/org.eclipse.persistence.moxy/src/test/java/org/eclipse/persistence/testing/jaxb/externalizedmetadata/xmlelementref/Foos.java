/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - December 03/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementref;

//@jakarta.xml.bind.annotation.XmlRootElement
public class Foos {
    //@jakarta.xml.bind.annotation.XmlElementWrapper(name="items")
    //@jakarta.xml.bind.annotation.XmlElementRef(type=Bar.class)
    public java.util.List<Bar> items;

    public boolean equals(Object obj){
        if(obj instanceof Foos){
            if(items.size() != ((Foos)obj).items.size()){
                return false;
            }
            for(int i=0; i<items.size(); i++){
                Object next = items.get(i);
                Object nextCompare = ((Foos)obj).items.get(i);
                if(!(next.equals(nextCompare))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
