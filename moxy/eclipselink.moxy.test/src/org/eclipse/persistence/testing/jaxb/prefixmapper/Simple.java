/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - October 2012
 ******************************************************************************/
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
