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
 * The EntityBean interface is implemented by every entity enterprise Bean 
 * class. The container uses the EntityBean methods to notify the enterprise
 * Bean instances of the instance's life cycle events.
 */
public interface EntityBean extends EnterpriseBean {
    /**
     * Set the associated entity context. The container invokes this method
     * on an instance after the instance has been created.
     *
     * <p> This method is called in an unspecified transaction context.
     *
     * @param ctx An EntityContext interface for the instance. The instance
     *    should store the reference to the context in an instance variable.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error. 
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void setEntityContext(EntityContext ctx) throws EJBException,
            RemoteException;

    /**
     * Unset the associated entity context. The container calls this method
     * before removing the instance.
     *
     * <p> This is the last method that the container invokes on the instance.
     * The Java garbage collector will eventually invoke the finalize() method
     * on the instance.
     *
     * <p> This method is called in an unspecified transaction context.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error. 
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void unsetEntityContext() throws EJBException, RemoteException;

    /**
     * A container invokes this method before it removes the EJB object
     * that is currently associated with the instance. This method
     * is invoked when a client invokes a remove operation on the
     * enterprise Bean's home interface or the EJB object's remote interface.
     * This method transitions the instance from the ready state to the pool 
     * of available instances.
     * 
     * <p> This method is called in the transaction context of the remove 
     * operation.
     *
     * @exception RemoveException The enterprise Bean does not allow
     *    destruction of the object.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void ejbRemove() throws RemoveException, EJBException, 
	RemoteException;

    /**
     * A container invokes this method when the instance
     * is taken out of the pool of available instances to become associated
     * with a specific EJB object. This method transitions the instance to 
     * the ready state.
     *
     * <p> This method executes in an unspecified transaction context.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void ejbActivate() throws EJBException, RemoteException;

    /**
     * A container invokes this method on an instance before the instance
     * becomes disassociated with a specific EJB object. After this method
     * completes, the container will place the instance into the pool of
     * available instances.
     *
     * <p> This method executes in an unspecified transaction context.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void ejbPassivate() throws EJBException, RemoteException;

    /**
     * A container invokes this method to instruct the
     * instance to synchronize its state by loading it state from the
     * underlying database.
     * 
     * <p> This method always executes in the transaction context determined
     * by the value of the transaction attribute in the deployment descriptor.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void ejbLoad() throws EJBException, RemoteException;

    /**
     * A container invokes this method to instruct the
     * instance to synchronize its state by storing it to the underlying 
     * database.
     *
     * <p> This method always executes in the transaction context determined
     * by the value of the transaction attribute in the deployment descriptor.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     * @exception RemoteException This exception is defined in the method
     *    signature to provide backward compatibility for enterprise beans 
     *    written for the EJB 1.0 specification. Enterprise beans written 
     *    for the EJB 1.1 specification should throw the
     *    javax.ejb.EJBException instead of this exception.
     *    Enterprise beans written for the EJB2.0 and higher specifications
     *    must throw the javax.ejb.EJBException instead of this exception.
     */
    public void ejbStore() throws EJBException, RemoteException;
}


