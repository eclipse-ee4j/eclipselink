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
 * A <code>ResourceAdapterInternalException</code> indicates any 
 * system-level error conditions related to a resource adapter. 
 * The common conditions indicated by this exception type are:
 *  <UL>
 *  <LI>Invalid configuration for creation of a new physical connection. An
        example is invalid server name for a target EIS instance.
 *  <LI>Failure to create a physical connection to a EIS instance due to 
 *      communication protocol error or any resource adapter implementation 
 *      specific error.
 *  <LI>Error conditions internal to resource adapter implementation.
 *  </UL>
 *
 * @version 1.0
 * @author Rahul Sharma
 * @author Ram Jeyaraman
 */

public class ResourceAdapterInternalException 
        extends javax.resource.ResourceException {

    /**
     * Constructs a new instance with null as its detail message.
     */
    public ResourceAdapterInternalException() { super(); }

    /**
     * Constructs a new instance with the specified detail message.
     *
     * @param message the detail message.
     */
    public ResourceAdapterInternalException(String message) {
	super(message);
    }

    /**
     * Constructs a new throwable with the specified cause.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public ResourceAdapterInternalException(Throwable cause) {
	super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * @param message the detail message.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public ResourceAdapterInternalException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * an error code.
     *
     * @param message a description of the exception.
     * @param errorCode a string specifying the vendor specific error code.
     */
    public ResourceAdapterInternalException(String message, String errorCode) {
	super(message, errorCode);
    }
}
