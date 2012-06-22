/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref;

//Example 1
import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementRefHierarchyTest extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelementref/xmlelementrefhierarchy.xml";

    public XmlElementRefHierarchyTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[4];
        classes[3] = Task.class;
        classes[0] = JarTask.class;
        classes[1] = JavacTask.class;
        classes[2] = Target.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
    	Target target = new Target();
    	target.tasks = new ArrayList();
    	JarTask jarTask = new JarTask();
    	jarTask.name = new String("first jar task");
    	target.tasks.add(jarTask);
    	JavacTask javacTask = new JavacTask();
    	javacTask.name = "first javac task"; 
    	target.tasks.add(javacTask);
        return target;
    }
}
