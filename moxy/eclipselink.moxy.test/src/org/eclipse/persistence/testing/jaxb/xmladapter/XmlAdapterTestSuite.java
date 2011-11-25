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
package org.eclipse.persistence.testing.jaxb.xmladapter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.jaxb.xmladapter.bytearray.ByteArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.classlevel.ClassLevelAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.composite.XmlAdapterCompositeTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositecollection.XmlAdapterCompositeCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection.CollapsedStringListTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection.NormalizedStringListTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection.XmlAdapterCompositeDirectCollectionTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.compositedirectcollection.XmlAdapterDirectCollectionArrayTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.CollapsedStringTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.ListToStringAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.NormalizedStringTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.XmlAdapterDirectTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.direct.objectlist.ObjectListTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.elementref.XmlAdapterElementRefListTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.elementref.XmlAdapterElementRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.generics.AdapterWithGenericsTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.hexbinary.XmlAdapterHexBinaryTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.list.XmlAdapterListMultipleBarTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.list.XmlAdapterListSingleBarTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.map.JAXBMapWithAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.PackageLevelAdapterTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters.PackageLevelAdaptersTestCases;

public class XmlAdapterTestSuite extends TestCase {
    public XmlAdapterTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.jaxb.xmladapter.XmlAdapterTestSuite" });
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XmlAdapter Test Suite");
        suite.addTestSuite(XmlAdapterCompositeTestCases.class);
        suite.addTestSuite(XmlAdapterCompositeCollectionTestCases.class);
        suite.addTestSuite(XmlAdapterCompositeDirectCollectionTestCases.class);
        suite.addTestSuite(XmlAdapterDirectTestCases.class);
        suite.addTestSuite(ListToStringAdapterTestCases.class);
        suite.addTestSuite(PackageLevelAdapterTestCases.class);
        suite.addTestSuite(PackageLevelAdaptersTestCases.class);
        suite.addTestSuite(ClassLevelAdapterTestCases.class);
        suite.addTestSuite(JAXBMapWithAdapterTestCases.class);
        suite.addTestSuite(ObjectListTestCases.class);
        suite.addTestSuite(XmlAdapterElementRefListTestCases.class);
        suite.addTestSuite(XmlAdapterElementRefTestCases.class);
        suite.addTestSuite(XmlAdapterHexBinaryTestCases.class);
        suite.addTestSuite(XmlAdapterListSingleBarTestCases.class);
        suite.addTestSuite(XmlAdapterListMultipleBarTestCases.class);
        suite.addTestSuite(AdapterWithGenericsTestCases.class);
        suite.addTestSuite(XmlAdapterDirectCollectionArrayTestCases.class);
        suite.addTestSuite(ByteArrayTestCases.class);
        suite.addTestSuite(CollapsedStringListTestCases.class);
        suite.addTestSuite(CollapsedStringTestCases.class);
        suite.addTestSuite(NormalizedStringListTestCases.class);
        suite.addTestSuite(NormalizedStringTestCases.class);
        return suite;
    }
}
