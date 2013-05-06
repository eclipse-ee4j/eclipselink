/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - May 2013
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Thing {
   
     public String name;
     
     public boolean equals(Object obj){
         if(obj instanceof Thing){
             return ((name ==null && ((Thing)obj).name == null) ||(name.equals(((Thing)obj).name)));
         }
         return false;
     }
}
