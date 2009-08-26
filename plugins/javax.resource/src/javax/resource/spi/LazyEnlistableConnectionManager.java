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
 * this interface must support the lazy transaction enlistment optimization.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface LazyEnlistableConnectionManager {

    /**
     * This method is called by a resource adapter (that is capable of
     * lazy transaction enlistment optimization) in order to lazily enlist
     * a connection object with a XA transaction. 
     *
     * @param mc The <code>ManagedConnection</code> instance that needs to be
     * lazily associated.
     *
     * @throws  ResourceException Generic exception.
     *
     * @throws  ApplicationServerInternalException 
     *                            Application server specific exception.
     *
     * @throws  ResourceAllocationException
     *                            Failed to allocate system resources for
     *                            connection request.
     *
     * @throws  ResourceAdapterInternalException
     *                            Resource adapter related error condition.
     */
    void lazyEnlist(ManagedConnection mc) throws ResourceException;
}

