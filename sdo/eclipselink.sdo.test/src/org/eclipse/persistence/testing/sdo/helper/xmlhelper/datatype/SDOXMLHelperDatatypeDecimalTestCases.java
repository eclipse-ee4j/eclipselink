/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
* 
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
******************************************************************************/

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype;

import java.math.BigDecimal;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeDecimalTestCases extends SDOXMLHelperDatatypeTestCase {
    
	public SDOXMLHelperDatatypeDecimalTestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeDecimalTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
    	return BigDecimal.class;
    }    
    
    protected SDOType getValueType() {
    	return SDOConstants.SDO_DECIMAL;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myDecimal-1.xml");
    }

    protected String getControlRootURI() {
        return "myDecimal-NS";
    }

    protected String getControlRootName() {
        return "myDecimal";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myDecimal.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myDecimal-builtin.xsd";
    }

}
