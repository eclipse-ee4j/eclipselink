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
import java.security.Principal;
import javax.transaction.UserTransaction;

/**
 * The EJBContext interface provides an instance with access to the 
 * container-provided runtime context of an enterprise Bean instance. 
 *
 * <p> This interface is extended by the SessionContext, EntityContext,
 * and MessageDrivenContext interfaces to provide additional methods 
 * specific to the enterprise interface Bean type.
 * 
 */
public interface EJBContext
{
    /**
     * Obtain the enterprise bean's remote home interface.
     *
     * @return The enterprise bean's remote home interface.
     *
     * @exception java.lang.IllegalStateException if the enterprise bean 
     * does not have a remote home interface.
     */
    EJBHome getEJBHome();

    /**
     * Obtain the enterprise bean's local home interface.
     *
     * @return The enterprise bean's local home interface.
     *
     * @exception java.lang.IllegalStateException if the enterprise bean 
     * does not have a local home interface.
     */
    EJBLocalHome getEJBLocalHome();

    /**
     * Obtain the enterprise bean's environment properties.
     * 
     * <p><b>Note:</b> If the enterprise bean has no environment properties 
     * this method returns an empty java.util.Properties object. This method
     * never returns null.
     *
     * @return The environment properties for the enterprise bean.
     *
     * @deprecated Use the JNDI naming context java:comp/env to access
     *    enterprise bean's environment.
     */
    Properties getEnvironment();

    /**
     * Obtain the java.security.Identity of the caller.
     *
     * This method is deprecated in EJB 1.1. The Container
     * is allowed to return alway null from this method. The enterprise
     * bean should use the getCallerPrincipal method instead.
     *
     * @return The Identity object that identifies the caller.
     *
     * @deprecated Use Principal getCallerPrincipal() instead.
     */
    Identity getCallerIdentity();

 
    /**
     * Obtain the java.security.Principal that identifies the caller.
     * 
     * @return The Principal object that identifies the caller. This
     *    method never returns null.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to call this method.
     */
    Principal getCallerPrincipal();

    /**
     * Test if the caller has a given role.
     *
     * <p>This method is deprecated in EJB 1.1. The enterprise bean
     * should use the isCallerInRole(String roleName) method instead.
     *
     * @param role The java.security.Identity of the role to be tested.
     *
     * @return True if the caller has the specified role.
     *
     * @deprecated Use boolean isCallerInRole(String roleName) instead.
     */
    boolean isCallerInRole(Identity role);

    /**  
     * Test if the caller has a given security role.
     *   
     * @param roleName The name of the security role. The role must be one of
     *    the security roles that is defined in the deployment descriptor.
     *   
     * @return True if the caller has the specified role.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to call this method.
     */  
    boolean isCallerInRole(String roleName);

    /**
     * Obtain the transaction demarcation interface.
     *
     * Only enterprise beans with bean-managed transactions are allowed to
     * to use the UserTransaction interface. As entity beans must always use
     * container-managed transactions, only session beans or message-driven
     * beans with bean-managed transactions are allowed to invoke this method. 
     *
     * @return The UserTransaction interface that the enterprise bean
     *    instance can use for transaction demarcation.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use the UserTransaction interface
     *    (i.e. the instance is of a bean with container-managed transactions).
     */
    UserTransaction getUserTransaction() throws IllegalStateException;

    /**
     * Mark the current transaction for rollback. The transaction will become
     * permanently marked for rollback. A transaction marked for rollback
     * can never commit.
     *
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (i.e. the
     *    instance is of a bean with bean-managed transactions).
     */
    void setRollbackOnly() throws IllegalStateException;

    /**
     * Test if the transaction has been marked for rollback only. An enterprise
     * bean instance can use this operation, for example, to test after an
     * exception has been caught, whether it is fruitless to continue
     * computation on behalf of the current transaction.
     *
     * Only enterprise beans with container-managed transactions are allowed
     * to use this method.
     *
     * @return True if the current transaction is marked for rollback, false
     *   otherwise.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (i.e. the
     *    instance is of a bean with bean-managed transactions).
     */
    boolean getRollbackOnly() throws IllegalStateException;

    /**
     * Get access to the EJB Timer Service.
     *
     * @exception IllegalStateException The Container throws the exception
     *    if the instance is not allowed to use this method (e.g. if the bean
     *    is a stateful session bean)
     */
    TimerService getTimerService() throws IllegalStateException;

    /**
     * Lookup a resource within the component's private naming context.
     * 
     * @param name Name of the entry (relative to java:comp/env).
     *
     * @exception IllegalArgumentException The Container throws the exception
     *    if the given name does not match an entry within the component's
     *    environment.
     */
    Object lookup(String name);

}
