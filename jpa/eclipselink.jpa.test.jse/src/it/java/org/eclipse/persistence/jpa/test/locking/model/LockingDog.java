/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     02/24/2015-2.6.0 Rick Curtis
//       - 460740: Fix pessimistic locking with setFirst/Max results on DB2
//     03/13/2015-2.6.0 Will Dazey
//       - 458301: Added named queries for associated tests in TestPessimisticLocking
package org.eclipse.persistence.jpa.test.locking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Version;

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
