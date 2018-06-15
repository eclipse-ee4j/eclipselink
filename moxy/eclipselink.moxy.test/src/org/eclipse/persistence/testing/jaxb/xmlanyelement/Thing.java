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
//     Denise Smith - May 2013
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
