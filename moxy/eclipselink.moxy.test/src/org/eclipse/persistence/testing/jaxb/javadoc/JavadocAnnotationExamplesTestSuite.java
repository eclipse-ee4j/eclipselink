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
package org.eclipse.persistence.testing.jaxb.javadoc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JavadocAnnotationExamplesTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Javadoc Annotation Examples Test Suite");
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessororder.XmlAccessorOrderTest.class);
		// suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessortype.XmlAccessorTypeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlanyattribute.XmlAnyAttributeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlanyelement.XmlAnyElementCollectionModelTest.class);
		// suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlattachmentref.XmlAttachmentRefExampleTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlattribute.MapPropertyToXmlAttributeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlattribute.MapCollectionToXmlAttributeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelement.XmlElementNillableRequiredTest.class);
		// suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelementdecl.XmlElementDeclExample1Test.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref.XmlElementRefHierarchyTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelementrefs.XmlElementRefsTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelements.XmlElementsListOfElementTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelementwrapper.XmlElementWrapperTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue.XmlEnumValueConstantNameValueTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlidref.XmlIdRefContainmentTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmllist.XmlListTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlid.XmlIdTest.class);
		// suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlmixed.XmlMixedTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlns.XmlNsCustomizePrefixAndUriTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement.XmlRootElementNotInheritedByDerivedTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlschema.XmlSchemaElementFormUnqualifiedTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlschematype.XmlSchemaTypeGregorianCalendarTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlseealso.XmlSeeAlsoTest.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlseealso.XmlSeeAlsoTest2.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltransient.XmlTransientTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.XmlTypeCustomizedOrderingTest.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.AttributeWithAnonymousTypeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue.XmlValueSimpleContentTest.class);

		// Phase-II
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelement.XmlElementNillableTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelement.XmlElementNonStaticTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlelements.XmlElementsListOfElementWrappedTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue.XmlEnumValueConstantNameTest3.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement.XmlRootElementBasicTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement.XmlRootElementBasicTest2.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.XmlTypeUnspecifiedOrderTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.XmlTypeAnonymousTypeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.XmlTypeAnonymousLocalElementTest.class);
		//suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmltype.XmlTypeAnonymousAttributeTest.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue.XmlValueSimpleTypeTest.class);

		return suite;
	}

}
