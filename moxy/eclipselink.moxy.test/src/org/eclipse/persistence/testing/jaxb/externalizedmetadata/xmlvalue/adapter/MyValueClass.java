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
// dmccann - October 27/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter;


//@XmlRootElement(name="boolean")
public class MyValueClass {
    //@XmlJavaTypeAdapter(MyValueAdapter.class)
    //@XmlValue
    public Boolean blah;

    public boolean equals(Object obj){
      if(obj instanceof MyValueClass){
          return blah.equals(((MyValueClass)obj).blah);
      }
      return false;
    }
}
