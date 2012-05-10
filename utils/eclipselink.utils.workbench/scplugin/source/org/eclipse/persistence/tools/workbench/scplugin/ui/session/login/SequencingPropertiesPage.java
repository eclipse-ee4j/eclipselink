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
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.Adapter;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DefaultSequenceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.NativeSequenceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SequenceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TableSequenceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.UnaryTableSequenceAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.DatabaseSessionNode;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.NewSequenceDialog.NewSequenceBuilder;
import org.eclipse.persistence.tools.workbench.scplugin.ui.session.login.NewSequenceDialog.SequenceStateObject;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullTransformer;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyCollectionValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

// Mapping Workbench

/**
 * This pane shows the information regarding {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter
 * DatabaseLoginAdapter}.
 * 
 * @see DatabaseSessionTabbedPane - The parent of this pane
 * @see DefaultSequencePropertyPane
 * @see NativeSequencePropertyPane
 * @see TableSequencePropertyPane
 * @see UnaryTableSequencePropertyPane
 * @see XMLFileSequencePropertyPane
 *
 * @version 11.1.0
 * @author Pascal Filion
 */
public class SequencingPropertiesPage extends AbstractLoginPropertiesPage
{
	private DefaultSequencePropertyPane defaultSequencePane;
	private NativeSequencePropertyPane nativeSequencePane;
	private TableSequencePropertyPane tableSequencePane;
	private UnaryTableSequencePropertyPane unaryTableSequencePane;

	/**
	 * A constant associated with the default custom table sequence type.
	 */
	public static String CUSTOM_TABLE_SEQUENCE = "CustomTableSequence";

	/**
	 * A constant associated with the default default sequence type.
	 */
	public static String DEFAULT_TABLE_SEQUENCE = "DefaultTableSequence";

	/**
	 * A constant associated with the default Native sequence type.
	 */
	public static String NATIVE_TABLE_SEQUENCE = "NativeTableSequence";

	/**
	 * Creates a new <code>RdbmsSequencingPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of {@link DatabaseSessionNode}
	 */
	public SequencingPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates this panel along with its widgets.
	 *
	 * @return The fully initialized pane
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 2;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		//	Sequences list pane
		AddRemoveListPanel listPane = buildSequenceListPane();
		PropertyValueModel selectedSequenceHolder = listPane.getSelectedItemHolder();

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.VERTICAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(15, 0, 0, 0);

		container.add(listPane, constraints);

		// Sequence properties pane
		defaultSequencePane    = buildDefaultSequencePane(selectedSequenceHolder);
		nativeSequencePane     = buildNativeSequencePane(selectedSequenceHolder);
		tableSequencePane      = buildTableSequencePane(selectedSequenceHolder);
		unaryTableSequencePane = buildUnaryTableSequencePane(selectedSequenceHolder);

		SwitcherPanel sequencePane = new SwitcherPanel(selectedSequenceHolder, buildSequencePaneTransformer());

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(15, 10, 0, 0);

		container.add(sequencePane, constraints);
		
