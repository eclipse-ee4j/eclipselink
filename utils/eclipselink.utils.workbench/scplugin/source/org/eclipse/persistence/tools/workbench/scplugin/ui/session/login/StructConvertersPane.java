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

//JDK
import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

// Mapping Workbench

/**
 * This pane shows the {@link org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel
 * AddRemoveListPanel} for Event Listener classes.
 * <p>
 * Here the layout:
 * <pre>
 * __________________________________________________
 * |                                                |
 * | Struct Converters:                             |
 * | ___________________________________ __________ |
 * | |                               |^| | Add... | |<- Shows the Class chooser
 * | |                               | | ---------- |
 * | |                               ||| __________ |
 * | |                               ||| | Remove | |
 * | |                               ||| ---------- |
 * | |                               |||            |
 * | |                               | |            |
 * | |                               |v|            |
 * | -----------------------------------            |
 * --------------------------------------------------</pre>
 *
 * Known container of this pane:<br>
 * - {@link RdbmsOptionsPropertiesPage}
 *
 * @see SessionAdapter
 *
 * @version 11.1.1.0.0
 * @author Les Davis
 */
final class StructConvertersPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>StructConvertersPane</code>.
	 *
	 * @param loginAdapterHolder The holder of {@link LoginAdapter}
	 * @param context The context used to retrieve the localized strings
	 * @param group The parent <code>ComponentAligner</code> of the
	 * <code>ComponentAligner</code> used by this pane
	 */
	public StructConvertersPane(PropertyValueModel loginAdapterHolder,
									  WorkbenchContextHolder contextHolder)
	{
		super(new BorderLayout(), loginAdapterHolder, contextHolder);
		addHelpTopicId(this, "rdbms.options.structConverters");
	}

	/**
	 * Creates the <code>AddRemoveListPanel.Adapter</code> that will takes care
	 * of updating the model when items are either added or removed from the
	 * list.
	 *
	 * @return A new <code>AddRemoveListPanel.Adapter</code>
	 */
	private AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter()
	{
		return new AddRemoveListAdapter();
	}

	/**
	 * Creates the <code>ListValueModel</code> containing all the items to
	 * be shown in the Struct Converter Classes list pane.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private ListValueModel buildStructConverterListHolder()
	{
		return new ListAspectAdapter(getSubjectHolder(), DatabaseLoginAdapter.STRUCT_CONVERTER_COLLECTION_PROPERTY)
		{
			protected ListIterator getValueFromSubject()
			{
				DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) this.subject;
				return adapter.structConvertersClasses();
			}
		};
	}

	/**
	 * Creates a new <code>AddRemoveListPanel</code> that will shows the struct converters
	 * contained in the login.
	 *
	 * @return A new <code>AddRemoveListPanel</code>
	 */
	private AddRemoveListPanel buildStructConverterListPane()
	{
		return new CustomizedAddRemoveListPanel();
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		AddRemoveListPanel structConverterListPanel = buildStructConverterListPane();
		structConverterListPanel.setCellRenderer(new SimpleListCellRenderer());
		structConverterListPanel.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("OPTIONS_STRUCT_CONVERTER_LIST"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		add(structConverterListPanel, BorderLayout.CENTER);
		addPaneForAlignment(structConverterListPanel);
	}

	/**
	 * This <code>Adapter</code> is responsible to perform the action upon a
	 * click on either the Add or Remove buttons.
	 */
	private class AddRemoveListAdapter implements AddRemoveListPanel.Adapter
	{
		public void addNewItem(ObjectListSelectionModel listSelectionModel)
		{
			DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject();
			SimplePropertyValueModel selectionHolder = new SimplePropertyValueModel();

			ClassChooserTools.promptForType
			(
				getWorkbenchContext(),
				login.getClassRepository(),
				selectionHolder
			);

			String structConverterClassName = (String) selectionHolder.getValue();

			if ((structConverterClassName != null) &&
				 ! CollectionTools.contains(login.structConvertersClasses(), structConverterClassName))
			{
				login.addStructConverterClass(structConverterClassName);
			}
		}

		public void removeSelectedItems(ObjectListSelectionModel listSelectionModel)
		{
			DatabaseLoginAdapter adapter = (DatabaseLoginAdapter) subject();
			Object[] selectedValues = listSelectionModel.getSelectedValues();

			for (int index = selectedValues.length; --index >= 0;)
			{
				adapter.removeStructConverterClass((String) selectedValues[index]);
			}
		}
	}

	/**
	 * This customized <code>AddRemoveListPanel</code> simply renames the Add and
	 * Remove buttons.
	 */
	private class CustomizedAddRemoveListPanel extends AddRemoveListPanel
	{
		private CustomizedAddRemoveListPanel()
		{
			super(StructConvertersPane.this.getApplicationContext(),
					buildAddRemoveListPanelAdapter(),
					buildStructConverterListHolder(),
					AddRemoveListPanel.RIGHT);
		}

		protected String addButtonKey()
		{
			return "OPTIONS_STRUCT_CONVERTERS_ADD_BUTTON";
		}

		protected void buildButtonPanel(JComponent buttonPanel,
												  JButton addButton,
												  JButton removeButton,
												  JButton optionalButton)
		{
			buttonPanel.add(addButton);
			buttonPanel.add(removeButton);
		}

		protected String removeButtonKey()
		{
			return "OPTIONS_STRUCT_CONVERTERS_REMOVE_BUTTON";
		}
	}
}
