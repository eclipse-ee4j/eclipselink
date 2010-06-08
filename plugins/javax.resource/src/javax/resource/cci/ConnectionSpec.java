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

/** ConnectionSpec is used by an application component to pass 
 *  connection request-specific properties to the ConnectionFactory.
 *  getConnection method.
 *
 *  <p>It is recommended that the ConnectionSpec interface be 
 *  implemented as a JavaBean to support tools. The properties 
 *  on the ConnectionSpec implementation class must be defined 
 *  through the getter and setter methods pattern. 
 *  
 *  <p>The CCI specification defines a set of standard properties 
 *  for an ConnectionSpec. The properties are defined either on
 *  a derived interface or an implementation class of an empty
 *  ConnectionSpec interface. In addition, a resource adapter may 
 *  define additional properties specific to its underlying EIS.
 *  
 *  @author  Rahul Sharma
 *  @version 1.0 Public Draft 1
 *  @see     javax.resource.cci.ConnectionFactory
 **/

public interface ConnectionSpec {

}
