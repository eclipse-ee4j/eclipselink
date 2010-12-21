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
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic;

// JDK
import java.awt.Component;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.ConnectionPoolAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NullLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.PropertyPane;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;

// Mapping Workbench

/**
 * ...
 * <p>
 * Here the layout of this pane:
 * <pre>
 * _________________________________________________
 * | _________________________________ ___________ |
 * | | |    Key       |    Value     | | Add...  | |<- Shows the {@link PropertyEditor}
 * | |¯|¯¯¯¯¯¯¯¯¯¯¯¯¯¯|¯¯¯¯¯¯¯¯¯¯¯¯¯¯| ¯¯¯¯¯¯¯¯¯¯¯ |   to add a new key/value
 * | |-|--------------|--------------| ___________ |
 * | | |              |              | | Edit... | |<- Shows the {@link PropertyEditor}
 * | |-|--------------|--------------| ¯¯¯¯¯¯¯¯¯¯¯ |   to edit the selected row
 * | | |              |              | ___________ |
 * | |-|--------------|--------------| | Remove  | |
 * | | |              |              | ¯¯¯¯¯¯¯¯¯¯¯ |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯             |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * @see LoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class LoginPropertiesPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>LoginPropertiesPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link LoginAdapter}
	 * @param context
	 */
	public LoginPropertiesPropertiesPage(PropertyValueModel subjectHolder, WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
		addHelpTopicId(this, "session.login.properties");
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * boolean state of the <code>ComponentEnabler</code>.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildEnableStateHolder()
	{
		return new TransformationPropertyValueModel(getSelectionHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(value != NullLoginAdapter.instance());
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{
		JComponent subPane = new PropertyPane(getSelectionHolder(), getWorkbenchContextHolder());
		subPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buildPropertyPaneComponentEnabler(subPane);
		return subPane;
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the given component in sync with the boolean value calculated by the
	 * enabled state holder.
	 *
	 * @param component The component where their enable state will be updated
	 * when necessary
	 * @return A new <code>ComponentEnabler</code>
	 */
	private void buildPropertyPaneComponentEnabler(JComponent component)
	{
		new ComponentEnabler(buildEnableStateHolder(), Collections.singleton(component));
	}

	/**
	 * Creates the selection holder that will hold the user object to be edited
	 * by this page.
	 *
	 * @param nodeHolder The holder of {@link DatabaseSessionNode}
	 * @return The <code>PropertyValueModel</code> containing the {@link EisLoginAdapter}
	 * to be edited by this page
	 */
	protected PropertyValueModel buildSelectionHolder()
	{
		return new PropertyAspectAdapter(super.buildSelectionHolder(),
													ConnectionPoolAdapter.LOGIN_CONFIG_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				ConnectionPoolAdapter session = (ConnectionPoolAdapter) subject;
				return session.getLogin();
			}
		};
	}
}
