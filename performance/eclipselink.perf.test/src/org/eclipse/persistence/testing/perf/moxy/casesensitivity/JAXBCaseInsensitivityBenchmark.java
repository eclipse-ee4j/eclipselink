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
 *     Marcel Valovy - 2.6.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.perf.moxy.casesensitivity;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.perf.moxy.casesensitivity.correctCase.LoremIpsum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Performance test case which compares case sensitive unmarshalling against case insensitive unmarshalling
 *
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=331241">EclipseLink Forum, Bug 331241.</a>
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class JAXBCaseInsensitivityBenchmark extends junit.framework.TestCase {

    private static final File FILE = new File(Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/casesensitivity/loremIpsum.xml").getPath());

    private static final int WARM_UP = 10000;
    private static final int LAPS    = 100000;

    private Unmarshaller unmOtherCaseInsensitive;
    private Unmarshaller unmCorrectCaseSensitive;

    @Test
    public void testMain() throws Exception {

        ci: {
            // Warm up the code
            for (int i = 0; i < WARM_UP; ++i)
                unmarshalOtherCaseInsensitive();

            // Time it
            final long start = System.nanoTime();
            for(int i=0; i < LAPS; i++)
                unmarshalOtherCaseInsensitive();
            final long elapsed = System.nanoTime() - start;
            System.out.println("Case insensitive: \n\t" + LAPS+" iterations took " + TimeUnit.NANOSECONDS.toMillis(elapsed) + "ms.");
        }

        cs: {
            // Warm up the code
            for (int i = 0; i < WARM_UP; ++i)
                unmarshalCorrectCaseSensitive();

            // Time it
            final long start = System.nanoTime();
            for(int i=0; i < LAPS; i++)
                unmarshalCorrectCaseSensitive();
            final long elapsed = System.nanoTime() - start;
            System.out.println("Case sensitive: \n\t" + LAPS+" iterations took " + TimeUnit.NANOSECONDS.toMillis(elapsed) + "ms.");
        }

    }

    private LoremIpsum unmarshalCorrectCaseSensitive() throws JAXBException {

        LoremIpsum loremCorrectCase
                = (LoremIpsum) unmCorrectCaseSensitive.unmarshal(FILE);

        return loremCorrectCase;
    }

    private org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase.LoremIpsum
    unmarshalOtherCaseInsensitive() throws JAXBException {

        org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase.LoremIpsum loremOtherCase
                = (org.eclipse.persistence.testing.perf.moxy.casesensitivity.otherCase.LoremIpsum) unmOtherCaseInsensitive.unmarshal(FILE);

        return loremOtherCase;
    }

    @Before
    public void setUp() throws Exception {

        /* Create and assign case-sensitive unmarshaller */
        JAXBContext ctxCorrectCaseSensitive = JAXBContextFactory.createContext(new Class[]{LoremIpsum.class}, null);
        unmCorrectCaseSensitive = ctxCorrectCaseSensitive.createUnmarshaller();

        /* Create and assign case-insensitive unmarshaller */
        JAXBContext ctxOtherCaseInsensitive = JAXBContextFactory.createContext(new Class[]{ org.eclipse.persistence
                .testing.perf.moxy.casesensitivity.otherCase.LoremIpsum.class}, null);
        unmOtherCaseInsensitive = ctxOtherCaseInsensitive.createUnmarshaller();
        unmOtherCaseInsensitive.setProperty(UnmarshallerProperties.UNMARSHALLING_CASE_INSENSITIVE, Boolean.TRUE);
    }

    @After
    public void tearDown() throws Exception {
        unmCorrectCaseSensitive = unmOtherCaseInsensitive = null;
    }
}
