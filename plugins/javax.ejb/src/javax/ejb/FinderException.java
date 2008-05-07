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
 * The FinderException exception must be included in the throws clause
 * of every findMETHOD(...) method of an entity Bean's home interface.
 *
 * <p> The exception is used as a standard application-level exception to 
 * report a failure to find the requested EJB object(s).
 */
public class FinderException extends java.lang.Exception {
    /**
     * Constructs an FinderException with no detail message.
     */  
    public FinderException() {
    }

    /**
     * Constructs an FinderException with the specified
     * detail message.
     */  
    public FinderException(String message) {
        super(message);
    }
}

