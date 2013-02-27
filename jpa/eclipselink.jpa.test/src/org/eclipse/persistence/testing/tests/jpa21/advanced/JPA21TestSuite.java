/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 *     10/09/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 *     01/23/2013-2.5 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;


import junit.framework.Test;
import junit.framework.TestSuite;

public class JPA21TestSuite extends TestSuite {
    
    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("JPA_2.1_TestSuite");
        
        fullSuite.addTest(StoredProcedureQueryTestSuite.suite());
        fullSuite.addTest(ConverterTestSuite.suite());
        fullSuite.addTest(CriteriaQueryTestSuite.suite());
        fullSuite.addTest(CriteriaQueryMetamodelTestSuite.suite());
        fullSuite.addTest(DDLTestSuite.suite());
        fullSuite.addTest(ForeignKeyTestSuite.suite());
        fullSuite.addTest(IndexTestSuite.suite());
        fullSuite.addTest(EntityManagerFactoryTestSuite.suite());
        fullSuite.addTest(EntityGraphTestSuite.suite());
        fullSuite.addTest(QueryTestSuite.suite());
        fullSuite.addTest(EntityManagerTestSuite.suite());
        fullSuite.addTest(XMLNamedStoredProcedureQueryTestSuite.suite());
        fullSuite.addTest(XMLConverterTestSuite.suite());
        fullSuite.addTest(XMLForeignKeyTestSuite.suite());
        fullSuite.addTest(XMLIndexTestSuite.suite());
        fullSuite.addTest(XMLEntityGraphTestSuite.suite());
        return fullSuite;
    }
}

