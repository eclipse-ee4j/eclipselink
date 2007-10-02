/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.simpletable;

// javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

// Java extension imports

// JUnit imports
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// Parameterized Before JUnit extension
import junit.extensions.pb4.ParameterizedBeforeRunner;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;

// Testing imports
import org.eclipse.persistence.testing.dbws.DBWSTestSuite;

@RunWith(ParameterizedBeforeRunner.class)
public class SimpleTableTestSuite extends DBWSTestSuite {

    @Test
    public void findByPrimaryKeyTest() {
        Invocation invocation = new Invocation("findByPrimaryKey");
        invocation.setParameter("id",1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ONE_PERSON_XML));
        assertTrue("control document not same as XRService instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ONE_PERSON_XML =
    "<?xml version = '1.0' encoding = 'UTF-8'?>" +
    "<xr_simpletable>" +
       "<id>1</id>" +
       "<name>mike</name>" +
       "<since>2001-12-25T00:00:00.0</since>" +
    "</xr_simpletable>";
}