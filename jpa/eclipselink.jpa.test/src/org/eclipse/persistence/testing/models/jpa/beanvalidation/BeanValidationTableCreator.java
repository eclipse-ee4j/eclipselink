/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import org.eclipse.persistence.tools.schemaframework.*;

public class BeanValidationTableCreator extends TableCreator {
    public BeanValidationTableCreator() {
        setName("BeanValidationEmployeeProject");

        addTableDefinition(buildProjectTable());
        addTableDefinition(buildEmployeeTable());
        addTableDefinition(buildEmployeeProjectTable());
    }

    public TableDefinition buildProjectTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BV_PROJECT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(20);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);

        return table;
    }

    public TableDefinition buildEmployeeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BV_EMPLOYEE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSalaray = new FieldDefinition();
        fieldSalaray.setName("SALARY");
        fieldSalaray.setTypeName("NUMBER");
        fieldSalaray.setSize(19);
        fieldSalaray.setShouldAllowNull(true);
        fieldSalaray.setIsPrimaryKey(false);
        fieldSalaray.setUnique(false);
        fieldSalaray.setIsIdentity(false);
        table.addField(fieldSalaray);

        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(20);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);

        FieldDefinition fieldManagedProject = new FieldDefinition();
        fieldManagedProject.setName("MANAGEDPROJECT_ID");
        fieldManagedProject.setTypeName("NUMERIC");
        fieldManagedProject.setSize(19);
        fieldManagedProject.setShouldAllowNull(true);
        fieldManagedProject.setIsPrimaryKey(false);
        fieldManagedProject.setUnique(false);
        fieldManagedProject.setIsIdentity(false);
        fieldManagedProject.setForeignKeyFieldName("CMP3_BV_PROJECT.ID");
        table.addField(fieldManagedProject);

        FieldDefinition fieldStreet = new FieldDefinition();
        fieldStreet.setName("STREET");
        fieldStreet.setTypeName("VARCHAR");
        fieldStreet.setSize(20);
        fieldStreet.setShouldAllowNull(true);
        fieldStreet.setIsPrimaryKey(false);
        fieldStreet.setUnique(false);
        fieldStreet.setIsIdentity(false);
        table.addField(fieldStreet);

        FieldDefinition fieldCity = new FieldDefinition();
        fieldCity.setName("CITY");
        fieldCity.setTypeName("VARCHAR");
        fieldCity.setSize(20);
        fieldCity.setShouldAllowNull(true);
        fieldCity.setIsPrimaryKey(false);
        fieldCity.setUnique(false);
        fieldCity.setIsIdentity(false);
        table.addField(fieldCity);

        FieldDefinition fieldState = new FieldDefinition();
        fieldState.setName("STATE");
        fieldState.setTypeName("VARCHAR");
        fieldState.setSize(20);
        fieldState.setShouldAllowNull(true);
        fieldState.setIsPrimaryKey(false);
        fieldState.setUnique(false);
        fieldState.setIsIdentity(false);
        table.addField(fieldState);

        return table;
    }


    public TableDefinition buildEmployeeProjectTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BV_EMPLOYEE_PROJECT");

        FieldDefinition fieldEmployeeID = new FieldDefinition();
        fieldEmployeeID.setName("CMP3_BV_EMPLOYEE_ID");
        fieldEmployeeID.setTypeName("NUMERIC");
        fieldEmployeeID.setSize(19);
        fieldEmployeeID.setShouldAllowNull(false);
        fieldEmployeeID.setIsPrimaryKey(true);
        fieldEmployeeID.setUnique(false);
        fieldEmployeeID.setIsIdentity(false);
        fieldEmployeeID.setForeignKeyFieldName("CMP3_BV_EMPLOYEE.ID");
        table.addField(fieldEmployeeID);

        FieldDefinition fieldProjectID = new FieldDefinition();
        fieldProjectID.setName("PROJECTS_ID");
        fieldProjectID.setTypeName("NUMERIC");
        fieldProjectID.setSize(19);
        fieldProjectID.setShouldAllowNull(false);
        fieldProjectID.setIsPrimaryKey(true);
        fieldProjectID.setUnique(false);
        fieldProjectID.setIsIdentity(false);
        fieldProjectID.setForeignKeyFieldName("CMP3_BV_PROJECT.ID");
        table.addField(fieldProjectID);
        return table;
        
    }

}