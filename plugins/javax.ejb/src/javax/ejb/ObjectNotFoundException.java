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
 * The ObjectNotFoundException exception is thrown by a finder method to
 * indicate that the specified EJB object does not exist.
 *
 * <p> Only the finder methods that are declared to return a single EJB object
 * use this exception. This exception should not be thrown by finder methods
 * that return a collection of EJB objects (they should return an empty 
 * collection instead).
 */
public class ObjectNotFoundException extends FinderException {
    /**
     * Constructs an ObjectNotFoundException with no detail message.
     */  
    public ObjectNotFoundException() {
    }

    /**
     * Constructs an ObjectNotFoundException with the specified
     * detail message.
     */  
    public ObjectNotFoundException(String message) {
        super(message);
    }
}

