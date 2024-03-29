/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     tware - testing for DDL issue with embedded and MTM
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToMany;
import org.eclipse.persistence.annotations.CascadeOnDelete;

import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PatentCollection {

    @ManyToMany
    @CascadeOnDelete
    private List<Patent> patents = new ArrayList<>();

    public List<Patent> getPatents() {
        return patents;
    }

    public void setPatents(List<Patent> patents) {
        this.patents = patents;
    }
}
