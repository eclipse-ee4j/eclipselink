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

package javax.resource.spi.security;

import java.security.Principal; // to fix javadoc warning
import javax.resource.spi.SecurityException;

/** The interface <code>javax.resource.spi.security.GenericCredential</code> 
 *  defines a security mechanism independent interface for accessing 
 *  security credential of a resource principal. 
 *
 *  <p>The <code>GenericCredential</code> interface provides a Java 
 *  wrapper over an underlying mechanism specific representation of
 *  a security credential. For example, the <code>GenericCredential</code>
 *  interface can be used to wrap Kerberos credentials.
 *
 *  <p>The connector architecture does not define any standard format 
 *  and requirements for security mechanism specific credentials. For 
 *  example, a security credential wrapped by a GenericCredential 
 *  interface can have a native representation specific to an operating 
 *  system.
 *
 *  <p>The GenericCredential interface enables a resource adapter to 
 *  extract information about a security credential. The resource adapter
 *  can then manage EIS sign-on for a resource principal by either:
 *  <UL>
 *    <LI>using the credentials in an EIS specific manner if the underlying
 *        EIS supports the security mechanism type represented by the 
 *        GenericCredential instance, or,
 *    <LI>using GSS-API if the resource adapter and underlying EIS 
 *        instance support GSS-API.
 *  </UL>
 *
 *  @author  Rahul Sharma
 *  @version 0.7
 *  @since   0.7
 *  @see     javax.security.auth.Subject
 *  @see     java.security.Principal
 *  @deprecated The preferred way to represent generic credential information 
 *  is via the <code>org.ietf.jgss.GSSCredential</code> interface in 
 *  J2SE Version 1.4, which provides similar functionality.
 */

public interface GenericCredential {

  /** Returns the name of the resource principal associated 
   *  with a GenericCredential instance.
   *
   *  @return     Name of the principal
  **/
  public 
  String getName();

  /** Returns the mechanism type for the GenericCredential instance. 
   *  The mechanism type definition for GenericCredential should be 
   *  consistent with the Object Identifier (OID) based representation
   *  specified in the GSS specification. In the GenericCredential
   *  interface, the mechanism type is returned as a stringified 
   *  representation of the OID specification.
   *
   *  @return    mechanism type
  **/
  public 
  String getMechType();

  /** Gets security data for a specific security mechanism represented
   *  by the GenericCredential. An example is authentication data required
   *  for establishing a secure association with an EIS instance on
   *  behalf of the associated resource principal. 
   *
   *  <p>The getCredentialData method returns the credential 
   *  representation as an array of bytes. Note that the connector 
   *  architecture does not define any standard format for the returned 
   *  credential data.
   *
   *  @return   credential representation as an array of bytes.
   *  @throws   SecurityException   
   *                      Failed operation due to security related
   *                      error condition
   **/
  public
  byte[] getCredentialData() throws SecurityException;

  /** Tests if this GenericCredential instance refers to the same entity 
   *  as the supplied object.  The two credentials must be acquired over
   *  the same mechanisms and must refer to the same principal.  
   *
   *  Returns true if the two GenericCredentials refer to the same entity;
   *  false otherwise.
  **/
  public 
  boolean equals(Object another);

  /** Returns the hash code for this GenericCredential
   * 
   *  @return  hash code for this GenericCredential
   **/
  public
  int hashCode();

}
