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
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.json.unmarshal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.oxm.json.JsonStructureSource;
import org.eclipse.persistence.testing.perf.json.model.Employee;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Tests Json unmarshalling.
 */
@State(Scope.Benchmark)
public class JsonUnmarshalBenchmark {

    private static final String INPUT_JSON = "org/eclipse/persistence/testing/perf/json/unmarshal/input.json";

    private JAXBContext jaxbContext;

    /*
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJAXBContext();
    }

    @Benchmark
    public void testJsonReaderUnmarshal(Blackhole bh) throws Exception {

        InputStream is = null;

        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT_JSON);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Parse the JSON
            JsonReader jsonReader = Json.createReader(is);

            // Unmarshal Root Level JsonArray
            JsonArray employeesArray = jsonReader.readArray();

            JsonStructureSource arraySource = new JsonStructureSource(employeesArray);
            @SuppressWarnings("unchecked")
            List<Employee> employees = (List<Employee>) unmarshaller.unmarshal(arraySource, Employee.class).getValue();
            bh.consume(employees);

            // Unmarshal Nested JsonObject
            JsonObject employeeObject = employeesArray.getJsonObject(1);
            JsonStructureSource objectSource = new JsonStructureSource(employeeObject);
            Employee employee = unmarshaller.unmarshal(objectSource, Employee.class).getValue();

            bh.consume(employee);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    @Benchmark
    public void testJsonReader(Blackhole bh) throws Exception {

        InputStream is = null;

        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT_JSON);

            // Parse the JSON
            JsonReader jsonReader = Json.createReader(is);

            // Unmarshal Root Level JsonArray
            JsonArray employeesArray = jsonReader.readArray();

            bh.consume(employeesArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    @Benchmark
    public void testJsonMOXyUnmarshal(Blackhole bh) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT_JSON);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object testObject = unmarshaller.unmarshal(new StreamSource(inputStream), Employee.class);
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Employee> employees = (List<Employee>)(((JAXBElement)testObject).getValue());
            bh.consume(employees);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }

    }

    @Benchmark
    public void testJsonParser(Blackhole bh) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT_JSON);
            JsonParser jr = Json.createParser(inputStream);
            Event event = null;

            while(jr.hasNext()) {
                event = jr.next();
                bh.consume(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }

    private void prepareJAXBContext() throws Exception {
        Map<String, Object> jaxbProperties = new HashMap<String, Object>(2);
        jaxbProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        jaxbProperties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        jaxbContext = JAXBContext.newInstance(new Class[] { Employee.class }, jaxbProperties);
    }
}
