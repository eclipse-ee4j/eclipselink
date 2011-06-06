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

import java.util.*;
import java.security.Identity;

/**
 * The EntityContext interface provides an instance with access to the 
 * container-provided runtime context of an entity enterprise Bean instance. 
 * The container passes the EntityContext interface to an entity enterprise 
 * Bean instance after the instance has been created. 
 *
 * <p> The EntityContext interface remains associated with the instance for 
 * the lifetime of the instance. Note that the information that the instance
 * obtains using the EntityContext interface (such as the result of the
 * getPrimaryKey() method) may change, as the container assigns the instance
 * to different EJB objects during the instance's life cycle.
 */
public interface EntityContext extends EJBContext
{
    /**
     * Obtain a reference to the EJB local object that is currently 
     * associated with the instance.
     *
     * <p> An instance of an entity enterprise Bean can call this method only
     * when the instance is associated with an EJB local object identity, i.e.
     * in the ejbActivate, ejbPassivate, ejbPostCreate, ejbRemove,
     * ejbLoad, ejbStore, and business methods.
     *
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB local object currently associated with the instance.
     *
     * @exception IllegalStateException if the instance invokes this
     *    method while the instance is in a state that does not allow the
     *    instance to invoke this method, or if the instance does not have
     *    a local interface.
     */
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    /**
     * Obtain a reference to the EJB object that is currently associated with 
     * the instance.
     *
     * <p> An instance of an entity enterprise Bean can call this method only
     * when the instance is associated with an EJB object identity, i.e.
     * in the ejbActivate, ejbPassivate, ejbPostCreate, ejbRemove,
     * ejbLoad, ejbStore, and business methods.
     *
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB object currently associated with the instance.
     *
     * @exception IllegalStateException Thrown if the instance invokes this
     *    method while the instance is in a state that does not allow the
     *    instance to invoke this method, or if the instance does not have
     *    a remote interface.
     */
    EJBObject getEJBObject() throws IllegalStateException;

    /**
     * Obtain the primary key of the EJB object that is currently
     * associated with this instance.
     *
     * <p> An instance of an entity enterprise Bean can call this method only
     * when the instance is associated with an EJB object identity, i.e.
     * in the ejbActivate, ejbPassivate, ejbPostCreate, ejbRemove,
     * ejbLoad, ejbStore, and business methods.
     *
     * <p><b>Note</b>: The result of this method is that same as the
     * result of getEJBObject().getPrimaryKey().
     *
     * @return The primary key currently associated with the instance.
     *
     * @exception IllegalStateException Thrown if the instance invokes this
     *    method while the instance is in a state that does not allow the
     *    instance to invoke this method.
     */
    Object getPrimaryKey() throws IllegalStateException;
}
