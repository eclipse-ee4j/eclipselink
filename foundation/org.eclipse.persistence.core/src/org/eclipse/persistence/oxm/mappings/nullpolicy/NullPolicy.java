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

import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.XPathNode;

/**
 * PUBLIC:
 * <b>Description</b>: 
 * This null policy is the default implementation class.<br>
 * Marshal:<br>
 * The boolean value of the isSet() state of a node has no effect on whether a node will be written out
 * for a null value - a set is always performed unless the isSetPerformedForAbsentNode flag
 * is set to false for absent nodes.<br>
 * Unmarshal:<br>
 *  
 * <p><b>The following instance field can be set on top of the two from AbstractNullPolicy</b>:<ul>
 * <li>isSetPerformedForAbsentNode: </li>

 * </ul>
 * <p>
 *&nbsp;<b>Usage</b>:<br>
 * <p><b>Unmarshal null direct element xsi:nil node</b>:
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLDescriptor aDescriptor = new XMLDescriptor();<br>
 * aDescriptor.setJavaClass(Employee.class);<br>
 * aDescriptor.setDefaultRootElement("employee");<br>
 * XMLDirectMapping firstNameMapping = new XMLDirectMapping();<br>
 * firstNameMapping.setAttributeName("firstname");<br>
 * firstNameMapping.setXPath("first-name/text()");<br>
 * AbstractNullPolicy aNullPolicy = new NullPolicy();<br>
 * aDescriptor.addMapping(firstNameMapping);<br>
 * aNullPolicy.setSetPerformedForAbsentNode(false); // no effect<br>
 * aNullPolicy.setNullRepresentedByEmptyNodeNode(false); // default or no effect<br>
 * aNullPolicy.setNullRepresentedByXsiNil(false); // default or no effect<br>
 * </code>
 * <p><em>Input XML</em><br>
 * <code>
 * &lt;employee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;<br>
 * &nbsp;&nbsp;&lt;firstname xsi:nil=true/&gt;<br>
 * &lt;/employee&gt;<br>
 * </code>
 * <p><em>Output Object State</em><br>
 * <code>
 * anEmployee.getFirstname() = null<br>
 * anEmployee.isSet(firstname) = true<br>
 * </code>

 * <p><b>Marshal null composite object (isSet=true) as empty node</b>: .
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLDescriptor aDescriptor = new XMLDescriptor();<br>
 * aDescriptor.setJavaClass(Team.class);<br>
 * aDescriptor.setDefaultRootElement("team");<br>
 * XMLCompositeObjectMapping aManagerMapping = new XMLCompositeObjectMapping();<br>
 * aManagerMapping.setAttributeName("manager");<br>
 * aManagerMapping.setXPath("manager/text()");<br>
 * // do not modify the default NullPolicy or the 3 boolean flags<br>
 * AbstractNullPolicy aNullPolicy = afirstNameMapping.getNullPolicy();<br>
 * aDescriptor.addMapping(aManagerMapping);<br>
 * aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);<br>
 * </code>
 * 
 * <p><em>Input Object State</em><br>
 * <code>
 * aTeam.getManager() = null<br>
 * aTeam.isSet(manager) = no effect<br>
 * </code>
 * <p><em>Output XML</em><br>
 * <code>
 * &lt;team xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;<br>
 * &nbsp;&nbsp;&lt;manager/&gt;<br>
 * &lt;/team&gt;<br>
 * </code>
 * 
 * @see org.eclipse.persistence.internal.oxm.NullCapableValue
 * @since Oracle TopLink 11<i>g</i> Release 1 (11.1.1) 
 */
public class NullPolicy extends AbstractNullPolicy {
    /**
     * Default Constructor
     */
    public NullPolicy() {
    	super();
    }

    /**
     * Specific Constructor to set the Unmarshal flags
     * @param anIsSetMethodName
     * @param bIsSetPerformedForAbsentNode
     * @param bIsNullRepresentedByEmptyNode
     * @param bIsNullRepresentedByXsiNil
     */
    public NullPolicy(String anIsSetMethodName, //
    		boolean bIsSetPerformedForAbsentNode, boolean bIsNullRepresentedByEmptyNode, boolean bIsNullRepresentedByXsiNil) {
    	this();
    	setSetPerformedForAbsentNode(bIsSetPerformedForAbsentNode);
		setNullRepresentedByEmptyNode(bIsNullRepresentedByEmptyNode);
		setNullRepresentedByXsiNil(bIsNullRepresentedByXsiNil);
    }

    /**
     * Specific Constructor to set both the Marshal enum and the Unmarshal flags
     * @param anIsSetMethodName
     * @param bIsSetPerformedForAbsentNode
     * @param bIsNullRepresentedByEmptyNode
     * @param bIsNullRepresentedByXsiNil
     * @param aMarshalNullRepresentation
     */
    public NullPolicy(String anIsSetMethodName, //
    		boolean bIsSetPerformedForAbsentNode, boolean bIsNullRepresentedByEmptyNode, boolean bIsNullRepresentedByXsiNil, //
    		XMLNullRepresentationType aMarshalNullRepresentation) {
    	this(anIsSetMethodName, bIsSetPerformedForAbsentNode, bIsNullRepresentedByEmptyNode, bIsNullRepresentedByXsiNil);
		setMarshalNullRepresentation(aMarshalNullRepresentation);		
    }

    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
    	// No operation for non-isSet modes
    }
    
    /**
     * Set the isSetPerformedForAbsentNode flag
     * @param performSet
     */
    public void setSetPerformedForAbsentNode(boolean performSet) {
    	isSetPerformedForAbsentNode = performSet;
    }
}
