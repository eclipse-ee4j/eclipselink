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

package javax.resource;

import javax.naming.Reference;

/** The Referenceable interface extends the javax.naming.Referenceable
 *  interface. It enables support for JNDI Reference mechanism for 
 *  the registration of the connection factory in the JNDI name space. 
 *  Note that the implementation and structure of Reference is specific
 *  to an application server.
 *
 *  <p>The implementation class for a connection factory interface is 
 *  required to implement both java.io.Serializable and 
 *  javax.resource.Referenceable interfaces to support JNDI registration.
 *
 *  @version     0.9
 *  @author      Rahul Sharma
 *
**/

public interface Referenceable extends javax.naming.Referenceable {

  /** Sets the Reference instance. This method is called by the
   *  deployment code to set the Reference that can be later
   *  returned by the getReference method (as defined in the
   *  javax.naming.Referenceable interface).
   *
   *  @param   reference  A Reference instance
   *  @see     javax.naming.Referenceable#getReference
   *
  **/
  public 
  void setReference(Reference reference);

}
