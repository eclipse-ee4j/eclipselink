/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.CommunicationException;
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
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

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
        suite.addTest(new JPARCMLocalChangeSetTestSuite("testSetup"));
        addTests(suite);
        suite.addTest(new JPARCMLocalChangeSetTestSuite("testTeardown"));
        return suite;
    }
    
    public static TestSuite addTests(TestSuite suite){
        suite.setName("JPARCMLocalChangeSetTestSuite");
        suite.addTest(new JPARCMLocalChangeSetTestSuite("testAssociateNewEntityWithExistingEntityAfterFlush"));
        return suite;
    }
    
    public void testSetup() {
        ServerSession session = getServerSession();
        RemoteCommandManager rcm = new RemoteCommandManager(session);
        rcm.setShouldPropagateAsynchronously(true);
        session.setCommandManager(rcm);
        session.setShouldPropagateChanges(true);
        resetLocalConnection();
    }
    
    public void testTeardown() {
        getServerSession().setCommandManager(null);
    }
    
    public void resetLocalConnection() {
        LocalConnection localConn = getLocalConnection();
        if (localConn == null) {
            localConn = new LocalConnection(getServerSession());
            getServerSession().getCommandManager().getTransportManager().addConnectionToExternalService(localConn);
        }
        localConn.resetReceivedChangeSets();
        localConn.ignoreChanges(false);
    }
    
    public LocalConnection getLocalConnection() {
        TransportManager manager = getServerSession().getCommandManager().getTransportManager();
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
            Thread.sleep(2000);
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
        resetLocalConnection();
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
            
            LocalConnection conn = getLocalConnection();
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
            getLocalConnection().ignoreChanges(true);
            
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
            resetLocalConnection();
        }
    }

}
