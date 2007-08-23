/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;


/**
 * INTERNAL
 * All nillable mappings which can be added to org.eclipse.persistence.oxm.XMLDescriptor must
 * implement this interface.<br>
 * The default policy is OptionalNodeNullPolicy.<br>
 *
 *@see org.eclipse.persistence.oxm.mappings
 */
public interface XMLNillableMapping {

    /**
     * Set the NodeNullPolicy on the mapping<br>
     * The default policy is OptionalNodeNullPolicy.<br>
     *
     * @param aNodeNullPolicy
     */
    public void setNodeNullPolicy(NodeNullPolicy aNodeNullPolicy);

    /**
     * Get the NodeNullPolicy from the Mapping.<br>
     * The default policy is OptionalNodeNullPolicy.<br>
     * @return
     */
    public NodeNullPolicy getNodeNullPolicy();
}