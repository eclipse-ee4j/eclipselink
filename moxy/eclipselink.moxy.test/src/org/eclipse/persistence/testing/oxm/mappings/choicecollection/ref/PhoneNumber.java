/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
