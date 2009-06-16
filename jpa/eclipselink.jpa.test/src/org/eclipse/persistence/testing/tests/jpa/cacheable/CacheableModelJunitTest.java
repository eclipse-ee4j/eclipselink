/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.cacheable;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;
 
public class CacheableModelJunitTest extends JUnitTestCase {
    public CacheableModelJunitTest() {
        super();
    }
    
    public CacheableModelJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheableModelJunitTest");

        suite.addTest(new CacheableModelJunitTest("testSetup"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnALL"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnNONE"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnENABLE_SELECTIVE"));
        suite.addTest(new CacheableModelJunitTest("testCachingOnDISABLE_SELECTIVE"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    /**
     * Convenience method.
     */
    private boolean usesNoCache(ClassDescriptor descriptor) {
        return descriptor.isIsolated();
        //return descriptor.getIdentityMapClass().equals(ClassConstants.NoIdentityMap_Class); 
    }
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ALL.
     */
    public void testCachingOnALL() {
        ServerSession session = JUnitTestCase.getServerSession("ALL");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertFalse("CacheableFalseEntity (ALL) from annotations has caching turned off", usesNoCache(falseEntityDescriptor));
    
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ALL) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));

        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertFalse("SubCacheableFalseEntity (ALL) from annotations has caching turned off", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ALL) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertFalse("CacheableFalseEntity (ALL) from XML has caching turned off", usesNoCache(xmlFalseEntityDescriptor));
    
        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ALL) from XML has caching turned ff", usesNoCache(xmlTrueEntityDescriptor));
        
        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertFalse("SubCacheableFalseEntity (ALL) from XML has caching turned off", usesNoCache(xmlFalseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableTrueEntity (ALL) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to NONE.
     */
    public void testCachingOnNONE() {
        ServerSession session = JUnitTestCase.getServerSession("NONE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (NONE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
    
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertTrue("CacheableTrueEntity (NONE) from annotations has caching turned on", usesNoCache(trueEntityDescriptor));

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
     * Verifies the cacheable settings when caching (from persistence.xml) is set to ENABLE_SELECTIVE.
     */
    public void testCachingOnENABLE_SELECTIVE() {
        ServerSession session = JUnitTestCase.getServerSession("ENABLE_SELECTIVE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (ENABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
        
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ENABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));

        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (ENABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ENABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (ENABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));
        
        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (ENABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlTrueEntityDescriptor));
        
        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (ENABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (ENABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }
    
    /**
     * Verifies the cacheable settings when caching (from persistence.xml) is set to DISABLE_SELECTIVE.
     */
    public void testCachingOnDISABLE_SELECTIVE() {
        ServerSession session = JUnitTestCase.getServerSession("DISABLE_SELECTIVE");
        ClassDescriptor falseEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (DISABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseEntityDescriptor));
        
        ClassDescriptor trueEntityDescriptor = session.getDescriptorForAlias("JPA_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (DISABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(trueEntityDescriptor));
 
        ClassDescriptor falseSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (DISABLE_SELECTIVE) from annotations has caching turned on", usesNoCache(falseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor noneSubEntityDescriptor = session.getDescriptorForAlias("JPA_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (DISABLE_SELECTIVE) from annotations has caching turned off", usesNoCache(noneSubEntityDescriptor));

        ClassDescriptor xmlFalseEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_FALSE");
        assertTrue("CacheableFalseEntity (DISABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseEntityDescriptor));
        
        ClassDescriptor xmlTrueEntityDescriptor = session.getDescriptorForAlias("XML_CACHEABLE_TRUE");
        assertFalse("CacheableTrueEntity (DISABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlTrueEntityDescriptor));
        
        ClassDescriptor xmlFalseSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_FALSE");
        assertTrue("SubCacheableFalseEntity (DISABLE_SELECTIVE) from XML has caching turned on", usesNoCache(xmlFalseSubEntityDescriptor));
    
        // Should pick up true from the mapped superclass.
        ClassDescriptor xmlNoneSubEntityDescriptor = session.getDescriptorForAlias("XML_SUB_CACHEABLE_NONE");
        assertFalse("SubCacheableNoneEntity (DISABLE_SELECTIVE) from XML has caching turned off", usesNoCache(xmlNoneSubEntityDescriptor));
    }
}
