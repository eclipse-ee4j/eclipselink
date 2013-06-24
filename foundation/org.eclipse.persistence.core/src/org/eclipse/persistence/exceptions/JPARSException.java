/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.exceptions;

public class JPARSException extends EclipseLinkException {
    // Next range should start from LAST_ERROR_CODE (62000). 
    // The JPA-RS uses error codes between 61000-61999 (both inclusive).
    public enum ErrorCode {
        ENTITY_NOT_FOUND(61000),

        // end marker for JPA-RS error codes    
        LAST_ERROR_CODE(62000);
        private int value;

        private ErrorCode(int value) {
            this.value = value;
        }

        public String value() {
            return String.valueOf(value);
        }

    };

    public JPARSException() {
        super();
    }

    public JPARSException(String message) {
        super(message);
    }

    public JPARSException(String msg, Exception e) {
        super(msg, e);
    }
}