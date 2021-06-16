/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref;

//Example 1
import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementRefHierarchyTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelementref/xmlelementrefhierarchy.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelementref/xmlelementrefhierarchy.json";

    public XmlElementRefHierarchyTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
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
