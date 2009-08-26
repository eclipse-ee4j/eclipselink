package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledBorder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ClassAttributeCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
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

public class AccessorEditingPanel extends AbstractPanel {

	// **************** Variables *********************************************
	
	/** Used to govern how the accessor is created */
	private AccessorSpec accessorSpec;
	
	// **************** Constructors ******************************************
	
	public AccessorEditingPanel(AccessorSpec accessorSpec, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.accessorSpec = accessorSpec;
		this.initializeLayout();
	}

	// **************** Initialization ****************************************
	
	private void initializeLayout() {
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GridBagConstraints constraints = new GridBagConstraints();

		// methods radio button
		JRadioButton methodsButton = this.buildMethodsButton();
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		this.add(methodsButton, constraints);

		// methods chooser
		JPanel methodsChooserPanel = this.buildMethodsChooserPanel();
		methodsChooserPanel.setBorder(new AccessibleTitledBorder(methodsButton.getText()));
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(5, SwingTools.checkBoxIconWidth(), 0, 0);
		this.add(methodsChooserPanel, constraints);

		// attribute radio button
		JRadioButton attributeButton = this.buildAttributeButton();
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(10, 0, 0, 0);
		this.add(attributeButton, constraints);

		// attribute chooser
		JPanel attributeChooser = this.buildAttributeChooserPanel();
		attributeChooser.setBorder(new AccessibleTitledBorder(attributeButton.getText()));
		constraints.gridx      = 0;
		constraints.gridy      = 3;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(5, SwingTools.checkBoxIconWidth(), 0, 0);
		this.add(attributeChooser, constraints);
	}
	
	private JRadioButton buildMethodsButton() {
		JRadioButton button = new JRadioButton();
		button.setModel(this.buildMethodsButtonModel());
		button.setText(this.resourceRepository().getString("ACCESSOR_PANEL_METHODS_BUTTON"));
		button.setMnemonic(this.resourceRepository().getMnemonic("ACCESSOR_PANEL_METHODS_BUTTON"));
		return button;
	}
	
	private JRadioButton buildAttributeButton() {
		JRadioButton button = new JRadioButton();
		button.setModel(this.buildAttributeButtonModel());
		button.setText(this.resourceRepository().getString("ACCESSOR_PANEL_ATTRIBUTE_BUTTON"));
		button.setMnemonic(this.resourceRepository().getMnemonic("ACCESSOR_PANEL_ATTRIBUTE_BUTTON"));
		return button;
	}
	
	private ButtonModel buildAttributeButtonModel() {
		return new RadioButtonModelAdapter(this.accessorSpec.accessorTypeHolder(),  AccessorSpec.ACCESSOR_ATTRIBUTE);
	}

	private ButtonModel buildMethodsButtonModel() {
		return new RadioButtonModelAdapter(this.accessorSpec.accessorTypeHolder(), AccessorSpec.ACCESSOR_METHODS);
	}

	private JPanel buildMethodsChooserPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		JLabel getMethodLabel = this.buildGetMethodChooserLabel();
		panel.add(getMethodLabel);
		addAlignLeft(getMethodLabel);
		
		ListChooser getMethodChooser = this.buildGetMethodChooser();
		getMethodLabel.setLabelFor(getMethodChooser);
		panel.add(getMethodChooser);

		panel.add(Box.createRigidArea(new Dimension(10, 0)));

		JLabel setMethodsLabel = this.buildSetMethodChooserLabel();
		panel.add(setMethodsLabel);
		addAlignLeft(setMethodsLabel);

		ListChooser setMethodChooser = this.buildSetMethodChooser();
		setMethodsLabel.setLabelFor(setMethodChooser);
		panel.add(setMethodChooser);

