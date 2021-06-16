/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "TMP_UCOFFICE")
public class UniqueColorOffice {
    private int m_id;
    private long m_capacity;
    private Map<String, Cubicle> m_cubicles;

    @Id
    public int getId() {
        return m_id;
    }

    public void setId(final int id) {
        m_id = id;
    }

    @OneToMany
    @MapKey(name = "color")
    @JoinTable(name = "TMP_UCOFFICE_CUBICLE", joinColumns = { @JoinColumn(name = "OFFICE_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "CUBICLE_FLOOR", referencedColumnName = "FLOOR"),
            @JoinColumn(name = "CUBICLE_PLACE", referencedColumnName = "PLACE") })
    public Map<String, Cubicle> getCubicles() {
        return m_cubicles;
    }

    public void setCubicles(final Map<String, Cubicle> cubicles) {
        m_cubicles = cubicles;
    }

    public long getCapacity() {
        return m_capacity;
    }

    public void setCapacity(long capacity) {
        m_capacity = capacity;
    }

    @Transient
    public List<Employee> getOccupants() {
        final List<Employee> result = new ArrayList<Employee>();
        for (final Cubicle cubicle : m_cubicles.values()) {
            result.add(cubicle.getEmployee());
        }
        return result;
    }

    public void addCubicle(final Cubicle cubicle) {
        if (null == m_cubicles) {
            m_cubicles = new HashMap<String, Cubicle>();
        }
        m_cubicles.put(cubicle.getColor(), cubicle);
    }

}
