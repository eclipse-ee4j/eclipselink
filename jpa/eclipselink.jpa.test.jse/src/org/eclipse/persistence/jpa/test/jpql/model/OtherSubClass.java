/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     05/08/2019 - 2.7 Will Dazey
//       - 493804: LEFT OUTER JOIN do not account for empty JOIN results
package org.eclipse.persistence.jpa.test.jpql.model;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("OtherSub")
public class OtherSubClass extends SuperClass {
    public OtherSubClass() { }

    public OtherSubClass(String name) {
        super(name);
    }
}