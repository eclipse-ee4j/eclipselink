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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.basic;

// JDK
import java.awt.BorderLayout;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.ClassChooserTools;
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
 * | Event Listeners:                               |
 * | ___________________________________ __________ |
 * | |                               |^| | Add... | |<- Shows the Class chooser
 * | |                               | | ¯¯¯¯¯¯¯¯¯¯ |
 * | |                               ||| __________ |
 * | |                               ||| | Remove | |
 * | |                               ||| ¯¯¯¯¯¯¯¯¯¯ |
 * | |                               |||            |
 * | |                               | |            |
 * | |                               |v|            |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯            |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Known container of this pane:<br>
 * - {@link SessionOptionsPropertiesPage}
 *
 * @see SessionAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class EventListenersPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>EventListenersPane</code>.
	 *
	 * @param sessionAdapterHolder The holder of {@link SessionAdapter}
	 * @param context The context used to retrieve the localized strings
	 * @param group The parent <code>ComponentAligner</code> of the
	 * <code>ComponentAligner</code> used by this pane
	 */
	public EventListenersPane(PropertyValueModel sessionAdapterHolder,
									  WorkbenchContextHolder contextHolder)
	{
		super(new BorderLayout(), sessionAdapterHolder, contextHolder);
		addHelpTopicId(this, "session.options.eventListeners");
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
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Event Listener Classes list pane.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private ListValueModel buildEventListenerListHolder()
	{
		return new ListAspectAdapter(getSubjectHolder(), SessionAdapter.SESSION_EVENT_LISTENERS_CONFIGS_LIST)
		{
			protected ListIterator getValueFromSubject()
			{
				SessionAdapter adapter = (SessionAdapter) this.subject;
				return adapter.sessionEventListenerConfigs();
			}
		};
	}

	/**
	 * Creates a new <code>AddRemoveListPanel</code> that will shows the sessions
	 * contained in the sessions.xml.
	 *
	 * @return A new <code>AddRemoveListPanel</code>
	 */
	private AddRemoveListPanel buildEventListenerListPane()
	{
		return new CustomizedAddRemoveListPanel();
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		AddRemoveListPanel eventListenersListPanel = buildEventListenerListPane();
		eventListenersListPanel.setCellRenderer(new SimpleListCellRenderer());
		eventListenersListPanel.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("OPTIONS_EVENT_LISTENERS_LIST"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		add(eventListenersListPanel, BorderLayout.CENTER);
		addPaneForAlignment(eventListenersListPanel);
	}

	/**
	 * This <code>Adapter</code> is responsible to perform the action upon a
	 * click on either the Add or Remove buttons.
	 */
	private class AddRemoveListAdapter implements AddRemoveListPanel.Adapter
	{
		public void addNewItem(ObjectListSelectionModel listSelectionModel)
		{
			SessionAdapter session = (SessionAdapter) subject();
			SimplePropertyValueModel selectionHolder = new SimplePropertyValueModel();

			ClassChooserTools.promptForType
			(
				getWorkbenchContext(),
				session.getClassRepository(),
				selectionHolder
			);

			String eventListenerClassName = (String) selectionHolder.getValue();

			if ((eventListenerClassName != null) &&
				 ! CollectionTools.contains(session.sessionEventListenerConfigs(), eventListenerClassName))
			{
				session.addPropertyConfigNamed(eventListenerClassName);
			}
		}

		public void removeSelectedItems(ObjectListSelectionModel listSelectionModel)
		{
			SessionAdapter adapter = (SessionAdapter) subject();
			Object[] selectedValues = listSelectionModel.getSelectedValues();

			for (int index = selectedValues.length; --index >= 0;)
			{
				adapter.removePropertyConfigNamed((String) selectedValues[index]);
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
			super(EventListenersPane.this.getApplicationContext(),
					buildAddRemoveListPanelAdapter(),
					buildEventListenerListHolder(),
					AddRemoveListPanel.RIGHT);
		}

		protected String addButtonKey()
		{
			return "OPTIONS_EVENT_LISTENERS_ADD_BUTTON";
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
			return "OPTIONS_EVENT_LISTENERS_REMOVE_BUTTON";
		}
	}
}
