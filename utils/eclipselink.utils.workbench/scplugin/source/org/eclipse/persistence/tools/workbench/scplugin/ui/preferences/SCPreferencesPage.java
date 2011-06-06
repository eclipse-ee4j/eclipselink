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
package org.eclipse.persistence.tools.workbench.scplugin.ui.preferences;

// JDK
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.ClasspathPanel;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.SCClassRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.adapters.PreferencePropertyValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.DefaultChangeNotifier;


/**
 * Preferences page for general settings used by the Sessions Configuration.
 *
 * @author Pascal Filion
 * @version 10.1.3
 */
final class SCPreferencesPage extends AbstractPanel
{
	/**
	 * The model object that contains the classpath entries retrieved from the
	 * preferences.
	 */
	private CustomizedClassRepository repository;

	/**
	 * Creates a new <code>SCPreferencesPage</code>.
	 *
	 * @param context
	 */
	SCPreferencesPage(PreferencesContext context)
	{
		super(new BorderLayout(), context);
		intializeLayout();
		addHelpTopicId(this, "preferences.sessions.general");
	}

	private PropertyValueModel buildClasspathHolder()
	{
		PropertyValueModel valueModel = new BufferedPropertyValueModel
		(
			buildClasspathHolderImp(),
			getPreferencesContext().getBufferTrigger()
		);

		// Add a fake listener so that all the other listeners can be engaged
		valueModel.addPropertyChangeListener(PropertyValueModel.VALUE, new PropertyChangeListener() { public void propertyChange(PropertyChangeEvent e) {} });

		return valueModel;
	}

	private PropertyValueModel buildClasspathHolderImp()
	{
		return new PreferencePropertyValueModel(preferences(), SCPlugin.DEFAULT_CLASSPATH_PREFERENCE) {
			protected Object getValueFromSubject() {
				String classpath = (String)super.getValueFromSubject();
				if ("".equals(classpath)) {
					return null;
				} else {
					return classpath;
				}
				
			}
		};
	}

	/**
	 * Initializes the layout of this pane.
	 */
	private void intializeLayout()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel container = new JPanel(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(container);
		scrollPane.getVerticalScrollBar().setBlockIncrement(20);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		this.repository = new CustomizedClassRepository();
		PropertyValueModel classpathHolder = buildClasspathHolder();
		this.repository.update(classpathHolder);

		// Classpath
		ClasspathPanel classpathPanel = new ClasspathPanel
		(
			getApplicationContext(),
			new ClasspathListModel(this.repository, classpathHolder),
			true,
			"PREFERENCES_DEFAULT_CLASSPATH_GROUP_BOX"
		);

		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, 5, 5, 5);

		container.add(classpathPanel, constraints);
	}

	/**
	 * The model given to the classpath panel, this model keeps the preference
	 * holder up to date with any changes made to the classpath panel.
	 */
	private class ClasspathListModel extends ListAspectAdapter
	{
		private final PropertyValueModel preferenceHolder;

		private ClasspathListModel(SCClassRepository repository,
											PropertyValueModel preferenceHolder)
		{
			super(SCClassRepository.CLASSPATH_ENTRIES_LIST,
					repository);

			this.preferenceHolder = preferenceHolder;
		}

		public void addItem(int index, Object item)
		{
			SCClassRepository classpath = (SCClassRepository) subject;
			classpath.addClasspathEntry(index, (String) item);
			this.preferenceHolder.setValue(classpath.entries());
		}

		public Object getItem(int index)
		{
			SCClassRepository classpath = (SCClassRepository) subject;
			return classpath.getClasspathEntry(index);
		}

		protected ListIterator getValueFromSubject()
		{
			SCClassRepository classpath = (SCClassRepository) subject;
			return classpath.classpathEntries();
		}

		public Object removeItem(int index)
		{
			SCClassRepository classpath = (SCClassRepository) subject;
			Object entry = classpath.removeClasspathEntry(index);
			this.preferenceHolder.setValue(classpath.entries());
			return entry;
		}

		public Object replaceItem(int index, Object item)
		{
			SCClassRepository classpath = (SCClassRepository) subject;
			Object entry = classpath.replaceClasspathEntry(index, (String) item);
			this.preferenceHolder.setValue(classpath.entries());
			return entry;
		}
	}

	/**
	 * This <code>ClassRepository</code> serves as the gate between the
	 * Classpath panel and the preferences value model.
	 */
	private class CustomizedClassRepository extends SCClassRepository
	{
		private CustomizedClassRepository()
		{
			super(new String[0]);
		}

		public Validator getValidator()
		{
			return NULL_VALIDATOR;
		}

		//Not sure if this should even descend from SCClassRepository since that is a regular
		//model object and making this descend from it tries to tie it into the change notification system.
		//But overriding this method at least stops the exceptions this caused before noted in bug 4649391.
		public ChangeNotifier getChangeNotifier() {
			return DefaultChangeNotifier.instance();
		}

		public void update(PropertyValueModel classpathHolder)
		{
			String value = (String) classpathHolder.getValue();
			if (value == null) {
				return;
			}
			String entries[] = value.split(System.getProperty("path.separator"));

			if (entries.length == 0)
				return;

			List classpathEntries = CollectionTools.list(entries);
			CollectionTools.removeAll(classpathEntries, classpathEntries());

			if (!classpathEntries.isEmpty())
				addClasspathEntries(classpathEntriesSize(), classpathEntries);
		}
	}
}
