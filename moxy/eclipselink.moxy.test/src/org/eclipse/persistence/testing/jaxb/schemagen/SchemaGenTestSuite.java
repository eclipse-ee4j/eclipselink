/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen;

import org.eclipse.persistence.testing.jaxb.schemagen.date.DateTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.defaultmapping.SchemaGenMapTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.deploymentxml.DeploymentXMLSchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.employee.SchemaGenEmployeeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.SchemaGenImportTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance.InheritanceImportsTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.url.SchemaGenImportURLTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.inheritance.InheritanceWithTransientTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.inheritance.SchemaGenInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.scope.SchemaGenScopeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.typearray.TypeArraySchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.xmlpath.SchemaGenXmlPathTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype.AnonymousTypeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.anonymoustype.inheritance.AnonymousTypeInheritanceTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.classarray.ClassArraySchemaGenTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype.SchemaGenXMLTypeTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlvalue.SchemaGenXmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref.SchemaGenXmlElementRefByteArrayTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref.SchemaGenXmlElementRefTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementwrapper.SchemaGenXmlElementWrapperTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlid.SchemaGenXmlIDTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlidref.SchemaGenXmlIDREFTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmllist.SchemaGenXmlListTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlrootelement.SchemaGenXMLRootElementTestCases;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SchemaGenTestSuite extends TestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB 2.0 Schema Generation Test Suite");
        suite.addTestSuite(SchemaGenEmployeeTestCases.class);
        suite.addTestSuite(SchemaGenXmlPathTestCases.class); // added twice, delete this one, should be at the end
        suite.addTestSuite(SchemaGenXMLTypeTestCases.class);
        suite.addTestSuite(SchemaGenXMLRootElementTestCases.class);
        suite.addTestSuite(DeploymentXMLSchemaGenTestCases.class);
        suite.addTestSuite(SchemaGenXmlElementWrapperTestCases.class);
        suite.addTestSuite(SchemaGenXmlElementRefByteArrayTestCases.class);
        suite.addTestSuite(SchemaGenXmlElementRefTestCases.class);
        suite.addTestSuite(SchemaGenXmlListTestCases.class);
        suite.addTestSuite(SchemaGenXmlIDREFTestCases.class);
        suite.addTestSuite(ClassArraySchemaGenTestCases.class);
        suite.addTestSuite(TypeArraySchemaGenTestCases.class);
        suite.addTestSuite(SchemaGenScopeTestCases.class);
        suite.addTestSuite(SchemaGenImportTestCases.class);
        suite.addTestSuite(SchemaGenImportURLTestCases.class);
        suite.addTestSuite(DateTestCases.class);
        suite.addTestSuite(AnonymousTypeTestCases.class);
        suite.addTestSuite(AnonymousTypeInheritanceTestCases.class);
        suite.addTestSuite(SchemaGenInheritanceTestCases.class);
        suite.addTestSuite(InheritanceWithTransientTestCases.class);
        suite.addTestSuite(InheritanceImportsTestCases.class);
        suite.addTestSuite(SchemaGenXmlPathTestCases.class);
        suite.addTestSuite(SchemaGenXmlIDTestCases.class);
        suite.addTestSuite(SchemaGenXmlValueTestCases.class);
        suite.addTestSuite(SchemaGenMapTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestSuite" };
        junit.textui.TestRunner.main(arguments);
    }

}
