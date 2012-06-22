/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.errortests;

import java.io.InputStream;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class DirectToFieldErrorTestCases extends OXTestCase {
    public DirectToFieldErrorTestCases(String name) throws Exception {
        super(name);
    }

    public void testXPathNotSetError() throws Exception {
        try {
            XMLContext xmlContext = getXMLContext(new DirectToFieldErrorProject());
        } catch (IntegrityException e) {
            boolean foundException = false;
            Vector caughtExceptions = e.getIntegrityChecker().getCaughtExceptions();
            for (int i = 0; i < caughtExceptions.size(); i++) {
                Exception nextException = (Exception)caughtExceptions.elementAt(i);
                if (nextException instanceof DescriptorException) {
                    this.assertTrue("An incorrect Descriptor exception occurred.", ((DescriptorException)nextException).getErrorCode() == DescriptorException.FIELD_NAME_NOT_SET_IN_MAPPING);
                    foundException = true;
                }
            }
            this.assertTrue("The appropriate Descriptor exception was not encountered", foundException);
            return;
        } catch (XMLMarshalException marshalException) {
            this.assertTrue("An unexcepted XMLMarshalException occurred", !(metadata == Metadata.JAVA));
            return;
        } catch (Exception e) {
            fail("An unexcepted exception occurred");
            return;
        }
        fail("A Descriptor Exception should have been thrown but wasn't");
    }
}
