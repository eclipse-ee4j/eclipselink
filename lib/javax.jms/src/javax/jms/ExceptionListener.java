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


/** If a JMS provider detects a serious problem with a <CODE>Connection</CODE>
  * object, it informs the <CODE>Connection</CODE> object's 
  * <CODE>ExceptionListener</CODE>, if one has been registered. 
  * It does this by calling the listener's <CODE>onException</CODE> method, 
  * passing it a <CODE>JMSException</CODE> argument describing the problem.
  *
  * <P>An exception listener allows a client to be notified of a problem 
  * asynchronously. Some connections only consume messages, so they would have no
  * other way to learn that their connection has failed.
  *
  * <P>A JMS provider should attempt to resolve connection problems 
  * itself before it notifies the client of them.
  *
  * @version     1.0 - 9 March 1998
  * @author      Mark Hapner
  * @author      Rich Burridge
  *
  * @see javax.jms.Connection#setExceptionListener(ExceptionListener)
  */

public interface ExceptionListener {

    /** Notifies user of a JMS exception.
      *
      * @param exception the JMS exception
      */

    void 
    onException(JMSException exception);
}
