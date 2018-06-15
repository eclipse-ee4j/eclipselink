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
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="namespace3")
public class Simple {
   @XmlElement(namespace="namespace1")
  public int thing1;
   @XmlElement(namespace="namespace2")
  public int thing2;

   public boolean equals(Object obj){
       if(!(obj instanceof Simple)){
           return false;
       }
       Simple simpleObject = (Simple)obj;
       return thing1 == simpleObject.thing1 && thing2 == simpleObject.thing2;
   }
}
