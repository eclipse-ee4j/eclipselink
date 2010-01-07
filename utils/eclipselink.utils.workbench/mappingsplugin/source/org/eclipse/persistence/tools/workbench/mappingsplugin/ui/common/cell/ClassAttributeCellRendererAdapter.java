/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.NoneSelectedCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;


/**
 * the icon will reflect the attribute's access level (public, protected, package, private);
 * the text will display the attribute's name followed by its type declaration (e.g. foo : Object)
 */
public class ClassAttributeCellRendererAdapter extends NoneSelectedCellRendererAdapter {


	public ClassAttributeCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}
	
	protected String buildNonNullValueText(Object value) {
		return ((MWClassAttribute) value).nameWithShortType();
	}
			
    protected Icon buildNonNullValueIcon(Object value) {
		if (((MWClassAttribute) value).getModifier().isPackage())
			return resourceRepository().getIcon("field.default");
		else if (((MWClassAttribute) value).getModifier().isPublic())
			return resourceRepository().getIcon("field.public");
		else if (((MWClassAttribute) value).getModifier().isProtected())
			return resourceRepository().getIcon("field.protected");
		else// if (((MWClassAttribute) value).getModifier().isPrivate())
			return resourceRepository().getIcon("field.private");
	}
}
