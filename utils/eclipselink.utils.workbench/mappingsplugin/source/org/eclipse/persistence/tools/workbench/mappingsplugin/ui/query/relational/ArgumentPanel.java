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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;



/**
 * Abstract panel used by the ExpressionBuilderDialog
 * A specific panel is selected depending on what type of argument the user selects for their expression
 * (literal, queryable, parameter)
 */
abstract class ArgumentPanel 
	extends AbstractPanel 
{
	
	private PropertyValueModel argumentHolder;
	private PropertyValueModel queryHolder;
		
	protected ArgumentPanel(PropertyValueModel argumentHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.argumentHolder = buildQueryArgumentHolder(argumentHolder);
		this.queryHolder = buildQueryHolder();
	}
	
	protected PropertyValueModel buildQueryArgumentHolder(PropertyValueModel argumentHolder) {
	    return argumentHolder;
	}
	
	private PropertyValueModel buildQueryHolder() {
		return new PropertyAspectAdapter(this.argumentHolder) {
			protected Object getValueFromSubject() {
				return ((MWArgument) this.subject).getParentQuery();
			}
		};
	}
	
	protected PropertyValueModel getArgumentHolder() {
		return this.argumentHolder;
	}
	
	protected MWArgument getArgument() {
		return (MWArgument) this.argumentHolder.getValue();
	}
	
	protected PropertyValueModel getQueryHolder() {
		return this.queryHolder;
	}

}
