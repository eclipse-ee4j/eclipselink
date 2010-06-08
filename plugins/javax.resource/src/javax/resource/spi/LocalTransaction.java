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

/** LocalTransaction interface provides support for transactions that
 *  are managed internal to an EIS resource manager, and do not require
 *  an external transaction manager.
 * 
 *  <p>A resource adapter implements the javax.resource.spi.LocalTransaction 
 *  interface to provide support for local transactions that are performed
 *  on the underlying resource manager.
 *
 *  <p>If a resource adapter supports the LocalTransaction interface, then 
 *  the application server can choose to perform local transaction 
 *  optimization (uses local transaction instead of a JTA transaction for
 *  a single resource manager case).
 *
 *  @version     0.5
 *  @author      Rahul Sharma
 *  @see         javax.resource.spi.ManagedConnection
 **/



public interface LocalTransaction {
  /** Begin a local transaction
   *  
   *  @throws  ResourceException   generic exception if operation fails
   *  @throws  LocalTransactionException  
   *                               error condition related 
   *                               to local transaction management
   *  @throws  ResourceAdapterInternalException
   *                               error condition internal to resource
   *                               adapter
   *  @throws  EISSystemException  EIS instance specific error condition        
  **/
  public 
  void begin() throws ResourceException;

  /** Commit a local transaction 
   *
   *  @throws  ResourceException   generic exception if operation fails
   *  @throws  LocalTransactionException  
   *                               error condition related 
   *                               to local transaction management
   *  @throws  ResourceAdapterInternalException
   *                               error condition internal to resource
   *                               adapter
   *  @throws  EISSystemException  EIS instance specific error condition        
  **/
  public
  void commit() throws ResourceException;
  
  /** Rollback a local transaction
   *  @throws  ResourceException   generic exception if operation fails
   *  @throws  LocalTransactionException  
   *                               error condition related 
   *                               to local transaction management
   *  @throws  ResourceAdapterInternalException
   *                               error condition internal to resource
   *                               adapter
   *  @throws  EISSystemException  EIS instance specific error condition        
  **/
  public
  void rollback() throws ResourceException;

}
