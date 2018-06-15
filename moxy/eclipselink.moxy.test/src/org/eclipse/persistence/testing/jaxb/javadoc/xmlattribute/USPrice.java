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

@XmlRootElement
     public class USPrice {
         @XmlAttribute double price;

         public double getPrice() {
              return price;
          }
         public void setPrice(double price) {
              this.price = price;
         }
         public boolean equals(Object object) {
            USPrice p = ((USPrice)object);
            return p.price == this.price;
        }
     }
