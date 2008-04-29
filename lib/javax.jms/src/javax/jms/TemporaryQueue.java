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

/** A <CODE>TemporaryQueue</CODE> object is a unique <CODE>Queue</CODE> object 
  * created for the duration of a <CODE>Connection</CODE>. It is a 
  * system-defined queue that can be consumed only by the 
  * <CODE>Connection</CODE> that created it.
  *
  *<P>A <CODE>TemporaryQueue</CODE> object can be created at either the 
  * <CODE>Session</CODE> or <CODE>QueueSession</CODE> level. Creating it at the
  * <CODE>Session</CODE> level allows to the <CODE>TemporaryQueue</CODE> to 
  * participate in transactions with objects from the Pub/Sub  domain. 
  * If it is created at the <CODE>QueueSession</CODE>, it will only
  * be able participate in transactions with objects from the PTP domain.
  *
  * @version     1.1 - February 2, 2002
  * @author      Mark Hapner
  * @author      Rich Burridge
  * @author      Kate Stout
  *
  * @see Session#createTemporaryQueue()
  * @see QueueSession#createTemporaryQueue()
  */

public interface TemporaryQueue extends Queue {

    /** Deletes this temporary queue. If there are existing receivers
      * still using it, a <CODE>JMSException</CODE> will be thrown.
      *  
      * @exception JMSException if the JMS provider fails to delete the 
      *                         temporary queue due to some internal error.
      */

    void 
    delete() throws JMSException; 
}
