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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlattribute;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAttribute;
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
