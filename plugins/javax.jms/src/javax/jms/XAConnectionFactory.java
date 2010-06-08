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


package javax.jms;

/** The <CODE>XAConnectionFactory</CODE> interface is a base interface for the
  * <CODE>XAQueueConnectionFactory</CODE> and 
  * <CODE>XATopicConnectionFactory</CODE> interfaces.
  *
  * <P>Some application servers provide support for grouping JTS capable 
  * resource use into a distributed transaction (optional). To include JMS API transactions 
  * in a JTS transaction, an application server requires a JTS aware JMS
  * provider. A JMS provider exposes its JTS support using an
  * <CODE>XAConnectionFactory</CODE> object, which an application server uses 
  * to create <CODE>XAConnection</CODE> objects.
  *
  * <P><CODE>XAConnectionFactory</CODE> objects are JMS administered objects, 
  * just like <CODE>ConnectionFactory</CODE> objects. It is expected that 
  * application servers will find them using the Java Naming and Directory
  * Interface (JNDI) API.
  *
  *<P>The <CODE>XAConnectionFactory</CODE> interface is optional. JMS providers 
  * are not required to support this interface. This interface is for 
  * use by JMS providers to support transactional environments. 
  * Client programs are strongly encouraged to use the transactional support
  * available in their environment, rather than use these XA
  * interfaces directly. 
  *
  * @version     1.1 April 4, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.XAQueueConnectionFactory
  * @see         javax.jms.XATopicConnectionFactory
  */

public interface XAConnectionFactory {
    
     /** Creates an <CODE>XAConnection</CODE> with the default user identity.
      * The connection is created in stopped mode. No messages 
      * will be delivered until the <code>Connection.start</code> method
      * is explicitly called.
      *
      * @return a newly created <CODE>XAConnection</CODE>
      *
      * @exception JMSException if the JMS provider fails to create an XA  
      *                         connection due to some internal error.
      * @exception JMSSecurityException  if client authentication fails due to 
      *                         an invalid user name or password.
      * 
      * @since 1.1 
      */ 

    XAConnection
    createXAConnection() throws JMSException;


    /** Creates an XA  connection with the specified user identity.
      * The connection is created in stopped mode. No messages 
      * will be delivered until the <code>Connection.start</code> method
      * is explicitly called.
      *  
      * @param userName the caller's user name
      * @param password the caller's password
      *  
      * @return a newly created XA connection
      *
      * @exception JMSException if the JMS provider fails to create an XA  
      *                         connection due to some internal error.
      * @exception JMSSecurityException  if client authentication fails due to 
      *                         an invalid user name or password.
      *
      * @since 1.1 
      */ 

    XAConnection
    createXAConnection(String userName, String password) 
					     throws JMSException;
}
