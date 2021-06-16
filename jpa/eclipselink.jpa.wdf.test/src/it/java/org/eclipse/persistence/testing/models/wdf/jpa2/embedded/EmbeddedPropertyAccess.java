/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa2.embedded;

import static jakarta.persistence.AccessType.PROPERTY;

import jakarta.persistence.Access;
import jakarta.persistence.Embeddable;

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
