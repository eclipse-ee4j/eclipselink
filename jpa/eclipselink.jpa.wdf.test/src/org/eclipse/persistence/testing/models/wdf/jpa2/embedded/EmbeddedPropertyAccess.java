/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa2.embedded;

import static javax.persistence.AccessType.PROPERTY;

import javax.persistence.Access;
import javax.persistence.Embeddable;

@Embeddable
@Access(PROPERTY)
public class EmbeddedPropertyAccess {

    public EmbeddedPropertyAccess() {
    }

    private int value;

    public int getData() {
        return value;
    }

    public void setData(int data) {
        value = data;
    }

}
