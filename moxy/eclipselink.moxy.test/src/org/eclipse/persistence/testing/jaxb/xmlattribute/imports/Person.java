/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.4 - February 11, 2013
package org.eclipse.persistence.testing.jaxb.xmlattribute.imports;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlattribute.imports2.IdentifierType;

@XmlRootElement
public class Person {
      @XmlAttribute
      private IdentifierType id = null;

      public String name;

      public IdentifierType getId() {
          return id;
      }

      public void setId(IdentifierType id) {
          this.id = id;
      }

      public boolean equals(Object obj){
          if(obj instanceof Person){
              Person compare = (Person)obj;
              if(!name.equals(compare.name)){
                  return false;
              }
              if(!id.equals(compare.id)){
                  return false;
              }
              return true;
          }
          return false;
      }
}
