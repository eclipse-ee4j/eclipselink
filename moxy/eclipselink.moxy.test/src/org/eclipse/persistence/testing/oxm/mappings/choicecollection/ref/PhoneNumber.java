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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref;

public class PhoneNumber {
    public String id;
    public String number;

    public boolean equals(Object obj) {
        PhoneNumber num;

        try {
            num = (PhoneNumber)obj;
        } catch (Exception ex) {
            return false;
        }

        return this.id.equals(num.id) && this.number.equals(num.number);
    }

}
