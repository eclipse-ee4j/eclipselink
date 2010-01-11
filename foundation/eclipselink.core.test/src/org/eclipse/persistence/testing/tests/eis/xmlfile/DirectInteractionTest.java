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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import java.io.*;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Simple interaction test. Tests a file interaction.
 */
public class DirectInteractionTest extends DirectConnectTest {
    public Connection connection;
    public RecordFactory recordFactory;

    public DirectInteractionTest() {
        setName("DirectInteractionTest");
        setDescription("Testing interaction XML data");
    }

    public void test() throws Exception {
        connection = connect();
        recordFactory = connectionFactory.getRecordFactory();

        insertInteraction();
        updateInteraction();
        queryInteraction();
        deleteInteraction();

        connection.close();
    }

    protected void insertInteraction() throws Exception {
        Interaction interaction = connection.createInteraction();
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.INSERT);
        spec.setFileName("xml-file-insert-test.xml");

        String data1 = "<order id='1234' orderedby='Bob'></order>";

        EISDOMRecord input = (EISDOMRecord)recordFactory.createMappedRecord("input");
        input.transformFromXML(data1);

        interaction.execute(spec, input);

        interaction.close();
    }

    protected void queryInteraction() throws Exception {
        Interaction interaction = connection.createInteraction();
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.READ);
        spec.setFileName("xml-file-insert-test.xml");

        Record output = interaction.execute(spec, null);

        getSession().logMessage("output: " + output);
        interaction.close();
    }

    protected void updateInteraction() throws Exception {
        Interaction interaction = connection.createInteraction();
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.UPDATE);
        spec.setFileName("xml-file-insert-test.xml");

        String data1 = "<order id='1234' orderedby='Joe'></order>";

        EISDOMRecord input = (EISDOMRecord)recordFactory.createMappedRecord("input");
        input.transformFromXML(data1);

        interaction.execute(spec, input);

        interaction.close();
    }

    protected void deleteInteraction() throws Exception {
        Interaction interaction = connection.createInteraction();
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        spec.setFileName("xml-file-insert-test.xml");

        Record output = interaction.execute(spec, null);

        interaction.close();

        if (new File("xml-file-insert-test.xml").exists()) {
            throw new TestErrorException("delete did not remove the file.");
        }
    }
}
