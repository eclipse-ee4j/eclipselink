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

import java.beans.PropertyDescriptor;

/**
 * This exception is thrown to indicate invalid configuration 
 * property settings.
 *
 * @version 0.2
 * @author Ram Jeyaraman
 */
public class InvalidPropertyException
        extends javax.resource.ResourceException {

    /*
     * Holder for invalid properties.
     */
    private PropertyDescriptor[] invalidProperties;

    /**
     * Create a InvalidPropertyException.
     */
    public InvalidPropertyException() {
	super();
    }

    /**
     * Create a InvalidPropertyException.
     *
     * @param message a description of the exception
     */
    public InvalidPropertyException(String message) {
	super(message);
    }

    /**
     * Constructs a new throwable with the specified cause.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public InvalidPropertyException(Throwable cause) {
	super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * @param message the detail message.
     *
     * @param cause a chained exception of type <code>Throwable</code>.
     */
    public InvalidPropertyException(String message, Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * an error code.
     *
     * @param message a description of the exception.
     * @param errorCode a string specifying the vendor specific error code.
     */
    public InvalidPropertyException(String message, String errorCode) {
	super(message, errorCode);
    }

    /**
     * Set a list of invalid properties.
     */
    public void setInvalidPropertyDescriptors(
            PropertyDescriptor[] invalidProperties) {
	this.invalidProperties = invalidProperties;
    }

    /**
     * Get the list of invalid properties.
     */
    public PropertyDescriptor[] getInvalidPropertyDescriptors() {
        return this.invalidProperties;
    }
}
