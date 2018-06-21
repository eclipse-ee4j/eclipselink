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
//     bdoughan - August 6/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.converter;

public class Wrapper {

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean equals(Object object) {
        Wrapper wrapper = (Wrapper) object;
        if(null == value) {
            return null == wrapper.getValue();
        } else {
            return value.equals(wrapper.getValue());
        }
    }

}
