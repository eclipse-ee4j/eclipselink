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
 * A NoSuchEJBException is thrown if an attempt is made to invoke
 * a method on an object that no longer exists.
 */
public class NoSuchEJBException extends EJBException {

    /**
     * Constructs a NoSuchEJBException with no detail message.
     */  
    public NoSuchEJBException() {
    }

    /**
     * Constructs a NoSuchEJBException with the specified
     * detail message.
     */  
    public NoSuchEJBException(String message) {
        super(message);
    }

    /**
     * Constructs a NoSuchEJBException with the specified
     * detail message and a nested exception. 
     */  
    public NoSuchEJBException(String message, Exception ex) {
        super(message, ex);
    }
}
