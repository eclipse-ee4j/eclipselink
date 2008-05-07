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

import java.util.Map;
import java.util.Collection;
import javax.resource.ResourceException;
import javax.resource.NotSupportedException;


/** The RecordFactory interface is used for creating MappedRecord and 
 *  IndexedRecord instances. Note that the RecordFactory is only used 
 *  for creation of generic record instances. A CCI implementation 
 *  provides an implementation class for the RecordFactory interface.
 *
 *  @author   Rahul Sharma
 *  @since    0.8
 *  @see      javax.resource.cci.IndexedRecord
 *  @see      javax.resource.cci.MappedRecord
**/
public interface RecordFactory {
  
  /** Creates a MappedRecord. The method takes the name of the record
   *  that is to be created by the RecordFactory. The name of the 
   *  record acts as a pointer to the meta information (stored in 
   *  the metadata repository) for a specific record type.
   *
   *  @param  recordName   Name of the Record
   *  @return MappedRecord
   *  @throws ResourceException  Failed to create a MappedRecord.
   *                             Example error cases are:
   *                              
   *          <UL>
   *             <LI> Invalid specification of record name
   *             <LI> Resource adapter internal error
   *             <LI> Failed to access metadata repository
   *          </UL>
   *  @throws NotSupportedException Operation not supported          
   *                            
  **/
  public
  MappedRecord createMappedRecord(String recordName) 
                  throws ResourceException;

  /** Creates a IndexedRecord. The method takes the name of the record
   *  that is to be created by the RecordFactory. The name of the 
   *  record acts as a pointer to the meta information (stored in 
   *  the metadata repository) for a specific record type.
   *
   *  @param  recordName   Name of the Record
   *  @return IndexedRecord
   *  @throws ResourceException  Failed to create an IndexedRecord.
   *                             Example error cases are:
   *                              
   *          <UL>
   *             <LI> Invalid specification of record name
   *             <LI> Resource adapter internal error
   *             <LI> Failed to access metadata repository
   *          </UL>
   *  @throws NotSupportedException Operation not supported          
 **/
  public
  IndexedRecord createIndexedRecord(String recordName) 
                  throws ResourceException;

}

