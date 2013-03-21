/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings.nullpolicy;

/**
 * <b>Description</b>: 
 * An enum that is used within a Node Null Policy to determine what to marshal for a null node.<br>
 * We define 3 final instances available to the user (XSI_NIL, ABSENT_NODE(default) and EMPTY_NODE.
 * <p>
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">Flag</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1"> XSI_NIL </td>
 * <td headers="c2">Nillable: Write out an xsi:nil="true" attribute.</td>
 * </tr>
 * <tr>
 * <td headers="c1"> ABSENT_NODE(default) </td>
 * <td headers="c2">Optional: Write out no node.</td>
 * </tr>
 * <tr>
 * <td headers="c1" nowrap="true"> EMPTY_NODE </td>
 * <td headers="c2">Required: Write out an empty <node/> or node="" node.</td>
 * </tr>
 * </table>
 * @see org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy
 */
public enum XMLNullRepresentationType {
	
	/**
	 * Write out an xsi:nil="true" attribute. Nillable policy behavior.
	 */
	XSI_NIL,
	
	/**
	 * Do not write out anything (default optional policy behavior). 
	 */
	ABSENT_NODE,
	
	/**
	 * Write out an empty node. Required policy behavior
	 */
	EMPTY_NODE	
	}
