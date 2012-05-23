/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.io.*;

import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a 1:M relationship from an employee. Since many people have various numbers
 *                                they can be contacted at the type describes where the phone number could reach the
 *                                Employee.
 */
public class IsolatedPhoneNumber implements Serializable {

    /** Holds values such as Home, Work, Cellular, Pager, Fax, etc.  Since the combination of the Employee's ID and
    the type field are what makes the entry in the database unique the type fields must be unique within an
    Employee's Vector of PhoneNumbers.*/
    public String type;
    public String areaCode;

    /** 7 digit number with no hyphen, this is added during toString() only*/
    public String number;

    /** Owner maintains the 1:1 mapping to an Employee (required for 1:M relationship in Employee) */
    public ValueHolderInterface owner;
    public boolean hasChanges = false;

    public IsolatedPhoneNumber() {
        this("home", "###", "#######");
    }

    public IsolatedPhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = new ValueHolder();
    }

    /**
     * Builds the table definitions for this class
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition buildIsolatedTableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_PHONE");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(4);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("P_NUMBER");
        field3.setTypeName("VARCHAR");
        field3.setSize(8);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        return tabledefinition;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public IsolatedEmployee getOwner() {
        return (IsolatedEmployee)owner.getValue();
    }

    public String getType() {
        return type;
    }

    public void setAreaCode(String areaCode) {
        setChanged();
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        setChanged();
        this.number = number;
    }

    public void setOwner(IsolatedEmployee owner) {
        setChanged();
        this.owner.setValue(owner);
    }

    public void setType(String type) {
        setChanged();
        this.type = type;
    }

    /**
     * Print the phone.
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("IsolatedPhoneNumber [");
        writer.write(getType());
        writer.write("]: (");
        writer.write(this.getAreaCode());
        writer.write(")");
        writer.write(this.getNumber().substring(0, 3));
        writer.write("-");
        writer.write(this.getNumber().substring(3, 7));
        return writer.toString();
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public void clearChanges() {
        hasChanges = false;
    }

    public void setChanged() {
        hasChanges = true;
    }
}
