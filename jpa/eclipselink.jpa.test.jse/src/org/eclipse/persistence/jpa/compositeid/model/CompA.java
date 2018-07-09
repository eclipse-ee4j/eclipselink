/********************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 * Contributors:
 *     07/09/2018-2.6 Jody Grassel
 *       - 536853: MapsID processing sets up to fail validation
 ******************************************************************************/

package org.eclipse.persistence.jpa.compositeid.model;

import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class CompA {
    @EmbeddedId
    CompAId id;

    @OneToMany(mappedBy="compA")
    Set<CompB> compBs;

    public CompAId getId() {
        return id;
    }

    public void setId(CompAId id) {
        this.id = id;
    }

    public Set<CompB> getCompBs() {
        return compBs;
    }

    public void setCompBs(Set<CompB> compBs) {
        this.compBs = compBs;
    }


}
