/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.mappings.nullpolicy;

/**
 * <b>Description</b>:
 * An enum that is used within a Node Null Policy to determine what to marshal for a null node.<br>
 * We define 3 final instances available to the user (XSI_NIL, ABSENT_NODE(default) and EMPTY_NODE.
 *
 * <table border="1">
 * <caption>Node Null Policy</caption>
 * <tr>
 * <th id="c1">Flag</th>
 * <th id="c2">Description</th>
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
 * <td headers="c1" style="nowrap"> EMPTY_NODE </td>
 * <td headers="c2">Required: Write out an empty {@literal <node/>} or node="" node.</td>
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
