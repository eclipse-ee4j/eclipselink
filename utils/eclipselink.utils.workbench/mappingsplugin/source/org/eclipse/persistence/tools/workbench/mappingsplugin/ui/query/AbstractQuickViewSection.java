/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.QuickViewPanel.QuickViewSection;


public abstract class AbstractQuickViewSection implements QuickViewSection
{
	private final String accessibleNameKey;
	private final String displayStringKey;

	private ResourceRepository resourceRepository;
	
	protected AbstractQuickViewSection(ResourceRepository resourceRepository,
									String displayStringKey,
	                                String accessibleNameKey) {
		super();
		this.resourceRepository = resourceRepository;
		this.displayStringKey  = displayStringKey;
		this.accessibleNameKey = accessibleNameKey;
	}

	public String accessibleName() {
		return this.resourceRepository.getString(this.accessibleNameKey);
	}

	public String displayString() {
		return this.resourceRepository.getString(this.displayStringKey);
	}

	public Object getValue() {
		return null;
	}

	public Icon icon() {
		return null; // TODO
	}

	public final boolean isRemovable() {
		return false; // For now a section is not removable
	}

	public final void remove() {
		throw new UnsupportedOperationException("A IQuickViewSection cannot be removed");
	}
}
