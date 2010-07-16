/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TMP_OFFICE")
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
    @JoinTable(name = "TMP_OFFICE_CUBICLE", joinColumns = { @JoinColumn(name = "OFFICE_ID") }, inverseJoinColumns = {
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
