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

/** <p>ConnectionManager interface provides a hook for the resource adapter to
 *  pass a connection request to the application server. 
 *  
 *  <p>An application server provides implementation of the ConnectionManager
 *  interface. This implementation is not specific to any particular type of
 *  the resource adapter or connection factory interface.
 * 
 *  <p>The ConnectionManager implementation delegates to the application 
 *  server to enable latter to provide quality of services (QoS) - security,
 *  connection pool management, transaction management and error 
 *  logging/tracing. 
 * 
 *  <p>An application server implements these services in a generic manner, 
 *  independent of any resource adapter and EIS specific mechanisms. The 
 *  connector architecture does not specify how an application server 
 *  implements these services; the implementation is specific to an 
 *  application server.
 *  
 *  <p>After an application server hooks-in its services, the connection 
 *  request gets delegated to a ManagedConnectionFactory instance either 
 *  for the creation of a new physical connection or for the matching of 
 *  an already existing physical connection.
 *  
 *  <p>An implementation class for ConnectionManager interface is
 *  required to implement the <code>java.io.Serializable</code> interface.
 *  
 *  <p>In the non-managed application scenario, the ConnectionManager 
 *  implementation class can be provided either by a resource adapter (as
 *  a default ConnectionManager implementation) or by application 
 *  developers. In both cases, QOS can be provided as components by third
 *  party vendors.</p>
 *
 *  @since       0.6
 *  @author      Rahul Sharma
 *  @see         javax.resource.spi.ManagedConnectionFactory
**/

public interface ConnectionManager 
                    extends java.io.Serializable {

  /** <p>The method allocateConnection gets called by the resource adapter's
   *  connection factory instance. This lets connection factory instance 
   *  (provided by the resource adapter) pass a connection request to 
   *  the ConnectionManager instance.</p>
   *  
   *  <p>The connectionRequestInfo parameter represents information specific
   *  to the resource adapter for handling of the connection request.</p>
   *
   *  @param   mcf
   *                       used by application server to delegate
   *                       connection matching/creation
   *  @param   cxRequestInfo     
   *                       connection request Information
   *
   *  @return  connection handle with an EIS specific connection interface.
   * 
   *
   *  @throws  ResourceException     Generic exception
   *  @throws  ApplicationServerInternalException 
   *                                 Application server specific exception
   *  @throws  SecurityException     Security related error
   *  @throws  ResourceAllocationException
   *                                 Failed to allocate system resources for
   *                                 connection request
   *  @throws  ResourceAdapterInternalException
   *                                 Resource adapter related error condition
  **/
  public  
  Object allocateConnection(ManagedConnectionFactory mcf,
			    ConnectionRequestInfo cxRequestInfo)
                               throws ResourceException;

}
