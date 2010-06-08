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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.ListCellRenderer;
import javax.swing.border.TitledBorder;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.Nominative;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.project.RenameDialog;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This abstract implementation is intended to show the list of {@link SessionAdapter}s
 * contained in a {@link TopLinkSessionsAdapter} or contained in a
 * {@link SessionBrokerAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * ______________________________________________________
 * |                                                    |
 * | Sessions for '{0}':                                |
 * | _______________________________ __________________ |
 * | | MySession1 (Database)     |^| | Add Session... | |
 * | | MySession2 (Server)       | | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | |                           ||| __________________ |
 * | |                           ||| |    Remove...   | |
 * | |                           ||| ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | |                           ||| __________________ |
 * | |                           ||| |    Rename...   | |
 * | |                           | | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | |                           |v|                    |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯                    |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Known subclasses of this pane:<br>
 * - {@link org.eclipse.persistence.tools.workbench.scplugin.ui.broker.SessionsListPane}<br>
 * - {@link org.eclipse.persistence.tools.workbench.scplugin.ui.project.SessionsListPane}
 *
 * @see SessionAdapter
 * @see SessionBrokerAdapter
 * @see TopLinkSessionsAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public abstract class AbstractSessionsListPane extends AbstractSubjectPanel
{
	/**
	 * Creates a new <code>AbstractSessionsListPane</code>.
	 *
	 * @param subjectHolder The holder of a {@link Nominative}
	 * @param contextHolder The holder of the context to be used by this pane
	 */
	protected AbstractSessionsListPane(PropertyValueModel subjectHolder,
												  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Requests the subclass to add a new session.
	 */
	protected abstract void addNewSession();

	/**
	 * Creates the <code>ListCellRenderer</code> for the {@link AddRemoveListPanel}
	 * in order to decorates the items of the list; which are instance of
	 * {@link SessionAdapter}.
	 *
	 * @return A new <code>ListCellRenderer</code>
	 */
	private ListCellRenderer buidlAddRemoveListCellRenderer()
	{
		return new AdaptableListCellRenderer
		(
			new SessionCellRendererAdapter(resourceRepository())
		);
	}

	/**
	 * Creates the adapter required to update the underlying model when an item
	 * need to be added or remove. This add or remove a {@link TopLinkSessionsAdapter}.
	 *
	 * @return A new <code>AddRemoveListPanel.Adapter</code>
	 */
	protected AddRemoveListPanel.Adapter buildAddRemoveListPanelAdapter()
	{
		return new AddRemoveListAdapter();
	}

	/**
	 * Creates the <code>PropertyValueModel</code> that will be responsible to
	 * listens to a display string change.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	protected PropertyValueModel buildDisplayStringHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), Displayable.DISPLAY_STRING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				Nominative nominative = (Nominative) subject;
				return nominative.getName();
			}
		};
	}

	/**
	 * Creates the <code>PropertyChangeListener</code> that will be responsible
	 * to update the given titled border when the value changes.
	 *
	 * @param The border to update updated when the property changes
	 * @return A new <code>PropertyChangeListener</code>
	 */
	protected PropertyChangeListener buildDisplayStringListener(final TitledBorder border)
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				if (e.getNewValue() != null)
				{
					border.setTitle(resourceRepository().getString("PROJECT_SESSIONS_LIST", e.getNewValue()));
					repaint();
				}
			}
		};
	}

	/**
	 * Creates a new <code>AddRemoveListPanel</code> that will shows the sessions
	 * contained in the sessions.xml.
	 *
	 * @return A new <code>AddRemoveListPanel</code>
	 */
	protected AddRemoveListPanel buildSessionListPane()
	{
		return new CustomizedSessionListPanel();
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Sessions list.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	protected abstract CollectionValueModel buildSessionsCollectionHolder();

	/**
	 * Creates <code>ListValueModel</code> that will keep the rendering up to
	 * date when the item's displayable property has changed. For
	 * {@link SessionAdapter}, only its name is its displayable property.
	 *
	 * @return A new <code>ItemPropertyListValueModelAdapter</code>
	 */
	private ItemPropertyListValueModelAdapter buildSessionsItemListAdapter()
	{
		return new ItemPropertyListValueModelAdapter(buildSessionsCollectionHolder(),
																	SessionAdapter.NAME_PROPERTY);
	}

	/**
	 * Creates the <code>SortedListValueModelAdapter</code> responsible to keep
	 * the underlying list always sorted.
	 *
	 * @return A new <code>SortedListValueModelAdapter</code>
	 */
	private SortedListValueModelAdapter buildSortedSessionsListHolder()
	{
		return new SortedListValueModelAdapter(buildSessionsItemListAdapter());
	}

	/**
	 * Determines whether if the given collection of {@link SessionAdapter}s can
	 * be removed from the list.
	 *
	 * @param sessions The collection of {@link SessionAdapter}s that have been
	 * selected to be removed
	 * @return <code>true<code> if they can be removed; <code>false<code>
	 * otherwise
	 */
	protected boolean canRemoveSessions(Collection sessions)
	{
		return true;
	}

	/**
	 * Initializes the layout of this pane.
	 */
	protected void initializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Titled border
		TitledBorder border = new TitledBorder("");
		PropertyValueModel displayStringAdapter = buildDisplayStringHolder();
		displayStringAdapter.addPropertyChangeListener(PropertyValueModel.VALUE, buildDisplayStringListener(border));
		border.setTitle(resourceRepository().getString("PROJECT_SESSIONS_LIST", displayStringAdapter.getValue()));

		setBorder(BorderFactory.createCompoundBorder
		(
			border,
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));

		// Sessions list
		AddRemoveListPanel interfacesListPanel = buildSessionListPane();
		interfacesListPanel.setCellRenderer(buidlAddRemoveListCellRenderer());

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		add(interfacesListPanel, constraints);
		addPaneForAlignment(interfacesListPanel);
	}

	/**
	 * Removes the <code>SessionAdapter</code>s contained in the given collection.
	 *
	 * @param sessions The {@link SessionAdapter}s to be removed from their parent
	 */
	protected abstract void removeSessions(Collection sessions);

	/**
	 * Rename the selected session.
	 *
	 * @param session The session to be renamed
	 */
	protected void renameSession(SessionAdapter session)
	{
		TopLinkSessionsAdapter sessions = (TopLinkSessionsAdapter) session.getParent();
		SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();
		stringHolder.setValue(session.getName());

		RenameDialog dialog = new RenameDialog(getWorkbenchContext(), stringHolder, sessions.getAllSessionsNames());
		dialog.setVisible(true);

		if (dialog.wasConfirmed())
		{
			session.setName((String) stringHolder.getValue());
		}
	}

	/**
	 * The adapter used to perform the action when either the Add or Remove
	 * button is triggered.
	 */
	protected class AddRemoveListAdapter implements AddRemoveListPanel.OptionAdapter
	{
		/**
		 * Invoked when the user selects the Add button.
		 *
		 * @param listSelectionModel The model containing the selected items
		 */
		public void addNewItem(ObjectListSelectionModel listSelectionModel)
		{
			addNewSession();
		}

		/**
		 * Resource string key for the optional button.
		 *
		 * @return "PROJECT_SESSIONS_RENAME_BUTTON"
		 */
		public String optionalButtonKey()
		{
			return "PROJECT_SESSIONS_RENAME_BUTTON";
		}

		/**
		 * Invoked when the user selects the optional button.
		 *
		 * @param listSelectionModel The model containing the selected item
		 */
		public void optionOnSelection(ObjectListSelectionModel selectionModel)
		{
			renameSession((SessionAdapter) selectionModel.getSelectedValue());
		}
		
		public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
			return listSelectionModel.getSelectedValuesSize() == 1;
		}

		/**
		 * Invoked when the user selects the Remove button.
		 *
		 * @param listSelectionModel The model containing the selected items
		 */
		public void removeSelectedItems(ObjectListSelectionModel listSelectionModel)
		{
			Collection sessions = CollectionTools.collection(listSelectionModel.getSelectedValues());

			if (canRemoveSessions(sessions))
				removeSessions(sessions);
		}			
	}

	/**
	 * This subclass will allow any subclass of <code>AbstractSessionsListPane</code>
	 * to have access to the Add button in order to use a
	 * {@link org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler}.
	 */
	protected class CustomizedSessionListPanel extends AddRemoveListPanel
	{
		protected CustomizedSessionListPanel()
		{
			super(AbstractSessionsListPane.this.getApplicationContext(),
					buildAddRemoveListPanelAdapter(),
					buildSortedSessionsListHolder(),
					AddRemoveListPanel.RIGHT);
		}

		protected String addButtonKey()
		{
			return "PROJECT_SESSIONS_ADD_BUTTON";
		}
		
		protected String removeButtonKey()
		{
			return "PROJECT_SESSIONS_REMOVE_BUTTON";
		}
	}
}
