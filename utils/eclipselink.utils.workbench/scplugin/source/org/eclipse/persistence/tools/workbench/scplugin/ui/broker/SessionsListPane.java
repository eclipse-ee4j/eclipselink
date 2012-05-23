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
package org.eclipse.persistence.tools.workbench.scplugin.ui.broker;

// JDK
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.AbstractSessionsListPane;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.SessionCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;

// Mapping Workbench

/**
 * This pane shows the list of {@link SessionsAdapter}s contained in a
 * {@link SessionBrokerAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _______________________________
 * |                             |
 * |  {@link AbstractSessionsListPane}   |
 * |                             |
 * -------------------------------</pre>
 *
 * @see SessionBrokerAdapter
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
final class SessionsListPane extends AbstractSessionsListPane
{
	/**
	 * Creates a new <code>AbstractSessionsListPane</code>.
	 *
	 * @param subjectHolder The holder of {@link SessionBrokerAdapter}
	 * @param context
	 */
	SessionsListPane(PropertyValueModel subjectHolder,
						  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Requests to add unmanaged sessions to the edited session brokers.
	 */
	protected void addNewSession()
	{
		CollectionValueModel itemHolder = buildUnmanagedSessionsCollectionHolder();
		ObjectListSelectionModel selectionModel = buildSelectionModel(itemHolder);

		// Show the list of unmanaged Sessions
		SessionsListDialog dialog = new SessionsListDialog
		(
			getWorkbenchContext(),
			itemHolder,
			selectionModel,
			buildLabelDecorator()
		);

		dialog.setVisible(true);

		if (dialog.wasCanceled())
			return;

		// Add the selected Sessions to the Session Broker
		Iterator iter = CollectionTools.iterator(selectionModel.getSelectedValues());

		while (iter.hasNext())
		{
			SessionBrokerAdapter sessionBroker = (SessionBrokerAdapter) subject();
			SessionAdapter session = (SessionAdapter) iter.next();
			sessionBroker.manage(session.getName());
		}
	}

	/**
	 * Creates a new <code>LabelDecorator</code> that decorates {@link SessionAdapter}s.
	 *
	 * @return A new {@link SessionLabelDecorator}
	 */
	private CellRendererAdapter buildLabelDecorator()
	{
		return new SessionCellRendererAdapter(resourceRepository());
	}

	/**
	 * Creates the <code>ObjectListSelectionModel</code> responsible to keep the
	 * selected items from the list.
	 *
	 * @return A new <code>ObjectListSelectionModel</code>
	 */
	private ObjectListSelectionModel buildSelectionModel(CollectionValueModel itemHolder)
	{
		ListModelAdapter adapter = new ListModelAdapter(itemHolder);
		return new ObjectListSelectionModel(adapter);
	}

	/**
	 * Creates a new <code>AddRemoveListPanel</code> that will shows the sessions
	 * contained in the sessions.xml.
	 *
	 * @return A new <code>AddRemoveListPanel</code>
	 */
	protected AddRemoveListPanel buildSessionListPane()
	{
		AddRemoveListPanel panel = super.buildSessionListPane();
		registerListenerToUpdateAddButtonEnableState(panel);
		return panel;
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Sessions list.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	protected CollectionValueModel buildSessionsCollectionHolder()
	{
		return new CollectionAspectAdapter(getSubjectHolder(), SessionBrokerAdapter.SESSIONS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				return ((SessionBrokerAdapter) subject).sessions();
			}

			protected int sizeFromSubject()
			{
				return ((SessionBrokerAdapter) subject).sessionsSize();
			}
		};
	}

	/**
	 * Creates a new <code>CollectionValueModel</code> where the items are the
	 * unmanaged sessions (excluding session brokers and managed sessions).
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	private CollectionValueModel buildUnmanagedSessionsCollectionHolder()
	{
		Iterator iterator = buildUnmanagedSessionsIterator();
		return new ReadOnlyCollectionValueModel(CollectionTools.collection(iterator));
	}

	/**
	 * Creates an <code>Iterator</code> that will return one by one each of the
	 * unmanaged sessions excluding {@link SessionBrokerAdapter}s and managed
	 * {@link SessionAdapter}s.
	 *
	 * @return A new <code>Iterator</code> when the object are an instance of
	 * {@link SessionAdapter}
	 */
	private Iterator buildUnmanagedSessionsIterator()
	{
		SessionBrokerAdapter broker = (SessionBrokerAdapter) subject();
		TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) broker.getParent();
		return topLinkSessions.databaseSessions();
	}

	/**
	 * Installs a listener that will be notified when the collection of sessions
	 * contained by the TopLink Sessions has changed. Upon change, the enable
	 * state of the Add button will be updated.
	 *
	 * @param panel The panel showing the list of sessions
	 */
	private void registerListenerToUpdateAddButtonEnableState(final AddRemoveListPanel panel)
	{
		// Holder of the SessionBrokerAdapter's parent: TopLinkSessionsAdapter
		PropertyAspectAdapter parentHolder = new PropertyAspectAdapter(getSubjectHolder(), "")
		{
			protected Object getValueFromSubject()
			{
				SessionBrokerAdapter broker = (SessionBrokerAdapter) subject;
				return broker.getParent();
			}
		};

		// Holder of the sessions collection
		CollectionAspectAdapter collectionHolder = new CollectionAspectAdapter(parentHolder, TopLinkSessionsAdapter.SESSIONS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) subject;
				return topLinkSessions.databaseSessions();
			}
		};

		// Listen to any change in the collection and update the enable state of the Add button
		collectionHolder.addCollectionChangeListener(CollectionAspectAdapter.VALUE, new CollectionChangeListener()
		{
			public void collectionChanged(CollectionChangeEvent e)
			{
				updateAddButtonEnableState(panel);
			}

			public void itemsAdded(CollectionChangeEvent e)
			{
				updateAddButtonEnableState(panel);
			}

			public void itemsRemoved(CollectionChangeEvent e)
			{
				updateAddButtonEnableState(panel);
			}
		});

		updateAddButtonEnableState(panel);
	}

	/**
	 * Removes the <code>SessionAdapter</code>s contained in the given collection.
	 *
	 * @param sessions The {@link SessionAdapter}s to be removed from the edited
	 * session broker
	 */
	protected void removeSessions(Collection sessions)
	{
		SessionBrokerAdapter broker = (SessionBrokerAdapter) subject();
		broker.unManage(sessions);
	}

	/**
	 * Updates
	 *
	 * @param panel
	 */
	private void updateAddButtonEnableState(AddRemoveListPanel panel)
	{
		SessionBrokerAdapter broker = (SessionBrokerAdapter) subject();

		if (broker != null)
		{
			TopLinkSessionsAdapter topLinkSessions = (TopLinkSessionsAdapter) broker.getParent();
			boolean enabled = (topLinkSessions.databaseSessionsSize() > 0);
			panel.setAddButtonEnabled(enabled);
		}
	}
}
