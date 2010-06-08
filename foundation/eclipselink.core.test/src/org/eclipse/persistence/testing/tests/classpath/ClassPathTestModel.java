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
package org.eclipse.persistence.testing.tests.classpath;

import org.eclipse.persistence.testing.framework.TestModel;

/**
 * Set of test that require jars that TopLink typically requires to NOT be on the classpath.
 * These test will fail if the classpath has these jars on the path,
 * as they require to test TopLink's behavior when these jars are absent.
 */
 
public class ClassPathTestModel extends TestModel {
    //Jdbc jar shouldn't be on the path for DeploymentXMLJDBCDependencyTest, but should be on
    //the path for DeploymentXMLXDBEISDependencyTest
    public ClassPathTestModel() {
        super();
        addTest(new DeploymentXMLXDBEISDependencyTest());
        addTest(new DeploymentXMLJDBCDependencyTest());
        addTest(new DeploymentXMLAntlrDependencyTest());
    }    
}
