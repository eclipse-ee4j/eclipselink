/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.tools.schemaframework.OracleSequenceDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sequencing.Sequence;

public class OracleNativeSeqInitTest extends AutoVerifyTestCase {
    // the following two modes test OracleSequenceDefinition.createOnDatabase method.

    // this mode tests creation of a new sequence after
    // the sequence was droped:
    // Next avilable sequence number will be 1.
    public static final int DROP_CREATE = 0;

    // this mode tests creation of a sequence after
    // the sequence was already created:
    // in this case SequenceDefinition.createOnDatabase method calls
    // alterOnDatabase method.
    // Next available sequence number will be the same as 
    // the next available sequence number before the method was called.
    public static final int CREATE_CREATE = 1;

    // the following two modes test alterOnDatabase method.

    // NEXTVAL_ALTER in the case NEXTVAL has been called before on the sequence
    // and therefore CURRVAL is defined: in this case the increment on the existing sequence
    // Next available sequence number will be the same as 
    // the next available sequence number before the method was called.
    public static final int NEXTVAL_ALTER = 2;

    // CREATE_ALTER in case NEXTVAL was never called on the sequence
    // after it was created and therefore CURRVAL is undefined:
    // in this case a new sequence is created, just like in DROP_CREATE case.
    // Next avilable sequence number will be 1.
    public static final int CREATE_ALTER = 3;

    protected Boolean usesNativeSequencingOriginal;
    protected Sequence originalSequence;
    protected int seqPreallocationSizeOriginal;
    protected int lastSeqNumberOriginal;
    protected Boolean usesBatchWritingOriginal;
    protected Boolean shouldCacheAllStatementsOriginal;

    protected int mode;
    protected int seqPreallocationSizeOld = 10;
    protected int seqPreallocationSize = 50;
    protected int idExpected;
    protected int id;
    protected ValidationException exception;
    protected String seqName;
    protected OracleSequenceDefinition sequenceDefinition;

    protected boolean shouldUseSchemaManager;
    protected SchemaManager schemaManager;

