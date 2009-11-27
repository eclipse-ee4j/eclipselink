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
import javax.resource.NotSupportedException;


/** The <code>javax.resource.cci.Interaction</code> enables a component to 
 *  execute EIS functions. An Interaction instance supports the following ways 
 *  of interacting with an EIS instance:
 *  <UL>
 *     <LI><code>execute</code> method that takes an input Record, output
 *         Record and an InteractionSpec. This method executes the EIS 
 *         function represented by the InteractionSpec and updates the 
 *         output Record
 *     <LI><code>execute</code> method that takes an input Record and an 
 *         InteractionSpec. This method implementation executes the EIS 
 *         function represented by the InteractionSpec and produces the 
 *         output Record as a return value.
 *  </UL>
 *  <p>An Interaction instance is created from a Connection and is required
 *  to maintain its association with the Connection instance. The close method
 *  releases all resources maintained by the resource adapter for the 
 *  Interaction. The close of an Interaction instance should not close the 
 *  associated Connection instance.
 *       
 *  @author  Rahul Sharma
 *  @version 0.8
 *  @since   0.8
 *  @see     java.sql.ResultSet
**/

public interface Interaction {
  
  /** Closes the current Interaction and release all the resources
   *  held for this instance by the resource adapter. The close of an 
   *  Interaction instance does not close the associated Connection 
   *  instance. It is recommended that Interaction instances be
   *  closed explicitly to free any held resources.
   *
   *  @throws  ResourceException Failed to close the Interaction
   *                             instance. Invoking close on an 
   *                             already closed Interaction should 
   *                             also throw this exception. 
  **/
  public
  void close() throws ResourceException;


  /** Gets the Connection associated with the Interaction.
   *
   *  @return   Connection instance associated with the Interaction
  **/
  public
  Connection getConnection();

  /** Executes an interaction represented by the InteractionSpec.
   *  This form of invocation takes an input Record and updates
   *  the output Record. 
   *  
   *  @param   ispec   InteractionSpec representing a target EIS 
   *                   data/function module   
   *  @param   input   Input Record
   *  @param   output  Output Record
   * 
   *  @return  true if execution of the EIS function has been 
   *           successful and output Record has been updated; false
   *           otherwise
   *
   *  @throws  ResourceException   Exception if execute operation
   *                               fails. Examples of error cases
   *                               are:
   *         <UL>
   *           <LI> Resource adapter internal, EIS-specific or 
   *                communication error 
   *           <LI> Invalid specification of an InteractionSpec, 
   *                input or output record structure
   *           <LI> Errors in use of input or output Record
   *           <LI> Invalid connection associated with this 
   *                Interaction
   *	     </UL>
   *  @throws NotSupportedException Operation not supported 
   *                             
  **/
  public
  boolean execute(InteractionSpec ispec, 
		  Record input, 
		  Record output) throws ResourceException;

  /** Executes an interaction represented by the InteractionSpec.
   *  This form of invocation takes an input Record and returns an 
   *  output Record if the execution of the Interaction has been
   *  successfull.
   *  
   *  @param   ispec   InteractionSpec representing a target EIS 
   *                   data/function module   
   *  @param   input   Input Record

   *  @return  output Record if execution of the EIS function has been 
   *           successful; null otherwise
   *
   *  @throws  ResourceException   Exception if execute operation
   *                               fails. Examples of error cases
   *                               are:
   *         <UL>
   *           <LI> Resource adapter internal, EIS-specific or 
   *                communication error 
   *           <LI> Invalid specification of an InteractionSpec 
   *                or input record structure
   *           <LI> Errors in use of input Record or creation
   *                of an output Record
   *           <LI> Invalid connection associated with this 
   *                Interaction
   *	     </UL>
   *  @throws NotSupportedException Operation not supported 
  **/
  public
  Record execute(InteractionSpec ispec, 
		 Record input) throws ResourceException;

  /** Gets the first ResourceWarning from the chain of warnings
   *  associated with this Interaction instance.
   *
   *  @return   ResourceWarning at top of the warning chain
   *  @throws   ResourceException  Failed to get ResourceWarnings
   *                               associated with Interaction  
   **/
  public
  ResourceWarning getWarnings()  throws ResourceException;

  /** Clears all the warning reported by this Interaction instance. After 
   *  a call to this method, the method getWarnings will return null 
   *  until a new warning is reported for this Interaction.
   * 
   *  @throws   ResourceException  Failed to clear ResourceWarnings
   *                               associated with Interaction  
 **/
  public 
  void clearWarnings() throws ResourceException;

}
