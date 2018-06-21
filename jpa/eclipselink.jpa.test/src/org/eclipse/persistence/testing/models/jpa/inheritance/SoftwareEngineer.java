/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation

package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;

/**
 * This tests having two independent subclasses sharing the same table.
 * @author James Sutherland
 */
@Entity
@Table(name="CMP3_ENGINEER")
@DiscriminatorValue("4")
public class SoftwareEngineer extends Person {
    private String title;
    private Company company;

    @ManyToOne
    public Company getCompany() {
        return company;
    }

    @Column(name="TITLE")
    public String getTitle() {
        return title;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
