package org.eclipse.persistence.testing.jaxb.namespaceuri.schemacontext;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;

public class SchemaContextAsQNameTest extends TestCase {
	private static String NAMESPACE_URI = "urn:org.eclipse.persistence.testing.jaxb.namespaceuri.schemacontext";
	private static String LOCAL_PART = "root";
	private static QName qName = new QName(NAMESPACE_URI, LOCAL_PART);
	
	public void testSchemaContextAsQName() {
		try {
			JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { Root.class }, new HashMap());
			assertTrue("JAXBContext creation failed", ctx != null);
			XMLContext xCtx = ctx.getXMLContext();
			assertTrue("XMLContext is null", xCtx != null);
			XMLDescriptor xDesc = xCtx.getDescriptor(qName);
			assertTrue("No descriptor found for '{"+NAMESPACE_URI+"}"+LOCAL_PART+"'", xDesc != null);
			XMLSchemaReference sRef = xDesc.getSchemaReference();
			assertTrue("Expected SchemaContextAsQName [{"+NAMESPACE_URI+"}"+LOCAL_PART+"] but was was ["+sRef.getSchemaContextAsQName()+"]", sRef.getSchemaContextAsQName().equals(qName));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
