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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.eclipse.persistence.annotations.Struct;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLParameter;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLRecord;
import org.eclipse.persistence.platform.database.oracle.annotations.PLSQLTable;

/**
 * Used to test simple PLSQL record types.
 * 
 * @author James
 */
@PLSQLRecord(name="PLSQL_P.PLSQL_PHONE_REC", compatibleType="PLSQL_P_PLSQL_PHONE_REC", javaType=Phone.class,
        fields={
            @PLSQLParameter(name="AREA_CODE", databaseType="VARCHAR_TYPE"),
            @PLSQLParameter(name="P_NUM", databaseType="VARCHAR_TYPE")
        }
)
@PLSQLTable(name="PLSQL_P.PLSQL_PHONE_LIST", compatibleType="PLSQL_P_PLSQL_PHONE_LIST", nestedType="PLSQL_P.PLSQL_PHONE_REC")
@Embeddable
@Struct(name="PLSQL_P_PLSQL_PHONE_REC")
public class Phone {
    @Column(name="AREA_CODE")
    protected String areaCode;
    @Column(name="P_NUM")
    protected String number;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Phone)) {
            return false;
        }
        Phone address = (Phone) object;
        if (this.areaCode != null && !this.areaCode.equals(address.areaCode)) {
            return false;
        }
        if (this.number != null && !this.number.equals(address.number)) {
            return false;
        }
        return true;
    }
}
