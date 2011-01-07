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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

//java eXtension imports
import javax.persistence.EntityManagerFactory;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.server.Server;

//domain-specific (testing) imports
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper;
import static org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicTestHelper.DYNAMIC_PERSISTENCE_NAME;

public class MappingConfigTestSuite {

    //test fixtures
    static EntityManagerFactory emf = null;
    static JPADynamicHelper helper = null;
    static Server serverSession = null;
    
    @BeforeClass
    public static void setUp() throws Exception {
        emf = DynamicTestHelper.createEMF(DYNAMIC_PERSISTENCE_NAME);
        boolean isMySQL = JpaHelper.getServerSession(emf).getDatasourcePlatform().
            getClass().getName().contains("MySQLPlatform");
        assumeTrue(isMySQL);
        helper = new JPADynamicHelper(emf);
        DynamicEmployeeSystem.buildProject(helper);
        serverSession = JpaHelper.getServerSession(emf);
    }

    @AfterClass
    public static void tearDown() {
        serverSession.executeNonSelectingSQL("DROP TABLE D_SALARY");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PROJ_EMP");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PROJECT");
        serverSession.executeNonSelectingSQL("DROP TABLE D_PHONE");
        serverSession.executeNonSelectingSQL("DROP TABLE D_EMPLOYEE");
        serverSession.executeNonSelectingSQL("DROP TABLE D_ADDRESS");
        helper = null;
        emf.close();
        emf = null;
    }

    @Test
    public void verifyServerSession() throws Exception {
        assertNotNull(serverSession);
        assertTrue(serverSession.isConnected());
        assertTrue(serverSession.isServerSession());
        assertTrue(serverSession.getName().equals(DYNAMIC_PERSISTENCE_NAME));
    }

    @Test
    public void verifyEmployeeDescriptor() throws Exception {
        ClassDescriptor descriptor = serverSession.getDescriptorForAlias("Employee");
        assertNotNull(descriptor);
        assertEquals("Employee", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
        // Address Mapping
        OneToOneMapping addrMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("address");
        assertNotNull(addrMapping);
        assertTrue(addrMapping.isPrivateOwned());
        assertSame(serverSession.getDescriptorForAlias("Address"), addrMapping.getReferenceDescriptor());
        // PhoneNumber Mapping
        OneToManyMapping phoneMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("phoneNumbers");
        assertNotNull(phoneMapping);
        assertTrue(phoneMapping.isPrivateOwned());
        assertSame(serverSession.getDescriptorForAlias("PhoneNumber"), phoneMapping.getReferenceDescriptor());
        // Manager Mapping
        OneToOneMapping managerMapping = (OneToOneMapping)descriptor.getMappingForAttributeName("manager");
        assertNotNull(managerMapping);
        assertFalse(managerMapping.isPrivateOwned());
        assertSame(descriptor, managerMapping.getReferenceDescriptor());
        // Managed Employees Mapping
        OneToManyMapping managedEmployeesMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("managedEmployees");
        assertNotNull(managedEmployeesMapping);
        assertFalse(managedEmployeesMapping.isPrivateOwned());
        assertSame(descriptor, managedEmployeesMapping.getReferenceDescriptor());

        // Projects Mapping
        ManyToManyMapping projectsMapping = (ManyToManyMapping)descriptor.getMappingForAttributeName("projects");
        assertNotNull(projectsMapping);
        assertFalse(projectsMapping.isPrivateOwned());
        assertSame(serverSession.getDescriptorForAlias("Project"), projectsMapping.getReferenceDescriptor());
}

    @Test
    public void verifyAddressDescriptor() throws Exception {
        ClassDescriptor descriptor = serverSession.getDescriptorForAlias("Address");
        assertNotNull(descriptor);
        assertEquals("Address", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyPhoneNumberDescriptor() {
        ClassDescriptor descriptor = serverSession.getDescriptorForAlias("PhoneNumber");
        assertNotNull(descriptor);
        assertEquals("PhoneNumber", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyProjectDescriptor() {
        ClassDescriptor descriptor = serverSession.getDescriptorForAlias("Project");
        assertNotNull(descriptor);
        assertEquals("Project", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

}