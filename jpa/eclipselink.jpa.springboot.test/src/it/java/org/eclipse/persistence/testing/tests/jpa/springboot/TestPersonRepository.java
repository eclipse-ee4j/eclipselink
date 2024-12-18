/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.jpa.springboot;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.eclipse.persistence.testing.models.jpa.springboot.Person;
import org.eclipse.persistence.testing.models.jpa.springboot.PersonRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = { PersonApplication.class, EclipseLinkJpaConfiguration.class })
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestPersonRepository {

    Person PERSONS[] = new Person[] {
            new Person(1, "John", "Doe")
    };

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testPerson() {
        Person person = personRepository.findPersonById(PERSONS[0].getId());
        assertEquals(PERSONS[0], person);
    }

    @BeforeEach
    public void setUp() {
        personRepository.deleteAll();
        personRepository.save(PERSONS[0]);
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAll();
    }
}
