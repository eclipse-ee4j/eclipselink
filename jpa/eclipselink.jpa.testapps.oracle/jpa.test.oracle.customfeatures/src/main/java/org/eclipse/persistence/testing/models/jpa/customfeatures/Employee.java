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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.customfeatures;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.platform.database.oracle.NString;
import org.w3c.dom.Document;

import java.io.Serializable;

import static org.eclipse.persistence.annotations.Direction.IN;
import static org.eclipse.persistence.annotations.Direction.IN_OUT;
import static org.eclipse.persistence.annotations.Direction.OUT;
import static org.eclipse.persistence.annotations.Direction.OUT_CURSOR;

@Entity
@Table(name="CUSTOM_FEATURE_EMPLOYEE")
@NamedStoredProcedureQueries({
@NamedStoredProcedureQuery(
    name="ReadEmployeeInOut",
    resultClass=org.eclipse.persistence.testing.models.jpa.customfeatures.Employee.class,
    procedureName="Read_Employee_InOut",
    parameters={
        @StoredProcedureParameter(direction=IN_OUT, name="employee_id_v", queryParameter="ID", type=Integer.class),
        @StoredProcedureParameter(direction=OUT, name="nchar_v", queryParameter="NCHARTYPE", type=Character.class)}
),
@NamedStoredProcedureQuery(
    name="ReadEmployeeCursor",
    resultClass=org.eclipse.persistence.testing.models.jpa.customfeatures.Employee.class,
    procedureName="Read_Employee_Cursor",
    parameters={
        @StoredProcedureParameter(direction=IN, name="employee_id_v", queryParameter="ID", type=Integer.class),
        @StoredProcedureParameter(direction=OUT_CURSOR, queryParameter="RESULT_CURSOR")})
})
@Customizer(XmlDataCustomizer.class)
public class Employee implements Serializable {

    @SequenceGenerator(
      name="CUSTOMEMP_SEQ",
      sequenceName="EMPLOYEE_SEQUENCE",
      initialValue=50,
      allocationSize=1
    )

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUSTOMEMP_SEQ")
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Version
    @Column(name="VERSION")
    private Integer version;

    @Column(name="NCHARTYPE")
    @Convert(value="NChar")
    @TypeConverter(name="NChar", dataType=NString.class)
    private Character empNChar;

    @Column(name="XMLDATA")
    private String resume_xml;

    @Basic
    @Column(name="XMLDOM")
    private Document resume_dom;

    public Employee() {}

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public char getEmpNChar() {
        return this.empNChar;
    }

    public void setEmpNChar(char empNChar) {
        this.empNChar = empNChar;
    }

    public String getResume_xml() {
        return this.resume_xml;
    }

    public void setResume_xml(String resume_xml) {
        this.resume_xml = resume_xml;
    }

    public Document getResume_dom() {
        return this.resume_dom;
    }

    public void setResume_dom(Document resume_dom) {
        this.resume_dom = resume_dom;
    }


}

