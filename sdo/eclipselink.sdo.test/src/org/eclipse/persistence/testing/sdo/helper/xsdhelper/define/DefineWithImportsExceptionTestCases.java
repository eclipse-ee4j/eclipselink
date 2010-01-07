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
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.impl.HelperProvider;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
Bug 211772 Model
Level1RootIncludes imports(Level2Include1, Level2Include2)
Level2Include1 references T3 but does not import Level3Import1
Level2Include2 imports(Level3Import1)
Level3Import1 defines type T3 in a different targetNamespace than all previous
------------------------------------------------
There are 3 levels of exception, the top-level include, the lower-level import and the actual define exception.

Level3Import1.xsd
<xsd:schema targetNamespace="my3.uri" xmlns="my3.uri" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		<xsd:complexType name="T3">
  		<xsd:attribute ref="attributeInt"/>
		</xsd:complexType>
	<xsd:element name="t3" type="T3"/>
</xsd:schema>
*
*/
public class DefineWithImportsExceptionTestCases extends XSDHelperDefineTestCases {
	public DefineWithImportsExceptionTestCases(String name) {
    	super(name);
	}
	public static void main(String[] args) {
    	TestRunner.run(DefineWithImportsExceptionTestCases.class);
	}

	// As this schema tree is designed to fail - override normal Type testing
	public String getSchemaToDefine() { return null; }


    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/exception/";
    }

    // As this schema tree is designed to fail - override normal Type testing
    public List<Type> getControlTypes() {
    	return new ArrayList<Type>();
    }

    // As this schema tree is designed to fail - override normal Type testing
    public void testDefine() {}
    
    /**
	Exception [EclipseLink-45002] (Eclipse Persistence Services -  (Build )): org.eclipse.persistence.exceptions.SDOException
	Exception Description: An error occurred processing the include with schemaLocation [Level2Include2.xsd] .
	Internal Exception: Exception [EclipseLink-45001] (Eclipse Persistence Services -  (Build)): org.eclipse.persistence.exceptions.SDOException
	Exception Description: An error occurred processing the import with schemaLocation [Level3ImportRef.xsd] and namespace [my4.uri] .
	Internal Exception: java.lang.IllegalArgumentException: local part cannot be "null" when creating a QName
		at org.eclipse.persistence.exceptions.SDOException.errorProcessingInclude(SDOException.java:106)
		at org.eclipse.persistence.sdo.helper.SDOTypesGenerator.processInclude(SDOTypesGenerator.java:77)
		at org.eclipse.persistence.sdo.helper.SchemaParser.processIncludes(SchemaParser.java:120)
		at org.eclipse.persistence.sdo.helper.SchemaParser.processSchema(SchemaParser.java:87)
     */
    public void testErrorProcessingImportExceptionCase() {
    	List types = null;
        try {
            String invalidURLFile = "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/exception/Level1rootIncludes.xsd";
            InputStream is = getSchemaInputStream(invalidURLFile);
            types = xsdHelper.define(is, getSchemaLocation());
            assertEquals(6, types.size());
            // Verify global {my4.uri}attributeInt property was created
            //Property globalProp = xsdHelper.getGlobalProperty("my.uri", "attributeInt", false);	// with error
            Property globalProp = xsdHelper.getGlobalProperty("my4.uri", "attributeInt", false); // without error
            //assertNotSame(null, globalProp);
            assertSame(null, globalProp);
            //System.out.println("Global Prop: " + globalProp.getName());            
        } catch(SDOException ex) {
        	assertEquals(null, types);
        	assertEquals(SDOException.ERROR_PROCESSING_INCLUDE, ex.getErrorCode());
        	//ex.printStackTrace();
        }
    }
}
