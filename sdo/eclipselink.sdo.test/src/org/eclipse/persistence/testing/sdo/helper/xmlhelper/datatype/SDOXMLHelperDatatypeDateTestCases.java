/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.Date;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeDateTestCases extends SDOXMLHelperDatatypeTestCase {
    
	public SDOXMLHelperDatatypeDateTestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeDateTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
    	return String.class;
    }    
    
    protected SDOType getValueType() {
    	return SDOConstants.SDO_DATE;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myDate-1.xml");
    }

    protected String getControlRootURI() {
        return "myDate-NS";
    }

    protected String getControlRootName() {
        return "myDate";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myDate.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myDate-builtin.xsd";
    }

}
