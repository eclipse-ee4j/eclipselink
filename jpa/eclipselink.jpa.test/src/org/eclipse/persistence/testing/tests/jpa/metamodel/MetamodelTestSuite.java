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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     04/30/2009-2.0 Michael O'Brien  
 *       - 266912: JPA 2.0 Metamodel API (part of Criteria API)  
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The following functions from Ch 5 of the 17 Mar 2009 JSR-317 JPA 2.0 API PFD are tested here. 
 *
 */
public class MetamodelTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("MetamodelTestSuite");
        suite.addTest(EntityManagerFactoryImplTest.suite());
        suite.addTest(EntityManagerImplTest.suite());
/*        suite.addTest(MetamodelAbstractCollectionTest.suite());
        suite.addTest(MetamodelAttributeTest.suite());
        suite.addTest(MetamodelBasicTest.suite());
        suite.addTest(MetamodelCollectionAttributeTest.suite());
        suite.addTest(MetamodelEmbeddableTypeTest.suite());
        suite.addTest(MetamodelEntityTypeTest.suite());
        suite.addTest(MetamodelListAttributeTest.suite());
        suite.addTest(MetamodelManagedTypeTest.suite());
        suite.addTest(MetamodelMapAttributeTest.suite());
        suite.addTest(MetamodelMappedSuperclassTypeTest.suite());
        suite.addTest(MetamodelMemberTest.suite());
*/        suite.addTest(MetamodelMetamodelTest.suite());
/*        suite.addTest(MetamodelSetAttributeTest.suite());
        suite.addTest(MetamodelTypeTest.suite());
*/        return suite;
    }
}
