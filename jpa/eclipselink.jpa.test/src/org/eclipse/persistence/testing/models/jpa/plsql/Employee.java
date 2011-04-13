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

import javax.persistence.Entity;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredFunctionQuery;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQueries;
import org.eclipse.persistence.platform.database.oracle.annotations.NamedPLSQLStoredProcedureQuery;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLParameter;

@NamedPLSQLStoredProcedureQueries({
    @NamedPLSQLStoredProcedureQuery(
            name="PLSQL_SIMPLE_IN_DEFAULTS",
            procedureName="PLSQL_SIMPLE_IN_DEFAULTS",
            parameters={
                    @PLSQLParameter(queryParameter="P_VARCHAR", databaseType="VARCHAR_TYPE", optional=true),
                    @PLSQLParameter(queryParameter="P_BOOLEAN", databaseType="PLSQLBoolean", optional=true),
                    @PLSQLParameter(queryParameter="P_BINARY_INTEGER", databaseType="BinaryInteger", optional=true),
                    @PLSQLParameter(queryParameter="P_DEC", databaseType="Dec", optional=true),
                    @PLSQLParameter(queryParameter="P_INT", databaseType="Int", optional=true),
                    @PLSQLParameter(queryParameter="P_NATURAL", databaseType="Natural", optional=true),
                    @PLSQLParameter(queryParameter="P_NATURALN", databaseType="NaturalN", optional=true),
                    @PLSQLParameter(queryParameter="P_PLS_INTEGER", databaseType="PLSQLInteger", optional=true),
                    @PLSQLParameter(queryParameter="P_POSITIVE", databaseType="Positive", optional=true),
                    @PLSQLParameter(queryParameter="P_POSITIVEN", databaseType="PositiveN", optional=true),
                    @PLSQLParameter(queryParameter="P_SIGNTYPE", databaseType="SignType", optional=true),
                    @PLSQLParameter(queryParameter="P_NUMBER", databaseType="Number", optional=true)
            }
    ),
    @NamedPLSQLStoredProcedureQuery(
            name="PLSQL_ADDRESS_OUT",
            procedureName="PLSQL_ADDRESS_OUT",
            parameters={
                    @PLSQLParameter(queryParameter="P_ADDRESS", direction=Direction.OUT, databaseType="PLSQL_ADDRESS%ROWTYPE")
            }
    )
})
@NamedPLSQLStoredFunctionQuery(
        name="PLSQL_SIMPLE_IN_FUNC",
        functionName="PLSQL_SIMPLE_IN_FUNC",
        parameters={
                @PLSQLParameter(queryParameter="P_VARCHAR", databaseType="VARCHAR_TYPE"),
                @PLSQLParameter(queryParameter="P_BOOLEAN", databaseType="PLSQLBoolean"),
                @PLSQLParameter(queryParameter="P_BINARY_INTEGER", databaseType="BinaryInteger"),
                @PLSQLParameter(queryParameter="P_DEC", databaseType="Dec"),
                @PLSQLParameter(queryParameter="P_INT", databaseType="Int"),
                @PLSQLParameter(queryParameter="P_NATURAL", databaseType="Natural"),
                @PLSQLParameter(queryParameter="P_NATURALN", databaseType="NaturalN"),
                @PLSQLParameter(queryParameter="P_PLS_INTEGER", databaseType="PLSQLInteger"),
                @PLSQLParameter(queryParameter="P_POSITIVE", databaseType="Positive"),
                @PLSQLParameter(queryParameter="P_POSITIVEN", databaseType="PositiveN"),
                @PLSQLParameter(queryParameter="P_SIGNTYPE", databaseType="SignType"),
                @PLSQLParameter(queryParameter="P_NUMBER", databaseType="Number")
        },
        returnParameter=@PLSQLParameter(queryParameter="RESULT", direction=Direction.OUT, databaseType="PLSQLBoolean")
)
/**
 * Used to test simple PLSQL record types.
 * 
 * @author James
 */
@Entity
public class Employee {
    @Id
    protected BigDecimal id;
    protected String name;
    protected boolean active;
    protected Address address;
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
    	Employee employee = (Employee)object;
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
