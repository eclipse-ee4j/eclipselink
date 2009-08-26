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
 * This interface specifies the methods to associate a 
 * <code>ResourceAdapter</code> object with other objects that 
 * implement this interface like 
 * <code>ManagedConnectionFactory</code> and <code>ActivationSpec</code>.
 *
 * @version 1.0
 * @author  Ram Jeyaraman
 */
public interface ResourceAdapterAssociation {

    /**
     * Get the associated <code>ResourceAdapter</code> object.
     *
     * @return the associated <code>ResourceAdapter</code> object.
     */
    ResourceAdapter getResourceAdapter();

    /**
     * Associate this object with a <code>ResourceAdapter</code> object. 
     * Note, this method must be called exactly once. That is, the 
     * association must not change during the lifetime of this object.
     *
     * @param ra <code>ResourceAdapter</code> object to be associated with.
     *
     * @throws ResourceException generic exception.
     *
     * @throws ResourceAdapterInternalException
     *            resource adapter related error condition.
     *
     * @throws IllegalStateException indicates that this object is in an
     * illegal state for the method invocation. For example, this occurs when
     * this method is called more than once on the same object.
     */
    void setResourceAdapter(ResourceAdapter ra) throws ResourceException;
}

