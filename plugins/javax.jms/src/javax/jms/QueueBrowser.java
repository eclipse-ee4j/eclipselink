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

import java.util.Enumeration;

/** A client uses a <CODE>QueueBrowser</CODE> object to look at messages on a 
  * queue without removing them.
  *
  * <P>The <CODE>getEnumeration</CODE> method returns a 
  * <CODE>java.util.Enumeration</CODE> that is used to scan 
  * the queue's messages. It may be an enumeration of the entire content of a 
  * queue, or it may contain only the messages matching a message selector.
  *
  * <P>Messages may be arriving and expiring while the scan is done. The JMS API
  * does 
  * not require the content of an enumeration to be a static snapshot of queue 
  * content. Whether these changes are visible or not depends on the JMS 
  * provider.
  *
  *<P>A <CODE>QueueBrowser</CODE> can be created from either a 
  * <CODE>Session</CODE> or a <CODE> QueueSession</CODE>. 
  *
  * @version     1.1 April 9, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  *  @see         javax.jms.Session#createBrowser
  * @see         javax.jms.QueueSession#createBrowser
  * @see         javax.jms.QueueReceiver
  */

public interface QueueBrowser {

    /** Gets the queue associated with this queue browser.
      * 
      * @return the queue
      *  
      * @exception JMSException if the JMS provider fails to get the
      *                         queue associated with this browser
      *                         due to some internal error.
      */

    Queue 
    getQueue() throws JMSException;


    /** Gets this queue browser's message selector expression.
      *  
      * @return this queue browser's message selector, or null if no
      *         message selector exists for the message consumer (that is, if 
      *         the message selector was not set or was set to null or the 
      *         empty string)
      *
      * @exception JMSException if the JMS provider fails to get the
      *                         message selector for this browser
      *                         due to some internal error.
      */

    String
    getMessageSelector() throws JMSException;


    /** Gets an enumeration for browsing the current queue messages in the
      * order they would be received.
      *
      * @return an enumeration for browsing the messages
      *  
      * @exception JMSException if the JMS provider fails to get the
      *                         enumeration for this browser
      *                         due to some internal error.
      */

    Enumeration 
    getEnumeration() throws JMSException;


    /** Closes the <CODE>QueueBrowser</CODE>.
      *
      * <P>Since a provider may allocate some resources on behalf of a 
      * QueueBrowser outside the Java virtual machine, clients should close them
      * when they 
      * are not needed. Relying on garbage collection to eventually reclaim 
      * these resources may not be timely enough.
      *
      * @exception JMSException if the JMS provider fails to close this
      *                         browser due to some internal error.
      */

    void 
    close() throws JMSException;
}
