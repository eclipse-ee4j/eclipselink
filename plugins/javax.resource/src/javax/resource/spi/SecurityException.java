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

package javax.resource.spi;

/** 
 * A <code>SecurityException</code> indicates error conditions 
 * related to the security
 * contract between an application server and resource adapter. The common
 * error conditions represented by this exception are:
 * <UL>
 * <LI>Invalid security information (represented as a Subject instance) passed
 *     across the security contract - for example, credentials have expired or
 *     have invalid format.
 * <LI>Lack of support for a specific security mechanism in an EIS or resource 
 *     adapter.
 * <LI>Failure to create a connection to an EIS because of failed 
 *     authentication or authorization.
 * <LI>Failure to authenticate a resource principal to an EIS instance 
 *     or failure 
 *     to establish a secure association with an underlying EIS instance.
 * <LI>Access control exception to indicate that a requested access to an EIS 
 *     resource or a request to create a new connection is denied.
 *  </UL>
 *
 * @version 1.0
 * @author Rahul Sharma
 * @author Ram Jeyaraman
 */

public class SecurityException extends javax.resource.ResourceException {

    /**
     * Constructs a new instance with null as its detail message.
     */
    public SecurityException() { super(); }

    /**
     * Constructs a new instance with the specified detail message.
     *
     * @param message the detail message.
     */
    public SecurityException(String message) {
	super(message);
    }

    /**
     * Constructs a new throwable with the specified cause.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public SecurityException(Throwable cause) {
	super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * @param message the detail message.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public SecurityException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * an error code.
     *
     * @param message a description of the exception.
     * @param errorCode a string specifying the vendor specific error code.
     */
    public SecurityException(String message, String errorCode) {
	super(message, errorCode);
    }
}
