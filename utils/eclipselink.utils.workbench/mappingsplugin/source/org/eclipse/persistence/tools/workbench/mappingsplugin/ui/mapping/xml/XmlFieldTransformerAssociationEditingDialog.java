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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationEditingDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class XmlFieldTransformerAssociationEditingDialog
	extends FieldTransformerAssociationEditingDialog
{
	// **************** Static creators ***************************************
	
	public static void promptToAddFieldTransformerAssociation(
		MWXmlTransformationMapping transformationMapping, WorkbenchContext context
	) {
		MWXmlFieldTransformerAssociation association = 
			transformationMapping.buildEmptyFieldTransformerAssociation();
		XmlFieldTransformerAssociationEditor editor = 
			new XmlFieldTransformerAssociationEditor(association);
		XmlFieldTransformerAssociationEditingDialog dialog 
			= new XmlFieldTransformerAssociationEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
			transformationMapping.addFieldTransformerAssociation(association);
		}
	}
	
	public static void promptToEditFieldTransformerAssociation(
		MWXmlFieldTransformerAssociation association, WorkbenchContext context
	) {
		XmlFieldTransformerAssociationEditor editor = 
			new XmlFieldTransformerAssociationEditor(association);
		XmlFieldTransformerAssociationEditingDialog dialog 
			= new XmlFieldTransformerAssociationEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}
	
	
	// **************** Constructors ******************************************
	
	private XmlFieldTransformerAssociationEditingDialog(
		XmlFieldTransformerAssociationEditor associationEditor, 
		WorkbenchContext context
	) {
		super(associationEditor, context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(FieldTransformerAssociationEditor associationEditor) {
		super.initialize(associationEditor);
		this.associationEditor().xpathHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
	}
	
	protected Component buildMainPanel() {
		return new XmlFieldTransformerAssociationEditingPanel(this.associationEditor(), this.getWorkbenchContext());
	}
	
	protected void updateMessage() {
		super.updateMessage();
		
		if (this.associationEditor().xpath().equals("")) {
			this.setErrorMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_XPATH_ERROR");
		}
		
		if (this.associationEditor().xpathIsDuplicate()) {
			this.setWarningMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.DUPLICATE_XPATH_WARNING");
		}
	}
	
	
	// **************** Behavior **********************************************
	
	protected void prepareToShow() {
		pack();
		setSize(Math.max(600, getWidth()), getHeight());
		this.setLocationRelativeTo(this.getParent());
	}
	
	
	// **************** Convenience *******************************************
	
	private XmlFieldTransformerAssociationEditor associationEditor() {
		return (XmlFieldTransformerAssociationEditor) this.associationEditor;
	}
	
	
	// **************** Member classes ****************************************
	
	private static class XmlFieldTransformerAssociationEditor
		extends AbstractFieldTransformerAssociationEditor
		implements XmlFieldTransformerAssociationEditingPanel.XmlFieldTransformerAssociationSpec
	{
		// **************** Variables *****************************************
		
		private ValueModel xmlFieldHolder;
		
		private BufferedPropertyValueModel xpathHolder;
		
		private BufferedPropertyValueModel.Trigger xpathTrigger;
		
		
		// **************** Constructors **************************************
		
		protected XmlFieldTransformerAssociationEditor(MWXmlFieldTransformerAssociation association) {
			super(association);
			this.xpathTrigger = new BufferedPropertyValueModel.Trigger();
		}
		
		
		// **************** Convenience ***************************************
		
		private MWXmlFieldTransformerAssociation association() {
			return (MWXmlFieldTransformerAssociation) this.association;
		}
		
		private MWXmlTransformationMapping transformationMapping() {
			return (MWXmlTransformationMapping) this.association().getMapping();
		}
		
		
		// **************** Xml field *****************************************
		
		public ValueModel xmlFieldHolder() {
			if (this.xmlFieldHolder == null) {
				this.xmlFieldHolder = this.buildXmlFieldHolder();
			}
			
			return this.xmlFieldHolder;
		}
		
		private PropertyValueModel buildXmlFieldHolder() {
			return new PropertyAspectAdapter(MWXmlFieldTransformerAssociation.FIELD_PROPERTY, this.association()) {
				protected Object getValueFromSubject() {
					return ((MWXmlFieldTransformerAssociation) this.subject).getXmlField();
				}
			};
		}
		
		public MWXmlField xmlField() {
			return (MWXmlField) this.xmlFieldHolder.getValue();
		}
		
		
		// **************** Xpath *********************************************
		
		public PropertyValueModel xpathHolder() {
			if (this.xpathHolder == null) {
				this.xpathHolder = this.buildXpathHolder();
			}
			
			return this.xpathHolder;
		}
		
		private BufferedPropertyValueModel buildXpathHolder() {
			return new BufferedPropertyValueModel(this.buildInternalXpathHolder(), this.xpathTrigger);
		}
		
		private PropertyValueModel buildInternalXpathHolder() {
			return new PropertyAspectAdapter(this.xmlFieldHolder(), MWXmlField.XPATH_PROPERTY) {
				protected Object getValueFromSubject() {
					return ((MWXmlField) this.subject).getXpath();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWXmlField) this.subject).setXpath((String) value);
				}
			};
		}
		
		public String xpath() {
			return (String) this.xpathHolder.getValue();
		}
		
		public boolean xpathIsDuplicate() {
			return this.association().duplicateXpath(this.xpath());
		}
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			super.commit();
			this.xpathTrigger.accept();
		}
	}
}

