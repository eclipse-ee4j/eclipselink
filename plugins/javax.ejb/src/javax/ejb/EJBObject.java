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
 * The EJBObject interface is extended by all enterprise Beans' remote 
 * interfaces. An enterprise Bean's remote interface provides the remote
 * client view
 * of an EJB object. An enterprise Bean's remote interface defines 
 * the business methods callable by a remote client.
 *
 * <p> The remote interface must
 * extend the javax.ejb.EJBObject interface, and define the enterprise Bean
 * specific business methods.
 *
 * <p> The enterprise Bean's remote interface is defined by the enterprise
 * Bean provider and implemented by the enterprise Bean container.
 */
public interface EJBObject extends java.rmi.Remote {
    /**
     * Obtain the enterprise Bean's remote home interface. The remote home 
     * interface defines the enterprise Bean's create, finder, remove,
     * and home business methods.
     * 
     * @return A reference to the enterprise Bean's home interface.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    public EJBHome getEJBHome() throws RemoteException; 

    /**
     * Obtain the primary key of the EJB object. 
     *
     * <p> This method can be called on an entity bean. An attempt to invoke
     * this method on a session bean will result in RemoteException.
     *
     * @return The EJB object's primary key.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure or when invoked on a session bean.
     */
    public Object getPrimaryKey() throws RemoteException;

    /**
     * Remove the EJB object.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     *
     * @exception RemoveException The enterprise Bean or the container
     *    does not allow destruction of the object.
     */ 
    public void remove() throws RemoteException, RemoveException;

    /**
     * Obtain a handle for the EJB object. The handle can be used at later
     * time to re-obtain a reference to the EJB object, possibly in a
     * different Java Virtual Machine.
     *
     * @return A handle for the EJB object.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    public Handle getHandle() throws RemoteException;

    /**
     * Test if a given EJB object is identical to the invoked EJB object.
     *
     * @param obj An object to test for identity with the invoked object.
     *
     * @return True if the given EJB object is identical to the invoked object,
     *    false otherwise.
     *
     * @exception RemoteException Thrown when the method failed due to a
     *    system-level failure.
     */
    boolean isIdentical(EJBObject obj) throws RemoteException;
} 
