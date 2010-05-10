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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
