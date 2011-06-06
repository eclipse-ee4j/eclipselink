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
 * The CreateException exception must be included in the throws clauses of
 * all create methods defined in an enterprise Bean's home 
 * interface. 
 *
 * <p> This exception is used as a standard application-level exception to
 * report a failure to create an EJB object.
 */
public class CreateException extends java.lang.Exception {

    /**
     * Constructs a CreateException with no detail message.
     */  
    public CreateException() {
    }

    /**
     * Constructs a CreateException with the specified
     * detail message.
     */  
    public CreateException(String message) {
        super(message);
    }
}

