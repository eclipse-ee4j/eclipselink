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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement;

//Example 2

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlRootElementNotInheritedByDerivedTest extends JAXBTestCases {

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlrootelement/point3d.xml";

    public  XmlRootElementNotInheritedByDerivedTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[2];
        classes[0] = Point3D.class;
        classes[1] = Point2D.class;
        setClasses(classes);
    }

    protected Object getControlObject() {

        Point3D point3d = new Point3D(10,20,5);
        return point3d;
    }
}
