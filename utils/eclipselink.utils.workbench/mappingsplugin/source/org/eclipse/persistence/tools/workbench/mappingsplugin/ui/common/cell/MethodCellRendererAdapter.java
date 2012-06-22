/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;


/**
 * the icon will reflect the method's access level (public, protected, package, private);
 * the text will display the method's "short" signature, where the parm types are
 * abbreviated
 */
public class MethodCellRendererAdapter extends NoneSelectedCellRendererAdapter {

	public MethodCellRendererAdapter(ResourceRepository repository) {
		super(repository);
	}

	protected String buildNonNullValueText(Object value) {
		return ((MWMethod) value).shortSignatureWithReturnType();
	}

    protected Icon buildNonNullValueIcon(Object value) {
		if (((MWMethod) value).getModifier().isPackage()) {
			return resourceRepository().getIcon("method.default");
		}
		else if (((MWMethod) value).getModifier().isPublic()) {
			return resourceRepository().getIcon("method.public");
		}
		else if (((MWMethod) value).getModifier().isProtected()) {
			return resourceRepository().getIcon("method.protected");
		}
		else { //if (((MWMethod) value).getModifier().isPrivate()) 
			return resourceRepository().getIcon("method.private");
		}
	}

}
