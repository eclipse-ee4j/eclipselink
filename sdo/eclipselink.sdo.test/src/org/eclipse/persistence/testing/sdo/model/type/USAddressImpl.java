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
package org.eclipse.persistence.testing.sdo.model.type;

import commonj.sdo.Type;
import org.eclipse.persistence.sdo.SDODataObject;

public class USAddressImpl extends SDODataObject implements USAddress {

    public java.lang.String getName() {
        return (java.lang.String)get("name");
    }

    public void setName(java.lang.String value) {
        set("name", value);
    }

    public java.lang.String getStreet() {
        return (java.lang.String)get("street");
    }

    public void setStreet(java.lang.String value) {
        set("street", value);
    }
}
