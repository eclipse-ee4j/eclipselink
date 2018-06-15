/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlattribute;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Iterator;
import java.util.List;

//Example3 : xmlattribute

@XmlRootElement
     public class MapCollectionToXmlAttribute {
         @XmlAttribute List<String> items;

        public String toString()
        {
            return "List of items: " + items;
        }

        public boolean equals(Object object) {
            MapCollectionToXmlAttribute model = ((MapCollectionToXmlAttribute)object);
            if(model.items == null && items == null) {
                return true;
            } else if(model.items == null || items == null)
            {
                return false;
            }
            if(model.items.size() != items.size()) {
                return false;
            }
            Iterator items1 = model.items.iterator();
            Iterator items2 = items.iterator();
            while(items1.hasNext()) {
                if(!(items1.next().equals(items2.next()))) {
                    System.out.println("returning false");
                    return false;
                }
            }
            return true;
        }
     }
