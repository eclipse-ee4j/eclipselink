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

import java.sql.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Cacheable(value = true)
@Entity
@Table(name = "TMP_PATENT")
public class Patent {
    private PatentId m_id;
    private String m_description;
    private Date m_assignation;

    public Patent() {
        m_id = new PatentId();
    }

    public Patent(final String name, final int year, final String description, final Date assignation) {
        this();
        m_id.setName(name);
        m_id.setYear(year);
        m_description = description;
        m_assignation = assignation;
    }

    @EmbeddedId
    @AttributeOverrides( { @AttributeOverride(name = "name", column = @Column(name = "PAT_NAME")),
            @AttributeOverride(name = "year", column = @Column(name = "PAT_YEAR")) })
    public PatentId getId() {
        return m_id;
    }

    public void setId(final PatentId id) {
        m_id = id;
    }

    @Basic
    @Column(name = "PAT_ASSIGNATION")
    public Date getAssignation() {
        return m_assignation;
    }

    public void setAssignation(final Date assignation) {
        m_assignation = assignation;
    }

    @Basic
    @Column(name = "PAT_DESCRIPTION")
    public String getDescription() {
        return m_description;
    }

    public void setDescription(final String description) {
        m_description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Patent) {
            final Patent other = (Patent) obj;
            return m_id.equals(other.m_id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 + m_id.hashCode();
    }
}
