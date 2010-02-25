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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.employee;

//javase imports

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.sessions.DatabaseSession;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;

/*
 * Test cases verifying the use of employee-project.xml 
 */
public class MappingConfigTestSuite  {
    
    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @BeforeClass
    public static void setUp() {
        session = createSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        dynamicHelper = new DynamicHelper(session);
        DynamicEmployeeSystem.buildProject(dynamicHelper);
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE D_SALARY");
        session.executeNonSelectingSQL("DROP TABLE D_PROJ_EMP");
        session.executeNonSelectingSQL("DROP TABLE D_PROJECT");
        session.executeNonSelectingSQL("DROP TABLE D_PHONE");
        session.executeNonSelectingSQL("DROP TABLE D_EMPLOYEE");
        session.executeNonSelectingSQL("DROP TABLE D_ADDRESS");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @Test
    public void verifyEmployeeDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Employee");
        assertNotNull(descriptor);
        assertEquals("Employee", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());

        // Address Mapping
        OneToOneMapping addrMapping = (OneToOneMapping) descriptor.getMappingForAttributeName("address");
        assertNotNull(addrMapping);
        assertTrue(addrMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("Address"), addrMapping.getReferenceDescriptor());

        // PhoneNumber Mapping
        OneToManyMapping phoneMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("phoneNumbers");
        assertNotNull(phoneMapping);
        assertTrue(phoneMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("PhoneNumber"), phoneMapping.getReferenceDescriptor());

        // Manager Mapping
        OneToOneMapping managerMapping = (OneToOneMapping) descriptor.getMappingForAttributeName("manager");
        assertNotNull(managerMapping);
        assertFalse(managerMapping.isPrivateOwned());
        assertEquals(descriptor, managerMapping.getReferenceDescriptor());

        // Managed Employees Mapping
        OneToManyMapping managedEmployeesMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("managedEmployees");
        assertNotNull(managedEmployeesMapping);
        assertFalse(managedEmployeesMapping.isPrivateOwned());
        assertEquals(descriptor, managedEmployeesMapping.getReferenceDescriptor());

        // Projects Mapping
        ManyToManyMapping projectsMapping = (ManyToManyMapping) descriptor.getMappingForAttributeName("projects");
        assertNotNull(projectsMapping);
        assertFalse(projectsMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("Project"), projectsMapping.getReferenceDescriptor());
    }

    @Test
    public void verifyAddressDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Address");
        assertNotNull(descriptor);
        assertEquals("Address", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyPhoneNumberDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("PhoneNumber");
        assertNotNull(descriptor);
        assertEquals("PhoneNumber", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyProjectDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Project");
        assertNotNull(descriptor);
        assertEquals("Project", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

}