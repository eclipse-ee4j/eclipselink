/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.exceptions;


/**
 * Exception handler can catch errors that occur on queries or during database access.
 * The exception handler has the option of re-throwing the exception,throwing a different
 * exception or re-trying the query or database operation.
 */
public interface ExceptionHandler {

    /**
     * To re-throwing the exception,throwing a different
     * exception or re-trying the query or database operation.
     */
    Object handleException(RuntimeException exception);
}
