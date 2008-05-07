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


/** A <CODE>MessageListener</CODE> object is used to receive asynchronously 
  * delivered messages.
  *
  * <P>Each session must insure that it passes messages serially to the
  * listener. This means that a listener assigned to one or more consumers
  * of the same session can assume that the <CODE>onMessage</CODE> method 
  * is not called with the next message until the session has completed the 
  * last call.
  *
  * @version     1.0 - 13 March 1998
  * @author      Mark Hapner
  * @author      Rich Burridge
  */

public interface MessageListener {

    /** Passes a message to the listener.
      *
      * @param message the message passed to the listener
      */

    void 
    onMessage(Message message);
}
