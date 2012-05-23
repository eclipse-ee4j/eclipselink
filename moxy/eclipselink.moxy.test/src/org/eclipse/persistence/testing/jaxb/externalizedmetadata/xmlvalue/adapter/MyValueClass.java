/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - October 27/2010 - 2.2 - Initial implementation
 ******************************************************************************/
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
