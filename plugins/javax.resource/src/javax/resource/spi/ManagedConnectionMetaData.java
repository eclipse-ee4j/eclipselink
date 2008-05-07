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

import javax.resource.ResourceException;

/** The ManagedConnectionMetaData interface provides information about the 
 *  underlying EIS instance associated with a ManagedConnection instance.
 *  An application server uses this information to get runtime information
 *  about a connected EIS instance.
 *
 *  <p>The method ManagedConnection.getMetaData returns a 
 *  ManagedConnectionMetaData instance.
 *  
 *  @version     0.8
 *  @author      Rahul Sharma
 *  @see         javax.resource.spi.ManagedConnection
**/

public interface ManagedConnectionMetaData {

  /** Returns Product name of the underlying EIS instance connected 
   *  through the ManagedConnection.
   *
   *  @return  Product name of the EIS instance.
  **/
  public
  String getEISProductName() throws ResourceException;

  /** Returns product version of the underlying EIS instance connected 
   *  through the ManagedConnection.
   *
   *  @return  Product version of the EIS instance
  **/
  public
  String getEISProductVersion() throws ResourceException;

  /** Returns maximum limit on number of active concurrent connections 
   *  that an EIS instance can support across client processes. If an EIS 
   *  instance does not know about (or does not have) any such limit, it 
   *  returns a 0.
   *
   *  @return  Maximum limit for number of active concurrent connections
  **/
  public
  int getMaxConnections() throws ResourceException;
  
  /** Returns name of the user associated with the ManagedConnection
   *  instance. The name corresponds to the resource principal under whose
   *  whose security context, a connection to the EIS instance has been
   *  established.
   *
   *  @return  name of the user
  **/
  public
  String getUserName() throws ResourceException;
}
