/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation

package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.*;

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
