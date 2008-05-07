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
 * The NoSuchEntityException exception is thrown by an Entity Bean 
 * instance to its container to report that the invoked business method 
 * or callback method could not be completed because of the underlying
 * entity was removed from the database. 
 *
 * <p>This exception may be thrown by the bean class methods that implement
 * the business methods defined in the bean's component interface; and by 
 * the ejbLoad and ejbStore methods.
 */
public class NoSuchEntityException extends EJBException {
    /**
     * @serial
     */

    /**
     * Constructs a NoSuchEntityException with no detail message.
     */  
    public NoSuchEntityException() {
    }

    /**
     * Constructs a NoSuchEntityException with the specified
     * detailed message.
     */  
    public NoSuchEntityException(String message) {
        super(message);
    }

    /**
     * Constructs a NoSuchEntityException that embeds the originally 
     * thrown exception.
     */  
    public NoSuchEntityException(Exception  ex) {
        super(ex);
    }
}
