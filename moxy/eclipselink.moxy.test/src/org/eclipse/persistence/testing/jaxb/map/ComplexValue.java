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
