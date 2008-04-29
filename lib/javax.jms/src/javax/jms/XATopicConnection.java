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

/** An <CODE>XATopicConnection</CODE> provides the same create options as 
  * <CODE>TopicConnection</CODE> (optional). The Topic connections created are
  * transactional.
  *
  * <P>The <CODE>XATopicConnection</CODE> interface is optional.  JMS providers 
  * are not required to support this interface. This interface is for 
  * use by JMS providers to support transactional environments. 
  * Client programs are strongly encouraged to use the transactional support
  * available in their environment, rather than use these XA
  * interfaces directly. 
  *
  * @version     1.1 - February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.XAConnection
  */

public interface XATopicConnection 
	extends XAConnection, TopicConnection {

    /** Creates an <CODE>XATopicSession</CODE> object.
      *  
      * @return a newly created XA topic session
      *  
      * @exception JMSException if the <CODE>XATopicConnection</CODE> object
      *                         fails to create an XA topic session due to some 
      *                         internal error.
      */ 

    XATopicSession
    createXATopicSession() throws JMSException;

    /** Creates an <CODE>XATopicSession</CODE> object.
      *
      * @param transacted usage undefined
      * @param acknowledgeMode usage undefined
      *  
      * @return a newly created XA topic session
      *  
      * @exception JMSException if the <CODE>XATopicConnection</CODE> object
      *                         fails to create an XA topic session due to some 
      *                         internal error.
      */ 

    TopicSession
    createTopicSession(boolean transacted,
                       int acknowledgeMode) throws JMSException;
}
