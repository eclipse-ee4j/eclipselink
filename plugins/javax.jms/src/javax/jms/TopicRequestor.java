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

/** The <CODE>TopicRequestor</CODE> helper class simplifies
  * making service requests.
  *
  * <P>The <CODE>TopicRequestor</CODE> constructor is given a non-transacted 
  * <CODE>TopicSession</CODE> and a destination <CODE>Topic</CODE>. It creates a 
  * <CODE>TemporaryTopic</CODE> for the responses and provides a 
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
  * @see         javax.jms.QueueRequestor
  */

public class TopicRequestor {

    TopicSession    session;    // The topic session the topic belongs to.
    Topic           topic;      // The topic to perform the request/reply on.  
    TemporaryTopic  tempTopic;
    TopicPublisher  publisher;
    TopicSubscriber subscriber;


    /** Constructor for the <CODE>TopicRequestor</CODE> class.
      * 
      * <P>This implementation assumes the session parameter to be non-transacted,
      * with a delivery mode of either <CODE>AUTO_ACKNOWLEDGE</CODE> or 
      * <CODE>DUPS_OK_ACKNOWLEDGE</CODE>.
      *
      * @param session the <CODE>TopicSession</CODE> the topic belongs to
      * @param topic the topic to perform the request/reply call on
      *
      * @exception JMSException if the JMS provider fails to create the
      *                         <CODE>TopicRequestor</CODE> due to some internal
      *                         error.
      * @exception InvalidDestinationException if an invalid topic is specified.
      */ 

    public 
    TopicRequestor(TopicSession session, Topic topic) throws JMSException {
	this.session = session;
	this.topic   = topic;
        tempTopic    = session.createTemporaryTopic();
        publisher    = session.createPublisher(topic);
        subscriber   = session.createSubscriber(tempTopic);
    }


    /** Sends a request and waits for a reply. The temporary topic is used for
      * the <CODE>JMSReplyTo</CODE> destination; the first reply is returned, 
      * and any following replies are discarded.
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
	message.setJMSReplyTo(tempTopic);
        publisher.publish(message);
	return(subscriber.receive());
    }


    /** Closes the <CODE>TopicRequestor</CODE> and its session.
      *
      * <P>Since a provider may allocate some resources on behalf of a 
      * <CODE>TopicRequestor</CODE> outside the Java virtual machine, clients 
      * should close them when they 
      * are not needed. Relying on garbage collection to eventually reclaim 
      * these resources may not be timely enough.
      *
      * <P>Note that this method closes the <CODE>TopicSession</CODE> object 
      * passed to the <CODE>TopicRequestor</CODE> constructor.
      *  
      * @exception JMSException if the JMS provider fails to close the
      *                         <CODE>TopicRequestor</CODE> due to some internal
      *                         error.
      */

    public void
    close() throws JMSException {

	// publisher and consumer created by constructor are implicitly closed.
	session.close();
	tempTopic.delete();
    }
}
