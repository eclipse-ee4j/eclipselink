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

/**
 * <P>This is the root class of all JMS API exceptions.
 *
 * <P>It provides the following information:
 * <UL>
 *   <LI> A provider-specific string describing the error. This string is 
 *        the standard exception message and is available via the
 *        <CODE>getMessage</CODE> method.
 *   <LI> A provider-specific string error code 
 *   <LI> A reference to another exception. Often a JMS API exception will 
 *        be the result of a lower-level problem. If appropriate, this 
 *        lower-level exception can be linked to the JMS API exception.
 * </UL>
 * @version     1.0 - 5 Oct 1998
 * @author      Mark Hapner
 * @author      Rich Burridge
 **/

public class JMSException extends Exception {

  /** Vendor-specific error code.
  **/
  private String errorCode;

  /** <CODE>Exception</CODE> reference.
  **/
  private Exception linkedException;


  /** Constructs a <CODE>JMSException</CODE> with the specified reason and 
   *  error code.
   *
   *  @param  reason        a description of the exception
   *  @param  errorCode     a string specifying the vendor-specific
   *                        error code
   **/
  public 
  JMSException(String reason, String errorCode) {
    super(reason);
    this.errorCode = errorCode;
    linkedException = null;
  }

  /** Constructs a <CODE>JMSException</CODE> with the specified reason and with
   *  the error code defaulting to null.
   *
   *  @param  reason        a description of the exception
   **/
  public 
  JMSException(String reason) {
    super(reason);
    this.errorCode = null;
    linkedException = null;
  }

  /** Gets the vendor-specific error code.
   *  @return   a string specifying the vendor-specific
   *                        error code
  **/
  public 
  String getErrorCode() {
    return this.errorCode;
  }

  /**
   * Gets the exception linked to this one.
   *
   * @return the linked <CODE>Exception</CODE>, null if none
  **/
  public 
  Exception getLinkedException() {
    return (linkedException);
  }

  /**
   * Adds a linked <CODE>Exception</CODE>.
   *
   * @param ex       the linked <CODE>Exception</CODE>
  **/
  public 
  synchronized void setLinkedException(Exception ex) {
      linkedException = ex;
  }
}
