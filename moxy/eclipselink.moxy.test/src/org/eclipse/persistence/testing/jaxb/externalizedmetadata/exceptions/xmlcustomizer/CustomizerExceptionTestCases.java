/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - July 29/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.exceptions.xmlcustomizer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests XMLCustomizer exception handling.
 *
 */
public class CustomizerExceptionTestCases extends ExternalizedMetadataTestCases {
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public CustomizerExceptionTestCases(String name) {
        super(name);
    }

    /**
     * Tests a non-customizer class set via Java annotation.
     * 
     * Negative test.
     */
    public void testNonCustomizerClass() {
        Class<?>[] classes = {
                Employee.class,
        };
        
        
        JAXBContext jaxbContext;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            return;
        } catch (Exception x) {
        }
        fail("The expected JAXBException was not thrown.");
    }
}
