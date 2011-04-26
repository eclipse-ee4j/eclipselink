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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.persistence.annotations.Struct;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLParameter;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLRecord;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLRecords;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLTable;

/**
 * Used to test simple PLSQL record types.
 * 
 * @author James
 */
@PLSQLRecords({
     @PLSQLRecord(name="PLSQL_ADDRESS%ROWTYPE", compatibleType="PLSQL_P_PLSQL_ADDRESS_REC", javaType=Address.class,
             fields={
                 @PLSQLParameter(name="ADDRESS_ID", databaseType="NUMERIC_TYPE"),
                 @PLSQLParameter(name="STREET_NUM", databaseType="NUMERIC_TYPE"),
                 @PLSQLParameter(name="STREET"),
                 @PLSQLParameter(name="CITY"),
                 @PLSQLParameter(name="STATE")
             }
     ),
     @PLSQLRecord(name="PLSQL_P.PLSQL_ADDRESS_REC", compatibleType="PLSQL_P_PLSQL_ADDRESS_REC", javaType=Address.class,
             fields={
                 @PLSQLParameter(name="ADDRESS_ID", databaseType="NUMERIC_TYPE"),
                 @PLSQLParameter(name="STREET_NUM", databaseType="NUMERIC_TYPE"),
                 @PLSQLParameter(name="STREET"),
                 @PLSQLParameter(name="CITY"),
                 @PLSQLParameter(name="STATE")
             }
     )
})
@PLSQLTable(name="PLSQL_P.PLSQL_ADDRESS_LIST", compatibleType="PLSQL_P_PLSQL_ADDRESS_LIST", nestedType="PLSQL_P.PLSQL_ADDRESS_REC")
@Embeddable
@Struct(name="PLSQL_P_PLSQL_ADDRESS_REC", fields={"ADDRESS_ID", "STREET_NUM", "STREET", "CITY", "STATE"})
public class Address {
    @Column(name="ADDRESS_ID")
    protected BigDecimal id;
    @Column(name="STREET_NUM")
    protected Integer number;
    protected String street;
    protected String city;
    protected String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
    public boolean equals(Object object) {
    	if (!(object instanceof Address)) {
    		return false;
    	}
    	Address address = (Address)object;
    	if (this.id != null && !this.id.equals(address.id)) {
    		return false;
    	}
    	if (this.number != null && !this.number.equals(address.number)) {
    		return false;
    	}
    	if (this.street != null && !this.street.equals(address.street)) {
    		return false;
    	}
    	if (this.city != null && !this.city.equals(address.city)) {
    		return false;
    	}
    	if (this.state != null && !this.state.equals(address.state)) {
    		return false;
    	}
    	return true;
    }
}