		return panel;
	}
	
	private JLabel buildGetMethodChooserLabel() {
		JLabel label = buildLabel("ACCESSOR_PANEL_GET_METHOD_CHOOSER");
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(label, AccessorSpec.ACCESSOR_METHODS)
		);
		label.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_METHODS);
		return label;
	}
	
	private ListChooser buildGetMethodChooser() {
		ListChooser chooser = new DefaultListChooser(
			this.buildGetMethodChooserModel(),
			this.getWorkbenchContextHolder(),
			this.buildMethodChooserDialogBuilder()
		);
		chooser.setRenderer(this.buildMethodRenderer());
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(chooser, AccessorSpec.ACCESSOR_METHODS)
		);
		chooser.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_METHODS);
		return chooser;
	}

	private ComboBoxModel buildGetMethodChooserModel() {
		return new ComboBoxModelAdapter(this.buildMethodChoicesValue(), this.accessorSpec.accessorGetMethodHolder());
	}
	
	private ListValueModel buildMethodChoicesValue() {
		return new SortedListValueModelAdapter(
			new ReadOnlyCollectionValueModel(
				CollectionTools.collection(this.accessorSpec.candidateAccessorMethods())
			)
		);
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
	
	private ListCellRenderer buildMethodRenderer() {
		return new AdaptableListCellRenderer(
			new MethodCellRendererAdapter(this.resourceRepository()) {
                protected Icon buildNonNullValueIcon(Object value) {
					return null;
				}
			}
		);
	}
	
	private JLabel buildSetMethodChooserLabel() {
		JLabel label = buildLabel("ACCESSOR_PANEL_SET_METHOD_CHOOSER");
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(label, AccessorSpec.ACCESSOR_METHODS)
		);
		label.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_METHODS);
		return label;
	}	

	private ListChooser buildSetMethodChooser() {
		ListChooser chooser = new DefaultListChooser(
			this.buildSetMethodChooserModel(),
			this.getWorkbenchContextHolder(),
			this.buildMethodChooserDialogBuilder()
		);
		chooser.setRenderer(this.buildMethodRenderer());
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(chooser, AccessorSpec.ACCESSOR_METHODS)
		);
		chooser.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_METHODS);
		return chooser;
	}
	
	private ComboBoxModel buildSetMethodChooserModel() {
		return new ComboBoxModelAdapter(this.buildMethodChoicesValue(), this.accessorSpec.accessorSetMethodHolder());
	}

	private PropertyChangeListener buildComponentEnablingListener(final Component component, final String enabledValue) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				component.setEnabled(evt.getNewValue() == enabledValue);
			}
		};
	}
	
	private JPanel buildAttributeChooserPanel() {
		JPanel panel = new AccessibleTitledPanel(new BorderLayout(5, 0));
		
		JLabel label = this.buildAttributeChooserLabel();
		panel.add(label, BorderLayout.LINE_START);
		addAlignLeft(label);
		
		ListChooser chooser = this.buildAttributeChooser();
		label.setLabelFor(chooser);
		panel.add(chooser, BorderLayout.CENTER);
		
		return panel;
	}

	private JLabel buildAttributeChooserLabel() {
		JLabel label = new JLabel(this.resourceRepository().getString("ACCESSOR_PANEL_ATTRIBUTE_CHOOSER"));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("ACCESSOR_PANEL_ATTRIBUTE_CHOOSER"));
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(label, AccessorSpec.ACCESSOR_ATTRIBUTE)
		);
		label.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_ATTRIBUTE);
		return label;
	}

	private ListChooser buildAttributeChooser() {
		ListChooser chooser = new DefaultListChooser(
			this.buildAttributeChooserModel(),
			this.getWorkbenchContextHolder(),
			this.buildAttributeChooserDialogBuilder()
		);
		chooser.setRenderer(this.buildAttributeRenderer());
		this.accessorSpec.accessorTypeHolder().addPropertyChangeListener(
			PropertyValueModel.VALUE, 
			this.buildComponentEnablingListener(chooser, AccessorSpec.ACCESSOR_ATTRIBUTE)
		);
		chooser.setEnabled(this.accessorSpec.accessorTypeHolder().getValue() == AccessorSpec.ACCESSOR_ATTRIBUTE);
		return chooser;
	}

	private ComboBoxModel buildAttributeChooserModel() {
		return new ComboBoxModelAdapter(this.buildAttributeChoicesValue(), this.accessorSpec.accessorAttributeHolder());
	}
	
	private ListValueModel buildAttributeChoicesValue() {
		return new SortedListValueModelAdapter(
			new ReadOnlyCollectionValueModel(
				CollectionTools.collection(this.accessorSpec.candidateAccessorAttributes())
			)
		);
	}

	private DefaultListChooserDialog.Builder buildAttributeChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setStringConverter(buildMWClassAttributeStringConverter());
		return builder;
	}

	private StringConverter buildMWClassAttributeStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWClassAttribute) o).getName();
			}
		};
	}

	private ListCellRenderer buildAttributeRenderer() {
		return new AdaptableListCellRenderer(
			new ClassAttributeCellRendererAdapter(this.resourceRepository()) {
                protected Icon buildNonNullValueIcon(Object value) {
					return null;
				}
			}
		);
	}
	
	// **************** Member classes ****************************************
	
	public static interface AccessorSpec
	{
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel accessorTypeHolder();
		
		/** The value of the above holder */
		String accessorType();
			// the allowable accessor types
			public final static String ACCESSOR_METHODS 	= "accessorMethods";
			public final static String ACCESSOR_ATTRIBUTE 		= "accessorAttribute";
			// this one is only used to be changed *from*
			// i.e. it is an absence of the other two
			public final static String NULL_ACCESSOR			= "nullAccessor";
		
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel accessorGetMethodHolder();
		
		/** The value of the above holder */
		MWMethod accessorGetMethod();
		
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel accessorSetMethodHolder();
		
		/** The value of the above holder */
		MWMethod accessorSetMethod();
		
		/** The methods that may be chosen for this accessor */
		Iterator candidateAccessorMethods();
		
		/** The attributes that may be chose for this accessor */
		Iterator candidateAccessorAttributes();
				
		/** Should return an unchanging holder (only the value may change) */
		PropertyValueModel accessorAttributeHolder();
		
		/** The value of the above holder */
		MWClassAttribute accessorAttribute();
		
	}
}
