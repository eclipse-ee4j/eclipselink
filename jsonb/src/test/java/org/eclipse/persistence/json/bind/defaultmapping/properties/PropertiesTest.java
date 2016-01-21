/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David Král - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.defaultmapping.properties;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;


/**
 * This class contains properties tests
 *
 * @author David Král
 */
public class PropertiesTest {

    @Test
    public void testPropertiesWithoutLocale() throws IOException {
        String template = "Process class: {0} from json using converter: {1}";
        String message = Messages.getMessage(MessageKeys.PROCESS_FROM_JSON);

        assertEquals(template, message);
    }

    @Test
    public void testPropertiesWithLocale() throws IOException {
        String templateCS = "Zpracovávám třídu: {0} do jsonu za použití convertoru: {1}";
        String messageCS = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("cs"));
        String templateEN = "Process class: {0} to json using converter: {1}";
        String messageEN = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("en"));

        assertEquals(templateCS, messageCS);
        assertEquals(templateEN, messageEN);
    }

    @Test
    public void testPropertiesAttributeSetting() throws IOException {
        String template = "Zpracovávám třídu: Test do jsonu za použití convertoru: Test1";
        String message = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("cs"), "Test", "Test1");

        assertEquals(template, message);
    }

}

