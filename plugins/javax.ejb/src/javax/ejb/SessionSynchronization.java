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
 * <p> The SessionSynchronization interface allows a session Bean instance
 * to be notified by its container of transaction boundaries.
 *
 * <p>  An session Bean class is not required to implement this interface.
 * A session Bean class should implement this interface only if it wishes 
 * to synchronize its state with the transactions.
 */
public interface SessionSynchronization {
    /**
     * The afterBegin method notifies a session Bean instance that a new
     * transaction has started, and that the subsequent business methods on the
     * instance will be invoked in the context of the transaction.
     *
     * <p> The instance can use this method, for example, to read data
     * from a database and cache the data in the instance fields.
     *
     * <p> This method executes in the proper transaction context.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 and higher specifications should throw the
     *    javax.ejb.EJBException instead of this exception. 
     *    Enterprise beans written for the EJB 2.0 and higher specifications 
     *    must not throw the java.rmi.RemoteException.
     */
    public void afterBegin() throws EJBException, RemoteException;

    /**
     * The beforeCompletion method notifies a session Bean instance that
     * a transaction is about to be committed. The instance can use this
     * method, for example, to write any cached data to a database.
     *
     * <p> This method executes in the proper transaction context.
     *
     * <p><b>Note:</b> The instance may still cause the container to
     * rollback the transaction by invoking the setRollbackOnly() method
     * on the instance context, or by throwing an exception.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 and higher specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB 2.0 and higher specifications 
     *    must not throw the java.rmi.RemoteException.
     */
    public void beforeCompletion() throws EJBException, RemoteException;

    /**
     * The afterCompletion method notifies a session Bean instance that a
     * transaction commit protocol has completed, and tells the instance
     * whether the transaction has been committed or rolled back.
     *
     * <p> This method executes with no transaction context.
     *
     * <p> This method executes with no transaction context.
     *
     * @param committed True if the transaction has been committed, false
     *    if is has been rolled back.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 and higher specification should throw the
     *    javax.ejb.EJBException instead of this exception. 
     *    Enterprise beans written for the EJB 2.0 and higher specifications 
     *    must not throw the java.rmi.RemoteException.
     */
    public void afterCompletion(boolean committed) throws EJBException,
	    RemoteException;
}
