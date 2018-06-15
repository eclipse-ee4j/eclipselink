/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.perf.beanvalidation;

import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Performance baseline for JAXB processes using Bean Validation.
 * Tests provide comparison in performance of bean validation callbacks occurring during basic MOXy mapping
 * processes, with objects that are constrained against the same objects that are not constrained.
 *
 * @author Marcel Valovy
 */
@State(Scope.Benchmark)
public class MOXyValidationBenchmark {

    private static final String EMPLOYEE_XML = "org/eclipse/persistence/testing/perf/validation/employee.xml";
    private static final String EMPLOYEE_ANNOTATED_XML =
            "org/eclipse/persistence/testing/perf/validation/employeeAnnotated.xml";
    private static final Class[] EMPLOYEES = new Class[]{ Employee.class, EmployeeAnnotated.class };

    private JAXBContext ctx;
    private JAXBUnmarshaller unm;
    private JAXBMarshaller mar;

    @Benchmark
    public void testMarshal(Blackhole bh) throws Exception {
        StringWriter writer = new StringWriter();
        Employee employee = new Employee().withAge(51289).withPersonalName("Robert Paulson")
                .withPhoneNumber("(420)333-4444").withId(250);
        mar.marshal(employee, writer);
        bh.consume(writer);
        bh.consume(employee);
    }

    @Benchmark
    public void testUnmarshal(Blackhole bh) throws Exception {
        Employee result = (Employee) unm.unmarshal(Thread.currentThread().getContextClassLoader().getResource
                (EMPLOYEE_XML));
        bh.consume(result);
    }

    @Benchmark
    public void testMarshalAnnotated(Blackhole bh) throws Exception {
        StringWriter writer = new StringWriter();
        EmployeeAnnotated employee = new EmployeeAnnotated().withAge(51289).withPersonalName("Robert Paulson")
                .withPhoneNumber("(420)333-4444").withId(250);
        mar.marshal(employee, writer);
        bh.consume(writer);
        bh.consume(employee);
    }

    @Benchmark
    public void testUnmarshalAnnotated(Blackhole bh) throws Exception {
        EmployeeAnnotated result = (EmployeeAnnotated) unm.unmarshal(Thread.currentThread().getContextClassLoader()
                .getResource(EMPLOYEE_ANNOTATED_XML));
        bh.consume(result);
    }

    /**
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJAXB();
    }

    /**
     * Clean-up.
     */
    @TearDown
    public void tearDown() throws Exception {
        ctx = null;
        unm = null;
        mar = null;
    }

    private void prepareJAXB() throws Exception {
        ctx = JAXBContextFactory.createContext(EMPLOYEES, new HashMap<Object, Object>(){{put(JAXBContextProperties
                .BEAN_VALIDATION_MODE, BeanValidationMode.CALLBACK);}});
        mar = (JAXBMarshaller) ctx.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        unm = (JAXBUnmarshaller) ctx.createUnmarshaller();
    }
}