    public OracleNativeSeqInitTest(boolean shouldUseSchemaManager, int mode) {
        this.mode = mode;
        this.shouldUseSchemaManager = shouldUseSchemaManager;
        if (shouldUseSchemaManager) {
            if (mode == DROP_CREATE) {
                setName(getName() + " SchemaManager DROP CREATE");
                setDescription("Tests SchemaManager.createObject method");
            } else if (mode == CREATE_CREATE) {
                setName(getName() + " SchemaManager CREATE CREATE");
                setDescription("Tests SchemaManager.createObject method");
            } else if (mode == NEXTVAL_ALTER) {
                setName(getName() + " SchemaManager NEXTVAL_ALTER");
                setDescription("Tests SchemaManager.alterSequenceIncrement method");
            } else if (mode == CREATE_ALTER) {
                setName(getName() + " SchemaManager CREATE_ALTER");
                setDescription("Tests SchemaManager.alterSequenceIncrement method");
            }
        } else {
            if (mode == DROP_CREATE) {
                setName(getName() + " OracleSequenceDefinition DROP CREATE");
                setDescription("Tests OracleSequenceDefinition.createOnDatabase method");
            } else if (mode == CREATE_CREATE) {
                setName(getName() + " OracleSequenceDefinition  CREATE CREATE");
                setDescription("Tests OracleSequenceDefinition.createOnDatabase method");
            } else if (mode == NEXTVAL_ALTER) {
                setName(getName() + " OracleSequenceDefinition NEXTVAL_ALTER");
                setDescription("Tests OracleSequenceDefinition.alterOnDatabase method");
            } else if (mode == CREATE_ALTER) {
                setName(getName() + " OracleSequenceDefinition CREATE_ALTER");
                setDescription("Tests OracleSequenceDefinition.alterOnDatabase method");
            }
        }
    }

    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("This test works with Oracle only");
        }
        ClassDescriptor descriptor = getSession().getDescriptor(Employee.class);
        originalSequence = getSession().getPlatform().getSequence(descriptor.getSequenceNumberName());
        usesNativeSequencingOriginal = originalSequence instanceof NativeSequence;
        if (!usesNativeSequencingOriginal) {
            Sequence newSequence = new NativeSequence(originalSequence.getName(), originalSequence.getPreallocationSize());
            newSequence.onConnect(originalSequence.getDatasourcePlatform());
            getAbstractSession().getPlatform().addSequence(newSequence);
        }

        seqPreallocationSizeOriginal = originalSequence.getPreallocationSize();

        lastSeqNumberOriginal = getSession().getNextSequenceNumberValue(Employee.class).intValue() - 1;

        usesBatchWritingOriginal = Boolean.valueOf(getSession().getPlatform().usesBatchWriting());

        shouldCacheAllStatementsOriginal = Boolean.valueOf(getSession().getPlatform().shouldCacheAllStatements());

        getDatabaseSession().getSequencingControl().initializePreallocated();

        // Get sequence corresponding to Employee class
        String tableQualifier = getSession().getLogin().getTableQualifier();
        if(tableQualifier != "")
                        seqName = tableQualifier + "." + descriptor.getSequenceNumberName();
        else
                        seqName = descriptor.getSequenceNumberName();
        if (!descriptor.usesSequenceNumbers()) {
            throw new TestWarningException("Employee doesn't use sequencing");
        }

        sequenceDefinition = new OracleSequenceDefinition(seqName);
        if (shouldUseSchemaManager) {
            schemaManager = new SchemaManager(getDatabaseSession());

            // make sure that upcoming DROP and CREATE haven't been cached
            // and therefore for sure will go through
            getSession().getPlatform().setShouldCacheAllStatements(false);

            // This is the worst case scenario settings - SchemaManager should handle it.
            getSession().getPlatform().setUsesBatchWriting(true);
            getSession().getPlatform().setShouldCacheAllStatements(true);
        } else {
            getSession().getPlatform().setUsesBatchWriting(false);
            getSession().getPlatform().setShouldCacheAllStatements(false);
        }

        // all three modes start with dropping an existing sequence (if any)
        try {
            drop();
        } catch (DatabaseException exception) {
            // Ignore already deleted
        }

        if (mode == DROP_CREATE) {
            // sequence doesn't exist.
            // create sequence with seqPreallocationSize.
            // note that both increment and starting value are set to
            // sequenceDefinition.getIncrement()
            sequenceDefinition.setStartAndIncrement(seqPreallocationSize);
            create();

            // next available sequence number.
            idExpected = 1;
        } else if (mode == CREATE_CREATE) {
            // sequence doesn't exist,
            // create sequence with seqPreallocationSizeOld
            // note that both increment and starting value are set to
            // sequenceDefinition.getIncrement()
            sequenceDefinition.setStartAndIncrement(seqPreallocationSizeOld);
            create();

            // now sequence exists,
            // create sequence with seqPreallocationSize
            // Note that createOnDatabase will call alterOnDatabase
            sequenceDefinition.setStartAndIncrement(seqPreallocationSize);
            create();

            // next available sequence number.
            // note that the second createOnDatabase selects NEXTVAL during existance check,
            // because it is the first call to NEXTVAL, the starting sequence value is returned,
            // and this value was set to seqPreallocationSizeOld by the first createOnDatabase
            idExpected = 1 + seqPreallocationSizeOld;
        } else if (mode == NEXTVAL_ALTER) {
            // sequence doesn't exist,
            // create sequence with seqPreallocationSizeOld
            // note that both increment and starting value are set to
            // sequenceDefinition.getIncrement()
            sequenceDefinition.setStartAndIncrement(seqPreallocationSizeOld);
            create();

            // now sequence exists,
            // select NEXTVAL 
            // because it is the first call to NEXTVAL, the starting sequence value is returned,
            // and this value was set to seqPreallocationSizeOld by the first createOnDatabase
            getSession().executeSelectingCall(new SQLCall("SELECT " + seqName + ".NEXTVAL FROM DUAL"));

            // alter increment of sequence with seqPreallocationSize.
            sequenceDefinition.setStartAndIncrement(seqPreallocationSize);
            alter();

            // next available sequence number.
            // because there was just one call to NEXTVAL, the starting sequence value is returned,
            // and this value was set to seqPreallocationSizeOld by createOnDatabase
            idExpected = 1 + seqPreallocationSizeOld;
        } else if (mode == CREATE_ALTER) {
            // sequence doesn't exist,
            // create sequence with seqPreallocationSizeOld
            // note that both increment and starting value are set to
            // sequenceDefinition.getIncrement()
            sequenceDefinition.setStartAndIncrement(seqPreallocationSizeOld);
            create();

            // alter increment of sequence with seqPreallocationSize.
            sequenceDefinition.setStartAndIncrement(seqPreallocationSize);
            alter();

            // next available sequence number.
            idExpected = 1;
        }
        getSession().getPlatform().getSequence(descriptor.getSequenceNumberName()).setPreallocationSize(seqPreallocationSize);
    }

    public void test() {
        try {
            id = getSession().getNextSequenceNumberValue(Employee.class).intValue();
            exception = null;
        } catch (ValidationException ex) {
            id = 0;
            exception = ex;
        }
    }

    public void verify() {
        if (exception != null) {
            throw new TestErrorException("Sequence allocation failed", exception);
        }
        if (id != idExpected) {
            throw new TestErrorException("Wrong sequencing number");
        }
    }

    public void reset() {
        // make sure that upcoming DROP and CREATE haven't been cached
        // and therefore for sure will go through
        getSession().getPlatform().setShouldCacheAllStatements(false);
        // Drop the sequence
        drop();

        // Should setup Employee's sequence so that:
        // 1. seqPreallocationOriginal is used as an increment;
        sequenceDefinition.setIncrement(seqPreallocationSizeOriginal);
        // 2. the next available number is lastSeqNumberOriginal + 1
        sequenceDefinition.setStart(lastSeqNumberOriginal + seqPreallocationSizeOriginal);

        // Re-create sequence in its original state
        create();

        getDatabaseSession().getSequencingControl().initializePreallocated();
        getSession().getPlatform().getSequence(getSession().getDescriptor(Employee.class).getSequenceNumberName()).setPreallocationSize(seqPreallocationSizeOriginal);

        if (shouldCacheAllStatementsOriginal != null) {
            getSession().getPlatform().setShouldCacheAllStatements(shouldCacheAllStatementsOriginal.booleanValue());
        }
        if (usesBatchWritingOriginal != null) {
            getSession().getPlatform().setUsesBatchWriting(usesBatchWritingOriginal.booleanValue());
        }

        if ((usesNativeSequencingOriginal != null) && !usesNativeSequencingOriginal) {
            getAbstractSession().getPlatform().addSequence(originalSequence);
        }
    }

    protected void drop() {
        if (shouldUseSchemaManager) {
            schemaManager.dropObject(sequenceDefinition);
        } else {
            sequenceDefinition.dropFromDatabase(getAbstractSession());
        }
    }

    protected void create() {
        if (shouldUseSchemaManager) {
            schemaManager.createObject(sequenceDefinition);
        } else {
            sequenceDefinition.createOnDatabase(getAbstractSession());
        }
    }

    protected void alter() {
        if (shouldUseSchemaManager) {
            schemaManager.alterSequence(sequenceDefinition);
        } else {
            sequenceDefinition.alterOnDatabase(getAbstractSession());
        }
    }
}