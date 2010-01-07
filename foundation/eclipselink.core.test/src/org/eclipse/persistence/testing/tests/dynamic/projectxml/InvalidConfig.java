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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.projectxml;

//javase imports
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

//JUnit4 imports
import org.junit.Test;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.sessions.DatabaseLogin;

/**
 * Test cases verifying invalid args passed to
 * {@link DynamicTypeBuilder#loadDynamicProject(String, DatabaseLogin)}
 */
public class InvalidConfig {

    public static final String PACKAGE_PREFIX = 
        InvalidConfig.class.getPackage().getName();
    static final String INVALID_PROJECT_XML = 
        PACKAGE_PREFIX.replace('.', '/') + "/bar.xml";
    
    @Test(expected=NullPointerException.class)
    public void nullResource() throws Exception {
        DynamicTypeBuilder.loadDynamicProject((String)null, null, null);
    }
    
    @Test(expected=NullPointerException.class)
    public void nullClassLoader() throws Exception {
        File temp = File.createTempFile("foo",".txt");
        temp.deleteOnExit();
        FileInputStream fis = new FileInputStream(temp);
        DynamicTypeBuilder.loadDynamicProject(fis, new DatabaseLogin(), null);
    }

    @Test(expected=XMLMarshalException.class)
    public void invalidResource() throws Exception {
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(
            InvalidConfig.class.getClassLoader());
        InputStream is = dynamicClassLoader.getResourceAsStream(INVALID_PROJECT_XML);
        DynamicTypeBuilder.loadDynamicProject(is, new DatabaseLogin(),
            dynamicClassLoader);
    }
}