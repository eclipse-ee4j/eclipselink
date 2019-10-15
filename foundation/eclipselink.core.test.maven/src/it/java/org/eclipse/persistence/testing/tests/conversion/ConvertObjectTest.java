/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ConvertObjectTest extends AutoVerifyTestCase {
    protected boolean m_failure;
    protected boolean m_testingFailure;
    protected Object m_testObj;
    protected Class m_targetClass;

    /**
     * This constructor is used when an exception being thrown implies the test
     * failed.
     */
    public ConvertObjectTest(Object testObj, Class targetClass) {
        m_testObj = testObj;
        m_targetClass = targetClass;
        m_testingFailure = false;
    }

    /**
     * This constructor is used when an exception being thrown implies the test
     * passed.
     */
    public ConvertObjectTest(Object testObj, Class targetClass, boolean testingFailure) {
        m_testObj = testObj;
        m_targetClass = targetClass;
        m_testingFailure = testingFailure;
    }

    public void setup() {
        m_failure = false;
    }

    public void test() {
        try {
            new ConversionManager().convertObject(m_testObj, m_targetClass);
        } catch (ConversionException cx) {
            if (!m_testingFailure) {
                m_failure = true;
            }
        }
    }

    public void verify() {
        if (m_failure) {
            throw (new TestErrorException("Conversion of '" + m_testObj + "' to '" + m_targetClass + "' falied."));
        }
    }

    public void reset() {
        m_failure = false;
        m_testingFailure = false;
        m_testObj = null;
        m_targetClass = null;
    }
}
