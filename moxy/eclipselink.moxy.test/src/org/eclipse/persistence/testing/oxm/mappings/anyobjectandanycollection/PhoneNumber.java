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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection;

import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Child;

public class PhoneNumber {
    private String phone_content;

    public PhoneNumber() {
    }

    public String getContent() {
        return phone_content;
    }

    public void setContent(String phone_content) {
        this.phone_content = phone_content;
    }

    public String toString() {
        return "PhoneNumber[" + getContent() + "]";
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof PhoneNumber) {
            return this.phone_content.equals(((PhoneNumber)object).getContent());
        }
        return false;
    }
}
