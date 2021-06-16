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
//     8/12/2015-2.6 Mythily Parthasarathy
//       - 474752: Creation of TireDetail
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="OR_TIREDETAIL")
public class TireDetail {
    @Id
    @Column(name="TIRE_ID")
    private int tireId;
    @Column(nullable=true)
    private String detail;

    public void setTireId(int id) {
        this.tireId = id;
    }
    
    public int getTireId() {
        return tireId;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getDetail() {
        return this.detail;
    }
}
