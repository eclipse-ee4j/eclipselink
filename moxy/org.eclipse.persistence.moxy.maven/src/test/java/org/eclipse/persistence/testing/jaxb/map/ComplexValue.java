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
//     Denise Smith  February, 2013
package org.eclipse.persistence.testing.jaxb.map;

public class ComplexValue {

    public String thing1;
    public String thing2 ;

    public ComplexValue(){

    }
    public ComplexValue(String thing1, String thing2){
        this.thing1 = thing1;
        this.thing2 = thing2;
    }

     public boolean equals(Object obj){
            if(!(obj instanceof ComplexValue)) {
                return false;
            }
            ComplexValue compare = (ComplexValue)obj;
            return thing1.equals(compare.thing1) && thing2.equals(compare.thing2);
      }
}