		addHelpTopicId(container, "session.login.sequencing");
		return container;
	}

	private Transformer buildSequencePaneTransformer()
	{
		return new Transformer()
		{
			public Component transform(Object sequence)
			{
				if (sequence == null)
				{
					return null;
				}

				if (((SequenceAdapter)sequence).getType() == SequenceType.DEFAULT)
				{
					return defaultSequencePane;
				}

				if (((SequenceAdapter)sequence).getType() == SequenceType.NATIVE)
				{
					return nativeSequencePane;
				}

				if (((SequenceAdapter)sequence).getType() == SequenceType.TABLE)
				{
					return tableSequencePane;
				}

				if (((SequenceAdapter)sequence).getType() == SequenceType.UNARY_TABLE)
				{
					return unaryTableSequencePane;
				}

				return null;
			}
		};
	}

	private ValueModel buildTableSequenceHolder(ValueModel selectedSequenceHolder)
	{
		return new TransformationValueModel(selectedSequenceHolder)
		{
			@Override
			protected TableSequenceAdapter transformNonNull(Object value)
			{
				return (((SequenceAdapter)value).getType() == SequenceType.TABLE) ? (TableSequenceAdapter) value : null;
			}
		};
	}

	private TableSequencePropertyPane buildTableSequencePane(ValueModel selectedSequenceHolder)
	{
		return new TableSequencePropertyPane
		(
			buildTableSequenceHolder(selectedSequenceHolder),
			getWorkbenchContextHolder()
		);
	}

	private ValueModel buildUnaryTableSequenceHolder(ValueModel selectedSequenceHolder)
	{
		return new TransformationValueModel(selectedSequenceHolder)
		{
			@Override
			protected UnaryTableSequenceAdapter transformNonNull(Object value)
			{
				return (((SequenceAdapter)value).getType() == SequenceType.UNARY_TABLE) ? (UnaryTableSequenceAdapter) value : null;
			}
		};
	}

	private UnaryTableSequencePropertyPane buildUnaryTableSequencePane(ValueModel selectedSequenceHolder)
	{
		return new UnaryTableSequencePropertyPane
		(
			buildUnaryTableSequenceHolder(selectedSequenceHolder),
			getWorkbenchContextHolder()
		);
	}

	private ValueModel buildNativeSequenceHolder(ValueModel selectedSequenceHolder)
	{
		return new TransformationValueModel(selectedSequenceHolder)
		{
			@Override
			protected NativeSequenceAdapter transformNonNull(Object value)
			{
				return (((SequenceAdapter)value).getType() == SequenceType.NATIVE) ? (NativeSequenceAdapter) value : null;
			}
		};
	}

	private NativeSequencePropertyPane buildNativeSequencePane(ValueModel selectedSequenceHolder)
	{
		return new NativeSequencePropertyPane
		(
			buildNativeSequenceHolder(selectedSequenceHolder),
			getWorkbenchContextHolder()
		);
	}

	private ValueModel buildDefaultSequenceHolder(ValueModel selectedSequenceHolder)
	{
		return new TransformationValueModel(selectedSequenceHolder)
		{
			@Override
			protected DefaultSequenceAdapter transformNonNull(Object value)
			{
				return (((SequenceAdapter)value).getType() == SequenceType.DEFAULT) ? (DefaultSequenceAdapter) value : null;
			}
		};
	}

	private DefaultSequencePropertyPane buildDefaultSequencePane(ValueModel selectedSequenceHolder)
	{
		return new DefaultSequencePropertyPane
		(
			buildDefaultSequenceHolder(selectedSequenceHolder),
			getWorkbenchContextHolder()
		);
	}

	private AddRemoveListPanel buildSequenceListPane()
	{
		AddRemoveListPanel panel = new AddRemoveListPanel
		(
			getApplicationContext(),
			buildSequenceListAdapter(),
			buildSortedSequenceListHolder()
		) {
			@Override
			protected void updateRemoveButton(JButton removeButton) {
				SequenceAdapter sequence = (SequenceAdapter)this.getSelectionModel().getSelectedValue();
		        removeButton.setEnabled((sequence != null)&&(getSelectedValues().length == 1)&&(!sequence.isTheDefaultSequence()));
			}
		};
		panel.setCellRenderer(buildSequenceListRenderer());
		return panel;
		
	}
	
	private ListCellRenderer buildSequenceListRenderer() {
		return new SimpleListCellRenderer() {
		
			protected String buildText(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
				SequenceAdapter sequence = (SequenceAdapter)value;
				StringBuilder sb = new StringBuilder();		
				boolean hasName = !StringTools.stringIsEmpty(sequence.getName());

				if (hasName)
				{
					sb.append(sequence.getName());
				}
				else if (!sequence.isTheDefaultSequence())
				{
					sb.append(resourceRepository().getString("SEQUENCING_PANE_SEQUENCE_NO_NAME", index));
				}
				else
				{
					sb.append(resourceRepository().getString("SEQUENCING_PANE_DEFAULT_SEQUENCE_TEXT"));
				}
		
				StringBuilder sequenceType = new StringBuilder();
				sequenceType.append("SEQUENCING_PANE_SEQUENCE_TYPE_");
				sequenceType.append(sequence.getType().name());
		
				sb.append(' ');
				sb.append('(');
				sb.append(resourceRepository().getString(sequenceType.toString()));
				sb.append(')');
		
				if (hasName && sequence.isTheDefaultSequence())
				{
					sb.append(' ');
					sb.append(resourceRepository().getString("SEQUENCING_PANE_DEFAULT_SEQUENCE_TEXT"));
				}
		
				return sb.toString();
			}};

	}

	private CollectionValueModel buildAllSequenceCollectionHolder()
	{
		CollectionValueModel collectionHolders =
			new SimpleCollectionValueModel();

		collectionHolders.addItem(buildSequenceCollectionHolder());
		collectionHolders.addItem(buildDefaultSequenceCollectionHolder());

		return new CompositeCollectionValueModel
		(
			collectionHolders,
			NullTransformer.instance()
		);
	}

	private CollectionValueModel buildDefaultSequenceCollectionHolder()
	{
		return new PropertyCollectionValueModelAdapter
		(
			buildDefaultCollectionHolder()
		);
	}

	private PropertyValueModel buildDefaultCollectionHolder()
	{
		return new PropertyAspectAdapter(getSelectionHolder(), LoginAdapter.DEFAULT_SEQUENCE_PROPERTY)
		{
			@Override
			protected SequenceAdapter getValueFromSubject()
			{
				return ((LoginAdapter)subject).getDefaultSequence();
			}
		};
	}

	private CollectionValueModel buildSequenceCollectionHolder()
	{
		return new ListCollectionValueModelAdapter
		(
			buildSequenceListHolder()
		);
	}

	private ListValueModel buildSequenceListHolder()
	{
		return new ListAspectAdapter(getSelectionHolder(), LoginAdapter.SEQUENCES_COLLECTION)
		{
			@Override
			protected ListIterator getValueFromSubject()
			{
				return ((LoginAdapter)subject).sequences();
			}

			@Override
			protected int sizeFromSubject()
			{
				return ((LoginAdapter)subject).sequencesSize();
			}
		};
	}

	private ListValueModel buildSortedSequenceListHolder()
	{
		return new SortedListValueModelAdapter
		(
			buildAllSequenceCollectionHolder()
		);
	}

	private LoginAdapter login() {
		return (LoginAdapter)selection();
	}
	
	private void editSequence(ObjectListSelectionModel listSelectionModel)
	{
		SequenceAdapter sequence = (SequenceAdapter) listSelectionModel.getSelectedValue();

		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setTextFieldDescription(resourceRepository().getString("SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_LABEL"));
		builder.setTitle(resourceRepository().getString("SEQUENCING_PANE_EDIT_SEQUENCE_DIALOG_TITLE"));
		builder.setHelpTopicId("dialog.sequence");
		builder.setExistingNames(login().sequenceNames());
		builder.setOriginalName(sequence.getName());
		builder.setOriginalNameIsLegal(false);
		builder.setEmptyNameIsLegal(sequence.isDefault());

		NewNameDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed())
		{
			String name = dialog.getNewName().trim();
			sequence.setName(name);
			listSelectionModel.setSelectedValue(sequence);
		}

	}
	
	private void addSequence(ObjectListSelectionModel listSelectionModel)
	{
		NewSequenceBuilder builder = new NewSequenceBuilder(resourceRepository());
		builder.addExistingNames(login().sequenceNames());

		NewSequenceDialog dialog = builder.buildDialog(getWorkbenchContext());
		dialog.show();
		if (dialog.wasConfirmed())
		{
			SequenceStateObject stateObject = (SequenceStateObject)dialog.subject();
			SequenceAdapter sequence;

			if (stateObject.isDefaultSequence())
			{
				sequence = login().createAndSetDefaultSequence
				(
					stateObject.getName(),
					stateObject.getSequenceType()
				);
			}
			else
			{
				sequence = login().addSequence
				(
					stateObject.getName(),
					stateObject.getSequenceType()
				);
			}

			listSelectionModel.setSelectedValue(sequence);
		}
	}
	


	private Adapter buildSequenceListAdapter()
	{
		return new AddRemoveListPanel.OptionAdapter()
		{
			public void addNewItem(ObjectListSelectionModel listSelectionModel)
			{
				addSequence(listSelectionModel);
			}

			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel)
			{
				return listSelectionModel.getSelectedValuesSize() == 1;
			}

			public String optionalButtonKey()
			{
				return "SEQUENCING_PANE_EDIT_BUTTON";
			}

			public void optionOnSelection(ObjectListSelectionModel listSelectionModel)
			{
				editSequence(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel)
			{
				for (Object sequence : listSelectionModel.getSelectedValues())
				{
					login().removeSequence((SequenceAdapter) sequence);
				}
			}
		};
	}
}
