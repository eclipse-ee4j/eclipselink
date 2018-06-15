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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.oxm.reader;

public abstract class Event {

    @Override
    public boolean equals(Object object) {
        if(null == object || object.getClass() != getClass()) {
            return false;
        }
        return true;
    }

    public boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        } else {
            return control.equals(test);
        }
    }

}
