/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package javax.ejb;

/**
 * The RemoveException exception is thrown at an attempt to remove
 * an EJB object when the enterprise Bean or the container does not allow 
 * the EJB object to be removed.
 */
public class RemoveException extends java.lang.Exception {
    /**
     * Constructs an RemoveException with no detail message.
     */  
    public RemoveException() {
    }

    /**
     * Constructs an RemoveException with the specified
     * detail message.
     */  
    public RemoveException(String message) {
        super(message);
    }
}

