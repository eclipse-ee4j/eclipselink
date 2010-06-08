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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledBorder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public final class TransformerEditingPanel 
	extends AbstractPanel
{
	// **************** Variables *********************************************
	
	/** Used to govern how the transformer is created */
	private TransformerSpec transformerSpec;
	
	
	// **************** Constructors ******************************************
	
	public TransformerEditingPanel(TransformerSpec transformerSpec, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.transformerSpec = transformerSpec;
		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
	
	private void initializeLayout() {
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();

		// method radio button
		JRadioButton methodButton = this.buildMethodButton();
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		this.add(methodButton, constraints);

		// method chooser
		JPanel methodChooserPanel = this.buildMethodChooserPanel();
		methodChooserPanel.setBorder(new AccessibleTitledBorder(methodButton.getText()));
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, SwingTools.checkBoxIconWidth(), 0, 0);
		this.add(methodChooserPanel, constraints);

		// class radio button
		JRadioButton classButton = this.buildClassButton();
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(10, 0, 0, 0);
		this.add(classButton, constraints);

		// class chooser
		JPanel classChooser = this.buildClassChooserPanel();
		classChooser.setBorder(new AccessibleTitledBorder(classButton.getText()));
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, SwingTools.checkBoxIconWidth(), 0, 0);
		this.add(classChooser, constraints);
	}
	
	private JRadioButton buildMethodButton() {
		JRadioButton button = new JRadioButton();
		button.setModel(this.buildMethodButtonModel());
		button.setText(this.resourceRepository().getString("TRANSFORMER_PANEL_METHOD_BUTTON"));
		button.setMnemonic(this.resourceRepository().getMnemonic("TRANSFORMER_PANEL_METHOD_BUTTON"));
		return button;
	}
	
	private ButtonModel buildMethodButtonModel() {
		return new RadioButtonModelAdapter(this.transformerSpec.transformerTypeHolder(), TransformerSpec.TRANSFORMATION_METHOD);
	}
	
	private JPanel buildMethodChooserPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 0));
		
		JLabel methodLabel = this.buildMethodChooserLabel();
		panel.add(methodLabel, BorderLayout.LINE_START);
		addAlignLeft(methodLabel);
		
		ListChooser methodChooser = this.buildMethodChooser();
		methodLabel.setLabelFor(methodChooser);
		panel.add(methodChooser, BorderLayout.CENTER);

		Spacer spacer = new Spacer();
		panel.add(spacer, BorderLayout.LINE_END);
		addAlignRight(spacer);

		return panel;
	}
	
	private JLabel buildMethodChooserLabel() {
		JLabel label = buildLabel("TRANSFORMER_PANEL_METHOD_CHOOSER");
		this.transformerSpec.transformerTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(label, TransformerSpec.TRANSFORMATION_METHOD)
		);
		label.setEnabled(this.transformerSpec.transformerTypeHolder().getValue() == TransformerSpec.TRANSFORMATION_METHOD);
		return label;
	}
	
	private ListChooser buildMethodChooser() {
		ListChooser chooser = new DefaultListChooser(
			this.buildMethodChooserModel(),
			this.getWorkbenchContextHolder(),
			this.buildMethodChooserDialogBuilder()
		);
		chooser.setRenderer(this.buildMethodRenderer());
		this.transformerSpec.transformerTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(chooser, TransformerSpec.TRANSFORMATION_METHOD)
		);
		chooser.setEnabled(this.transformerSpec.transformerTypeHolder().getValue() == TransformerSpec.TRANSFORMATION_METHOD);
		return chooser;
	}
	
	private DefaultListChooserDialog.Builder buildMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setStringConverter(buildMWMethodStringConverter());
		return builder;
	}

	private StringConverter buildMWMethodStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWMethod) o).shortSignatureWithReturnType();
			}
		};
	}

	private ComboBoxModel buildMethodChooserModel() {
		return new ComboBoxModelAdapter(this.buildMethodChoicesValue(), this.transformerSpec.transformationMethodHolder());
	}
	
	private ListValueModel buildMethodChoicesValue() {
		return new SortedListValueModelAdapter(
			new ReadOnlyCollectionValueModel(
				CollectionTools.collection(this.transformerSpec.candidateTransformationMethods())
			)
		);
	}
	
	private ListCellRenderer buildMethodRenderer() {
		return new AdaptableListCellRenderer(
			new MethodCellRendererAdapter(this.resourceRepository()) {
                protected Icon buildNonNullValueIcon(Object value) {
					return null;
				}
			}
		);
	}
	
	private JRadioButton buildClassButton() {
		JRadioButton button = new JRadioButton();
		button.setModel(this.buildClassButtonModel());
		button.setText(this.resourceRepository().getString("TRANSFORMER_PANEL_CLASS_BUTTON"));
		button.setMnemonic(this.resourceRepository().getMnemonic("TRANSFORMER_PANEL_CLASS_BUTTON"));
		return button;
	}
	
	private ButtonModel buildClassButtonModel() {
		return new RadioButtonModelAdapter(this.transformerSpec.transformerTypeHolder(), TransformerSpec.TRANSFORMER_CLASS);
	}
	
	private JPanel buildClassChooserPanel() {
		JPanel panel = new AccessibleTitledPanel(new BorderLayout(5, 0));
		
		JLabel label = this.buildClassChooserLabel();
		panel.add(label, BorderLayout.LINE_START);
		addAlignLeft(label);
		
		ClassChooserPanel chooser = this.buildClassChooserPanel2(label);
		panel.add(chooser, BorderLayout.CENTER);
		addPaneForAlignment(chooser);
		
		return panel;
	}
	
	private JLabel buildClassChooserLabel() {
		JLabel label = new JLabel(this.resourceRepository().getString("TRANSFORMER_PANEL_CLASS_CHOOSER"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("TRANSFORMER_PANEL_CLASS_CHOOSER"));
		this.transformerSpec.transformerTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(label, TransformerSpec.TRANSFORMER_CLASS)
		);
		label.setEnabled(this.transformerSpec.transformerTypeHolder().getValue() == TransformerSpec.TRANSFORMER_CLASS);
		return label;
	}
	
	private ClassChooserPanel buildClassChooserPanel2(JLabel label) {
		ClassChooserPanel chooserPanel = ClassChooserTools.buildPanel(
						this.transformerSpec.transformerClassHolder(),
						this.buildClassRepositoryHolder(),
						ClassChooserTools.buildDeclarableNonVoidFilter(),
						label,
						this.getWorkbenchContextHolder()
		);
		this.transformerSpec.transformerTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(chooserPanel, TransformerSpec.TRANSFORMER_CLASS)
		);
		chooserPanel.setEnabled(this.transformerSpec.transformerTypeHolder().getValue() == TransformerSpec.TRANSFORMER_CLASS);
		return chooserPanel;
	}
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return TransformerEditingPanel.this.transformerSpec.classRepository();
			}
		};
	}
	
	private PropertyChangeListener buildComponentEnablingListener(final Component component, final String enabledValue) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == enabledValue);
			}
		};
	}
	
	
	// **************** Member classes ****************************************
	
	public static interface TransformerSpec
	{
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel transformerTypeHolder();
		
		/** The value of the above holder */
		String transformerType();
			// the allowable transformer types
			public final static String TRANSFORMATION_METHOD 	= "transformationMethod";
			public final static String TRANSFORMER_CLASS 		= "transformerClass";
			// this one is only used to be changed *from*
			// i.e. it is an absence of the other two
			public final static String NULL_TRANSFORMER			= "nullTransformer";
		
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel transformationMethodHolder();
		
		/** The value of the above holder */
		MWMethod transformationMethod();
		
		/** The methods that may be chosen for this transformer */
		Iterator candidateTransformationMethods();
		
		/** Return whether the method signature is valid for a transformation method */
		boolean transformationMethodIsValid();
		
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel transformerClassHolder();
		
		/** The value of the above holder */
		MWClass transformerClass();
		
		/** The class repository from which transformer classes may be chosen */
		MWClassRepository classRepository();
		
	}
}
