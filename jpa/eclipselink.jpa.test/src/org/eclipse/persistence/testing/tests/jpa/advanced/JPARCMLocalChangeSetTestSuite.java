/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.coordination.ConnectToHostCommand;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import org.eclipse.persistence.sessions.changesets.ChangeRecord;
import org.eclipse.persistence.sessions.changesets.DirectToFieldChangeRecord;
import org.eclipse.persistence.sessions.changesets.ObjectReferenceChangeRecord;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.sessions.coordination.ServiceId;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableFalseEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableForceProtectedEntity;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JPARCMLocalChangeSetTestSuite
 * Simple low resource/setup JPA test suite & framework allowing for local 
 * testing and verification of ChangeSets distributed by RCM.
 * @author dminsky
 */
public class JPARCMLocalChangeSetTestSuite extends JUnitTestCase {
    
    public JPARCMLocalChangeSetTestSuite() {
        super();
    }
    
    public JPARCMLocalChangeSetTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JPARCMLocalChangeSetTestSuite");
        addTestToSuite("testSetup", suite);
        addTestToSuite("testAssociateNewEntityWithExistingEntityAfterFlush", suite);
        if (!JUnitTestCase.isJPA10()) {
            addTestToSuite("testPropagateProtectedForeignKeyValuesForNewObject", suite);
            addTestToSuite("testPropagateProtectedForeignKeyValuesForExistingObjectWithSendChanges", suite);
            addTestToSuite("testPropagateProtectedForeignKeyValuesForExistingObjectWithSendNewObjects", suite);
        }
        return suite;
    }
    
    public static void addTestToSuite(String testName, TestSuite suite) {
        suite.addTest(new JPARCMLocalChangeSetTestSuite(testName));
    }
    
    public void testSetup() {
        ServerSession session = getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        clearServerSessionCache();
        
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession("MulitPU-1"));
        clearCache("MulitPU-1");  
    }
    
    public void initializeRCMOnSession(ServerSession session) {
        RemoteCommandManager rcm = new RemoteCommandManager(session);
        rcm.setShouldPropagateAsynchronously(true);
        session.setCommandManager(rcm);
        session.setShouldPropagateChanges(true);
        resetLocalConnection(session);
    }
    
    public void resetRCMOnSession(ServerSession session) {
        session.setCommandManager(null);
    }
    
    public void resetLocalConnection(ServerSession session) {
        LocalConnection localConn = getLocalConnection(session);
        if (localConn == null) {
            localConn = new LocalConnection(session);
            session.getCommandManager().getTransportManager().addConnectionToExternalService(localConn);
        }
        localConn.resetReceivedChangeSets();
        localConn.ignoreChanges(false);
    }
    
    public LocalConnection getLocalConnection(ServerSession session) {
        TransportManager manager = session.getCommandManager().getTransportManager();
        return (LocalConnection) manager.getConnectionsToExternalServices().get(LocalConnection.class.getSimpleName());
    }
    
    protected class LocalConnection extends RemoteConnection {
        
        protected AbstractSession session;
        protected boolean ignoreChanges;
        protected List<UnitOfWorkChangeSet> receivedChangeSets;
        
        public LocalConnection(AbstractSession session) {
            super();
            setSession(session);
            resetReceivedChangeSets();
            ignoreChanges(false);
        }
        
        @Override
        public Object executeCommand(Command command) throws CommunicationException {
            if (!shouldIgnoreChanges()) {
                if (command instanceof ConnectToHostCommand) {
                    ServiceId commandServiceId = command.getServiceId();
                    setServiceId(new ServiceId(commandServiceId.getChannel(), this.getClass().getSimpleName(), commandServiceId.getURL()));
                } else if (command instanceof MergeChangeSetCommand) {
                    UnitOfWorkChangeSet uowCs = ((MergeChangeSetCommand)command).getChangeSet(getSession());
                    getReceivedChangeSets().add(uowCs);
                }
            }
            return null;
        }
        
        @Override
        public Object executeCommand(byte[] commandBytes) throws CommunicationException {
            Command command = (Command) JavaSerializer.instance.deserialize(commandBytes, null);
            return executeCommand(command);
        }
        
        public List<UnitOfWorkChangeSet> getReceivedChangeSets() {
            return this.receivedChangeSets;
        }
        
        public void setReceivedChangeSets(List<UnitOfWorkChangeSet> changesReceived) {
            this.receivedChangeSets = changesReceived;
        }
        
        public void resetReceivedChangeSets() {
            setReceivedChangeSets(new ArrayList<UnitOfWorkChangeSet>());
        }
        
        public void ignoreChanges(boolean ignoreChanges) {
            this.ignoreChanges = ignoreChanges;
        }
        
        public boolean shouldIgnoreChanges() {
            return this.ignoreChanges;
        }

        public AbstractSession getSession() {
            return session;
        }

        public void setSession(AbstractSession session) {
            this.session = session;
        }
        
    }
    
    public void allowForChangePropagation() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }
    
    /*
     * EclipseLink Bug 477399 
     * Test creating a new Entity, persisting and flushing it, and then associating it
     * with an existing Entity, making a further change to it, and committing the transaction.
     * A full set of changes for the new Entity is expected to be distributed. The defect which this bug  
     * resolves only has changes for country, province & postal code (and version, automatically).
     */
    public void testAssociateNewEntityWithExistingEntityAfterFlush() {
        initializeRCMOnSession(getServerSession());
        
        // reset cache co-ordination type
        int oldEmployeeCacheSyncType = getServerSession().getDescriptor(Employee.class).getCacheSynchronizationType();
        int oldAddressCacheSyncType = getServerSession().getDescriptor(Address.class).getCacheSynchronizationType();
        getServerSession().getDescriptor(Employee.class).setCacheSynchronizationType(ClassDescriptor.SEND_OBJECT_CHANGES);
        getServerSession().getDescriptor(Address.class).setCacheSynchronizationType(ClassDescriptor.SEND_OBJECT_CHANGES);
        
        // create an Employee to modify and clear the cache
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Employee employee = new Employee();
        employee.setFirstName("Bob");
        employee.setLastName("Smith");
        employee.setMale();
        em.persist(employee);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        clearCache();
        resetLocalConnection(getServerSession());
        try {
            em = createEntityManager();
            beginTransaction(em);
            
            // create address, persist and flush
            Address address = new Address();
            address.setStreet("123 Main St.");
            address.setCity("Ottawa");
            address.setProvince("Ontario");
            address.setCountry("Canada");
            address.setPostalCode("K1S2B2");
            
            em.persist(address);
            em.flush(); // important
            
            // modify address
            address.setCountry("USA");
            address.setProvince("Kansas");
            address.setPostalCode("66067");
            
            // find employee and associate with new address
            Employee employeeFound = em.find(Employee.class, employee.getId());
            employeeFound.setAddress(address);
            
            commitTransaction(em);
            closeEntityManager(em);
            em = null;
            
            allowForChangePropagation();
            
            LocalConnection conn = getLocalConnection(getServerSession());
            assertEquals("Should have received one ObjectChangeSet", 1, conn.getReceivedChangeSets().size());
            UnitOfWorkChangeSet uowcs = conn.getReceivedChangeSets().get(0);
            
            Map<ObjectChangeSet, ObjectChangeSet> csMap = uowcs.getAllChangeSets();
            for (ObjectChangeSet ocs : csMap.keySet()) {
                if (!ocs.getClassName().equals(Employee.class.getName())) {
                    fail("Changes are only expected in this test for an Employee");
                } else {
                    // Changes are an object reference change - Employee->Address ('address')
                    List<ChangeRecord> employeeChanges = ocs.getChanges();
                    boolean foundAddressChange = false;
                    for (ChangeRecord employeeChange : employeeChanges) {
                        if (employeeChange.getAttribute().equals("address")) {
                            foundAddressChange = true;
                            ObjectReferenceChangeRecord addressRecord = (ObjectReferenceChangeRecord) employeeChange;
                            assertNotNull("Employee->Address changes should not be null", addressRecord);
                            
                            ObjectChangeSet addressCs = (ObjectChangeSet) addressRecord.getNewValue();
                            assertNotNull("Address ChangeSet newValue should not be null", addressCs);
                            
                            // Validate the direct to field attribute values which have been changed
                            Map<String, ChangeRecord> addressChanges = new HashMap<String, ChangeRecord>();
                            for (ChangeRecord record : addressCs.getChanges()) {
                                addressChanges.put(record.getAttribute(), record);
                            }
                            DirectToFieldChangeRecord countryRecord = (DirectToFieldChangeRecord)addressChanges.get("country");
                            DirectToFieldChangeRecord provinceRecord = (DirectToFieldChangeRecord)addressChanges.get("province");
                            DirectToFieldChangeRecord postalCodeRecord = (DirectToFieldChangeRecord)addressChanges.get("postalCode");
                            DirectToFieldChangeRecord idRecord = (DirectToFieldChangeRecord)addressChanges.get("ID");
                            DirectToFieldChangeRecord streetRecord = (DirectToFieldChangeRecord)addressChanges.get("street");
                            DirectToFieldChangeRecord versionRecord = (DirectToFieldChangeRecord)addressChanges.get("version");
                            DirectToFieldChangeRecord cityRecord = (DirectToFieldChangeRecord)addressChanges.get("city");
                            
                            // Validate change existence
                            assertNotNull("ID should not be null", idRecord);
                            assertNotNull("Street should not be null", streetRecord);
                            assertNotNull("City should not be null", cityRecord);
                            assertNotNull("Country should not be null", countryRecord);
                            assertNotNull("Province should not be null", provinceRecord);
                            assertNotNull("PostalCode should not be null", postalCodeRecord);
                            assertNotNull("Version should not be null", versionRecord);
                            
                            // Validate changes against source object
                            assertEquals("Country should be equal.", address.getCountry(), countryRecord.getNewValue());
                            assertEquals("Province should be equal.", address.getProvince(), provinceRecord.getNewValue());
                            assertEquals("PostalCode should be equal.", address.getPostalCode(), postalCodeRecord.getNewValue());
                            assertEquals("ID should be equal.", address.getID(), idRecord.getNewValue());
                            assertEquals("Street should be equal.", address.getStreet(), streetRecord.getNewValue());
                            assertEquals("Version should be equal.", address.getVersion(), versionRecord.getNewValue());
                            assertEquals("City should be equal.", address.getCity(), cityRecord.getNewValue());
                        }
                    }
                    if (!foundAddressChange) {
                        fail("No address changes were found when processing ChangeSets");
                    }
                }
            }
        } finally {
            // set local connection to temporarily ignore any changes made
            getLocalConnection(getServerSession()).ignoreChanges(true);
            
            // remove test data for the Employee & Address created
            em = createEntityManager();
            Employee empToDelete = em.find(Employee.class, employee.getId());
            if (empToDelete != null) {
                beginTransaction(em);
                if (empToDelete.getAddress() != null) {
                    em.remove(empToDelete.getAddress());
                }
                em.remove(empToDelete);
                commitTransaction(em);
            }
            closeEntityManager(em);
            
            // replace cache coordination type values on existing Descriptors
            getServerSession().getDescriptor(Employee.class).setCacheSynchronizationType(oldEmployeeCacheSyncType);
            getServerSession().getDescriptor(Address.class).setCacheSynchronizationType(oldAddressCacheSyncType);
            // completely reset local connection for the next test
            //resetLocalConnection(getServerSession());
            resetRCMOnSession(getServerSession());
        }
    }
    
    /*
     * EclipseLink Bug 486845 
     * Test creating two new objects, one cacheable, one non-cacheable, associating them,
     * and propagating changes with the SEND_NEW_OBJECTS_WITH_CHANGES setting. The non-cacheable object
     * will not be propagated, but the protected foreign key values for the non-cacheable object should be included 
     * in the ObjectChangeSet for the cacheable object referencing the non-cacheable object.
     */
    public void testPropagateProtectedForeignKeyValuesForNewObject() {
        String puName = "MulitPU-1";
        
        ServerSession session = getServerSession(puName);
        initializeRCMOnSession(session); // must be initialized before any use
        
        ClassDescriptor cacheableDescriptor = session.getClassDescriptor(CacheableForceProtectedEntity.class);
        ClassDescriptor nonCacheableDescriptor = session.getClassDescriptor(CacheableFalseEntity.class);
        cacheableDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);

        CacheableForceProtectedEntity cacheableEntity = new CacheableForceProtectedEntity();
        cacheableEntity.setName("Bob");
        CacheableFalseEntity nonCacheableEntity = new CacheableFalseEntity();
        cacheableEntity.setCacheableFalse(nonCacheableEntity);
        
        resetLocalConnection(session);
        
        try {
            EntityManager em = createEntityManager(puName);
            beginTransaction(em);
            
            em.persist(cacheableEntity);
            em.persist(nonCacheableEntity);
            
            commitTransaction(em);
            closeEntityManager(em);
            em = null;
            
            allowForChangePropagation();
            
            LocalConnection conn = getLocalConnection(session);
            assertEquals("Should have received one UnitOfWorkChangeSet", 1, conn.getReceivedChangeSets().size());
            UnitOfWorkChangeSet uowcs = conn.getReceivedChangeSets().get(0);
            
            Map<ObjectChangeSet, ObjectChangeSet> csMap = uowcs.getAllChangeSets();
            for (ObjectChangeSet ocs : csMap.keySet()) {
                AbstractRecord protectedForeignKeys = ocs.getProtectedForeignKeys();
                Vector<DatabaseField> fkFields = cacheableDescriptor.getMappingForAttributeName("cacheableFalse").getFields();
                
                assertNotNull("ObjectChangeSet should have a non-null protected foreign key", protectedForeignKeys);
                assertEquals("ObjectChangeSet's protectedForeignKeys should be non-empty", fkFields.size(), protectedForeignKeys.size());
                
                DatabaseField pkField = fkFields.get(0);
                Object pkValue = protectedForeignKeys.get(pkField);
                assertEquals("ObjectChangeSet's protectedForeignKeys should contain a valid FK value", pkValue, nonCacheableEntity.getId());
            }
        } finally {
            // set local connection to temporarily ignore any changes made
            getLocalConnection(session).ignoreChanges(true);
            
            // remove test data for the Employee & Address created
            EntityManager em = createEntityManager(puName);
            CacheableForceProtectedEntity toDelete = em.find(CacheableForceProtectedEntity.class, cacheableEntity.getId());
            if (toDelete != null) {
                beginTransaction(em);
                if (toDelete.getCacheableFalse() != null) {
                    em.remove(toDelete.getCacheableFalse());
                }
                em.remove(toDelete);
                commitTransaction(em);
            }
            closeEntityManager(em);
            
            // replace cache coordination type values on existing Descriptors
            cacheableDescriptor.setCacheSynchronizationType(ClassDescriptor.DO_NOT_SEND_CHANGES);
            
            // completely reset local connection for the next test
            resetLocalConnection(session);
            resetRCMOnSession(session);
        }
    }
    
    /*
     * EclipseLink Bug 486845
     * Common utility test method.
     */
    private void testPropagateProtectedForeignKeyValuesForExistingObject(int cacheSynchronizationType) {
        String puName = "MulitPU-1";
        
        ServerSession session = getServerSession(puName);
        
        ClassDescriptor cacheableDescriptor = session.getClassDescriptor(CacheableForceProtectedEntity.class);
        ClassDescriptor nonCacheableDescriptor = session.getClassDescriptor(CacheableFalseEntity.class);
        cacheableDescriptor.setCacheSynchronizationType(cacheSynchronizationType);

        CacheableForceProtectedEntity cacheableEntity = new CacheableForceProtectedEntity();
        cacheableEntity.setName("Bob");
        CacheableFalseEntity nonCacheableEntity = new CacheableFalseEntity();
        cacheableEntity.setCacheableFalse(nonCacheableEntity);
        
        EntityManager em = createEntityManager(puName);
        beginTransaction(em);
        
        em.persist(cacheableEntity);
        em.persist(nonCacheableEntity);
        
        commitTransaction(em);
        closeEntityManager(em);
        em = null;
        
        initializeRCMOnSession(session); // must be initialized before any use
        
        try {
            em = createEntityManager(puName);
            beginTransaction(em);
            
            CacheableForceProtectedEntity cacheableEntityRead = em.find(CacheableForceProtectedEntity.class, cacheableEntity.getId());
            assertNotNull(cacheableEntityRead);
            assertNotNull(cacheableEntityRead.getCacheableFalse());
            
            // new non-cacheable object
            CacheableFalseEntity newCacheableFalseEntity = new CacheableFalseEntity();
            cacheableEntityRead.setCacheableFalse(newCacheableFalseEntity);
            em.persist(newCacheableFalseEntity);
            
            commitTransaction(em); // protected FKs should be set into cache key
            closeEntityManager(em);
            em = null;
            
            allowForChangePropagation();
            
            LocalConnection conn = getLocalConnection(session);
            List<UnitOfWorkChangeSet> changeSets = conn.getReceivedChangeSets();
            for (UnitOfWorkChangeSet changeSet : changeSets) {
                assertNull(changeSet.getSession());
            }
            assertEquals("Should have received one UnitOfWorkChangeSet", 1, conn.getReceivedChangeSets().size());
            UnitOfWorkChangeSet uowcs = conn.getReceivedChangeSets().get(0);
            
            Map<ObjectChangeSet, ObjectChangeSet> csMap = uowcs.getAllChangeSets();
            for (ObjectChangeSet ocs : csMap.keySet()) {
                AbstractRecord protectedForeignKeys = ocs.getProtectedForeignKeys();
                Vector<DatabaseField> fkFields = cacheableDescriptor.getMappingForAttributeName("cacheableFalse").getFields();
                
                assertNotNull("ObjectChangeSet should have a non-null protected foreign key", protectedForeignKeys);
                assertEquals("ObjectChangeSet's protectedForeignKeys should be non-empty", fkFields.size(), protectedForeignKeys.size());
                
                DatabaseField pkField = fkFields.get(0);
                Object pkValue = protectedForeignKeys.get(pkField);
                
                int idExpected = cacheableEntityRead.getCacheableFalse().getId();
                assertEquals("ObjectChangeSet's protectedForeignKeys should contain a valid FK value", pkValue, idExpected);
            }
        } finally {
            // set local connection to temporarily ignore any changes made
            getLocalConnection(session).ignoreChanges(true);
            
            // remove test data for the Employee & Address created
            em = createEntityManager(puName);
            beginTransaction(em);
            CacheableForceProtectedEntity toDelete = em.find(CacheableForceProtectedEntity.class, cacheableEntity.getId());
            if (toDelete != null) {
                if (toDelete.getCacheableFalse() != null) {
                    em.remove(toDelete.getCacheableFalse());
                }
                em.remove(toDelete);
            }
            CacheableFalseEntity toDelete2 = em.find(CacheableFalseEntity.class, nonCacheableEntity.getId());
            if (toDelete2 != null) {
                em.remove(toDelete2);
            }
            commitTransaction(em);
            closeEntityManager(em);
            
            // replace cache coordination type values on existing Descriptors
            cacheableDescriptor.setCacheSynchronizationType(ClassDescriptor.DO_NOT_SEND_CHANGES);
            
            // completely reset local connection for the next test
            resetLocalConnection(session);
            resetRCMOnSession(session);
        }
    }
    
    /*
     * EclipseLink Bug 486845
     * Test modifying the non-cacheable object associated with a cacheable object and propagating changes with 
     * the SEND_OBJECT_CHANGES setting. The non-cacheable object will not be propagated, but the 
     * protected foreign key values for the new non-cacheable object should be included 
     * in the ObjectChangeSet for the cacheable object referencing the non-cacheable object.
     */
    public void testPropagateProtectedForeignKeyValuesForExistingObjectWithSendChanges() {
        testPropagateProtectedForeignKeyValuesForExistingObject(ClassDescriptor.SEND_OBJECT_CHANGES);
    }
        
    /*
     * EclipseLink Bug 486845
     * Test modifying the non-cacheable object associated with a cacheable object and propagating changes with 
     * the SEND_NEW_OBJECTS_WITH_CHANGES setting. The non-cacheable object will not be propagated, but the 
     * protected foreign key values for the new non-cacheable object should be included 
     * in the ObjectChangeSet for the cacheable object referencing the non-cacheable object.
     */
    public void testPropagateProtectedForeignKeyValuesForExistingObjectWithSendNewObjects() {
        testPropagateProtectedForeignKeyValuesForExistingObject(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);
    }


}
