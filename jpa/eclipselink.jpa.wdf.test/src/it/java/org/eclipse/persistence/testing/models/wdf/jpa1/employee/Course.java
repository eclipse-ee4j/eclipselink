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
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name = "TMP_COURSE")
@TableGenerator(name = "CourseGenerator", table = "TMP_COURSE_GEN", pkColumnName = "BEAN_NAME", valueColumnName = "MAX_ID")
public class Course {
    private long m_id;
    private List<Employee> m_attendees = new ArrayList<Employee>();
    private Material material;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CourseGenerator")
    @Column(name = "COURSE_ID")
    public long getCourseId() {
        return m_id;
    }

    @Deprecated
    // for use by the entity manager only
    protected void setCourseId(long id) {
        m_id = id;
    }

    // <attribute name="attendees">
    // <many-to-many target-entity="com.sap.jpa.example.Employee" fetch="EAGER"/>
    // <join-table name="TMP_COURSE_EMP">
    // <join-column name="COURSE_ID"/>
    // <inverse-join-column name="EMP_ID"/>
    // </join-table>
    // <order-by>lastname</order-by>
    // </attribute>
    @ManyToMany(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinTable(name = "TMP_COURSE_EMP", joinColumns = { @JoinColumn(name = "COURSE_ID") }, inverseJoinColumns = { @JoinColumn(name = "EMP_ID") })
    @OrderBy("lastname")
    public List<Employee> getAttendees() {
        return m_attendees;
        // FIXME bugzilla 309681
//        return Collections.unmodifiableList(m_attendees);
    }

    public void setAttendees(final List<Employee> attendees) {
        m_attendees.clear();
        m_attendees.addAll(attendees);
    }

    public void addAttendee(final Employee attendee) {
        m_attendees.add(attendee);
    }

    public void removeAttendee(final Employee attendee) {
        m_attendees.remove(attendee);
    }

    public void clearAttendees() {
        m_attendees.clear();
    }

    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.PERSIST, optional=true)
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
