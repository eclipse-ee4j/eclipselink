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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.RegexpDocument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.TypeDeclarationCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;


/**
 * This panel is used on the ExpressionBuilderDialog
 * If the user choose to create a literal argument for their expression, this panel is shown.
 * They must then edit the literal
 */
final class LiteralArgumentPanel extends ArgumentPanel 
{
	private JTextField literalTextField;
	private PropertyValueModel typeHolder;
	
	LiteralArgumentPanel(PropertyValueModel argumentHolder, WorkbenchContextHolder contextHolder, Collection enablingComponents) {
		super(argumentHolder, contextHolder);
		initialize(enablingComponents);
	}
	
	private void initialize(Collection enablingComponents)
	{
		GridBagConstraints constraints = new GridBagConstraints();		
		
		JLabel typeLabel = new JLabel(resourceRepository().getString("TYPE_LABEL:"));
		typeLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("TYPE_LABEL:"));
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(typeLabel, constraints);	
		enablingComponents.add(typeLabel);

		JComboBox typeChooser = buildTypeChooser();
		typeLabel.setLabelFor(typeChooser);

		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(typeChooser, constraints);	
		enablingComponents.add(typeChooser);


		JLabel valueLabel = new JLabel(resourceRepository().getString("VALUE_LABEL:"));
		valueLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("VALUE_LABEL:"));
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(valueLabel, constraints);	
		enablingComponents.add(valueLabel);

		this.literalTextField = buildLiteralTextField();
		constraints.gridx		= 1;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.NORTHWEST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		valueLabel.setLabelFor(this.literalTextField);
		add(this.literalTextField, constraints);	
		enablingComponents.add(this.literalTextField);

	}
	// *************** type *************
	
	private JComboBox buildTypeChooser() {
		JComboBox chooser = new JComboBox(buildAttributeTypeComboBoxModel());
		chooser.setRenderer(new AdaptableListCellRenderer(new TypeDeclarationCellRendererAdapter(resourceRepository())));
		return chooser;
	}

	private ComboBoxModel buildAttributeTypeComboBoxModel() {
	    this.typeHolder = buildTypeHolder();
		return new ComboBoxModelAdapter(buildTypesCollectionHolder(), this.typeHolder); 
	}

	
	private CollectionValueModel buildTypesCollectionHolder() {
		return new CollectionAspectAdapter(getArgumentHolder()) {
			protected Iterator getValueFromSubject() {
				return ((MWLiteralArgument) this.subject).buildBasicTypes().iterator();
			}
		};
	}

	private PropertyValueModel buildTypeHolder() {
	    return new PropertyAspectAdapter(getArgumentHolder(), MWLiteralArgument.TYPE_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWLiteralArgument) this.subject).getLiteralType();
            }
            protected void setValueOnSubject(Object value) {
               ((MWLiteralArgument) this.subject).setType((MWTypeDeclaration) value);
            }
	    };
	}
	
	private JTextField buildLiteralTextField() {
		JTextField textField = new JTextField(0);
		final PropertyValueModel literalHolder = buildLiteralHolder();
		textField.setDocument(new DocumentAdapter(literalHolder));
		typeHolder.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				MWTypeDeclaration type = (MWTypeDeclaration) evt.getNewValue();
				if (type != null) {
					if (type.getType() == getArgument().typeFor(Integer.class)
							|| type.getType() == getArgument().typeFor(BigInteger.class)
							|| type.getType() == getArgument().typeFor(Long.class)
							|| type.getType() == getArgument().typeFor(Short.class)) {
						literalTextField.setDocument(new DocumentAdapter(literalHolder, new RegexpDocument(RegexpDocument.RE_NUMERIC_INTEGER)));
					}
					else if (type.getType() == getArgument().typeFor(BigDecimal.class)
							|| type.getType() == getArgument().typeFor(Double.class)
							|| type.getType() == getArgument().typeFor(Float.class)) {
						literalTextField.setDocument(new DocumentAdapter(literalHolder, new RegexpDocument(RegexpDocument.RE_NUMERIC_DECIMAL)));
						
					}
					else {
						literalTextField.setDocument(new DocumentAdapter(literalHolder, new PlainDocument()));
					}
				}
			}
		
		});
		return textField;
	}
	
	private PropertyValueModel buildLiteralHolder() {
		return new PropertyAspectAdapter(getArgumentHolder(), MWLiteralArgument.VALUE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWLiteralArgument) this.subject).getValue();
			}

            protected void setValueOnSubject(Object value) {
                ((MWLiteralArgument) this.subject).setValue((String)value);
            }
		};
	}
		

    protected PropertyValueModel buildQueryArgumentHolder(PropertyValueModel argumentHolder) {
		return new FilteringPropertyValueModel(argumentHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWLiteralArgument;
			}
		};		
    }

}

