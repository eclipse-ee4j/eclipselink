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

/** A <CODE>ServerSessionPool</CODE> object is an object implemented by an 
  * application server to provide a pool of <CODE>ServerSession</CODE> objects 
  * for processing the messages of a <CODE>ConnectionConsumer</CODE> (optional).
  *
  * <P>Its only method is <CODE>getServerSession</CODE>. The JMS API does not 
  * architect how the pool is implemented. It could be a static pool of 
  * <CODE>ServerSession</CODE> objects, or it could use a sophisticated 
  * algorithm to dynamically create <CODE>ServerSession</CODE> objects as 
  * needed.
  *
  * <P>If the <CODE>ServerSessionPool</CODE> is out of 
  * <CODE>ServerSession</CODE> objects, the <CODE>getServerSession</CODE> call 
  * may block. If a <CODE>ConnectionConsumer</CODE> is blocked, it cannot 
  * deliver new messages until a <CODE>ServerSession</CODE> is 
  * eventually returned.
  *
  * @version     1.0 - 9 March 1998
  * @author      Mark Hapner
  * @author      Rich Burridge
  *
  * @see javax.jms.ServerSession
  */

public interface ServerSessionPool {

    /** Return a server session from the pool.
      *
      * @return a server session from the pool
      *  
      * @exception JMSException if an application server fails to
      *                         return a <CODE>ServerSession</CODE> out of its
      *                         server session pool.
      */ 

    ServerSession
    getServerSession() throws JMSException;
}
