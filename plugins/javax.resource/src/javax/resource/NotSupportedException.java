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

package javax.resource;

/**
 * A <code>NotSupportedException</code> is thrown to indicate that 
 * callee (resource adapter
 * or application server for system contracts) cannot execute an operation 
 * because the operation is not a supported feature. For example, if the 
 * transaction support level for a resource adapter is 
 * <code>NO_TRANSACTION</code>, an invocation of <code>getXAResource</code> 
 * method on a <code>ManagedConnection</code> object should throw a
 * <code>NotSupportedException</code> exception.
 * 
 * @version 1.0
 * @author Rahul Sharma
 * @author Ram Jeyaraman
 */

public class NotSupportedException extends javax.resource.ResourceException {
    
    /**
     * Constructs a new instance with null as its detail message.
     */
    public NotSupportedException() { super(); }

    /**
     * Constructs a new instance with the specified detail message.
     *
     * @param message the detail message.
     */
    public NotSupportedException(String message) {
	super(message);
    }

    /**
     * Constructs a new throwable with the specified cause.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public NotSupportedException(Throwable cause) {
	super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * @param message the detail message.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public NotSupportedException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * error code.
     *
     * @param message a description of the exception.
     * @param errorCode a string specifying the vendor specific error code.
     */
    public NotSupportedException(String message, String errorCode) {
	super(message, errorCode);
    }
}
