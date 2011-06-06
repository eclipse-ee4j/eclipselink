/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.io.FileWriter;

import org.eclipse.persistence.sessions.factories.ProjectClassGenerator;


/**
 * Test the write(Project project, String projectClassName, Writer writer) method of project class generator
 * Added for code coverage
 */
public class ProjectClassGeneratorWriteMethodTest extends ProjectClassGeneratorResultFileTest {
    public static final String FILE_NAME = "TopLinkProject.java";

    public ProjectClassGeneratorWriteMethodTest() {
        super(new EmployeeSubProject(), "DirectToFieldMapping");
    }

    /**
     * override test to call write instead of generate
     */
    public void test() {
        try {
            fileName = FILE_NAME;
            FileWriter writer = new FileWriter(fileName);
            ProjectClassGenerator.write(project, "org.eclipse.persistence.testing.tests.workbenchintegration.EmployeeSubProject", 
                                        writer);
        } catch (Exception exception) {
            generationException = exception;
        }
    }
}
