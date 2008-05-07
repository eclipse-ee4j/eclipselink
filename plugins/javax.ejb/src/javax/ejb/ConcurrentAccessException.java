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
 * A ConcurrentAccessException indicates that the client 
 * has attempted an invocation on a stateful session bean
 * while another invocation is in progress.
 */
public class ConcurrentAccessException extends EJBException {
    /**
     * Constructs an ConcurrentAccessException with no detail message.
     */  
    public ConcurrentAccessException() {
    }

    /**
     * Constructs an ConcurrentAccessException with the specified
     * detailed message.
     */  
    public ConcurrentAccessException(String message) {
        super(message);
    }

    /**
     * Constructs an ConcurrentAccessException with the specified
     * detail message and a nested exception. 
     */  
    public ConcurrentAccessException(String message, Exception ex) {
        super(message, ex);
    }
}
