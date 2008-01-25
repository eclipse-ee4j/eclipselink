/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.session.login;

// JDK
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.LoginAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SCAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SequenceAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

// Mapping Workbench

/**
 * This pane shows the information regarding {@link org.eclipse.persistence.tools.workbench.scplugin.model.adapter.DatabaseLoginAdapter
 * DatabaseLoginAdapter}.
 * <p>
 * Here the layout:
 * <pre>
 * _____________________________________________
 * |                       ____________        |
 * |  Preallocation Size:  |        |I|        |<- If Native Sequencing is checked,
 * |                       ¯¯¯¯¯¯¯¯¯¯¯¯        |   and the database platform is not
 * |                                           |   Oracle, then it is disabled
 * |   o Default Sequence Table                |
 * |                                           |
 * |   o Native Sequencing                     |<- Only enabled for Oracle,
 * |                                           |   SQL Server, Sybase, Informix
 * | _ o Custom Sequence Table________________ |
 * | |                      ________________ | |
 * | | Table:               | I            | | |
 * | |                      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ | |
 * | |                      ________________ | |
 * | | Name Field:          | I            | | |
 * | |                      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ | |
 * | |                      ________________ | |
 * | | Counter Field:       | I            | | |
 * | |                      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 * 
 *
 * @see DatabaseLoginAdapter
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class RdbmsSequencingPropertiesPage extends AbstractLoginPropertiesPage
{
	/**
	 * This model is used to transform 3 properties into a boolean property.
	 */
	private PseudoPreallocationSizeModel pseudoModel;

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
	public RdbmsSequencingPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Counter Field value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildCounterFieldDocumentAdapter()
	{
		return new DocumentAdapter(buildCounterFieldHolder(),
											new RegexpDocument(RegexpDocument.RE_SQL_RELATED));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Counter Field property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildCounterFieldHolder()
	{
		return new PropertyAspectAdapter(buildCustomTableSequenceHolder(), LoginAdapter.SEQUENCE_COUNTER_FIELD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				return login.getSequenceCounterField();
			}

			protected void setValueOnSubject(Object value)
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				login.setSequenceCounterField((String) value);
			}
		};
	}

	/**
	 * Creates a new <code>PropertyValueModel</code> that will only returns the
	 * default sequence if the sequence type is default custom sequence.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildCustomTableSequenceHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.DEFAULT_SEQUENCE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return login.getDefaultSequence();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				SequenceAdapter sequence = (SequenceAdapter) value;

				if (sequence.isCustom())
					return sequence;

				return null;
			}
		};
	}

	/**
	 * Creates a new <code>PropertyValueModel</code> that will handle the default
	 * sequence.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildDefaultSequenceHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.DEFAULT_SEQUENCE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return login.getDefaultSequence();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				DatabaseLoginAdapter login = (DatabaseLoginAdapter) ((SCAdapter)value).getParent().getParent();
				return login;
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Name Field value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildNameFieldDocumentAdapter()
	{
		return new DocumentAdapter(buildNameFieldHolder(),
											new RegexpDocument(RegexpDocument.RE_SQL_RELATED));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Name Field property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildNameFieldHolder()
	{
		return new PropertyAspectAdapter(buildCustomTableSequenceHolder(), DatabaseLoginAdapter.SEQUENCE_NAME_FIELD_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				return login.getSequenceNameField();
			}

			protected void setValueOnSubject(Object value)
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				login.setSequenceNameField((String) value);
			}
		};
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the components contained in the given collection in sync with the
	 * boolean value calculated by the enabled state holder.
	 *
	 * @return A new <code>ComponentEnabler</code>
	 */
	private ComponentEnabler buildNativeSequencingEnabler(JComponent component)
	{
		return new ComponentEnabler(buildNativeSequencingEnableStateHolder(), Collections.singleton(component));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * enabled state of the Preallocation Size widgets.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildNativeSequencingEnableStateHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), DatabaseLoginAdapter.PLATFORM_CLASS_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return login.getPlatformClass();
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				try
				{
				String platformClass = (String) value;

				DatabasePlatform platform =
					DatabasePlatformRepository.getDefault().platformForRuntimePlatformClassNamed(platformClass);

				return Boolean.valueOf(platform.supportsNativeSequencing());
			}
				catch (IllegalArgumentException e)
				{
					return Boolean.FALSE;
				}
			}
		};
	}

	/**
	 * Creates this panel along with its widgets.
	 *
	 * @return The fully initialized pane
	 */
	protected Component buildPage()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		PropertyValueModel sequencingTypeHolder = buildSequencingTypeHolder();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Preallocation Size label
		JComponent preallocationSizeWidgets = buildLabeledSpinnerNumber
		(
			"LOGIN_PREALLOCATION_SIZE_SPINNER",
			buildPreallocationSizeSpinnerAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 5, 0, 0);

		container.add(preallocationSizeWidgets, constraints);
		buildPreallocationSizeEnabler(preallocationSizeWidgets);
		addHelpTopicId(preallocationSizeWidgets, "session.login.database.sequencing.preallocation");

		// Default Sequence Table radio button
		JRadioButton defaultSequenceTableRadioButton = buildRadioButton
		(
			"LOGIN_DEFAULT_SEQUENCE_TABLE_RADIO_BUTTON",
			buildSequenceTypeRadioButtonAdapter(sequencingTypeHolder, DEFAULT_TABLE_SEQUENCE)
		);

		addHelpTopicId(defaultSequenceTableRadioButton, "session.login.database.sequencing.default");

		// Native Sequencing radio button
		JRadioButton nativeSequencingRadioButton = buildRadioButton
		(
			"LOGIN_NATIVE_SEQUENCING_RADIO_BUTTON",
			buildSequenceTypeRadioButtonAdapter(sequencingTypeHolder, NATIVE_TABLE_SEQUENCE)
		);

		buildNativeSequencingEnabler(nativeSequencingRadioButton);
		addHelpTopicId(nativeSequencingRadioButton, "session.login.database.sequencing.native");

		// Custom Sequence Table radio button
		JRadioButton tableSequenceTableRadioButton = buildRadioButton
		(
			"LOGIN_CUSTOM_SEQUENCE_TABLE_RADIO_BUTTON",
			buildSequenceTypeRadioButtonAdapter(sequencingTypeHolder, CUSTOM_TABLE_SEQUENCE)
		);
		addHelpTopicId(tableSequenceTableRadioButton, "session.login.database.sequencing.table");

		// Table Sequencing sub-pane
		JComponent tableSequencingPanel = buildTableSequencingPane(tableSequenceTableRadioButton);
		addHelpTopicId(tableSequencingPanel, "session.login.database.sequencing.table");

		// Add the widgets to the container
		GroupBox groupBox = new GroupBox
		(
			defaultSequenceTableRadioButton,
			nativeSequencingRadioButton,
			tableSequenceTableRadioButton,
			tableSequencingPanel
		);

		constraints.gridx       = 0;
		constraints.gridy       = 3;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 1;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(10, 0, 0, 0);

		container.add(groupBox, constraints);

		addHelpTopicId(container, "session.login.database.sequencing");
		return container;
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the components contained in the given collection in sync with the
	 * boolean value calculated by the enabled state holder.
	 *
	 * @param widgets The widgets to keep their enable state in sync with the
	 * aspect of the enable state holder
	 * @return A new <code>ComponentEnabler</code>
	 */
	private ComponentEnabler buildPreallocationSizeEnabler(JComponent widgets)
	{
		return new ComponentEnabler(buildPreallocationSizeEnableStateHolder(),
											 Collections.singleton(widgets));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * enabled state of the Preallocation Size widgets.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPreallocationSizeEnableStateHolder()
	{
		return new PropertyAspectAdapter("enabled", pseudoModel)
		{
			protected Object getValueFromSubject()
			{
				PseudoPreallocationSizeModel model = (PseudoPreallocationSizeModel) subject;
				return Boolean.valueOf(model.isEnabled());
			}
		};
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Preallocation Size property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildPreallocationSizeHolder()
	{
		return new PropertyAspectAdapter(buildDefaultSequenceHolder(), DatabaseLoginAdapter.SEQUENCE_PREALLOCATION_SIZE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return new Integer(login.getSequencePreallocationSize());
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				login.setSequencePreallocationSize(((Integer) value).intValue());
			}
		};
	}

	/**
	 * Creates the <code>SpinnerModel</code> that keeps the value from the
	 * spinner in sync with the Preallocation Size value in the model and vice
	 * versa.
	 * 
	 * @return A new <code>SpinnerNumberModel</code>
	 */
	private SpinnerNumberModel buildPreallocationSizeSpinnerAdapter()
	{
		return new NumberSpinnerModelAdapter(buildPreallocationSizeHolder(), 0, Integer.MAX_VALUE, 1);
	}

	/**
	 * Creates the <code>PropertyChangeListener</code> to keep the parent object
	 * of the {@link PseudoPreallocationSizeModel} in sync with the selection
	 * object.
	 *
	 * @return A new <code>PropertyChangeListener</code>
	 */
	private PropertyChangeListener buildSelectionHolderListener ()
	{
		return new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent e)
			{
				pseudoModel.setParentNode((AbstractNodeModel) e.getNewValue());
			}
		};
	}

	/**
	 * Creates a new <code>ButtonModel</code> responsible to handle the Native
	 * Sequencing property.
	 *
	 * @param nativeSequencingHolder The value holder
	 * @param buttonValue The value used to discriminate between different
	 * radio buttons, which acts like a ButtonBroup
	 * @return A new <code>ButtonModel</code>
	 */
	private ButtonModel buildSequenceTypeRadioButtonAdapter(PropertyValueModel nativeSequencingHolder,
																			  Object buttonValue)
	{
		return new RadioButtonModelAdapter(nativeSequencingHolder, buttonValue);
	}
	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Native Sequencing property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildSequencingTypeHolder()
	{
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(getSelectionHolder(), LoginAdapter.DEFAULT_SEQUENCE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;
				return login.getDefaultSequence();
			}

			protected void setValueOnSubject(Object value)
			{
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) subject;

				if ((value == DEFAULT_TABLE_SEQUENCE) &&
					 !login.sequencingIsDefault())
				{
					login.setDefaultTableSequenceTable();
				}
				else if ((value == CUSTOM_TABLE_SEQUENCE)  &&
							!login.sequencingIsCustom())
				{
					login.setCustomTableSequence();
				}
				else if ((value == NATIVE_TABLE_SEQUENCE) &&
							!login.sequencingIsNative())
				{
					login.setNativeSequencing();
				}
			}
		};

		return new TransformationPropertyValueModel(adapter)
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				SequenceAdapter sequence = (SequenceAdapter)value;

				if ( sequence.isDefault())
					return DEFAULT_TABLE_SEQUENCE;

				else if ( sequence.isCustom())
					return CUSTOM_TABLE_SEQUENCE;

				else if ( sequence.isNative())
					return NATIVE_TABLE_SEQUENCE;

				throw new IllegalArgumentException(value + " is unknown");
			}
		};
	}

	/**
	 * Creates the <code>DocumentAdapter</code> that keeps the value from the
	 * text field in sync with the Table value in the model and vice versa.
	 *
	 * @return A new <code>Document</code>
	 */
	private Document buildTableDocumentAdapter()
	{
		return new DocumentAdapter(buildTableHolder(),
											new RegexpDocument(RegexpDocument.RE_SQL_RELATED));
	}

	/**
	 * Creates the <code>PropertyValueModel</code> responsible to handle the
	 * Table property.
	 *
	 * @return A new <code>PropertyValueModel</code>
	 */
	private PropertyValueModel buildTableHolder()
	{
		return new PropertyAspectAdapter(buildCustomTableSequenceHolder(), DatabaseLoginAdapter.SEQUENCE_TABLE_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				return login.getSequenceTable();
			}

			protected void setValueOnSubject(Object value)
			{
				SequenceAdapter sequence = (SequenceAdapter) subject;
				DatabaseLoginAdapter login = (DatabaseLoginAdapter) sequence.getParent().getParent();
				login.setSequenceTable((String) value);
			}
		};
	}

	/**
	 * Creates the pane that contains the table sequencing information. See
	 * {@link RdbmsSequencingPropertiesPage} for the layout.
	 *
	 * @param titleComponent The title shown above this pane
	 * @return The fully initialized pane
	 */
	private JComponent buildTableSequencingPane(JComponent titleComponent)
	{
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the container
		JPanel container = new JPanel(new GridBagLayout());

		// Table widgets
		Component tableWidgets = buildLabeledTextField
		(
			"LOGIN_TABLE_NAME_FIELD",
			buildTableDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);

		container.add(tableWidgets, constraints);

		// Name Field widgets
		Component nameFieldWidgets = buildLabeledTextField
		(
			"LOGIN_NAME_FIELD_FIELD",
			buildNameFieldDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		container.add(nameFieldWidgets, constraints);

		// Counter Field widgets
		Component counterFieldWidgets = buildLabeledTextField
		(
			"LOGIN_COUNTER_FIELD_FIELD",
			buildCounterFieldDocumentAdapter()
		);

		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(5, 0, 0, 0);

		container.add(counterFieldWidgets, constraints);

		// Create the object responsible to keep the enable state of the String
		// Size label and spinner in sync with the underlying model
		buildTableSequencingPaneEnabler(container.getComponents());

		return container;
	}

	/**
	 * Creates the <code>ComponentEnabler</code> that will keep the enable state
	 * of the components contained in the given collection in sync with the
	 * boolean value calculated by the enabled state holder.
	 *
	 * @param tableSequencingHolder The holder of the Native Sequencing property,
	 * which it is used in reversed (Table Sequencing is handled)
	 * @param components The list of components where their enable state will be
	 * updated when necessary
	 * @return A new <code>ComponentEnabler</code>
	 */
	private ComponentEnabler buildTableSequencingPaneEnabler(Component[] components)
	{
		// This will transform the local constant into a Boolean value
		PropertyValueModel booleanHolder = new TransformationPropertyValueModel(buildCustomTableSequenceHolder())
		{
			protected Object transform(Object value)
			{
				return Boolean.valueOf(value != null);
			}
		};

		return new ComponentEnabler(booleanHolder, components);
	}

	/**
	 * Initializes this <code>RdbmsSequencingPropertiesPage</code>.
	 *
	 * @param nodeHolder The holder of the subject, which is <code>DatabaseLoginAdapter</code>
	 */
	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);

		getSelectionHolder().addPropertyChangeListener(PropertyValueModel.VALUE, buildSelectionHolderListener());

		pseudoModel = new PseudoPreallocationSizeModel();
		pseudoModel.setParentNode((AbstractNodeModel) selection());
	}

	/**
	 * This pseudo model helps to translate 3 properties from the real model
	 * object, {@link DatabaseLoginAdapter} into a boolean property.
	 */
	private class PseudoPreallocationSizeModel extends AbstractNodeModel
	{
		private boolean enabled;
		private PropertyChangeListener listener;

		private PseudoPreallocationSizeModel()
		{
			super();
		}

		private PropertyChangeListener buildPropertyChangeListener()
		{
			return new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent e)
				{
					updateEnableState();
				}
			};
		}

		protected void checkParent(Node parent)
		{
		}

		private void disengageListeners()
		{
			AbstractNodeModel parent = (AbstractNodeModel) getParent();

			if (parent != null)
			{
				parent.removePropertyChangeListener(DatabaseLoginAdapter.DEFAULT_SEQUENCE_PROPERTY, listener);
				parent.removePropertyChangeListener(DatabaseLoginAdapter.SEQUENCES_COLLECTION, listener);
				parent.removePropertyChangeListener(DatabaseLoginAdapter.PLATFORM_CLASS_PROPERTY, listener);
			}
		}

		public String displayString()
		{
			return null;
		}

		private void engageListeners()
		{
			AbstractNodeModel parent = (AbstractNodeModel) getParent();

			if (parent != null)
			{
				parent.addPropertyChangeListener(DatabaseLoginAdapter.DEFAULT_SEQUENCE_PROPERTY, listener);
				parent.addPropertyChangeListener(DatabaseLoginAdapter.SEQUENCES_COLLECTION, listener);
				parent.addPropertyChangeListener(DatabaseLoginAdapter.PLATFORM_CLASS_PROPERTY, listener);
			}
		}

		protected void initialize()
		{
			super.initialize();
			listener = buildPropertyChangeListener();
		}

		public boolean isEnabled()
		{
			return enabled;
		}

		public void setEnabled(boolean enabled)
		{
			boolean oldEnabled = isEnabled();
			this.enabled = enabled;
			firePropertyChanged("enabled", oldEnabled, enabled);
		}

		public void setParentNode(Node parentNode)
		{
			disengageListeners();
			super.setParent(parentNode);
			engageListeners();

			if (parentNode != null)
				updateEnableState();
		}

		private void updateEnableState()
		{
			DatabaseLoginAdapter login = (DatabaseLoginAdapter) getParent();
			String platformClass = login.getPlatformClass();
			boolean oraclePlatform = (platformClass != null);

			// Verify if the Database Platform is Oracle
			if (oraclePlatform)
			{
				try
				{
					DatabasePlatform platform =
						DatabasePlatformRepository.getDefault().platformForRuntimePlatformClassNamed(platformClass);

				oraclePlatform = platform.getName().toLowerCase().indexOf("oracle") != -1;
			}
				catch (IllegalArgumentException e)
				{
					oraclePlatform = false;
				}
			}

			// Preallocation Size is enabled only if the platform is Oracle when
			// Native Sequencing is selected
			if (login.usesNativeSequencing())
			{
				setEnabled(oraclePlatform);
			}
			// Always enabled for any other case
			else
			{
				setEnabled(true);
			}
		}
	}
}