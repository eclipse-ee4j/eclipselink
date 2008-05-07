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
import javax.transaction.xa.XAResource;
import javax.resource.spi.UnavailableException;

/**
 * This serves as a factory for creating message endpoints.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface MessageEndpointFactory {

    /**
     * This is used to create a message endpoint. The message endpoint is
     * expected to implement the correct message listener type.
     *
     * @param xaResource an optional <code>XAResource</code> 
     * instance used to get transaction notifications when the message delivery
     * is transacted.
     *
     * @return a message endpoint instance.
     *
     * @throws UnavailableException indicates a transient failure
     * in creating a message endpoint. Subsequent attempts to create a message
     * endpoint might succeed.
     */
    MessageEndpoint createEndpoint(XAResource xaResource)
	throws UnavailableException;

    /**
     * This is used to find out whether message deliveries to a target method
     * on a message listener interface that is implemented by a message 
     * endpoint will be transacted or not. 
     *
     * The message endpoint may indicate its transacted delivery preferences 
     * (at a per method level) through its deployment descriptor. The message 
     * delivery preferences must not change during the lifetime of a 
     * message endpoint. 
     * 
     * @param method description of a target method. This information about
     * the intended target method allows an application server to find out 
     * whether the target method call will be transacted or not.
     *
     * @throws NoSuchMethodException indicates that the specified method
     * does not exist on the target endpoint.
     *
     * @return true, if message endpoint requires transacted message delivery.
     */
    boolean isDeliveryTransacted(java.lang.reflect.Method method)
	throws NoSuchMethodException;
}



