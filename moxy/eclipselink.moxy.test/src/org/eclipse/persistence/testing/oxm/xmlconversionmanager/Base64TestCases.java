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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class Base64TestCases extends OXTestCase {
    // XML Conversion Manager
    private XMLConversionManager xcm;

    public Base64TestCases(String name) {
        super(name);
    }

    public void setUp() {
        xcm = XMLConversionManager.getDefaultXMLManager();
    }
    
    public void testIntegerToString_base64() {
        try {
            Integer integer = new Integer(1);
            xcm.convertObject(integer, ClassConstants.ABYTE, XMLConstants.BASE_64_BINARY_QNAME);
        } catch (ConversionException e) {
            assertTrue("The incorrect exception was thrown", e.getErrorCode() == ConversionException.COULD_NOT_BE_CONVERTED);
        }      
    }
}
