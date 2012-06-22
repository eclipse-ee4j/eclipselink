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
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.exceptions.*;

/**
 * <p>
 * <b>Purpose</b>: Any exception raised by TopLink should be a subclass of this exception class.
 */
public class TestException extends EclipseLinkException {
    public TestException(String theMessage) {
        super(theMessage);
    }

    public TestException(String theMessage, Throwable internalException) {
        super(theMessage, internalException);
    }
}
