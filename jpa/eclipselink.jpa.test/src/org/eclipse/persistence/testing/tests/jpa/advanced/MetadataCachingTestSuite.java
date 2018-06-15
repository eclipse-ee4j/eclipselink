/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/01/2012-2.5 Chris Delahunt
//       - 371950: Metadata caching
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.metadata.FileBasedProjectCache;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.tests.jpa.advanced.compositepk.AdvancedCompositePKJunitTest;

import junit.framework.TestSuite;
import junit.framework.Test;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * @author cdelahun
 *
 */
public class MetadataCachingTestSuite extends JUnitTestCase {

    String fileName = "MetadataCachingTestProject.file";

    public MetadataCachingTestSuite() {
        super();
    }

    public MetadataCachingTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MetadataCachingTestSuite");
        suite.addTest(new MetadataCachingTestSuite("testProjectCacheALLWithDefaultPU"));
        return suite;
    }

    public Map getProperties() {
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
        //this causes deployment to occur on refreshMetadata rather than wait until an em is obtained
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
        //property we are testing:
        properties.put(PersistenceUnitProperties.PROJECT_CACHE, "java-serialization");
        properties.put(PersistenceUnitProperties.PROJECT_CACHE_FILE, fileName);
        return properties;
    }

    public void testSetup() {
        boolean exists = false;
        File file = new File(fileName);

        //remove the file if it already exists so it can be created in testing
        exists = file.exists()? !file.delete() : false;

        if (exists) {
            fail("the file exists and could not be removed.  file: "+fileName);
        }
    }

    public void testFileBasedProjectCacheWriting(String persistenceUnitName) {
        EntityManager em = createEntityManager(persistenceUnitName);
        Map properties = getProperties();

        // JEE requires a transaction to keep the em open.
        beginTransaction(em);
        try {
            JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
        } finally {
            commitTransaction(em);
            closeEntityManager(em);
        }

        File file = new File(fileName);
        if (!file.exists()) {
            fail("the project cache file was not created on deployment using PROJECT_CACHE 'java-serialization'");
        }
    }

    public void testFileBasedProjectCacheReading(String persistenceUnitName) {
        FileBasedProjectCache projectCache = new FileBasedProjectCache();
        Session session = this.getServerSession(persistenceUnitName);
        Project project = projectCache.retrieveProject(getProperties(), session.getDatasourcePlatform().getConversionManager().getLoader(), session.getSessionLog());
        if (project == null) {
            fail("Project returned from FileBasedProjectCache.retrieveProject() was null");
        }
    }

    /*
     * This test just verifies the EM can be refreshed using the cached project written out in testFileBasedProjectCacheWriting
     * It must be run after testFileBasedProjectCacheWriting and testFileBasedProjectCacheReading
     */
    public void testFileBasedProjectCacheLoading(String persistenceUnitName) {
        EntityManager em = createEntityManager(persistenceUnitName);
        beginTransaction(em);
        try {
            JpaHelper.getEntityManagerFactory(em).refreshMetadata(getProperties());
        } finally {
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /* Test project cache in runtime on J2EE with default persistence unit*/
    public void testProjectCacheALLWithDefaultPU(){
        testSetup();
        testFileBasedProjectCacheWriting("default");
        testFileBasedProjectCacheReading("default");
        testFileBasedProjectCacheLoading("default");
    }

    /* Test project cache in runtime on JEE with default persistence unit*/
    public void testProjectCacheWithDefaultPU(){
        testFileBasedProjectCacheLoading("default");
    }

}
