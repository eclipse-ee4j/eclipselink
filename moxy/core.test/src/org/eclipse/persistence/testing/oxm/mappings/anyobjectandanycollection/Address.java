/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection;

public class Address {
    private String address_content;

    public Address() {
    }

    public String getContent() {
        return address_content;
    }

    public void setContent(String content) {
        this.address_content = content;
    }

    public String toString() {
        return "Address[" + getContent() + "]";
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Address) {
            return this.address_content.equals(((Address)object).getContent());
        }
        return false;
    }
}