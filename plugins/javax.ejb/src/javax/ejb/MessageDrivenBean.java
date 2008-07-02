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
 * The MessageDrivenBean interface is implemented by every message-driven
 * enterprise Bean class. The container uses the MessageDrivenBean methods
 * to notify the enterprise Bean instances of the instance's life cycle 
 * events.
 */
public interface MessageDrivenBean extends EnterpriseBean {
    /**
     * Set the associated message-driven context. The container calls 
     * this method after the instance creation.
     *
     * <p> The enterprise Bean instance should store the reference to the
     * context object in an instance variable.
     *
     * <p> This method is called with no transaction context.
     *
     * @param ctx A MessageDrivenContext interface for the instance.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     */
    void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException;

    /**
     * A container invokes this method before it ends the life of the 
     * message-driven object. This happens when a container decides to 
     * terminate the message-driven object.
     * 
     * <p> This method is called with no transaction context.
     *
     * @exception EJBException Thrown by the method to indicate a failure
     *    caused by a system-level error.
     *
     */
     void ejbRemove() throws EJBException;

}
