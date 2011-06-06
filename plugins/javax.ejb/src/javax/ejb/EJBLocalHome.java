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
 * The EJBLocalHome interface must be extended by all enterprise
 * Beans' local home interfaces. An enterprise Bean's local home
 * interface defines the methods that allow local clients to create, 
 * find, and remove EJB objects, as well as home business methods that
 * are not specific to a bean instance (session Beans do not have
 * finders and home business methods).
 *
 * <p> The local home interface is defined by the enterprise Bean provider
 * and implemented by the enterprise Bean container.
 */
public interface EJBLocalHome {

    /**
     * Remove an EJB object identified by its primary key.
     *
     * <p>This method can only be used by local clients of an entity bean.
     * An attempt
     * to call this method on a session bean will result in a RemoveException.
     *
     * @exception RemoveException Thrown if the enterprise Bean or
     *    the container does not allow the client to remove the object.
     *
     * @exception EJBException Thrown when the method failed due to a
     *    system-level failure.
     *
     */
    void remove(Object primaryKey) throws RemoveException, EJBException;

}

