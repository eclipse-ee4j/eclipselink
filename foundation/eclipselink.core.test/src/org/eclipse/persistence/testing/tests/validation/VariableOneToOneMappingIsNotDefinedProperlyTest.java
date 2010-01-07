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
package org.eclipse.persistence.testing.tests.validation;

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.ObjectReferenceChangeRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.interfaces.Actor;


//Created by Ian Reid
//Date: Mar 27, 2k3

public class VariableOneToOneMappingIsNotDefinedProperlyTest extends ExceptionTest {
    public VariableOneToOneMappingIsNotDefinedProperlyTest(String testMode) {
        if ((testMode.endsWith("IntoRow") || testMode.equalsIgnoreCase("writeFromObjectIntoRow"))) {
            this.testMode = 0;
        } else if ((testMode.endsWith("ChangeRecord") || testMode.equalsIgnoreCase("writeFromObjectIntoRowWithChangeRecord"))) {
            this.testMode = 1;
        } else if ((testMode.endsWith("WhereClause") || testMode.equalsIgnoreCase("writeFromObjectIntoRowForWhereClause"))) {
            this.testMode = 2;
        } else {
            this.testMode = -1;
        }
        setDescription("This tests Variable One-To-One Mapping Is Not Defined Properly (" + testMode + ") (TL-ERROR 166)");
    }
    private int testMode = 0; //allows this same class to be used for all three methods which throw the error
    VariableOneToOneMapping mapping;
    DatabaseField sourceField;
    String targetQueryKeyName;
    ObjectReferenceChangeRecord changeRecord;
    DeleteObjectQuery deleteObjectQuery;
    DatabaseRecord databaseRow;
    ClassDescriptor descriptor;
    Actor actor;


    protected void setup() {

        descriptor = ((DatabaseSession)getSession()).getDescriptor(Actor.class);
        mapping = (VariableOneToOneMapping)descriptor.getMappingForAttributeName("program");

        sourceField = new DatabaseField("ACTOR.PROGRAM_ID");
        targetQueryKeyName = (String)mapping.getSourceToTargetQueryKeyNames().get(sourceField);
        mapping.addForeignQueryKeyName("ACTOR.PROGRAM_ID", "name2");
        mapping.getForeignKeyFields().removeElement(sourceField);

        actor = Actor.example4();
        databaseRow = new DatabaseRecord();

        if (testMode == 0) {
            //nothing extra needed
        } else if (testMode == 1) {
            ObjectChangeSet changeSet = new ObjectChangeSet(new Vector(), descriptor, actor, new UnitOfWorkChangeSet(), true);
            changeRecord = new ObjectReferenceChangeRecord(changeSet);
            changeRecord.setNewValue(changeSet);
        } else if (testMode == 2) {
            deleteObjectQuery = new DeleteObjectQuery(actor);
            deleteObjectQuery.setSession((AbstractSession)getSession());
        }

        expectedException = DescriptorException.variableOneToOneMappingIsNotDefinedProperly(mapping, descriptor, targetQueryKeyName);

    }

    public void reset() {
        mapping.addForeignQueryKeyName("ACTOR.PROGRAM_ID", targetQueryKeyName);
        mapping.getForeignKeyFields().removeElement(sourceField);
    }

    public void test() {
        try {
            if (testMode == 0) {
                mapping.writeFromObjectIntoRow(actor, databaseRow, (AbstractSession)getSession()); //test one
            } else if (testMode == 1) {
                mapping.writeFromObjectIntoRowWithChangeRecord((org.eclipse.persistence.internal.sessions.ChangeRecord)changeRecord, databaseRow, (AbstractSession)getSession()); //test two
            } else if (testMode == 2) {
                mapping.writeFromObjectIntoRowForWhereClause(deleteObjectQuery, databaseRow); //test three           
            } else {
                throw new org.eclipse.persistence.testing.framework.TestProblemException("Invalid method test name for VariableOneToOneMappingIsNotDefinedProperlyTest");
            }
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
