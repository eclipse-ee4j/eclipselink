/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
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
