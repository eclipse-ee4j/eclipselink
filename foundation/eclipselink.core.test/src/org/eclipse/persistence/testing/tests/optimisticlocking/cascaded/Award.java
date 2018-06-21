/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.indirection.*;

public class Award  {
    public int id;
    public String description;
    public ValueHolderInterface qualification;

    public Award() {
        this.qualification = new ValueHolder();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Qualification getQualification() {
        return (Qualification) qualification.getValue();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQualification(Qualification qualification) {
        this.qualification.setValue(qualification);
    }
}
