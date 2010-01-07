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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.lang.reflect.Method;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

/**
 *  @version $Header: WorkbenchIntegrationSystemHelper.java 31-jul-2007.11:40:26 gpelleti Exp $
 *  @author  gpelleti
 *  @since   11g
 */
public class WorkbenchIntegrationSystemHelper {

    /**
     * For the given project, generate the class file, compile it and set
     * it to be the project.
     */
    public static Project buildProjectClass(Project project, String filename) {
        ProjectClassGenerator generator = new ProjectClassGenerator(project, filename, filename + ".java");
        generator.generate();

        try {
        	Object[] params = new Object[1];
            String[] source = { filename + ".java" } ;
            params[0] = source;
            Class mainClass = Class.forName("com.sun.tools.javac.Main");
            Class[] parameterTypes = new Class[1];
            parameterTypes[0] = String[].class;
            Method method = mainClass.getMethod("compile", parameterTypes);
            int result = ((Integer)method.invoke(null, params)).intValue();           
            if (result != 0) {
                throw new TestErrorException("Project class generation compile failed. This could either be a legitimate compile " +
                 		"failure, or could result if you do not have the tools.jar from your JDK on the classpath.");
            }
            Class projectClass = Class.forName(filename);
            return (Project) projectClass.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException("Project class generation failed.It may be possible to solve this issue by adding the tools.jar from your JDK to the classpath.", exception);
        }
    }
    
    /**
     * For the given project, generate the project xml and read it back in.
     */
    public static Project buildProjectXML(Project project, String filename) {
        return buildProjectXML(project, filename, project.getClass().getClassLoader());
    }
    
    /**
     * For the given project, generate the project xml and read it back in.
     */
    public static Project buildProjectXML(Project project, String filename, ClassLoader loader) {
        XMLProjectWriter.write(filename + ".xml", project);
        return XMLProjectReader.read(filename + ".xml", loader);
    }
}
