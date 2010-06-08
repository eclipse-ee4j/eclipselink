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

import java.io.Serializable;

/** An <CODE>ObjectMessage</CODE> object is used to send a message that contains
  * a serializable object in the Java programming language ("Java object").
  * It inherits from the <CODE>Message</CODE> interface and adds a body
  * containing a single reference to an object. Only <CODE>Serializable</CODE> 
  * Java objects can be used.
  *
  * <P>If a collection of Java objects must be sent, one of the 
  * <CODE>Collection</CODE> classes provided since JDK 1.2 can be used.
  *
  * <P>When a client receives an <CODE>ObjectMessage</CODE>, it is in read-only 
  * mode. If a client attempts to write to the message at this point, a 
  * <CODE>MessageNotWriteableException</CODE> is thrown. If 
  * <CODE>clearBody</CODE> is called, the message can now be both read from and 
  * written to.
  *
  * @version     1.0 - 6 August 1998
  * @author      Mark Hapner
  * @author      Rich Burridge
  *
  * @see         javax.jms.Session#createObjectMessage()
  * @see         javax.jms.Session#createObjectMessage(Serializable)
  * @see         javax.jms.BytesMessage
  * @see         javax.jms.MapMessage
  * @see         javax.jms.Message
  * @see         javax.jms.StreamMessage
  * @see         javax.jms.TextMessage
  */

public interface ObjectMessage extends Message {

    /** Sets the serializable object containing this message's data.
      * It is important to note that an <CODE>ObjectMessage</CODE>
      * contains a snapshot of the object at the time <CODE>setObject()</CODE>
      * is called; subsequent modifications of the object will have no 
      * effect on the <CODE>ObjectMessage</CODE> body.
      *
      * @param object the message's data
      *  
      * @exception JMSException if the JMS provider fails to set the object
      *                         due to some internal error.
      * @exception MessageFormatException if object serialization fails.
      * @exception MessageNotWriteableException if the message is in read-only
      *                                         mode.
      */

    void 
    setObject(Serializable object) throws JMSException;


    /** Gets the serializable object containing this message's data. The 
      * default value is null.
      *
      * @return the serializable object containing this message's data
      *  
      * @exception JMSException if the JMS provider fails to get the object
      *                         due to some internal error.
      * @exception MessageFormatException if object deserialization fails.
      */

    Serializable 
    getObject() throws JMSException;
}
