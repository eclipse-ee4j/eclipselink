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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.testing.framework.TestErrorException;


//Created by Ian Reid
//Date: Mar 5, 2k3
//None standard Test

public class ProxyIndirectionNotAvailableTest extends ExceptionTest {
    public ProxyIndirectionNotAvailableTest() {
        super();
        setDescription("This tests Proxy Indirection Not Available (TL-ERROR 159)");
    }

    protected void setup() {
        expectedException = DescriptorException.proxyIndirectionNotAvailable(null);
    }

    public void test() {
        ProxyIndirectionPolicy policy = new ProxyIndirectionPolicy();
        try {
            policy.initialize();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    protected void verify() {
        String jdkVersion = System.getProperty("java.version");
        if (jdkVersion.compareTo("1.3.0") < 0) { //jdk 1.2
            if (caughtException == null) {
                throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "caught exception was null! \n\n[EXPECTING] " + expectedException + " with JDK less than 1.3.0");
            }
            if (caughtException.getErrorCode() != expectedException.getErrorCode()) {
                throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "[CAUGHT] " + caughtException + "\n\n[EXPECTING] " + expectedException + " with JDK less than 1.3.0");
            }
        } //end jdk 1.2 testing
        else { //jdk 1.3 testing
            if (caughtException != null) {
                throw new TestErrorException("The proper exception was not thrown:" + org.eclipse.persistence.internal.helper.Helper.cr() + "[CAUGHT] " + caughtException + "\n\n[EXPECTING] no Exception with JDK 1.3");
            }
        }
    }

}
