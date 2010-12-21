/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;

import junit.textui.TestRunner;

public class SDOXMLHelperDatatypeCharacterTestCases extends SDOXMLHelperDatatypeTestCase {
    
	public SDOXMLHelperDatatypeCharacterTestCases(String name) {
        super(name);
    }
	
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.datatype.SDOXMLHelperDatatypeCharacterTestCases" };
        TestRunner.main(arguments);
    }

    protected Class getDatatypeJavaClass() {
    	return String.class;
    }    
    
    protected SDOType getValueType() {
    	return SDOConstants.SDO_CHARACTER;
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/datatype/myCharacter-1.xml");
    }

    protected String getControlRootURI() {
        return "myCharacter-NS";
    }

    protected String getControlRootName() {
        return "myCharacter";
    }

    protected String getSchemaNameForUserDefinedType() {
        return getSchemaLocation() + "myCharacter.xsd";
    }

    protected String getSchemaNameForBuiltinType() {
        return getSchemaLocation() + "myCharacter-builtin.xsd";
    }

}
