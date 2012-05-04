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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

// JDK
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.Nominative;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionBrokerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.AbstractSessionsListPane;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;

// Mapping Workench

/**
 * This pane shows the list of unmanaged {@link DatabaseSessionAdapter}s or of
 * {@link SessionBrokerAdapter}s contained in a {@link TopLinkSessionsAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * __________________________________________________________
 * |                                                        |
 * | _Sessions for 'sessions.xml'__________________________ |
 * | | _______________________________ __________________ | |
 * | | | MySession1 (Database)     |^| | Add Session... | | |
 * | | | MySession2 (Server)       | | ------------------ | |
 * | | |                           | | __________________ | |
 * | | |                           ||| | Add Broker...  | | |
 * | | |                           ||| ------------------ | |
 * | | |                           ||| __________________ | |
 * | | |                           ||| |    Remove...   | | |
 * | | |                           ||| ------------------ | |
 * | | |                           ||| __________________ | |
 * | | |                           | | |    Rename...   | | |
 * | | |                           | | ------------------ | |
 * | | |                           |v|                    | |
 * | | -------------------------------                    | |
 * | ------------------------------------------------------ |
 * ----------------------------------------------------------</pre>
 *
 * @see TopLinkSessionsAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
final class SessionsListPane extends AbstractSessionsListPane
{
	/**
	 * Creates a new <code>AbstractSessionsListPane</code>.
	 *
	 * @param subjectHolder The holder of {@link TopLinkSessionsAdapter}
	 * @param context
	 */
	SessionsListPane(PropertyValueModel subjectHolder,
						  WorkbenchContextHolder contextHolder)
	{
		super(subjectHolder, contextHolder);
	}

	/**
	 * Add a new database or server session.
	 */
	protected void addNewSession()
	{
		AddNewSessionAction action = new AddNewSessionAction(getWorkbenchContext(), false);
		action.execute();
	}

	/**
	 * Adds a new session broker.
	 */
	protected void addNewSessionBroker()
	{
		AddNewBrokerAction action = new AddNewBrokerAction(getWorkbenchContext(), false);
		action.execute();
	}

	/**
	 * Creates a new <code>AddRemoveListPanel</code> that will shows the sessions
	 * contained in the sessions.xml.
	 *
	 * @return A new <code>AddRemoveListPanel</code>
	 */
	protected AddRemoveListPanel buildSessionListPane()
	{
		return new ExtendedSessionListPanel();
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing all the items to
	 * be shown in the Sessions list.
	 *
	 * @return A new <code>CollectionValueModel</code>
	 */
	protected CollectionValueModel buildSessionsCollectionHolder()
	{
		return new CollectionAspectAdapter(getSubjectHolder(), TopLinkSessionsAdapter.SESSIONS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				TopLinkSessionsAdapter adapter = (TopLinkSessionsAdapter) subject;
				return adapter.sessions();
			}	

			protected int sizeFromSubject()
			{
				TopLinkSessionsAdapter adapter = (TopLinkSessionsAdapter) subject;
				return adapter.sessionsSize();
			}
		};
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
		Nominative nominative = (Nominative) subject();
		String name = nominative.getName();
		int confirmation;
		Vector brokers = retrieveSessionBrokerWithManagedSessions(sessions);

		if (!brokers.isEmpty())
		{
			if (brokers.size() == 1)
			{
				SessionBrokerAdapter broker = (SessionBrokerAdapter) brokers.firstElement();

				JOptionPane.showMessageDialog
				(
					currentWindow(),
					resourceRepository().getString("DELETE_BROKER_MESSAGE", broker.displayString()),
					resourceRepository().getString("DELETE_BROKER_TITLE"),
					JOptionPane.WARNING_MESSAGE
				);
			}
			else
			{
				JOptionPane.showMessageDialog
				(
					currentWindow(),
					resourceRepository().getString("DELETE_BROKERS_MESSAGE"),
					resourceRepository().getString("DELETE_BROKER_TITLE"),
					JOptionPane.WARNING_MESSAGE
				);
			}

			confirmation = JOptionPane.NO_OPTION;
		}
		else if (sessions.size() == 1)
		{
			SessionAdapter session = (SessionAdapter) sessions.iterator().next();

			confirmation = JOptionPane.showConfirmDialog
			(
				currentWindow(),
				resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_SINGLE", session.displayString(), name),
				resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_SINGLE_TITLE"),
				JOptionPane.YES_NO_OPTION
			);
		}
		else
		{
			confirmation = JOptionPane.showConfirmDialog
			(
				currentWindow(),
				resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_MULTI", name),
				resourceRepository().getString("PROJECT_SESSIONS_PROMPT_REMOVE_MULTI_TITLE"),
				JOptionPane.YES_NO_OPTION
			);
		}

		return (confirmation == JOptionPane.OK_OPTION);
	}

	/**
	 * Verifies if in the given collection of {@link SessionAdapter}s, there is
	 * at least one {@link SessionBrokerAdapter} with managed sessions.
	 *
	 * @param sessions The {@link SessionAdapter}s to be verified
	 * @return <code>true<code> if there is a {@link SessionBrokerAdapter} in the
	 * list with managed session; <code>false<code> otherwise
	 */
	private Vector retrieveSessionBrokerWithManagedSessions(Collection sessions)
	{
		Vector brokers = new Vector();

		for (Iterator iter = sessions.iterator(); iter.hasNext();)
		{
			SessionAdapter session = (SessionAdapter) iter.next();

			if (session.isBroker())
			{
				SessionBrokerAdapter broker = (SessionBrokerAdapter) session;

				if (broker.sessionsSize() > 0)
					brokers.add(broker);
			}
		}

		return brokers;
	}

	/**
	 * Removes the <code>SessionAdapter</code>s contained in the given collection.
	 *
	 * @param sessions The {@link SessionAdapter}s to be removed from their parent
	 */
	protected void removeSessions(Collection sessions)
	{
		TopLinkSessionsAdapter adapter = (TopLinkSessionsAdapter) subject();
		adapter.removeSessions(sessions);
	}

	/**
	 * This extended <code>AddRemoveListPanel</code> simply reverse the order of
	 * the Remove and Optional buttons.
	 */
	private class ExtendedSessionListPanel extends CustomizedSessionListPanel
	{
		/**
		 * Create the <code>ActionListener</code> responsible to add a new broker.
		 *
		 * @return A new <code>ActionListener</code>
		 */
		private ActionListener buildAddBrokerAction()
		{
			return new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addNewSessionBroker();
				}
			};
		}

		/**
		 * Creates the button responsible to add a new session broker.
		 *
		 * @return A new <code>JButton</code>
		 */
		protected JButton buildAddBrokerButton()
		{
			JButton button = buildButton("PROJECT_SESSIONS_BROKER_ADD_BUTTON");
			button.addActionListener(buildAddBrokerAction());
			return button;
		}

		/**
		 * Creates a container that will properly lay out the given buttons, the
		 * layout will be created once this method has added all its widgets, this
		 * will let any subclass the ability to add or reorder the buttons.
		 *
		 * @param addButton The button used to add a new item
		 * @param removeButton The button used to remove the selected items
		 * @param optionalButton A button that can perform additional operation
		 * over the selected item
		 * @return A new container containing the given widgets
		 */
		protected void buildButtonPanel(JComponent buttonPanel,
												  JButton addButton,
												  JButton removeButton,
												  JButton optionalButton)
		{
			buttonPanel.add(addButton);
			buttonPanel.add(buildAddBrokerButton());
			buttonPanel.add(removeButton);
			buttonPanel.add(optionalButton);
		}
	}
}
