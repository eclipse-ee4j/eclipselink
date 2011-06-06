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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TransportManagerAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;

// Mapping Workbench

/**
 * This pane gives an easy creation of the Remove Connection on Error check box
 * the sub-pane needs to add into its layout.
 * <pre>
 * Known subclasses of this pane:<br>
 * - {@link RCMJMSPane}<br>
 * - {@link RCMRMIPane}<br>
 * - {@link RCMUserDefinedPane}
 * <p>
 * Known containers of this pane:<br>
 * - {@link RemoteCommandManagerPane}
 *
 * @see TransportManagerAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
abstract class AbstractTransportManagerPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>AbstractTransportManagerPane</code>.
	 *
	 * @param subjectHolder The holder of the {@link TransportManagerAdapter}
	 * @param context
	 */
	AbstractTransportManagerPane(ValueModel subjectHolder,
										  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Creates the check box responsible to update the Remove Connection On Error
	 * property.
	 *
	 * @return A new <code>JCheckBox</code>
	 */
	final JCheckBox buildRemoveConnectionOnError()
	{
		JCheckBox removeConnectionOnErrorCheckBox =
			buildCheckBox("CLUSTERING_REMOVE_CONNECTION_ON_ERROR_CHECK_BOX",
							  buildRemoveConnectionOnErrorCheckBoxAdapter());

		addHelpTopicId(removeConnectionOnErrorCheckBox, "session.clustering.rcm.removeConnectionOnError");

		return removeConnectionOnErrorCheckBox;
	}

	/**
	 * Creates the <code>ButtonModel</code> that keeps the selected state from
	 * the check box in sync with the value in the model and vice versa.
	 * 
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildRemoveConnectionOnErrorCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildRemoveConnectionOnErrorHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Remove Connection On Error property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildRemoveConnectionOnErrorHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), TransportManagerAdapter.ON_CONNECTION_ERROR_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				TransportManagerAdapter transport = (TransportManagerAdapter) subject;
				return Boolean.valueOf(transport.removeConnectionOnError());
			}

			protected void setValueOnSubject(Object value)
			{
				TransportManagerAdapter transport = (TransportManagerAdapter) subject;
				transport.setRemoveConnectionOnError(Boolean.TRUE.equals(value));
			}
		};
	}
}
