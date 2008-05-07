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

/** A <CODE>TextMessage</CODE> object is used to send a message containing a 
  * <CODE>java.lang.String</CODE>.
  * It inherits from the <CODE>Message</CODE> interface and adds a text message 
  * body.
  *
  * <P>This message type can be used to transport text-based messages, including
  *  those with XML content.
  *
  * <P>When a client receives a <CODE>TextMessage</CODE>, it is in read-only 
  * mode. If a client attempts to write to the message at this point, a 
  * <CODE>MessageNotWriteableException</CODE> is thrown. If 
  * <CODE>clearBody</CODE> is 
  * called, the message can now be both read from and written to.
  *
  * @version     1.1 - February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see         javax.jms.Session#createTextMessage()
  * @see         javax.jms.Session#createTextMessage(String)
  * @see         javax.jms.BytesMessage
  * @see         javax.jms.MapMessage
  * @see         javax.jms.Message
  * @see         javax.jms.ObjectMessage
  * @see         javax.jms.StreamMessage
  * @see         java.lang.String
  */
 
public interface TextMessage extends Message { 

    /** Sets the string containing this message's data.
      *  
      * @param string the <CODE>String</CODE> containing the message's data
      *  
      * @exception JMSException if the JMS provider fails to set the text due to
      *                         some internal error.
      * @exception MessageNotWriteableException if the message is in read-only 
      *                                         mode.
      */ 

    void
    setText(String string) throws JMSException;


    /** Gets the string containing this message's data.  The default
      * value is null.
      *  
      * @return the <CODE>String</CODE> containing the message's data
      *  
      * @exception JMSException if the JMS provider fails to get the text due to
      *                         some internal error.
      */ 

    String
    getText() throws JMSException;
}
