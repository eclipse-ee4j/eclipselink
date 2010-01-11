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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test the identity maps and cache hits.
 */
public class IdentityMapTestSuite extends TestSuite {
    public IdentityMapTestSuite() {
        setDescription("This suite tests the functionality of the identity maps.");
    }

    protected void addMultipleIdentityTests(TestSuite suite, IdentityMap identityMap) {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        Vector primaryKeys = new Vector();
        Vector primaryKey1;
        Vector primaryKey2;
        Vector employees = new Vector();

        employee1.setFirstName("Wayne");
        employee1.setLastName("Gretzky");
        employee2.setFirstName("Eric");
        employee2.setLastName("Lindros");
        employees.addElement(employee1);
        employees.addElement(employee2);
        primaryKey1 = new Vector();
        primaryKey1.addElement(new Integer(99));
        primaryKey2 = new Vector();
        primaryKey2.addElement(new Integer(88));

        primaryKeys.addElement(primaryKey1);
        primaryKeys.addElement(primaryKey2);

        suite.addTest(new MultipleRegisterTest(identityMap, primaryKeys, employees));
        suite.addTest(new MultipleDeleteFromIdentityMapTest(identityMap, primaryKeys, employees, primaryKeys));
    }

    public void addTests() {
        addTest(getFullIdentityMapSuite());
        addTest(getNoIdentityMapSuite());
        addTest(getCacheIdentityMapSuite());
        addTest(getSoftCacheWeakIdentityMapSuite());
        addTest(getHardCacheWeakIdentityMapSuite());
        addTest(getWeakIdentityMapSuite());
        addTest(getSoftIdentityMapSuite());
        addTest(getDeleteWithGarbageCollectionTestSuite(new CacheIdentityMap(100)));
        addTest(new GetSizeRecurseOptionTest());
        addTest(new ClearLastAccessedIdentityMapTest());
        addTest(new InitializeIdentityMapByDescriptorJavaClassTest());
        // Bug 5840635
        addTest(new CleanupCacheKeyCorrectnessTest());
    }

    private TestSuite getCacheIdentityMapSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Cache IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the CacheIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(CacheIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(CacheIdentityMap.class));
        suite.addTest(new SetWriteLockInIdentityMapTest(CacheIdentityMap.class));
        return suite;
    }

    private TestSuite getFullIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("Full IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the FullIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(FullIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(FullIdentityMap.class));

        addMultipleIdentityTests(suite, new FullIdentityMap(100));
        suite.addTest(new RemoveFromIdentityMapReturnTest());
        suite.addTest(new SetWriteLockInIdentityMapTest(FullIdentityMap.class));

        return suite;
    }

    private TestSuite getHardCacheWeakIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("Hard Cache Weak IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the HardCacheWeakIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(HardCacheWeakIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(HardCacheWeakIdentityMap.class));
        addMultipleIdentityTests(suite, new HardCacheWeakIdentityMap(100, null));
        suite.addTest(new SetWriteLockInIdentityMapTest(HardCacheWeakIdentityMap.class));
        
        suite.addTest(new HardCacheWeakIdentityMapTest());

        return suite;
    }

    private TestSuite getNoIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("No IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the NoIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(NoIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(NoIdentityMap.class));
        suite.addTest(new SetWriteLockInIdentityMapTest(NoIdentityMap.class));

        return suite;
    }

    private TestSuite getSoftCacheWeakIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("Soft Cache Weak IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the SoftCacheWeakIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(SoftCacheWeakIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(SoftCacheWeakIdentityMap.class));
        addMultipleIdentityTests(suite, new SoftCacheWeakIdentityMap(100, null));
        suite.addTest(new SetWriteLockInIdentityMapTest(SoftCacheWeakIdentityMap.class));
        
        suite.addTest(new ReadSoftCacheWeakIdentityMapTest(SoftCacheWeakIdentityMap.class));

        return suite;
    }

    private TestSuite getWeakIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("Weak IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the WeakIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(WeakIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(WeakIdentityMap.class));
        addMultipleIdentityTests(suite, new WeakIdentityMap(100, null));
        suite.addTest(new SetWriteLockInIdentityMapTest(WeakIdentityMap.class));
        
        suite.addTest(new ReadWeakIdentityMapTest(WeakIdentityMap.class));
        //bug 3095146
        suite.addTest(new CreateCacheKeyWeakIdentityMapTest());
        suite.addTest(new InsertWeakIdentityMapTest());

        return suite;
    }

    private TestSuite getSoftIdentityMapSuite() {
        TestSuite suite = new TestSuite();

        suite.setName("Soft IdentityMap Test Suite");
        suite.setDescription("This suite tests the functionality of the SoftIdentityMap");

        suite.addTest(new RegisterInIdentityMapTest(SoftIdentityMap.class));
        suite.addTest(new DeleteFromIdentityMapTest(SoftIdentityMap.class));
        addMultipleIdentityTests(suite, new SoftIdentityMap(100, null));
        suite.addTest(new SetWriteLockInIdentityMapTest(SoftIdentityMap.class));
        
        return suite;
    }

    private TestSuite getDeleteWithGarbageCollectionTestSuite(CacheIdentityMap cache) {
        TestSuite suite = new TestSuite();
        suite.setName("DeleteWithGarbageCollectionTestSuite");
        suite.setDescription("This suite tests for an error condition in which removal of an object which is garbage collected is attempted.");
        suite.addTest(new DeleteWithGarbageCollectionTest(cache));
        return suite;
    }
}
