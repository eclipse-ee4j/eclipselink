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

/** The <CODE>XAConnection</CODE> interface extends the capability of 
  * <CODE>Connection</CODE> by providing an <CODE>XASession</CODE> (optional).
  *
  *<P>The <CODE>XAConnection</CODE> interface is optional. JMS providers 
  * are not required to support this interface. This interface is for 
  * use by JMS providers to support transactional environments. 
  * Client programs are strongly encouraged to use the transactional support
  * available in their environment, rather than use these XA
  * interfaces directly. 
  *
  * @version     1.1 February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.XAQueueConnection
  * @see         javax.jms.XATopicConnection
  */

public interface XAConnection extends Connection{
    
    /** Creates an <CODE>XASession</CODE> object.
      *  
      * @return a newly created <CODE>XASession</CODE>
      *  
      * @exception JMSException if the <CODE>XAConnection</CODE> object 
      *                         fails to create an <CODE>XASession</CODE> due to
      *                         some internal error.
      *
      * @since 1.1
      */ 

    XASession
    createXASession() throws JMSException;

    /** Creates an <CODE>Session</CODE> object.
      *
      * @param transacted       usage undefined
      * @param acknowledgeMode  usage undefined
      *  
      * @return a <CODE>Session</CODE> object
      *  
      * @exception JMSException if the <CODE>XAConnection</CODE> object 
      *                         fails to create an <CODE>Session</CODE> due to
      *                         some internal error.
      *
      * @since 1.1
      */ 
    Session
    createSession(boolean transacted,
                       int acknowledgeMode) throws JMSException;
}
