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
//     tware - testing for DDL issue with embedded and MTM
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PatentCollection {

    @ManyToMany
    @CascadeOnDelete
    private List<Patent> patents = new ArrayList<Patent>();

    public List<Patent> getPatents() {
        return patents;
    }

    public void setPatents(List<Patent> patents) {
        this.patents = patents;
    }

}
