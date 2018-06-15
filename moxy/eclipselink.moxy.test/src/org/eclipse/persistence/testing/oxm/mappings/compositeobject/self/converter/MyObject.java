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
//     rbarkhouse - 2009-10-14 11:21:57 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.converter;

public class MyObject {

    private String[] value;

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String toString = "MyObject #" + hashCode() + " {";

        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                toString += value[i];
                toString += (i == value.length - 1) ? "" : ", ";
            }

            toString += "}";
        }

        return toString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyObject) {
            MyObject myObj = (MyObject) obj;
            if (myObj.value == null && this.value != null) {
                return false;
            }
            if (myObj.value != null && this.value == null) {
                return false;
            }
            if (myObj.value == null && this.value == null) {
                return true;
            }
            if (myObj.value.length != this.value.length) {
                return false;
            }
            for (int i = 0; i < this.value.length; i++) {
                if (!(this.value[i].equals(myObj.value[i]))) {
                    return false;
                }
            }
            // At this point objects are considered equal
            return true;
        } else {
            return false;
        }
    }

}
