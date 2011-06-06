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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sequencing.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.sequencing.*;

/**
 * Bug 5703242 - REGRESSION: IN 10.1.3 TABLESEQUENCE IGNORES TABLE QUALIFIER
 * Test configuring a table qualifier for different sequencing types.
 * A sequencing object should use the table qualifier set on the DatasourcePlatform.
 */
public class SequencingTableQualifierTest extends AutoVerifyTestCase {

    public static final int TABLE_SEQUENCE = 1;
    public static final int UNARY_TABLE_SEQUENCE = 2;
    
    private int sequenceType = -1;
    private Sequence oldSequence;
    private String oldTableQualifier;
    private Exception storedException;

    public SequencingTableQualifierTest(int sequenceType) {
        super();
        this.sequenceType = sequenceType; 
        setDescription("Test to check that setting a sequencing table qualifier works correctly");
    }
    
    public void setup() {
        // cache existing sequence
        this.oldSequence = getDatabaseSession().getLogin().getDefaultSequence();
        this.oldTableQualifier = getDatabaseSession().getLogin().getTableQualifier();
        
        // need to log out of session
        getDatabaseSession().logout();
        
        // create new Sequence object
        Sequence newSequence = null;
        if (this.sequenceType == TABLE_SEQUENCE) {
            newSequence = createTableSequence();
        } else if (this.sequenceType == UNARY_TABLE_SEQUENCE) {
            newSequence = createUnaryTableSequence();
        }
        
        // set new Sequence
        getDatabaseSession().getLogin().setDefaultSequence(newSequence);

        // reset the table qualifier
        getDatabaseSession().getLogin().setTableQualifier("INVALID_QUALIFIER");

        getDatabaseSession().login();
    }
    
    public Sequence createUnaryTableSequence() {
        return new UnaryTableSequence("ADDRESS_SEQ", 40, "COUNTER");
    }
    
    public Sequence createTableSequence() {
        String sequenceTableName = "SEQUENCE";
        if (getSession().getPlatform().getDefaultSequence().isTable()) {
            sequenceTableName = getSession().getPlatform().getQualifiedSequenceTableName();
        }
        return new TableSequence("", 50, sequenceTableName, "SEQ_NAME", "SEQ_COUNT"); 
    }
    
    public void test() {
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
        try {
            SeqTestClass2 testObject = new SeqTestClass2();
            testObject.setTest1("Sequence Test Object");
            testObject.setTest2("test2");
            
            uow.registerObject(testObject);
            uow.writeChanges();
        } catch (Exception e) {
            // we expect that the sequence table will not exist (INVALID_QUALIFIER.SEQUENCE)
            this.storedException = e;
        } finally {
            uow.release();
        }
    }
    
    public void verify() {
        if (this.storedException == null) {
            throw new TestErrorException("No exception thrown - sequencing table qualifier was not used");
        }
        // might as well do an additional Oracle error code check
        if (getSession().getPlatform().isOracle()) {
            if (storedException.getMessage().indexOf("ORA-00942") == -1) {
                throw new TestErrorException("Unexpected Oracle exception: " + storedException.getMessage());
            }
        }
    }
    
    public void reset() {
        getDatabaseSession().logout();
        getDatabaseSession().getLogin().setDefaultSequence(this.oldSequence);
        getDatabaseSession().getLogin().setTableQualifier(this.oldTableQualifier);
        getDatabaseSession().login();
    }

}
