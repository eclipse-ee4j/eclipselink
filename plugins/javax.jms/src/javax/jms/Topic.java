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


/** A <CODE>Topic</CODE> object encapsulates a provider-specific topic name. 
  * It is the way a client specifies the identity of a topic to JMS API methods.
 * For those methods that use a <CODE>Destination</CODE> as a parameter, a 
  * <CODE>Topic</CODE> object may used as an argument . For 
  * example, a Topic can be used to create a <CODE>MessageConsumer</CODE>
  * and a <CODE>MessageProducer</CODE>
  * by calling:
  *<UL>
  *<LI> <CODE>Session.CreateConsumer(Destination destination)</CODE>
  *<LI> <CODE>Session.CreateProducer(Destination destination)</CODE>
  *
  *</UL>
  *
  * <P>Many publish/subscribe (pub/sub) providers group topics into hierarchies 
  * and provide various options for subscribing to parts of the hierarchy. The 
  * JMS API places no restriction on what a <CODE>Topic</CODE> object 
  * represents. It may be a leaf in a topic hierarchy, or it may be a larger 
  * part of the hierarchy.
  *
  * <P>The organization of topics and the granularity of subscriptions to 
  * them is an important part of a pub/sub application's architecture. The JMS 
  * API 
  * does not specify a policy for how this should be done. If an application 
  * takes advantage of a provider-specific topic-grouping mechanism, it 
  * should document this. If the application is installed using a different 
  * provider, it is the job of the administrator to construct an equivalent 
  * topic architecture and create equivalent <CODE>Topic</CODE> objects.
  *
  * @version     1.1 - February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see        Session#createConsumer(Destination)
  * @see        Session#createProducer(Destination)
  * @see        javax.jms.TopicSession#createTopic(String)
  */
 
public interface Topic extends Destination {

    /** Gets the name of this topic.
      *  
      * <P>Clients that depend upon the name are not portable.
      *  
      * @return the topic name
      *  
      * @exception JMSException if the JMS provider implementation of 
      *                         <CODE>Topic</CODE> fails to return the topic
      *                         name due to some internal
      *                         error.
      */ 

    String
    getTopicName() throws JMSException;


    /** Returns a string representation of this object.
      *
      * @return the provider-specific identity values for this topic
      */

    String
    toString();
}
