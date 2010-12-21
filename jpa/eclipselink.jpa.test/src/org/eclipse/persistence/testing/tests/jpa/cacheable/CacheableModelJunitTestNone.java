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
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     07/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     06/09/2010-2.0.3 Guy Pelletier 
 *       - 313401: shared-cache-mode defaults to NONE when the element value is unrecognized
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;

/*
 * The test is testing against "MulitPU-2" persistence unit which has <shared-cache-mode> to be NONE
 */
public class CacheableModelJunitTestNone extends JUnitTestCase {
    
    public CacheableModelJunitTestNone() {
        super();
    }
    
    public CacheableModelJunitTestNone(String name) {
        super(name);
        setPuName("MulitPU-2");
    }
    
    public void setUp() {
        super.setUp();
        clearCache("MulitPU-2");
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableModelJunitTestNone");

        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new CacheableModelJunitTestNone("testSetup"));
            suite.addTest(new CacheableModelJunitTestNone("testCachingOnNONE"));
        }
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession("MulitPU-2"));
        clearCache("MulitPU-2");
    }
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to NONE.
     */
    public void testCachingOnNONE() {
        ServerSession session = getServerSession("MulitPU-2");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
    
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertTrue("CacheableTrueEntity (NONE) from annotations has caching turned on", usesNoCache(trueEntityDescriptor));

        ClassDescriptor childFalseEntityDescriptor = session.getDescriptorForAlias("JPA_CHILD_CACHEABLE_FALSE");
        assertTrue("ChildCacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(childFalseEntityDescriptor));
        
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertTrue("SubCacheableNoneEntity (NONE) from annotations has caching turned on", usesNoCache(noneSubEntityDescriptor));

        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (NONE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));
    
        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertTrue("CacheableTrueEntity (NONE) from XML has caching turned on", usesNoCache(xmlTrueEntityDescriptor));
        
        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (NONE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertTrue("SubCacheableTrueEntity (NONE) from XML has caching turned on", usesNoCache(xmlNoneSubEntityDescriptor));
    }
    
    /**
     * Convenience method.
     */
    private boolean usesNoCache(ClassDescriptor descriptor) {
        return descriptor.isIsolated();
    }
}
