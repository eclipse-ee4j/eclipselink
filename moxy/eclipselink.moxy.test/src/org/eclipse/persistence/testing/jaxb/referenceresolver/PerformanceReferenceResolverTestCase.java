/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.referenceresolver;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.junit.After;
import org.junit.Before;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Performance test case which measures running time of Marshalling and Unmarshalling a huge container.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=437920">EclipseLink Forum, Bug 437920.</a>
 */
public class PerformanceReferenceResolverTestCase extends junit.framework.TestCase {
    private static final int LAPS = 1;

    private ClassicMoxyContainer c;
    private ClassicMoxyContainer cJaxb;
    private JAXBMarshaller marshaller;
    private JAXBUnmarshaller unmarshaller;
    private Unmarshaller unmarshallerJaxb;
    private Marshaller marshallerJaxb;
    private long[][] meas = new long[4][2]; // 4 tests, 2 timings (start and elapsed)

    private static final double THRESHOLD = 2.0;

    public void testPerformance() throws Exception {

        System.out.println("PerformanceReferenceResolverTestCase - Started");

        System.out.println("Marshalling:");
        meas[0][0] = System.nanoTime();
        for (int i = 0; i < LAPS; i++) {
            marshal();
        }
        meas[0][1] = System.nanoTime() - meas[0][0];
        System.out.println("\t" + LAPS + " iterations took " + meas[0][1] + "ms.");

        System.out.println("Unmarshalling:");
        meas[1][0] = System.nanoTime();
        for (int i = 0; i < LAPS; i++) {
            unmarshal();
        }
        meas[1][1] = System.nanoTime() - meas[1][0];
        System.out.println("\t" + LAPS + " iterations took " + meas[1][1] + "ms.");

        jaxbRi();
        checkThreshold();

        System.out.println("PerformanceReferenceResolverTestCase - Ended");
    }

    private void checkThreshold() {
        System.out.printf("%s%.2f%s\n",
                "EL Marshalling is ", (double) meas[0][1] / meas[2][1], " times slower than JAXB-RI."
                + (((double) meas[0][1] / meas[2][1] > THRESHOLD) ? " ...................THRESHOLD EXCEEDED." :
                " .....................THRESHOLD PASSED."));
        System.out.printf("%s%.2f%s\n",
                "EL Unmarshalling is ", (double) meas[1][1] / meas[3][1], " times slower than JAXB-RI."
                + (((double) meas[1][1] / meas[3][1] > THRESHOLD) ? " ...................THRESHOLD EXCEEDED." :
                " .....................THRESHOLD PASSED."));
    }

    private void jaxbRi() throws JAXBException, IOException {
        System.out.println("JAXB-RI comparison tests:");

        System.out.println("Marshalling JAXB-RI:");
        meas[2][0] = System.nanoTime();
        for (int i = 0; i < LAPS; i++) {
            marshalJaxbRi();
        }
        meas[2][1] = System.nanoTime() - meas[2][0];
        System.out.println("\t" + LAPS + " iterations took " + meas[2][1] + "ms.");

        System.out.println("Unmarshalling JAXB-RI:");
        meas[3][0] = System.nanoTime();
        for (int i = 0; i < LAPS; i++) {
            unmarshalJaxbRi();
        }
        meas[3][1] = System.nanoTime() - meas[3][0];
        System.out.println("\t" + LAPS + " iterations took " + meas[3][1] + "ms.");
    }

    @Before
    public void setUp() throws Exception {
        c = ClassicMoxyContainer.createHugeContainer();
        cJaxb = ClassicMoxyContainer.createHugeContainer();
        JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(
                new Class[]{ClassicMoxyContainer.class, Layer.class, Component.class}, new HashMap());
        javax.xml.bind.JAXBContext contextJaxb = JAXBContext.newInstance(
                ClassicMoxyContainer.class, Layer.class, Component.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(JAXBMarshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = context.createUnmarshaller();
        marshallerJaxb = contextJaxb.createMarshaller();
        marshallerJaxb.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshallerJaxb = contextJaxb.createUnmarshaller();
    }

    @After
    public void tearDown() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        new File("fc.xml").delete();
    }

    public void marshal() throws Exception {
        marshaller.marshal(c, new FileWriter("fc.xml"));
    }

    public void unmarshal() throws Exception {
        c = ClassicMoxyContainer.class.cast(unmarshaller.unmarshal(new FileReader("fc.xml")));
    }

    private void unmarshalJaxbRi() throws JAXBException, FileNotFoundException {
        cJaxb = ClassicMoxyContainer.class.cast(unmarshallerJaxb.unmarshal(new FileReader("fc.xml")));
    }

    private void marshalJaxbRi() throws JAXBException, IOException {
        marshallerJaxb.marshal(cJaxb, new FileWriter("fc.xml"));
    }
}
