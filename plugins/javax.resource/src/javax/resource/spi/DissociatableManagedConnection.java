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
 * <code>ManagedConnection</code> implementation. An implementation of
 * this interface must support the lazy connection association optimization.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface DissociatableManagedConnection {

    /**
     * This method is called by an application server (that is capable of
     * lazy connection association optimization) in order to dissociate
     * a <code>ManagedConnection</code> instance from all of its connection
     * handles.
     *
     * @throws ResourceException generic exception if operation fails.
     *
     * @throws ResourceAdapterInternalException
     *            resource adapter internal error condition
     * @throws IllegalStateException Illegal state for calling connection
     *            cleanup. Example - if a localtransaction is in progress 
     *            that doesn't allow connection cleanup.
     */
    void dissociateConnections() throws ResourceException;
}

