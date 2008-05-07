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


/** A client uses a <CODE>QueueReceiver</CODE> object to receive messages that 
  * have been delivered to a queue.
  *
  * <P>Although it is possible to have multiple <CODE>QueueReceiver</CODE>s 
  * for the same queue, the JMS API does not define how messages are 
  * distributed between the <CODE>QueueReceiver</CODE>s.
  *
  * <P>If a <CODE>QueueReceiver</CODE> specifies a message selector, the 
  * messages that are not selected remain on the queue. By definition, a message
  * selector allows a <CODE>QueueReceiver</CODE> to skip messages. This 
  * means that when the skipped messages are eventually read, the total ordering
  * of the reads does not retain the partial order defined by each message 
  * producer. Only <CODE>QueueReceiver</CODE>s without a message selector
  * will read messages in message producer order.
  *
  * <P>Creating a <CODE>MessageConsumer</CODE> provides the same features as
  * creating a <CODE>QueueReceiver</CODE>. A <CODE>MessageConsumer</CODE> object is 
  * recommended for creating new code. The  <CODE>QueueReceiver</CODE> is
  * provided to support existing code.
  *
  * @version     1.1 February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.Session#createConsumer(Destination, String)
  * @see         javax.jms.Session#createConsumer(Destination)
  * @see         javax.jms.QueueSession#createReceiver(Queue, String)
  * @see         javax.jms.QueueSession#createReceiver(Queue)
  * @see         javax.jms.MessageConsumer
  */

public interface QueueReceiver extends MessageConsumer {

    /** Gets the <CODE>Queue</CODE> associated with this queue receiver.
      *  
      * @return this receiver's <CODE>Queue</CODE> 
      *  
      * @exception JMSException if the JMS provider fails to get the queue for
      *                         this queue receiver
      *                         due to some internal error.
      */ 
 
    Queue
    getQueue() throws JMSException;
}
