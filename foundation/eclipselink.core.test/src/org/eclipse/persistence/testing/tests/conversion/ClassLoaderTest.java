/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
