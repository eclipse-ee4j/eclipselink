package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sequencing.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.sequencing.*;

/**
 * The purpose of this test is to test that when Sequencing shouldAcquireValueAfterInsert is set,
 * the SQL which is generated for an insert query does not include column name and values for the 
 * sequence number column defined for the object being inserted.
 */
public class SequenceFieldRemovalForAcquireValueAfterInsertTest extends TestCase {

    protected ClassDescriptor descriptor;
    protected SQLStringListener sqlListener;
    protected ModifyRowModifier modifyRowModifier;
    protected Sequence oldSequence;
    protected String sqlString;

    public SequenceFieldRemovalForAcquireValueAfterInsertTest() {
        super();
        setDescription("Tests removing the sequence field from an SQL insert statement, when Sequencing shouldAcquireValueAfterInsert is true");
    }
    
    public void setup() {
        this.descriptor = getSession().getDescriptor(SeqTestClass1.class);
        
        String sequenceNumberName = descriptor.getSequenceNumberName();
        Sequence sequence = getSession().getLogin().getSequence(sequenceNumberName);
        DatabasePlatform platform = getDatabaseSession().getPlatform();

        if (!platform.supportsNativeSequenceNumbers() || !platform.supportsIdentity()) {
            throw new TestWarningException("Test only supported on platforms supporting sequencing and identity");                
        }
        
        // if the sequence is not native, change it to be native and cache the old sequence
        if (!sequence.isNative()) {
            this.oldSequence = sequence;
            NativeSequence newSequence = new NativeSequence(sequenceNumberName, 1);
            newSequence.setShouldAcquireValueAfterInsert(true);
            getDatabaseSession().getLogin().removeSequence(sequenceNumberName);
            getDatabaseSession().getLogin().addSequence(newSequence);
            getDatabaseSession().getSequencingControl().resetSequencing();
        }

        this.sqlListener = new SQLStringListener();
        this.descriptor.getEventManager().addListener(sqlListener);
        // need to modify the size of the row to force reprepare
        this.modifyRowModifier = new ModifyRowModifier();
        this.descriptor.getEventManager().addListener(modifyRowModifier);

        beginTransaction();
    }
    
    public void test() {
        UnitOfWork uow = getDatabaseSession().acquireUnitOfWork();
       
        SeqTestClass1 object = new SeqTestClass1();
        object.setTest1("aTestValue");
        object.setTest2(ModifyRowModifier.OMISSION_MARKER); // marker to have modify row altered
        
        uow.registerObject(object);
        try {
            uow.commit();
            sqlString = this.sqlListener.getSQLString();
        } catch (DatabaseException d) {
            // catch exception, don't rethrow - only need SQL generated
            sqlString = ((DatabaseCall)d.getCall()).getSQLString();
        }

        if (sqlString == null) {
            throw new TestErrorException("Generated SQL string is null");
        }
    }
    
    public void reset() {
        rollbackTransaction();
        if (sqlListener != null) {
            this.descriptor.getEventManager().removeListener(sqlListener);
        }
        if (modifyRowModifier != null) {
            this.descriptor.getEventManager().removeListener(modifyRowModifier);
        }
        
        // if sequencing was changed, reset to the previous sequence
        if (oldSequence != null) {
            getDatabaseSession().getLogin().removeSequence(descriptor.getSequenceNumberName());
            getDatabaseSession().getLogin().addSequence(oldSequence);
            this.oldSequence = null;
            getDatabaseSession().getSequencingControl().resetSequencing();
        }
    }
    
    public void verify() {
        String fieldName = descriptor.getSequenceNumberField().getName();
        String qualifiedFieldName = descriptor.getSequenceNumberField().getQualifiedName();

        if (sqlString.indexOf(qualifiedFieldName) != -1 || sqlString.indexOf(fieldName) != -1) {
            throw new TestErrorException("Invalid SQL String - sequence field " + fieldName + " was included in SQL: (" + sqlString + ")"
                + "- incorrect for shouldAcquireValueAfterInsert = true");
        }
    }    
    
    class SQLStringListener extends DescriptorEventAdapter {
    
        protected String sqlString;
        
        public void postInsert(DescriptorEvent event) {
           sqlString = event.getQuery().getSQLString();
        }
        
        public String getSQLString() {
            return this.sqlString;
        }

    }
    
    class ModifyRowModifier extends DescriptorEventAdapter {

        public static final String OMISSION_MARKER = "omit_this_field";

        public void aboutToInsert(DescriptorEvent event) {
            Record modifyRow = event.getRecord();
            Object[] keys = modifyRow.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                Object key = keys[i];
                Object value = modifyRow.get(key);
                if (value != null && value.equals(OMISSION_MARKER)) {
                    modifyRow.remove(key);
                }
            }
        }
        
    }
    
}
