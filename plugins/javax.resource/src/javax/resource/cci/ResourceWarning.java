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

package javax.resource.cci;

/**
 * A <code>ResourceWarning</code> provides information on warnings related to
 * execution of an interaction with an EIS. Warnings are silently 
 * chained to the object whose method caused it to be reported.
 *
 * @see Interaction#getWarnings
 */
public class ResourceWarning extends javax.resource.ResourceException {

    /**
     * Constructs a new instance with null as its detail message.
     */
    public ResourceWarning() { super(); }

    /**
     * Constructs a new instance with the specified detail message.
     *
     * @param message the detail message.
     */
    public ResourceWarning(String message) {
	super(message);
    }

    /**
     * Constructs a new throwable with the specified cause.
     *
     * @param cause a chained exception of type 
     * <code>Throwable</code>.
     */
    public ResourceWarning(Throwable cause) {
	super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * @param message the detail message.
     *
     * @param cause a chained exception of type 
     * <code>Throwable</code>.
     */
    public ResourceWarning(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and 
     * an error code.
     *
     * @param message a description of the exception.
     * @param errorCode a string specifying the vendor specific error code.
     */
    public ResourceWarning(String message, String errorCode) {
	super(message, errorCode);
    }
  
    /**
     * Retrieves the warning chained to this <code>ResourceWarning</code>
     * object.
     *
     * @return next <code>ResourceWarning</code> in the chain; null if none.
     *
     * @deprecated J2SE release 1.4 supports a chained exception facility 
     * that allows any throwable to know about another throwable, if any,
     * that caused it to get thrown. Refer to <code>getCause</code> and 
     * <code>initCause</code> methods of the 
     * <code>java.lang.Throwable</code> class.
     */
    public ResourceWarning getLinkedWarning() {
	try {
	    return ((ResourceWarning)getLinkedException());
	} 
	catch (ClassCastException ex) {
	    return null;
	}
    }
  
    /**
     * Adds an <code>ResourceWarning</code> object to the end of the chain.
     *
     * @param warning <code>ResourceWarning</code> to be added to the chain.
     *
     * @deprecated J2SE release 1.4 supports a chained exception facility 
     * that allows any throwable to know about another throwable, if any,
     * that caused it to get thrown. Refer to <code>getCause</code> and 
     * <code>initCause</code> methods of the 
     * <code>java.lang.Throwable</code> class.
     */
    public void setLinkedWarning(ResourceWarning warning) {
	setLinkedException(warning);
    }
}
