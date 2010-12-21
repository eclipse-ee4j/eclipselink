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

/** IndexedRecord represents an ordered collection of record elements 
 *  based on the <code>java.util.List</code> interface. This interface 
 *  allows a client to access elements by their integer index (position
 *  in the list) and search for elements in the List.
 *
 *  @author  Rahul Sharma
 *  @since   0.8
**/
public interface IndexedRecord extends Record, java.util.List, 
                                       java.io.Serializable {

}
