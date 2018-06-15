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
package org.eclipse.persistence.testing.perf.json.marshal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.oxm.json.JsonArrayBuilderResult;
import org.eclipse.persistence.testing.perf.json.model.Employee;
import org.eclipse.persistence.testing.perf.json.model.PhoneNumber;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Tests Json marshalling.
 *
 * @author Martin Vojtek
 *
 */
@State(Scope.Benchmark)
public class JsonMarshalBenchmark {

    private JAXBContext jaxbContext;

    /*
     * Initial setup.
     */
    @Setup
    public void prepare() throws Exception {
        prepareJAXBContext();
    }

    @Benchmark
    public void testJsonMarshal(Blackhole bh) throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();

        // Create the JsonArrayBuilder
        JsonArrayBuilder customersArrayBuilder = Json.createArrayBuilder();

        // Build the First Employee
        Employee customer = new Employee();
        customer.setId(1);
        customer.setFirstName("Jane");
        customer.setLastName(null);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setType("cell");
        phoneNumber.setNumber("555-1111");
        customer.getPhoneNumbers().add(phoneNumber);

        // Marshal the First Customer Object into the JsonArray
        JsonArrayBuilderResult result =
            new JsonArrayBuilderResult(customersArrayBuilder);
        marshaller.marshal(customer, result);

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2);

        PhoneNumber workPhone = new PhoneNumber();
        workPhone.setType("work");
        workPhone.setNumber("555-2222");
        phoneNumbers.add(workPhone);

        PhoneNumber homePhone = new PhoneNumber();
        homePhone.setType("home");
        homePhone.setNumber("555-3333");
        phoneNumbers.add(homePhone);

        JsonArrayBuilderResult arrayBuilderResult = new JsonArrayBuilderResult();
        marshaller.marshal(phoneNumbers, arrayBuilderResult);

        customersArrayBuilder
            .add(Json.createObjectBuilder()
                .add("id", 2)
                .add("firstName", "Bob")
                .addNull("lastName")
                .add("phoneNumbers", arrayBuilderResult.getJsonArrayBuilder())
             );

        // Write JSON to output stream
        Map<String, Object> jsonProperties = new HashMap<String, Object>(1);
        jsonProperties.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(jsonProperties);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter writer = writerFactory.createWriter(baos);
        writer.writeArray(customersArrayBuilder.build());
        writer.close();
        baos.flush();

        baos.close();

        bh.consume(writer);
        bh.consume(baos);
    }

    private void prepareJAXBContext() throws Exception {

        Map<String, Object> jaxbProperties = new HashMap<String, Object>(2);
        jaxbProperties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
        jaxbProperties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, false);
        jaxbContext = JAXBContext.newInstance(new Class[] {Employee.class}, jaxbProperties);
    }
}
