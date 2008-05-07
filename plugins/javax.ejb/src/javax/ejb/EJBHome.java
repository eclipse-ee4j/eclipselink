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
package javax.ejb;

import java.rmi.RemoteException;

/**
 * The EJBHome interface must be extended by all enterprise
 * Beans' remote home interfaces. An enterprise Bean's remote home interface 
 * defines the
 * methods that allow a remote client to create, find, and remove EJB objects,
 * as well as home business methods that
 * are not specific to a bean instance (Session Beans do not have
 * finders and home methods).

 * <p> The remote home interface is defined by the enterprise Bean provider and 
 * implemented by the enterprise Bean container.
 */
public interface EJBHome extends java.rmi.Remote {

    /**
     * Remove an EJB object identified by its handle.
     *
     * @exception RemoveException Thrown if the enterprise Bean or
     *    the container does not allow the client to remove the object.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    void remove(Handle handle) throws RemoteException, RemoveException;

    /**
     * Remove an EJB object identified by its primary key.
     *
     * <p>This method can be used only for an entity bean. An attempt
     * to call this method on a session bean will result in a RemoveException.
     *
     * @exception RemoveException Thrown if the enterprise Bean or
     *    the container does not allow the client to remove the object.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    void remove(Object primaryKey) throws RemoteException, RemoveException;

    /**
     * Obtain the EJBMetaData interface for the enterprise Bean. The
     * EJBMetaData interface allows the client to obtain information about
     * the enterprise Bean.
     *
     * <p> The information obtainable via the EJBMetaData interface is
     * intended to be used by tools.
     *
     * @return The enterprise Bean's EJBMetaData interface.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    EJBMetaData getEJBMetaData() throws RemoteException;

    /**
     * Obtain a handle for the remote home object. The handle can be used at 
     * later time to re-obtain a reference to the remote home object, possibly 
     * in a different Java Virtual Machine.
     *
     * @return A handle for the remote home object.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.     
     */
    HomeHandle getHomeHandle() throws RemoteException;
}
