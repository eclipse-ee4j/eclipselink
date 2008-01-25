/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.EventListener;


/**
 * A "DescriptorCreationFailure" event gets fired whenever a project is unable
 * to create a descriptor for a particular MWClass
 */
public interface DescriptorCreationFailureListener extends EventListener {

    /**
     * This method gets called when a project is unable to create 
	 * or refresh a descriptor for a given mwClass "external" class repository
     * 
     * @param e A DescriptorCreationFailureEvent object describing the
     * event source, the class that did not have a descriptor created, 
     * and a resource string key to explain the cause of the failure.
     */
    void descriptorCreationFailure(DescriptorCreationFailureEvent e);
}
