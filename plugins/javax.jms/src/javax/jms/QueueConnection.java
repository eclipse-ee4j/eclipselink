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

/** A <CODE>QueueConnection</CODE> object is an active connection to a 
  * point-to-point JMS provider. A client uses a <CODE>QueueConnection</CODE> 
  * object to create one or more <CODE>QueueSession</CODE> objects
  * for producing and consuming messages.
  *
  *<P>A <CODE>QueueConnection</CODE> can be used to create a
  * <CODE>QueueSession</CODE>, from which specialized  queue-related objects
  * can be created.
  * A more general, and recommended, approach is to use the 
  * <CODE>Connection</CODE> object.
  * 
  *
  * <P>The <CODE>QueueConnection</CODE> object
  * should be used to support existing code that has already used it.
  *
  * <P>A <CODE>QueueConnection</CODE> cannot be used to create objects 
  * specific to the   publish/subscribe domain. The
  * <CODE>createDurableConnectionConsumer</CODE> method inherits
  * from  <CODE>Connection</CODE>, but must throw an 
  * <CODE>IllegalStateException</CODE>
  * if used from <CODE>QueueConnection</CODE>.
  *
  * @version     1.1 - April 9, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.Connection
  * @see         javax.jms.ConnectionFactory
  * @see	 javax.jms.QueueConnectionFactory
  */

public interface QueueConnection extends Connection {

    /** Creates a <CODE>QueueSession</CODE> object.
      *  
      * @param transacted indicates whether the session is transacted
      * @param acknowledgeMode indicates whether the consumer or the
      * client will acknowledge any messages it receives; ignored if the session
      * is transacted. Legal values are <code>Session.AUTO_ACKNOWLEDGE</code>, 
      * <code>Session.CLIENT_ACKNOWLEDGE</code>, and 
      * <code>Session.DUPS_OK_ACKNOWLEDGE</code>.
      *  
      * @return a newly created queue session
      *  
      * @exception JMSException if the <CODE>QueueConnection</CODE> object fails
      *                         to create a session due to some internal error or
      *                         lack of support for the specific transaction
      *                         and acknowledgement mode.
      *
      * @see Session#AUTO_ACKNOWLEDGE 
      * @see Session#CLIENT_ACKNOWLEDGE 
      * @see Session#DUPS_OK_ACKNOWLEDGE 
      */ 

    QueueSession
    createQueueSession(boolean transacted,
                       int acknowledgeMode) throws JMSException;


    /** Creates a connection consumer for this connection (optional operation).
      * This is an expert facility not used by regular JMS clients.
      *
      * @param queue the queue to access
      * @param messageSelector only messages with properties matching the
      * message selector expression are delivered. A value of null or
      * an empty string indicates that there is no message selector 
      * for the message consumer.
      * @param sessionPool the server session pool to associate with this 
      * connection consumer
      * @param maxMessages the maximum number of messages that can be
      * assigned to a server session at one time
      *
      * @return the connection consumer
      *  
      * @exception JMSException if the <CODE>QueueConnection</CODE> object fails
      *                         to create a connection consumer due to some
      *                         internal error or invalid arguments for 
      *                         <CODE>sessionPool</CODE> and 
      *                         <CODE>messageSelector</CODE>.
      * @exception InvalidDestinationException if an invalid queue is specified.
      * @exception InvalidSelectorException if the message selector is invalid.
      * @see javax.jms.ConnectionConsumer
      */ 

    ConnectionConsumer
    createConnectionConsumer(Queue queue,
                             String messageSelector,
                             ServerSessionPool sessionPool,
			     int maxMessages)
			   throws JMSException;
}
