/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.conversion;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ConvertClassTypeTest extends AutoVerifyTestCase {
    protected boolean m_failure;
    protected String m_testStr;
    protected Class m_testClass;
    protected Class m_type;

    public ConvertClassTypeTest(String testStr, Class type) {
        m_testStr = testStr;
        m_testClass = null;
        m_type = type;
    }

    public ConvertClassTypeTest(Class testClass, Class type) {
        m_testClass = testClass;
        m_testStr = null;
        m_type = type;
    }

    public void setup() {
        m_failure = false;
    }

    public void test() {
        if (m_testClass == null) {
            if (ConversionManager.getPrimitiveClass(m_testStr) != m_type) {
                m_failure = true;
            }
        } else {
            if (ConversionManager.getObjectClass(m_testClass) != m_type) {
                m_failure = true;
            }
        }
    }

    public void verify() {
        if (m_failure) {
            throw (new TestErrorException("Conversion of '" + m_testClass + "' to '" + m_type + "' falied."));
        }
    }

    public void reset() {
        m_failure = false;
        m_testStr = "";
        m_testClass = null;
        m_type = null;
    }
}
