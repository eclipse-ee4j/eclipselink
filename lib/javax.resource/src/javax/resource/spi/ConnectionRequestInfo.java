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

package javax.resource.spi;

/** <p>The ConnectionRequestInfo interface enables a resource adapter to 
 *  pass its own request specific data structure across the connection
 *  request flow. A resource adapter extends the empty interface to
 *  supports its own data structures for connection request.
 *  
 *  <p>A typical use allows a resource adapter to handle 
 *  application component specified per-connection request properties
 *  (example - client ID, language). The application server passes these 
 *  properties back across to match/createManagedConnection calls on 
 *  the resource adapter. These properties remain opaque to the 
 *  application server during the connection request flow. 
 *
 *  <p>Once the ConnectionRequestInfo reaches match/createManagedConnection
 *  methods on the ManagedConnectionFactory instance, resource adapter
 *  uses this additional per-request information to do connection 
 *  creation and matching.
 *
 *  @version     0.8
 *  @author      Rahul Sharma
 *  @see         javax.resource.spi.ManagedConnectionFactory
 *  @see         javax.resource.spi.ManagedConnection
**/

public interface ConnectionRequestInfo {

  /** Checks whether this instance is equal to another. Since
   *  connectionRequestInfo is defined specific to a resource
   *  adapter, the resource adapter is required to implement
   *  this method. The conditions for equality are specific
   *  to the resource adapter.
   *
   *  @return True if the two instances are equal.
  **/
  public
  boolean equals(Object other);

  /** Returns the hashCode of the ConnectionRequestInfo.
   *
   *  @return hash code os this instance
  **/
  public
  int hashCode();


}
