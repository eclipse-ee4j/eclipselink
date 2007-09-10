/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;


/**
 * <p>
 * <b>Purpose</b>: The exeception is raised if the test case failed because of TopLink logic.
 */
public class TestErrorException extends TestException {
    public TestErrorException(String message) {
        super(message);
    }

    public TestErrorException(String message, Throwable internalException) {
        super(message, internalException);
    }
}