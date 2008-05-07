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

/**
 * The EJBLocalObject interface must be extended by all enterprise Beans' local
 * interfaces. An enterprise Bean's local interface provides the local client 
 * view of an EJB object. An enterprise Bean's local interface defines 
 * the business methods callable by local clients.
 *
 * <p> The enterprise Bean's local interface is defined by the enterprise
 * Bean provider and implemented by the enterprise Bean container.
 */
public interface EJBLocalObject {
    /**
     * Obtain the enterprise Bean's local home interface. The local home
     * interface defines the enterprise Bean's create, finder, remove,
     * and home business methods that are available to local clients.
     * 
     * @return A reference to the enterprise Bean's local home interface.
     *
     * @exception EJBException Thrown when the method failed due to a
     *    system-level failure.
     *
     */
    public EJBLocalHome getEJBLocalHome() throws EJBException; 

    /**
     * Obtain the primary key of the EJB local object. 
     *
     * <p> This method can be called on an entity bean. 
     * An attempt to invoke this method on a session Bean will result in
     * an EJBException.
     *
     * @return The EJB local object's primary key.
     *
     * @exception EJBException Thrown when the method failed due to a
     *    system-level failure or when invoked on a session bean.
     *
     */
    public Object getPrimaryKey() throws EJBException;

    /**
     * Remove the EJB local object.
     *
     * @exception RemoveException The enterprise Bean or the container
     *    does not allow destruction of the object.
     *
     * @exception EJBException Thrown when the method failed due to a
     *    system-level failure.
     *
     */ 
    public void remove() throws RemoveException, EJBException;

    /**
     * Test if a given EJB local object is identical to the invoked EJB 
     * local object.
     *
     * @param obj An object to test for identity with the invoked object.
     *
     * @return True if the given EJB local object is identical to the 
     * invoked object, false otherwise.
     *
     *
     * @exception EJBException Thrown when the method failed due to a
     *    system-level failure.
     *
     */
    boolean isIdentical(EJBLocalObject obj) throws EJBException;
} 
