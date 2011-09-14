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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.unqualified;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class AttributeFormDefaultUnqualifiedTestCases extends JAXBTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/unqualifiedaddress.xml";

	public AttributeFormDefaultUnqualifiedTestCases(String name) throws Exception {
		super(name);
	}
	
	public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);

	    super.setUp();
	    Type[] types = new Type[1];
	    types[0] = Address.class;	        
	    setTypes(types);
	}


	protected Object getControlObject() {
		Address addr = new Address();
		addr.city = "Ottawa";
		addr.street ="Main Street";
		addr.street2 ="Street2";
		addr.street3 ="Street3";
		addr.street4 ="Street4";
		return addr;
	}
	
	public void testSchemaGen() throws Exception{
		InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/unqualified.xsd");				
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);
		
		this.testSchemaGen(controlSchemas);
	}
		
}
