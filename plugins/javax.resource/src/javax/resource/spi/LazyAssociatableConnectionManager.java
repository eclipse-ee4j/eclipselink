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

import javax.resource.ResourceException;

/**
 * This is a mix-in interface that may be optionally implemented by a 
 * <code>ConnectionManager</code> implementation. An implementation of
 * this interface must support the lazy connection association optimization.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface LazyAssociatableConnectionManager {

    /**
     * This method is called by a resource adapter (that is capable of
     * lazy connection association optimization) in order to lazily associate
     * a connection object with a <code>ManagedConnection</code> instance. 
     *
     * @param connection the connection object that is to be associated.
     *
     * @param mcf The <code>ManagedConnectionFactory</code> instance that was
     * originally used to create the connection object.
     *
     * @param cxReqInfo connection request information. This information must
     * be the same as that used to originally create the connection object.
     *
     * @throws  ResourceException     Generic exception.
     *
     * @throws  ApplicationServerInternalException 
     *                              Application server specific exception.
     *
     * @throws  SecurityException     Security related error.
     *
     * @throws  ResourceAllocationException
     *                              Failed to allocate system resources for
     *                              connection request.
     * @throws  ResourceAdapterInternalException
     *                              Resource adapter related error condition.
     */
    void associateConnection(Object connection, ManagedConnectionFactory mcf,
	ConnectionRequestInfo cxReqInfo) throws ResourceException;
}

