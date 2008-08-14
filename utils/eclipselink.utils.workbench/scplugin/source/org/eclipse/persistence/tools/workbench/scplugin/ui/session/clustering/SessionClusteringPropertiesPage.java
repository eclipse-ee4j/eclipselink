/*
 * @(#)SessionClusteringPropertiesPage.java
 *
 * Copyright 2004 by Oracle Corporation,
 * 500 Oracle Parkway, Redwood Shores, California, 94065, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Oracle Corporation.
 */
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.clustering;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.framework.uitools.TriStateBooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.RemoteCommandManagerAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.SessionNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.BooleanCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentVisibilityEnabler;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;

/**
 * This page shows the Clustering combo box with three possible selections.
 * However, to have Cache Synchronization available, the property had to be in
 * sessions.xml when it was read.
 * <p>
 * By changing the Clustering type, the panel underneith will be changed to
 * reflect the information for the new choice.
 * <p>
 * Here the layout:<pre>
 * _________________________________________
 * |                                       |
 * | x Enable Clustering                   |
 * |               _______________________ |  _________________________
 * |   Clustering: |                   |v| |<-| Remote Command        |
 * |               ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |  | Cache Synchronization |
 * |   ----------------------------------- |  ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * |   |                                 | |
 * |   | {@link RemoteCommandManagerPane}        | |
 * |   |  or                             | |
 * |   | {@link CacheSynchronizationManagerPane} | |
 * |   |                                 | |
 * |   ----------------------------------- |
 * |                                       |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 * Note: The Clustering combo box is only shown if the edited session supports
 * Cache Synchronization.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SessionClusteringPropertiesPage extends ScrollablePropertiesPage
{
	/**
	 * Creates a new <code>SessionClusteringPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link SessionNode}
	 */
	public SessionClusteringPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>CollectionValueModel</code> containing the actual items
	 * to be shown in the Clustering combo box.
	 *
	 * @return The <code>CollectionValueModel</code> containing the items
	 */
	private CollectionValueModel buildClusteringCollectionHolder()
	{
		return new CollectionAspectAdapter(getSelectionHolder(), null)
		{
			protected Iterator getValueFromSubject()
			{
				return new ArrayIterator(new Object[] { TriStateBoolean.UNDEFINED, TriStateBoolean.TRUE, TriStateBoolean.FALSE});
			}
		};
	}

	/**
	 * Creates the <code>ComboBoxModel</code> that keeps the selected item in the
	 * combo box in sync with the value in the model and vice versa.
	 *
	 * @return The model showing two choices: "Remote Command (True)" and "Cache
	 * Synchronization (False)"
	 */
	private ComboBoxModel buildClusteringComboBoxAdapter()
	{
		return new ComboBoxModelAdapter(buildClusteringCollectionHolder(),
												  buildClusteringTypeSelectionHolder());
	}

	/**
	 * Creates the decorator responsible to format the <code>Boolean</code>
	 * values in the Clustering combo box.
	 * 
	 * @return {@link SessionClusteringPropertiesPage.BooleanLabelDecorator}
	 */
	private CellRendererAdapter buildClusteringLabelDecorator()
	{
		ResourceRepository resourceRepository = resourceRepository();

		return new TriStateBooleanCellRendererAdapter(resourceRepository){
			@Override
			protected String trueResourceKey() {
				return "REMOTE_COMMAND";
			}
			@Override
			protected String falseResourceKey() {
				return "CACHE_SYNCHRONIZATION";
			}
			@Override
			protected String undefinedResourceKey() {			
				return "DEFAULT_CLUSTERING_TYPE";
			}
		};
	}

	/**
	 * Creates the <code>SwitcherPanel</code> ...
	 *
	 * @return A new <code>SwitcherPanel</code>
	 */
	private SwitcherPanel buildClusteringSwitcherPanel()
	{
		return new SwitcherPanel(buildClusteringTypeHolder(),
										 buildClusteringTypeTransformer());
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Clustering Type property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildClusteringTypeHolder()
	{
		String[] propertyNames = new String[]
		{
			SessionAdapter.REMOTE_COMMAND_MANAGER_CONFIG_PROPERTY,
		};

		return new PropertyAspectAdapter(getSelectionHolder(), propertyNames)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter session = (SessionAdapter) subject;

				if (session.hasRemoteCommandManager())
					return session.getRemoteCommandManager();

				return null;
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to listen to
	 * changes made to the type of clustering to be used, which is either Remote
	 * Command Manager or Cache Synchronization.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildClusteringTypeSelectionHolder()
	{
		String[] propertyNames = new String[]
		{
			SessionAdapter.REMOTE_COMMAND_MANAGER_CONFIG_PROPERTY,
		};

		return new PropertyAspectAdapter(getSelectionHolder(), propertyNames)
		{
			protected Object getValueFromSubject()
			{
				SessionAdapter adapter = (SessionAdapter) subject;

				if (adapter.hasNoClusteringService())
					return TriStateBoolean.UNDEFINED;

				return TriStateBoolean.valueOf(adapter.hasRemoteCommandManager());
			}

			protected void setValueOnSubject(Object value)
			{
				SessionAdapter adapter = (SessionAdapter) subject;

				if (TriStateBoolean.TRUE.equals(value))
				{
					adapter.setClusteringToRemoteCommandManager();
				}
				else if (TriStateBoolean.FALSE.equals(value) || TriStateBoolean.UNDEFINED.equals(value))
				{
					adapter.setClusteringToNothing();
				}
			}
		};
	}

	/**
	 * Creates the <code>Transformer</code> responsible to convert the Clustering
	 * type into the corresponding <code>Component</code>.
	 *
	 * @return A new <code>Transformer</code>
	 */
	private Transformer buildClusteringTypeTransformer()
	{
		// Create the choices used to convert an object to a JComponent
		final Object[] items = new Object[]
		{
			new RemoteCommandManagerChoice(),
		};

		return new Transformer()
		{
			public Object transform(Object value)
			{
				SessionAdapter session = (SessionAdapter) selection();

				if ((value == null) || (session == null))
					return null;

				Transformer choice;
				choice = (Transformer) items[0];			
				return choice.transform(value);
			}
		};
	}

	/**
	 * Initializes the layout of the Clustering sub-panel.
	 *
	 * @return The container with all its widgets
	 */
	private JPanel buildInternalPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());

		// Create Clustering label
		JComponent clusteringWidgets = buildLabeledComboBox
		(
			"CLUSTERING_CLUSTERING_COMBO_BOX",
			buildClusteringComboBoxAdapter(),
			new AdaptableListCellRenderer(buildClusteringLabelDecorator())
		);
		clusteringWidgets.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(clusteringWidgets, constraints);
		addHelpTopicId(clusteringWidgets, "session.clustering");

		// Create the sub-panel container
		SwitcherPanel clusteringPaneContainer = buildClusteringSwitcherPanel();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.BOTH;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		panel.add(clusteringPaneContainer, constraints);

		return panel;
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @return The container with all its widgets
	 */
	protected Component buildPage()
	{

		GridBagConstraints constraints = new GridBagConstraints();
		int offset = SwingTools.checkBoxIconWidth();

		// Create the container
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Create internal pane
		
		JPanel internalPane = buildInternalPage();

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.BOTH;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, offset, 0, 0);

		panel.add(internalPane, constraints);

		addHelpTopicId(this, "session.clustering");
		return panel;
	}

	/**
	 * This is one of the choice for Clustering Service that is used by
	 * {@link #buildClusteringTypeHolder()}.
	 */
	private class RemoteCommandManagerChoice implements Transformer
	{
		/**
		 * The pane containing Remote Command Manager specific information.
		 */
		private RemoteCommandManagerPane pane;

		/**
		 * Creates the <code>PropertyValueModel</code> that will be the subject
		 * holder for this pane. The subject will be a {@link RemoteCommandManagerAdapter}.
		 *
		 * @return A new <code>PropertyValueModel</code> listening for change of
		 * Remote Command Manager.
		 */
		private PropertyValueModel buildRemoteCommandManagerHolder()
		{
			return new PropertyAspectAdapter(getSelectionHolder(), SessionAdapter.REMOTE_COMMAND_MANAGER_CONFIG_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					SessionAdapter adapter = (SessionAdapter) subject;
					return adapter.getRemoteCommandManager();
				}
			};
		}

		/**
		 * Based on the given object, requests the associated component.
		 *
		 * @param value The value used to retrieve a pane
		 * @return {@link RemoteCommandManagerPane}
		 */
		public Object transform(Object value)
		{
			if (this.pane == null)
			{
				this.pane = new RemoteCommandManagerPane
				(
					buildRemoteCommandManagerHolder(),
					getWorkbenchContextHolder()
				);

				addPaneForAlignment(this.pane);
			}

			return this.pane;
		}
	}
}