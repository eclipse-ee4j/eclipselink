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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.scplugin.model.EisPlatformManager;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.EISLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.EisLoginPane;
import org.eclipse.persistence.tools.workbench.scplugin.ui.login.LoginExternalOptionsPane;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

// Mapping Workbench

/**
 * This page shows the information regarding the EIS login which is
 * specific for {@link EisLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * ___________________________________________
 * |           _____________________________ |
 * | Platform: |                         |V| |
 * |           ----------------------------- |
 * | --------------------------------------- |
 * | |                                     | |
 * | | {@link EisLoginPane}                           | |
 * | |                                     | |
 * | --------------------------------------- |
 * |                                         |
 * | --------------------------------------- |
 * | |                                     | |
 * | | {@link LoginExternalOptionsPane}               | |
 * | |                                     | |
 * | --------------------------------------- |
 * |                                         |
 * -------------------------------------------</pre>
 *
 * @see EisLoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class EisConnectionPropertiesPage extends AbstractLoginPropertiesPage
{
	/**
	 * Creates a new <code>RdbmsConnectionPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link org.eclipse.persistence.tools.workbench.scplugin.ui.session.DatabaseSessionNode DatabaseSessionNode}
	 */
	public EisConnectionPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>Comparator</code> responsible to compare to EIS Platform
	 * class name.
	 *
	 * @return A new <code>Comparator</code>
	 */
	private Comparator buildEisPlatformNameCompator()
	{
		return new Comparator()
		{
			public int compare(Object object1, Object object2)
			{
				String shortClassName1 = ((String) object1).replaceFirst( "Platform", "");

				String shortClassName2 = ((String) object2).replaceFirst( "Platform", "");
			
				return shortClassName1.compareTo(shortClassName2);
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
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Platform label
		JLabel platformLabel = buildLabel("CONNECTION_EIS_PLATFORM_FIELD");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		panel.add(platformLabel, constraints);
		addAlignLeft(platformLabel);
		helpManager().addTopicID(platformLabel, "session.login.database.connection.platform");

		// Platform combo box
		JComboBox platformComboBox = new JComboBox(buildPlatformComboAdapter());
		platformComboBox.setName("CONNECTION_EIS_PLATFORM_FIELD");
		platformComboBox.setRenderer(buildPlatformLabelDecorator());

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

		panel.add(platformComboBox, constraints);
		platformLabel.setLabelFor(platformComboBox);
		helpManager().addTopicID(platformComboBox, "session.login.database.connection.platform");

		// Spacer
		Spacer spacer = new Spacer();

		constraints.gridx			= 2;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 0, 0);

		panel.add(spacer, constraints);
		addAlignRight(spacer);

		// Login pane
		EisLoginPane loginPane = new EisLoginPane(getSelectionHolder(), getWorkbenchContextHolder());

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(loginPane, constraints);
		addPaneForAlignment(loginPane);

		// External Transaction Controller check box
		LoginExternalOptionsPane optionsPane = new LoginExternalOptionsPane(getSelectionHolder(), getApplicationContext());

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 3;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.FIRST_LINE_START;
		constraints.insets      = new Insets(5, 0, 0, 0);

		panel.add(optionsPane, constraints);
		helpManager().addTopicID(optionsPane, "session.login.externalPool");

		return panel;
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the EI Platform combo box.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private CollectionValueModel buildPlatformCollectionHolder()
	{
		Collection platforms = CollectionTools.sortedSet(EisPlatformManager.instance().platformShortNames());
		return new ReadOnlyCollectionValueModel(platforms);
	}

	/**
	 * Creates the <code>ComboBoxModel</code> that keeps the selected value from
	 * the combo box in sync with the Platform Class value in the model and vice
	 * versa.
	 *
	 * @return A new <code>ComboBoxModel</code>
	 */
	private ComboBoxModel buildPlatformComboAdapter()
	{
		return new ComboBoxModelAdapter(buildPlatformListHolder(),
												  buildPlatformHolder());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Platform Class property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPlatformHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), EISLoginAdapter.PLATFORM_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				EISLoginAdapter login = (EISLoginAdapter) subject;
				return login.getPlatformClass();
			}

			protected void setValueOnSubject(Object value)
			{
				EISLoginAdapter adapter = (EISLoginAdapter) subject;
				adapter.setPlatformClass((String) value);
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object reverseTransform(Object value)
			{
				if (value == null)
					return null;

				return EisPlatformManager.instance().getRuntimePlatformClassNameForClass(( String)value);
			}

			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return ClassTools.shortNameForClassNamed((String) value);
			}
		};
	}

	/**
	 * Creates the decorator responsible to format the class name (String) values
	 * in the Platform combo box.
	 * 
	 * @return A new <code>ListCellRenderer</code>
	 */
	private ListCellRenderer buildPlatformLabelDecorator()
	{
		return new SimpleListCellRenderer()
		{
			protected String buildText(Object cellValue)
			{
				if (((String) cellValue).equals("AQPlatform"))
				{
					return EisPlatformManager.AQ_ID;
				}
				else if (((String)cellValue).equals("JMSPlatform"))
				{
					return EisPlatformManager.JMS_ID;
				}
				else if (((String)cellValue).equals("MQPlatform"))
				{
					return EisPlatformManager.MQ_ID;
				} 
				else 
				{
					return EisPlatformManager.XML_ID;
				}
			}
		};
	}

	/**
	 * Creates the <code>ListValueModel</code> containing all the items to
	 * be shown in the Database Platform combo box.
	 *
	 * @return A new <code>ListValueModel</code>
	 */
	private ListValueModel buildPlatformListHolder()
	{
		return new SortedListValueModelAdapter(buildPlatformCollectionHolder(),
															buildEisPlatformNameCompator());
	}
}
