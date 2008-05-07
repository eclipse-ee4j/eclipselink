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

/** A ResultSet represents tabular data that is retrieved from an EIS
 *  instance by the execution of an Interaction.. The CCI ResultSet is 
 *  based on the JDBC ResultSet.   
 *
 *  <p>Refer the CCI specification in Connectors 1.0 for detailed
 *  requirements on the implementation of a CCI ResultSet.
 *
 *  @author  Rahul Sharma
 *  @since   0.8
 *  @see     java.sql.ResultSet
**/
public interface ResultSet extends Record, java.sql.ResultSet {

}
