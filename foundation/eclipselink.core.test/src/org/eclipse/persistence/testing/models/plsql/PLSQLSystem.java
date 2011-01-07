/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.plsql;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class PLSQLSystem extends TestSystem {
    public PLSQLSystem() {
        project = new PLSQLProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new PLSQLProject();
        }
        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
    	// Tables
        try {
            session.executeNonSelectingSQL("DROP TABLE PLSQL_ADDRESS");
        } catch (Exception ignore) {}
        session.executeNonSelectingSQL("CREATE TABLE PLSQL_ADDRESS ("
                + "ADDRESS_ID NUMBER(10) NOT NULL, STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(30), PRIMARY KEY (ADDRESS_ID))");
        
        // Procedures
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_IN(P_VARCHAR IN VARCHAR2, P_BOOLEAN IN BOOLEAN, P_BINARY_INTEGER IN BINARY_INTEGER, "
        		+ "P_DEC IN DEC, P_INT IN INT, P_NATURAL IN NATURAL, P_NATURALN IN NATURALN, "
        		+ "P_PLS_INTEGER IN PLS_INTEGER, P_POSITIVE IN POSITIVE, P_POSITIVEN IN POSITIVEN, P_SIGNTYPE IN SIGNTYPE, P_NUMBER IN NUMBER) AS "                
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_OUT(P_VARCHAR OUT VARCHAR2, P_BOOLEAN OUT BOOLEAN, P_BINARY_INTEGER OUT BINARY_INTEGER, "
        		+ "P_DEC OUT DEC, P_INT OUT INT, P_NATURAL OUT NATURAL, " //P_NATURALN OUT NATURALN, "
        		+ "P_PLS_INTEGER OUT PLS_INTEGER, P_POSITIVE OUT POSITIVE, " //P_POSITIVEN OUT POSITIVEN, "
        		+ "P_SIGNTYPE OUT SIGNTYPE, P_NUMBER OUT NUMBER) AS "                
                + "BEGIN P_VARCHAR := 'varchar'; P_BOOLEAN := true; P_BINARY_INTEGER := 123; "
                + "P_DEC := 1; P_INT := 1; P_NATURAL := 1; " //P_NATURALN := 1; "
        		+ "P_PLS_INTEGER := 1; P_POSITIVE := 1; " //P_POSITIVEN := 1; "
        		+ "P_SIGNTYPE := 1; P_NUMBER := 123; \n"
                + "END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_SIMPLE_INOUT(P_VARCHAR IN OUT VARCHAR2, P_BOOLEAN IN OUT BOOLEAN, P_BINARY_INTEGER IN OUT BINARY_INTEGER, "
        		+ "P_DEC IN OUT DEC, P_INT IN OUT INT, P_NATURAL IN OUT NATURAL, P_NATURALN IN OUT NATURALN, "
        		+ "P_PLS_INTEGER IN OUT PLS_INTEGER, P_POSITIVE IN OUT POSITIVE, P_POSITIVEN IN OUT POSITIVEN, P_SIGNTYPE IN OUT SIGNTYPE, P_NUMBER IN OUT NUMBER) AS "                
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_IN(P_ADDRESS IN PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_IN_DATA(P_ADDRESS IN PLSQL_ADDRESS%ROWTYPE, P_LOCAL IN VARCHAR2) AS "
                + "BEGIN NULL; END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_OUT(P_ADDRESS OUT PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_OUT_DATA(P_ADDRESS OUT PLSQL_ADDRESS%ROWTYPE, P_LOCAL OUT VARCHAR2) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON'; P_LOCAL := 'Local';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_INOUT(P_ADDRESS IN OUT PLSQL_ADDRESS%ROWTYPE) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON';  END;");
        session.executeNonSelectingSQL("CREATE OR REPLACE PROCEDURE PLSQL_ADDRESS_INOUT_DATA(P_ADDRESS IN OUT PLSQL_ADDRESS%ROWTYPE, P_LOCAL IN OUT VARCHAR2) AS "
                + "BEGIN P_ADDRESS.ADDRESS_ID := 1234; P_ADDRESS.STREET_NUM := 17; P_ADDRESS.STREET := 'Bank'; P_ADDRESS.CITY := 'Ottawa'; P_ADDRESS.STATE := 'ON'; P_LOCAL := 'Local';  END;");
        
        // Types
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_EMP_REC FORCE");
        } catch (Exception ignore) {}
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_ADDRESS_REC FORCE");
        } catch (Exception ignore) {}
        try {
            session.executeNonSelectingSQL("DROP TYPE PLSQL_P_PLSQL_PHONE_REC FORCE");
        } catch (Exception ignore) {}
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_ADDRESS_REC AS OBJECT ("
                + "ADDRESS_ID NUMBER(10), STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(2))");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_PHONE_REC AS OBJECT ("
                + "AREA_CODE VARCHAR2(3), P_NUM VARCHAR2(7))");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_PHONE_LIST AS VARRAY(30) OF PLSQL_P_PLSQL_PHONE_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_EMP_REC AS OBJECT ("
                + "EMP_ID NUMBER(10), NAME VARCHAR2(30), ACTIVE NUMBER(1), ADDRESS PLSQL_P_PLSQL_ADDRESS_REC, PHONES PLSQL_P_PLSQL_PHONE_LIST)");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_CITY_LIST AS VARRAY(255) OF VARCHAR2(100)");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_ADDRESS_LIST AS VARRAY(255) OF PLSQL_P_PLSQL_ADDRESS_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE TYPE PLSQL_P_PLSQL_EMP_LIST AS VARRAY(255) OF PLSQL_P_PLSQL_EMP_REC");
        session.executeNonSelectingSQL("CREATE OR REPLACE PACKAGE PLSQL_P AS \n"
	        		+ "TYPE PLSQL_ADDRESS_REC IS RECORD ("
	        			+ "ADDRESS_ID NUMBER(10), STREET_NUM NUMBER(10), STREET VARCHAR2(30), CITY VARCHAR2(30), STATE VARCHAR2(2)); \n"
	        		+ "TYPE PLSQL_PHONE_REC IS RECORD ("
	        			+ "AREA_CODE VARCHAR2(3), P_NUM VARCHAR2(7)); \n"
	        		+ "TYPE PLSQL_PHONE_LIST IS TABLE OF PLSQL_PHONE_REC INDEX BY BINARY_INTEGER; \n"
	                + "TYPE PLSQL_EMP_REC IS RECORD ("
	        			+ "EMP_ID NUMBER(10), NAME VARCHAR2(30), ACTIVE BOOLEAN, ADDRESS PLSQL_ADDRESS_REC, PHONES PLSQL_PHONE_LIST); \n"
	                + "TYPE PLSQL_CITY_LIST IS TABLE OF VARCHAR2(100) INDEX BY BINARY_INTEGER; \n"
                    + "TYPE PLSQL_ADDRESS_LIST IS TABLE OF PLSQL_ADDRESS_REC INDEX BY BINARY_INTEGER; \n"
                    + "TYPE PLSQL_EMP_LIST IS TABLE OF PLSQL_EMP_REC INDEX BY BINARY_INTEGER; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_IN(P_CITY_LIST IN PLSQL_CITY_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_CITY_LIST_OUT(P_CITY_LIST OUT PLSQL_CITY_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_CITY_LIST_INOUT(P_CITY_LIST IN OUT PLSQL_CITY_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_IN(P_ADDRESS_LIST IN PLSQL_ADDRESS_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_OUT(P_ADDRESS_LIST OUT PLSQL_ADDRESS_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_INOUT(P_ADDRESS_LIST IN OUT PLSQL_ADDRESS_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_IN(P_EMP_LIST IN PLSQL_EMP_LIST, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_OUT(P_EMP_LIST OUT PLSQL_EMP_LIST, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_LIST_INOUT(P_EMP_LIST IN OUT PLSQL_EMP_LIST, P_CITY IN OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_IN(P_EMP IN PLSQL_EMP_REC, P_CITY IN VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_OUT(P_EMP OUT PLSQL_EMP_REC, P_CITY OUT VARCHAR2); \n"
                    + "PROCEDURE PLSQL_EMP_INOUT(P_EMP IN OUT PLSQL_EMP_REC, P_CITY IN OUT VARCHAR2); \n"
                    + "END PLSQL_P; \n");
        session.executeNonSelectingSQL("CREATE OR REPLACE PACKAGE BODY PLSQL_P AS \n"
                    + "PROCEDURE PLSQL_CITY_LIST_IN(P_CITY_LIST IN PLSQL_CITY_LIST, P_CITY IN VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "NULL; \n"
                    	+ "END PLSQL_CITY_LIST_IN; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_OUT(P_CITY_LIST OUT PLSQL_CITY_LIST, P_CITY OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; \n"
                    	+ "P_CITY_LIST(1) := 'Ottawa'; \n"
                    	+ "END PLSQL_CITY_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_CITY_LIST_INOUT(P_CITY_LIST IN OUT PLSQL_CITY_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; \n"
                    	+ "P_CITY_LIST(1) := 'Ottawa'; \n"
                    	+ "END PLSQL_CITY_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_IN(P_ADDRESS_LIST IN PLSQL_ADDRESS_LIST, P_CITY IN VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "NULL; \n"
                    	+ "END PLSQL_ADDRESS_LIST_IN; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_OUT(P_ADDRESS_LIST OUT PLSQL_ADDRESS_LIST, P_CITY OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; "
                    	+ "END PLSQL_ADDRESS_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_ADDRESS_LIST_INOUT(P_ADDRESS_LIST IN OUT PLSQL_ADDRESS_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; \n"
                    	+ "END PLSQL_ADDRESS_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_IN(P_EMP_LIST IN PLSQL_EMP_LIST, P_CITY IN VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "NULL; \n"
                    	+ "END PLSQL_EMP_LIST_IN; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_OUT(P_EMP_LIST OUT PLSQL_EMP_LIST, P_CITY OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; "
                    	+ "END PLSQL_EMP_LIST_OUT; \n"
                    + "PROCEDURE PLSQL_EMP_LIST_INOUT(P_EMP_LIST IN OUT PLSQL_EMP_LIST, P_CITY IN OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; "
                    	+ "END PLSQL_EMP_LIST_INOUT; \n"
                    + "PROCEDURE PLSQL_EMP_IN(P_EMP IN PLSQL_EMP_REC, P_CITY IN VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "NULL; \n"
                    	+ "END PLSQL_EMP_IN; \n"
                    + "PROCEDURE PLSQL_EMP_OUT(P_EMP OUT PLSQL_EMP_REC, P_CITY OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; "
                    	+ "END PLSQL_EMP_OUT; \n"
                    + "PROCEDURE PLSQL_EMP_INOUT(P_EMP IN OUT PLSQL_EMP_REC, P_CITY IN OUT VARCHAR2) AS \n"
                    	+ "BEGIN \n"
                    	+ "P_CITY := 'Nepean'; "
                    	+ "END PLSQL_EMP_INOUT; \n"
                    + "END PLSQL_P; \n");
    }

    public void populate(DatabaseSession session) {
        //
    }
}
