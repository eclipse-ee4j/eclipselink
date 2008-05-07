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

package javax.resource.spi.endpoint;

import java.lang.NoSuchMethodException;
import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.ApplicationServerInternalException;
import javax.resource.spi.IllegalStateException;
import javax.resource.spi.UnavailableException;

/**
 * This defines a contract for a message endpoint. This is implemented by an
 * application server.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface MessageEndpoint {

    /**
     * This is called by a resource adapter before a message is delivered.
     *
     * @param method description of a target method. This information about
     * the intended target method allows an application server to decide 
     * whether to start a transaction during this method call, depending 
     * on the transaction preferences of the target method.
     * The processing (by the application server) of the actual message 
     * delivery method call on the endpoint must be independent of the 
     * class loader associated with this descriptive method object. 
     *
     * @throws NoSuchMethodException indicates that the specified method
     * does not exist on the target endpoint.
     *
     * @throws ResourceException generic exception.
     *
     * @throws ApplicationServerInternalException indicates an error 
     * condition in the application server.
     *
     * @throws IllegalStateException indicates that the endpoint is in an
     * illegal state for the method invocation. For example, this occurs when
     * <code>beforeDelivery</code> and <code>afterDelivery</code> 
     * method calls are not paired.
     *
     * @throws UnavailableException indicates that the endpoint is not 
     * available.
     */
    void beforeDelivery(java.lang.reflect.Method method)
	throws NoSuchMethodException, ResourceException;

    /**
     * This is called by a resource adapter after a message is delivered.
     *
     * @throws ResourceException generic exception.
     *
     * @throws ApplicationServerInternalException indicates an error 
     * condition in the application server.
     *
     * @throws IllegalStateException indicates that the endpoint is in an
     * illegal state for the method invocation. For example, this occurs when
     * beforeDelivery and afterDelivery method calls are not paired.
     *
     * @throws UnavailableException indicates that the endpoint is not 
     * available.
     */
    void afterDelivery() throws ResourceException;

    /**
     * This method may be called by the resource adapter to indicate that it
     * no longer needs a proxy endpoint instance. This hint may be used by
     * the application server for endpoint pooling decisions.
     */
    void release();
}
