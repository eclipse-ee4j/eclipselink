/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


/**
 * Bug 3432075
 * Test to ensure that cache maintenance settings are properly read from and written to XML
 */
public class QueryShouldMaintainCacheTest extends AutoVerifyTestCase {

    protected org.eclipse.persistence.sessions.Project project = null;
    // Changed original file name EmployeeProject.xml for this unique one:
    // another EmployeeProject.xml file is found in resource/foundation -
    // if it happens to be higher on the classpath this other file is read in
    // (instead of the one we just wrote out) - that causes NPE:
    // foundation/resources/EmployeeProject.xml doesn't have the queries defined in this test.
    public static final String FILENAME = "EmployeeProjectForQueryShouldMaintainCacheTest.xml";

    public QueryShouldMaintainCacheTest() {
        setDescription("Ensure should maintain cache settings for descriptors are properly written out.");
    }

    public void setup() {
        project = new EmployeeProject();
        ClassDescriptor descriptor = 
            project.getDescriptors().get(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        descriptor.disableCacheHits();

        //	ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        //	descriptor.getQueryManager().addQuery("UndefinedQuery", query);

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.maintainCache();
        descriptor.getQueryManager().addQuery("TrueQuery", query);

        query = new ReadObjectQuery(Employee.class);
        query.dontMaintainCache();
        descriptor.getQueryManager().addQuery("FalseQuery", query);

    }

    public void test() {
        XMLProjectWriter.write(FILENAME, project);
        project = XMLProjectReader.read(FILENAME, getClass().getClassLoader());
    }

    public void verify() {
        ClassDescriptor descriptor = 
            project.getDescriptors().get(org.eclipse.persistence.testing.models.employee.domain.Employee.class);

        // Here we test the maintainCache setting.  This should override any other settings.
        ReadObjectQuery query = (ReadObjectQuery)descriptor.getQueryManager().getQuery("TrueQuery");
        if /*getSession()*/(!query.shouldMaintainCache() || !query.shouldMaintainCache()) {
            throw new TestErrorException("Project read from XML should be set to maintainCache, but is not.");
        }

        // Here we test the donMaintainceCacheSetting.  This should override other settings.
        query = (ReadObjectQuery)descriptor.getQueryManager().getQuery("FalseQuery");
        if /*getSession()*/(query.shouldMaintainCache() || query.shouldMaintainCache()) {
            throw new TestErrorException("Project read from XML should be set not to maintainCache, but it does.");
        }

    }

    public void reset() {
        File file = new File(FILENAME);
        file.delete();
    }
}
