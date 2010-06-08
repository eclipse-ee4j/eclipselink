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
package org.eclipse.persistence.testing.tests.feature;

import java.io.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sequencing.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test custom sequence-table access
 */
public class CustomSequenceTest extends AutoVerifyTestCase {

    /** the original setting */
    private Sequence originalSequence;

    /** the new sequence table name */
    private String newSeqTableName;

    /** the initial value of the counter for EMP_SEQ in the new table */
    private int initialSeqCount;

    /** the Employee being inserted using the new Seq Table */
    private Employee newEmployee;

    public CustomSequenceTest() {
        setDescription("This test verifies that sequencing can be customized using setUpdateSequenceQuery(DataModifyQuery) and setSelectSequenceNumberQuery(ValueReadQuery).");
    }

    protected void buildNEW_SEQUENCETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName(this.newSeqTableName);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("SEQ_NAME");
        field.setType(String.class);
        field.setSize(50);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("SEQ_COUNT");
        field1.setType(Long.class);
        field1.setSize(15);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        new SchemaManager((DatabaseSession)getSession()).replaceObject(tabledefinition);
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        // Change Sequence Query stuff back to normal
        getSession().getProject().getLogin().setDefaultSequence(this.originalSequence);
        // Don't want the sequence numbers obtained from another sequence table to be used with the regular one.
        // Note that before rollbackTransaction used to wipe out all sequencing numbers, but now
        // sequencing numbers remain no matter how transaction ends (unless there is no separate sequencing
        // connection used)
        ((DatabaseSession)getSession()).getSequencingControl().initializePreallocated();
    }

    public void setup() {
        org.eclipse.persistence.internal.databaseaccess.DatabasePlatform p = getSession().getProject().getLogin().getPlatform();

        /**
         * Build another sequence table for our custom sequencing.
         */
        this.newSeqTableName = new String("NEW_SEQUENCE");
        this.initialSeqCount = 1233;
        buildNEW_SEQUENCETable();
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("INSERT INTO " + this.newSeqTableName + "(" + p.getSequenceNameFieldName() + ", " + p.getSequenceCounterFieldName() + ") values ('EMP_SEQ', " + this.initialSeqCount + ")"));

        /**
         * Set the Sequence Query Stuff to use our new sequence table.
         */
        // Save the old settings 
        this.originalSequence = getSession().getLogin().getDefaultSequence();

        // Build the new custom queries
        DataModifyQuery newUpdateSeqQuery = new DataModifyQuery();
        newUpdateSeqQuery.addArgument("seqName");
        StringWriter writer1 = new StringWriter();
        writer1.write("UPDATE " + this.newSeqTableName);
        writer1.write(" SET " + p.getSequenceCounterFieldName());
        writer1.write(" = " + p.getSequenceCounterFieldName());
        writer1.write(" + " + p.getSequencePreallocationSize());
        writer1.write(" WHERE " + p.getSequenceNameFieldName());
        writer1.write(" = #seqName");
        newUpdateSeqQuery.setSQLString(writer1.toString());

        ValueReadQuery newSelectSeqNumQuery = new ValueReadQuery();
        newSelectSeqNumQuery.addArgument("seqName");
        StringWriter writer2 = new StringWriter();
        writer2.write("SELECT " + p.getSequenceCounterFieldName());
        writer2.write(" FROM " + this.newSeqTableName);
        writer2.write(" WHERE " + p.getSequenceNameFieldName());
        writer2.write(" = #seqName");
        newSelectSeqNumQuery.setSQLString(writer2.toString());

        // Set the new queries
        QuerySequence newSequence = new QuerySequence();
        newSequence.setUpdateQuery(newUpdateSeqQuery);
        newSequence.setSelectQuery(newSelectSeqNumQuery);
        getSession().getLogin().setDefaultSequence(newSequence);
        ((DatabaseSession)getSession()).getSequencingControl().resetSequencing();
        ((DatabaseSession)getSession()).getSequencingControl().initializePreallocated();
    }

    public void test() {
        beginTransaction();

        // write a new Employee to the DB
        this.newEmployee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        this.newEmployee.setFirstName("Brendan");
        this.newEmployee.setLastName("Rickey");
        UnitOfWork uow1 = getSession().acquireUnitOfWork();
        uow1.registerObject(this.newEmployee);
        uow1.commit();

        // update the Employee
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        Employee empRead = (Employee)uow2.readObject(this.newEmployee);
        empRead.setFirstName("Object");
        empRead.setLastName("People");
        uow2.commit();
    }

    public void verify() {
        //make sure the Employee was inserted and updated, and that the new Seq table was used.
        Employee verifyEmp = (Employee)getSession().readObject(this.newEmployee);
        if (verifyEmp.getId().intValue() != (this.initialSeqCount + 1)) {
            throw new TestErrorException("The new Sequence Table was not used! " + verifyEmp.getId());
        }
    }
}
