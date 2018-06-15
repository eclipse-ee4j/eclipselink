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
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.emptyroot;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {
   public String something;

    @Override
    public int hashCode() {
        return something != null ? something.hashCode() : 0;
    }

    public boolean equals(Object obj){
       if(!(obj instanceof Root)){
           return false;
       }
       if(something == null){
           return ((Root)obj).something == null;
       }else{
           return something.equals(((Root)obj).something);
       }

   }
}
