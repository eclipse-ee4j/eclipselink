/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/24/2015-2.6.0 Rick Curtis
//       - 460740: Fix pessimistic locking with setFirst/Max results on DB2
//     03/13/2015-2.6.0 Will Dazey
//       - 458301: Added named queries for associated tests in TestPessimisticLocking
package org.eclipse.persistence.jpa.test.locking.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@Entity
@NamedQueries({
    @NamedQuery(name="find.lockingdogs", query="SELECT d FROM LockingDog d"),
    @NamedQuery(name="find.lockingdogs.id", query="SELECT d.id FROM LockingDog d"),
    @NamedQuery(name="find.lockingdogs.avg", query="SELECT AVG(d.id) FROM LockingDog d")})
public class LockingDog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Version
    int version;

    String name;

    public int getId() {
        return id;
    }
}
