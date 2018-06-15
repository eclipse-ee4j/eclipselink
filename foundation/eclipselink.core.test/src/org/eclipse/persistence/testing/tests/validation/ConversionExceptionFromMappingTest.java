/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import java.util.Hashtable;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


/**
 * @author Guy Pelletier
 * @version 1.0
 * @date June 25, 2003
 */
public class ConversionExceptionFromMappingTest extends AutoVerifyTestCase {
    ConversionException m_exception;

    public ConversionExceptionFromMappingTest() {
        setDescription("Ensures that the correct ConversionException is thrown.");
    }

    public void reset() {
    }

    public void setup() throws Exception {
    }

    public void test() throws Exception {
        DirectToFieldMapping map = new DirectToFieldMapping();
        map.setAttributeName("foobar");
        map.setAttributeClassification(Hashtable.class);

        try {
            map.getObjectValue("foobar", getSession());
        } catch (ConversionException e) {
            m_exception = e;
        }
    }

    public void verify() throws Exception {
        if (m_exception.getErrorCode() != ConversionException.COULD_NOT_BE_CONVERTED_EXTENDED) {
            throw new TestErrorException("Invalid conversion exception was thrown");
        }
    }
}
