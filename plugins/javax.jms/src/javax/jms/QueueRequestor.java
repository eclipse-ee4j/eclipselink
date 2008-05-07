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

/** The <CODE>QueueRequestor</CODE> helper class simplifies
  * making service requests.
  *
  * <P>The <CODE>QueueRequestor</CODE> constructor is given a non-transacted 
  * <CODE>QueueSession</CODE> and a destination <CODE>Queue</CODE>. It creates a
  * <CODE>TemporaryQueue</CODE> for the responses and provides a 
  * <CODE>request</CODE> method that sends the request message and waits 
  * for its reply.
  *
  * <P>This is a basic request/reply abstraction that should be sufficient 
  * for most uses. JMS providers and clients are free to create more 
  * sophisticated versions.
  *
  * @version     1.0 - 8 July 1998
  * @author      Mark Hapner
  * @author      Rich Burridge
  *
  * @see         javax.jms.TopicRequestor
  */

public class QueueRequestor {

    QueueSession   session;     // The queue session the queue belongs to.
    Queue          queue;       // The queue to perform the request/reply on.
    TemporaryQueue tempQueue;
    QueueSender    sender;
    QueueReceiver  receiver;


    /** Constructor for the <CODE>QueueRequestor</CODE> class.
      *  
      * <P>This implementation assumes the session parameter to be non-transacted,
      * with a delivery mode of either <CODE>AUTO_ACKNOWLEDGE</CODE> or 
      * <CODE>DUPS_OK_ACKNOWLEDGE</CODE>.
      *
      * @param session the <CODE>QueueSession</CODE> the queue belongs to
      * @param queue the queue to perform the request/reply call on
      *  
      * @exception JMSException if the JMS provider fails to create the
      *                         <CODE>QueueRequestor</CODE> due to some internal
      *                         error.
      * @exception InvalidDestinationException if an invalid queue is specified.
      */ 

    public
    QueueRequestor(QueueSession session, Queue queue) throws JMSException {
        this.session = session;
        this.queue   = queue;
        tempQueue    = session.createTemporaryQueue();
        sender       = session.createSender(queue);
        receiver     = session.createReceiver(tempQueue);
    }


    /** Sends a request and waits for a reply. The temporary queue is used for
      * the <CODE>JMSReplyTo</CODE> destination, and only one reply per request 
      * is expected.
      *  
      * @param message the message to send
      *  
      * @return the reply message
      *  
      * @exception JMSException if the JMS provider fails to complete the
      *                         request due to some internal error.
      */

    public Message
    request(Message message) throws JMSException {
	message.setJMSReplyTo(tempQueue);
	sender.send(message);
	return (receiver.receive());
    }


    /** Closes the <CODE>QueueRequestor</CODE> and its session.
      *
      * <P>Since a provider may allocate some resources on behalf of a 
      * <CODE>QueueRequestor</CODE> outside the Java virtual machine, clients 
      * should close them when they 
      * are not needed. Relying on garbage collection to eventually reclaim 
      * these resources may not be timely enough.
      *  
      * <P>Note that this method closes the <CODE>QueueSession</CODE> object 
      * passed to the <CODE>QueueRequestor</CODE> constructor.
      *
      * @exception JMSException if the JMS provider fails to close the
      *                         <CODE>QueueRequestor</CODE> due to some internal
      *                         error.
      */

    public void
    close() throws JMSException {

	// publisher and consumer created by constructor are implicitly closed.
	session.close();
        tempQueue.delete();
    }
}
