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
// dmccann - December 01/2010 - 2.3 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

public class PhoneNumber {
    public String id;
    public String number;

    public boolean equals(Object obj) {
        PhoneNumber num = (PhoneNumber)obj;
        try {
            return this.id.equals(num.id) && this.number.equals(num.number);
        } catch (Exception x) {
        }
        return false;
    }

}
