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
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import org.eclipse.persistence.indirection.*;

public class EmployeeForClientServerSession implements Serializable {

    /** Primary key, maped as a direct-to-field, BigDecimal -> NUMBER, that makes use of sequence numbers to generate the id. */
    public BigDecimal id;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;

    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    /** One-to-many mapping, employee references its collection of phone numbers using a foreign key in the phone's table. */
    public ValueHolderInterface phoneNumbers;

    public EmployeeForClientServerSession() {
        this.firstName = "";
        this.lastName = "";
        this.phoneNumbers = new ValueHolder(new Vector());

    }

    public Vector getPhoneNumbers() {
        return (Vector)phoneNumbers.getValue();
    }

    public void setPhoneNumbers(Vector phoneNumbers) {
        this.phoneNumbers.setValue(phoneNumbers);
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        getPhoneNumbers().addElement(phoneNumber);
        phoneNumber.setOwner(this);
    }

    public String getFirstName() {
        return firstName;
    }

    public BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
        definition.setName("EMPL");

        definition.addPrimaryKeyField("EMP_ID", java.math.BigDecimal.class, 15);

        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);

        definition.addField(field1);
        definition.addField("L_NAME", String.class, 20);

        return definition;
    }
}
