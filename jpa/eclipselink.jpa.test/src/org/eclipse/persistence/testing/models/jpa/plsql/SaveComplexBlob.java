/*******************************************************************************
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

// Contributors:
//     Oracle - initial implementation
package org.eclipse.persistence.testing.models.jpa.plsql;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.platform.database.oracle.annotations.*;

@Entity
@NamedPLSQLStoredFunctionQueries(value = {
        @NamedPLSQLStoredFunctionQuery(name = "PLSQL_BLOB_REC_IN", functionName = "PLSQL_P.PLSQL_BLOB_REC_IN",
                parameters = {@PLSQLParameter(name = "P_BLOB_REC", direction = Direction.IN, databaseType = "PLSQL_P_PLSQL_INNER_BLOB_REC"),
                        @PLSQLParameter(name = "P_CITY", direction = Direction.IN, databaseType = "VARCHAR_TYPE")},
                returnParameter = @PLSQLParameter(name = "RESULT", direction = Direction.OUT, databaseType = "VARCHAR_TYPE")),
        @NamedPLSQLStoredFunctionQuery(name = "PLSQL_STRUCT_INSTRUCT_REC_IN", functionName = "PLSQL_P.PLSQL_STRUCT_INSTRUCT_REC_IN",
                parameters = {@PLSQLParameter(name = "P_BLOB_REC", databaseType = "PLSQL_P_PLSQL_INNER_BLOB_REC"),
                        @PLSQLParameter(name = "P_STRUCT_REC", databaseType = "PLSQL_P_PLSQL_OUTER_STRUCT_REC"),
                        @PLSQLParameter(name = "P_CITY", databaseType = "VARCHAR_TYPE")},
                returnParameter = @PLSQLParameter(name = "RESULT", direction = Direction.OUT, databaseType = "VARCHAR_TYPE"))
        }
)
@PLSQLRecords(value = {
        @PLSQLRecord(name = "PLSQL_P_PLSQL_INNER_BLOB_REC", compatibleType = "PLSQL_P_PLSQL_INNER_BLOB_REC", javaType = InnerObjBlob.class,
                fields = {
                        @PLSQLParameter(name = "BLOB_ID", databaseType = "NUMERIC_TYPE"),
                        @PLSQLParameter(name = "BLOB_CONTENT", databaseType = "BLOB_TYPE"),
                        @PLSQLParameter(name = "CLOB_CONTENT", databaseType = "CLOB_TYPE")
                }
        ),
        @PLSQLRecord(name = "PLSQL_P_PLSQL_OUTER_STRUCT_REC", compatibleType = "PLSQL_P_PLSQL_OUTER_STRUCT_REC", javaType = OuterObjBlob.class,
                fields = {
                        @PLSQLParameter(name = "STRUCT_ID", databaseType = "NUMERIC_TYPE"),
                        @PLSQLParameter(name = "STRUCT_CONTENT", databaseType = "PLSQL_P_PLSQL_INNER_BLOB_REC")
                }

        )
}
)
public class SaveComplexBlob {

    @Id
    @Column(name = "RESULT")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
