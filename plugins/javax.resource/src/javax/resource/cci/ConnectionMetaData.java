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

package javax.resource.cci;


import javax.resource.ResourceException;

/** The interface <code>ConnectionMetaData</code> provides information 
 *  about an EIS instance connected through a Connection instance. A
 *  component calls the method <code>Connection.getMetaData</code> to
 *  get a <code>ConnectionMetaData</code> instance. 
 *
 *  @version     0.8
 *  @author      Rahul Sharma
 *  @see         javax.resource.cci.Connection
 *  @see         javax.resource.cci.ResultSetInfo
**/

public interface ConnectionMetaData {

  /** Returns product name of the underlying EIS instance connected
   *  through the Connection that produced this metadata.
   *
   *  @return   Product name of the EIS instance
   *  @throws   ResourceException  Failed to get the information for
   *                               the EIS instance
  **/
  public
  String getEISProductName() throws ResourceException;

  /** Returns product version of the underlying EIS instance.
   *
   *  @return   Product version of an EIS instance. 
   *  @throws   ResourceException  Failed to get the information for
   *                               the EIS instance
  **/
  public
  String getEISProductVersion() throws ResourceException;

  /** Returns the user name for an active connection as known to 
   *  the underlying EIS instance. The name corresponds the resource
   *  principal under whose security context a connection to the
   *  EIS instance has been established.
   *
   *  @return   String representing the user name
   *  @throws   ResourceException  Failed to get the information for
   *                               the EIS instance           
  **/
  public
  String getUserName() throws ResourceException;
}
