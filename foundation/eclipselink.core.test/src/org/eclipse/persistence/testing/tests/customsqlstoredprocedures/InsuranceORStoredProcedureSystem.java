/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;

/*
 * modeled after EmployeeCustomSQLSystem using the underlying ObjectRelational classes from
 * InsuranceORSystem instead of the Employee model.  
 */

public class InsuranceORStoredProcedureSystem extends org.eclipse.persistence.testing.models.insurance.objectrelational.InsuranceORSystem{
    public InsuranceORStoredProcedureSystem() {
    }

        public void addDescriptors(DatabaseSession session) {
            session.logout();
            super.addDescriptors(session);

            // Force re-initialize.
            session.login();
        }

        public StoredProcedureDefinition buildOracleStoredProcedureInsertPolicyHolders() {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("SProc_Insert_PHolders");
            
            proc.addArgument("ssn", Long.class);
            proc.addArgument("occupation", String.class, 20);
            proc.addArgument("sex", Character.class);
            proc.addArgument("firstName", String.class, 20);
            proc.addArgument("birthDate", java.sql.Date.class);
            proc.addArgument("lastName", String.class, 20);
            
            proc.addArgument(new FieldDefinition("address", "Address_type"));
            proc.addArgument(new FieldDefinition("childrenNames", "NameList_type"));
            proc.addArgument("phones", "PhoneList_type");
            
            String statement = "INSERT INTO PolicyHolders "+
                    "(SSN, OCCUPATION, SEX, FIRSTNAME, BIRTHDATE, LASTNAME, ADDRESS, PHONES, CHILDRENNAMES, POLICIES) "+
                    "VALUES (ssn, occupation, sex, firstName, birthDate, lastName, address, phones, childrenNames, null)";
            proc.addStatement(statement);
            return proc;
        }
        
        public StoredProcedureDefinition buildOracleStoredProcedureReadFromPolicyHolders() {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("SProc_Read_PHolders");
            
            proc.addInOutputArgument("ssn_v", Long.class);
            proc.addOutputArgument("occupation_v", String.class, 20);
            proc.addOutputArgument("sex_v", Character.class);
            proc.addOutputArgument("firstName_v", String.class, 20);
            proc.addOutputArgument("birthDate_v", java.sql.Date.class);
            proc.addOutputArgument("lastName_v", String.class, 20);
            
            proc.addOutputArgument("address_v", "Address_type");
            proc.addOutputArgument("childrenNames_v", "NameList_type");
            proc.addOutputArgument("phones_v", "PhoneList_type");
            
            String statement = "SELECT SSN, OCCUPATION, SEX, FIRSTNAME, BIRTHDATE, LASTNAME, ADDRESS, PHONES, CHILDRENNAMES "/*, POLICIES "*/+
                    "into ssn_v, occupation_v, sex_v, firstName_v, birthDate_v, lastName_v, address_v, phones_v, childrenNames_v "/*, policies_v "*/+
                    "from PolicyHolders where (SSN = ssn_v)";
            
            proc.addStatement(statement);
            return proc;
        }
        
        public StoredProcedureDefinition buildOracleStoredProcedureDeletePolicyHolders() {
            StoredProcedureDefinition proc = new StoredProcedureDefinition();
            proc.setName("SProc_Delete_PHolders");
            
            proc.addInOutputArgument("ssn_v", Long.class);
            proc.addInOutputArgument(new FieldDefinition("occupation_v", String.class, 20));
            proc.addInOutputArgument("sex_v", Character.class);
            proc.addInOutputArgument(new FieldDefinition("firstName_v", String.class, 20));
            proc.addInOutputArgument("birthDate_v", java.sql.Date.class);
            proc.addInOutputArgument(new FieldDefinition("lastName_v", String.class, 20));
            
            proc.addInOutputArgument(new FieldDefinition("address_v", "Address_type"));
            proc.addInOutputArgument(new FieldDefinition("childrenNames_v", "NameList_type"));
            proc.addInOutputArgument(new FieldDefinition("phones_v", "PhoneList_type"));
            
            String statement = "SELECT SSN, OCCUPATION, SEX, FIRSTNAME, BIRTHDATE, LASTNAME, ADDRESS, PHONES, CHILDRENNAMES "/*, POLICIES "*/+
                    "into ssn_v, occupation_v, sex_v, firstName_v, birthDate_v, lastName_v, address_v, phones_v, childrenNames_v "/*, policies_v "*/+
                    "from PolicyHolders where (SSN = ssn_v)";
            
            proc.addStatement(statement);
            proc.addStatement("DELETE FROM PolicyHolders where (SSN = ssn)");
            return proc;
        }
        
        /**
         * Also creates the procs.
         */
         public void createTables (DatabaseSession session) {
            super.createTables(session);
            org.eclipse.persistence.internal.databaseaccess.DatabasePlatform platform = session.getLogin().getPlatform();
            SchemaManager schema = new SchemaManager((session));

            if (platform.isSQLServer()) {

            }
            if (platform.isSybase() || platform.isSQLAnywhere()) {
            }
            if (platform.isOracle()) {
                
                schema.replaceObject(buildOracleStoredProcedureInsertPolicyHolders());
                schema.replaceObject(buildOracleStoredProcedureReadFromPolicyHolders());
                schema.replaceObject(buildOracleStoredProcedureDeletePolicyHolders());
            }
            if (platform.isDB2()) {
            }
        }

    }
