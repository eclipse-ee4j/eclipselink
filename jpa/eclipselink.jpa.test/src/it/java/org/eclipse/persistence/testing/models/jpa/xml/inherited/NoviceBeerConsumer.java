/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     01/28/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 1)
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class NoviceBeerConsumer extends RatedBeerConsumer<Integer, Integer, Integer> {
    public NoviceBeerConsumer() {
        super();
    }

    public void addCommittee(Committee committee) {
        getCommittees().add(committee);
        committee.addNoviceBeerConsumer(this);
    }
}
