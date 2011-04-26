/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James - initial impl
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.plsql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.Array;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.Struct;
import org.eclipse.persistence.annotations.Structure;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredFunctionQuery;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQueries;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQuery;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLParameter;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLRecord;

@NamedPLSQLStoredProcedureQueries( {
        @NamedPLSQLStoredProcedureQuery(name = "PLSQL_ADDRESS_INOUT", procedureName = "PLSQL_ADDRESS_INOUT",
                parameters = {@PLSQLParameter(name = "P_ADDRESS", direction = Direction.IN_OUT, databaseType = "PLSQL_ADDRESS%ROWTYPE") }),
        @NamedPLSQLStoredProcedureQuery(name = "PLSQL_ADDRESS_OUT", procedureName = "PLSQL_ADDRESS_OUT",
                parameters = {@PLSQLParameter(name = "P_ADDRESS", direction = Direction.OUT, databaseType = "PLSQL_ADDRESS%ROWTYPE") }),
        @NamedPLSQLStoredProcedureQuery(name = "PLSQL_ADDRESS_LIST_OUT", procedureName = "PLSQL_P.PLSQL_ADDRESS_LIST_OUT",
                parameters = {
                    @PLSQLParameter(name = "P_ADDRESS_LIST", direction = Direction.OUT, databaseType = "PLSQL_P.PLSQL_ADDRESS_LIST"),
                    @PLSQLParameter(name = "P_CITY", direction = Direction.OUT, databaseType = "VARCHAR_TYPE")
                }
        ),
        @NamedPLSQLStoredProcedureQuery(name = "PLSQL_SIMPLE_IN_DEFAULTS", procedureName = "PLSQL_SIMPLE_IN_DEFAULTS",
                parameters = {
                    @PLSQLParameter(name = "P_VARCHAR", databaseType = "VARCHAR_TYPE", optional = true),
                    @PLSQLParameter(name = "P_BOOLEAN", databaseType = "PLSQLBoolean", optional = true),
                    @PLSQLParameter(name = "P_BINARY_INTEGER", databaseType = "BinaryInteger", optional = true),
                    @PLSQLParameter(name = "P_DEC", databaseType = "Dec", optional = true),
                    @PLSQLParameter(name = "P_INT", databaseType = "Int", optional = true),
                    @PLSQLParameter(name = "P_NATURAL", databaseType = "Natural", optional = true),
                    @PLSQLParameter(name = "P_NATURALN", databaseType = "NaturalN", optional = true),
                    @PLSQLParameter(name = "P_PLS_INTEGER", databaseType = "PLSQLInteger", optional = true),
                    @PLSQLParameter(name = "P_POSITIVE", databaseType = "Positive", optional = true),
                    @PLSQLParameter(name = "P_POSITIVEN", databaseType = "PositiveN", optional = true),
                    @PLSQLParameter(name = "P_SIGNTYPE", databaseType = "SignType", optional = true),
                    @PLSQLParameter(name = "P_NUMBER", databaseType = "Number", optional = true)
                }
        ),
        @NamedPLSQLStoredProcedureQuery(name = "PLSQL_EMP_INOUT", procedureName = "PLSQL_P.PLSQL_EMP_INOUT",
                parameters = {
                    @PLSQLParameter(name = "P_EMP", direction=Direction.IN_OUT, databaseType = "PLSQL_P.PLSQL_EMP_REC"),
                    @PLSQLParameter(name = "P_CITY", direction=Direction.OUT, databaseType = "VARCHAR_TYPE")
        })
})
@NamedPLSQLStoredFunctionQuery(name = "PLSQL_SIMPLE_IN_FUNC", functionName = "PLSQL_SIMPLE_IN_FUNC",
        parameters = {
            @PLSQLParameter(name = "P_VARCHAR", databaseType = "VARCHAR_TYPE"),
            @PLSQLParameter(name = "P_BOOLEAN", databaseType = "PLSQLBoolean"),
            @PLSQLParameter(name = "P_BINARY_INTEGER", databaseType = "BinaryInteger"),
            @PLSQLParameter(name = "P_DEC", databaseType = "Dec"),
            @PLSQLParameter(name = "P_INT", databaseType = "Int"), @PLSQLParameter(name = "P_NATURAL", databaseType = "Natural"),
            @PLSQLParameter(name = "P_NATURALN", databaseType = "NaturalN"),
            @PLSQLParameter(name = "P_PLS_INTEGER", databaseType = "PLSQLInteger"),
            @PLSQLParameter(name = "P_POSITIVE", databaseType = "Positive"),
            @PLSQLParameter(name = "P_POSITIVEN", databaseType = "PositiveN"),
            @PLSQLParameter(name = "P_SIGNTYPE", databaseType = "SignType"),
            @PLSQLParameter(name = "P_NUMBER", databaseType = "Number") },
        returnParameter = @PLSQLParameter(name = "RESULT", direction = Direction.OUT, databaseType = "PLSQLBoolean")
)
@PLSQLRecord(name="PLSQL_P.PLSQL_EMP_REC", compatibleType="PLSQL_P_PLSQL_EMP_REC", javaType=Employee.class,
        fields={
            @PLSQLParameter(name="EMP_ID", databaseType="NUMERIC_TYPE"),
            @PLSQLParameter(name="NAME", databaseType="VARCHAR_TYPE"),
            @PLSQLParameter(name="ACTIVE", databaseType = "PLSQLBoolean"),
            @PLSQLParameter(name="ADDRESS", databaseType = "PLSQL_P.PLSQL_ADDRESS_REC"),
            @PLSQLParameter(name="PHONES", databaseType = "PLSQL_P.PLSQL_PHONE_LIST")
        }
)
/**
 * Used to test simple PLSQL record types.
 * 
 * @author James
 */
@Entity
@Struct(name="PLSQL_P_PLSQL_EMP_REC", fields={"EMP_ID", "NAME", "ACTIVE", "ADDRESS", "PHONES"})
public class Employee {
    @Id
    @Column(name="EMP_ID")
    protected BigDecimal id;
    protected String name;
    @TypeConverter(name="bool", dataType=Integer.class)
    @Convert("bool")
    protected boolean active;
    @Structure
    protected Address address;
    @Array(databaseType="PLSQL_P_PLSQL_PHONE_LIST")
    @Column(name="PHONES")
    protected List<Phone> phones = new ArrayList<Phone>();

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) object;
        if (this.id != null && !this.id.equals(employee.id)) {
            return false;
        }
        if (this.name != null && !this.name.equals(employee.name)) {
            return false;
        }
        if (this.address != null && !this.address.equals(employee.address)) {
            return false;
        }
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
