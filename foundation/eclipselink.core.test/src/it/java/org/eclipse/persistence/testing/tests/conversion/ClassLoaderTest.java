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

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ClassLoaderTest extends AutoVerifyTestCase {
    protected boolean m_failure;
    protected ConversionManager m_mgr;

    public ClassLoaderTest() {
    }

    public void setup() {
        m_failure = false;
        m_mgr = new ConversionManager();
        m_mgr.setShouldUseClassLoaderFromCurrentThread(false);
        m_mgr.setLoader(null);
        ConversionManager.setDefaultLoader(ConversionManager.class.getClassLoader());
    }

    public void test() {
        m_failure = (m_mgr.getLoader() != ConversionManager.getDefaultLoader());
    }

    public void verify() {
        if (m_failure) {
            throw (new TestErrorException("getLoader() method test failed."));
        }
    }

    public void reset() {
        m_failure = false;
        m_mgr = null;
    }
}
