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
package org.eclipse.persistence.testing.tests.dynamic.projectxml;

//javase imports
import java.io.IOException;

//JUnit4 imports
import org.junit.BeforeClass;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createLogin;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;

/*
 * Test cases verifying the use of simple-map-project-no-login.xml 
 */
public class SimpleMapProjectNoLogin extends SimpleMapProject {

    public static final String PACKAGE_PREFIX = 
        SimpleMapProjectNoLogin.class.getPackage().getName();
    static final String PROJECT_XML = 
        PACKAGE_PREFIX.replace('.', '/') + "/simple-map-project-no-login.xml";
    
    @BeforeClass
    public static void setUp() {
        DynamicClassLoader dcl = new DynamicClassLoader(SimpleMapProjectNoLogin.class.getClassLoader());
        Project project = null;
        try {
            project = DynamicTypeBuilder.loadDynamicProject(PROJECT_XML, createLogin(), dcl);
        }
        catch (IOException e) {
            //e.printStackTrace();
            fail("cannot find simple-map-project-no-login.xml");
        }
        session = project.createDatabaseSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        dynamicHelper = new DynamicHelper(session);
        session.login();
        new DynamicSchemaManager(session).createTables(new DynamicType[0]);
    }

}