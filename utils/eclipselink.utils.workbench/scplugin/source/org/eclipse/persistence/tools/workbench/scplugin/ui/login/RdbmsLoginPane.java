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
package org.eclipse.persistence.tools.workbench.scplugin.ui.login;

// Mapping Workbench
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseSessionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class RdbmsLoginPane extends AbstractRdbmsLoginPane
{
	/**
	 * Creates a new <code>RdbmsLoginPane</code>.
	 *
	 * @param subjectHolder The holder of {@link DatabaseLoginAdapter}
	 * @param context The plug-in context to be used, such as <code>ResourceRepository</code>
	 */
	public RdbmsLoginPane(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of Database Driver to be used, which is either
	 * Driver Manager or J2EE Data Source.
	 *
	 * @return {@link PropertyValueModel}
	 */
	protected PropertyValueModel buildDatabaseDriverSelectionHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.USE_DRIVER_MANAGER_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return Boolean.valueOf(login.databaseDriverIsDriverManager());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				DatabaseSessionAdapter session = (DatabaseSessionAdapter) login.getParent();

				if (Boolean.TRUE.equals(value))
					login.setDatabaseDriverAsDriverManager();
				else
					login.setDatabaseDriverAsDataSource();

				session.updateExternalConnectionPooling();
			}
		};
	}
}
