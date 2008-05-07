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


import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/** Streamable interface enables a resource adapter to extract data from
 *  an input Record or set data into an output Record as a stream of 
 *  bytes. 
 *
 *  <p>The Streamable interface provides a resource adapter's view
 *  of the data that has been set in a Record instance by a component.
 *  
 *  <p>The Streamable interface is not directly used by a component. It
 *  is used by a resource adapter implementation. A component uses Record 
 *  or any derived interfaces to manage records.
 *
 *  @author Rahul Sharma
 *  @since  0.8
 *  @see    javax.resource.cci.Record
**/

public interface Streamable {

  /** Read data from an InputStream and initialize fields of a 
   *  Streamable object. 
   *
   *  @param  istream   InputStream that represents a resource
   *                    adapter specific internal representation
   *                    of fields of a Streamable object
  **/
  public
  void read(InputStream istream) throws IOException;
  

  /** Write fields of a Streamable object to an OutputStream
   *  @param  ostream   OutputStream that holds value of a
   *                    Streamable object
  **/
  public
  void write(OutputStream ostream) throws IOException;

}

