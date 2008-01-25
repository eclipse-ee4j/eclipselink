/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public abstract class AbstractMWClassRefreshPolicy
	implements MWClassRefreshPolicy
{
	protected AbstractMWClassRefreshPolicy()
	{
		super();
	}
	
	public void refreshAttributes(MWClass mwClass, ExternalField[] externalFields)
	{
		// after we have looped through the Java fields,
		// 'removedAttributes' will be left with the attributes that need to be removed
		Collection missingAttributes = CollectionTools.collection(mwClass.attributes());
		for (int i = 0; i < externalFields.length; i++) {
			this.refreshAttribute(mwClass, externalFields[i], missingAttributes);
		}
		this.resolveMissingAttributes(mwClass, missingAttributes);
	}
	
	private void refreshAttribute(MWClass mwClass, ExternalField externalField, Collection missingAttributes) {
		if (externalField.isSynthetic()) {
			return;	// we are not interested in compiler-generated fields
		}
		MWClassAttribute existingAttribute = mwClass.attributeNamed(externalField.getName());
		if (existingAttribute == null) {
			// we have a new attribute
			mwClass.addAttribute(externalField);
		} else {
			// we need to refresh the existing attribute
			existingAttribute.refresh(externalField);
			missingAttributes.remove(existingAttribute);
		}
	}
	
	protected abstract void resolveMissingAttributes(MWClass mwClass, Collection missingAttributes);
}
