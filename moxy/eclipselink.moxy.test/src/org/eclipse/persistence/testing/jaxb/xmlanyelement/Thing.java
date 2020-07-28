/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
