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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.EisReferenceMappingFieldPairEditingPanel.FieldPairSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class EisReferenceMappingFieldPairEditingDialog
	extends AbstractValidatingDialog
{
	// **************** Variables *********************************************
	
	private FieldPairEditor fieldPairEditor;
	
	private PropertyChangeListener validatingListener;
	
	private boolean noError;
	
	
	// **************** Static methods ****************************************
	
	public static void promptToAddFieldPair(
		MWEisReferenceMapping eisReferenceMapping, WorkbenchContext context
	) {
		MWXmlFieldPair xmlFieldPair = eisReferenceMapping.buildEmptyFieldPair();
		FieldPairEditor editor = new FieldPairEditor(xmlFieldPair);
		EisReferenceMappingFieldPairEditingDialog dialog = 
			new EisReferenceMappingFieldPairEditingDialog(context, editor);
		
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
			eisReferenceMapping.addFieldPair(xmlFieldPair);
		}
	}
	
	public static void promptToEditFieldPair(
		MWXmlFieldPair xmlFieldPair, WorkbenchContext context
	) {
		FieldPairEditor editor = new FieldPairEditor(xmlFieldPair);
		EisReferenceMappingFieldPairEditingDialog dialog = 
			new EisReferenceMappingFieldPairEditingDialog(context, editor);
		
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}
	
	
	// **************** Constructors ******************************************
	
	private EisReferenceMappingFieldPairEditingDialog(
		WorkbenchContext context, FieldPairEditor editor
	) {
		super(context);
		this.initialize(editor);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.setTitle(this.resourceRepository().getString("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.TITLE"));
		this.getOKAction().setEnabled(false);
	}
	
	private void initialize(FieldPairEditor editor) {
		this.fieldPairEditor = editor;
		this.validatingListener = this.buildValidatingListener();
		this.fieldPairEditor.sourceXpathHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
		this.fieldPairEditor.targetXpathHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
	}
	
	private PropertyChangeListener buildValidatingListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				EisReferenceMappingFieldPairEditingDialog.this.updateMessage();
				EisReferenceMappingFieldPairEditingDialog.this.updateOKAction();
			}
		};
	}
	
	protected Component buildMainPanel() {
		return new EisReferenceMappingFieldPairEditingPanel(this.getWorkbenchContext(), this.fieldPairEditor);
	}
	
	private void updateMessage() {
		if (this.fieldPairEditor.sourceXpath().equals("")) {
			this.setErrorMessageKey("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.NULL_SOURCE_FIELD_ERROR");
		}
		else if (this.fieldPairEditor.targetXpath().equals("")) {
			this.setErrorMessageKey("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.NULL_TARGET_FIELD_ERROR");
		}
		else {
			this.clearErrorMessage();
		}
		
		if (this.fieldPairEditor.sourceXpathIsDuplicate()) {
			this.setWarningMessageKey("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.DUPLICATE_SOURCE_FIELD_WARNING");
		}
		else if (this.fieldPairEditor.targetXpathIsDuplicate()) {
			this.setWarningMessageKey("EIS_REFERENCE_MAPPING_FIELD_PAIR_EDITING_DIALOG.DUPLICATE_TARGET_FIELD_WARNING");
		}
		else {
			this.clearWarningMessage();
		}
	}
	
	/** Overridden to set error flag */
	protected void setErrorMessage(String message) {
		super.setErrorMessage(message);
		this.noError = (message == null);
	}
	
	/** Overridden to clear error flag */
	protected void clearErrorMessage() {
		super.clearErrorMessage();
		this.noError = true;
	}
	
	private void updateOKAction() {
		this.getOKAction().setEnabled(this.noError);
	}
	
	protected String helpTopicId() {
		return "dialog.editEisReferenceMappingFieldPair";
	}
	
	
	// **************** Member classes ****************************************
	
	public static class FieldPairEditor
		implements FieldPairSpec
	{
		// **************** Variables *****************************************
		
		private MWXmlFieldPair fieldPair;
		
		private ValueModel sourceXmlFieldHolder;
		
		private BufferedPropertyValueModel sourceXpathHolder;
		
		private ValueModel targetXmlFieldHolder;
		
		private BufferedPropertyValueModel targetXpathHolder;
		
		private BufferedPropertyValueModel.Trigger trigger;
		
		
		// **************** Constructors **************************************
		
		protected FieldPairEditor(MWXmlFieldPair fieldPair) {
			super();
			this.fieldPair = fieldPair;
			this.trigger = new BufferedPropertyValueModel.Trigger();
		}
		
		
		// **************** Source Xml field **********************************
		
		public ValueModel sourceXmlFieldHolder() {
			if (this.sourceXmlFieldHolder == null) {
				this.sourceXmlFieldHolder = this.buildSourceXmlFieldHolder();
			}
			
			return this.sourceXmlFieldHolder;
		}
		
		private PropertyValueModel buildSourceXmlFieldHolder() {
			return new SimplePropertyValueModel(this.fieldPair.getSourceXmlField());
		}
		
		public MWXmlField sourceXmlField() {
			return (MWXmlField) this.sourceXmlFieldHolder.getValue();
		}
		
		
		// **************** Source Xpath **************************************
		
		public PropertyValueModel sourceXpathHolder() {
			if (this.sourceXpathHolder == null) {
				this.sourceXpathHolder = this.buildSourceXpathHolder();
			}
			
			return this.sourceXpathHolder;
		}
		
		private BufferedPropertyValueModel buildSourceXpathHolder() {
			return new BufferedPropertyValueModel(this.buildInternalSourceXpathHolder(), this.trigger);
		}
		
		private PropertyValueModel buildInternalSourceXpathHolder() {
			return new PropertyAspectAdapter(this.sourceXmlFieldHolder(), MWXmlField.XPATH_PROPERTY) {
				protected Object getValueFromSubject() {
					return ((MWXmlField) this.subject).getXpath();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWXmlField) this.subject).setXpath((String) value);
				}
			};
		}
		
		public String sourceXpath() {
			return (String) this.sourceXpathHolder.getValue();
		}
		
		public boolean sourceXpathIsDuplicate() {
			return this.fieldPair.duplicateSourceXpath(this.sourceXpath());
		}
		
		
		// **************** Target Xml field **********************************
		
		public ValueModel targetXmlFieldHolder() {
			if (this.targetXmlFieldHolder == null) {
				this.targetXmlFieldHolder = this.buildTargetXmlFieldHolder();
			}
			
			return this.targetXmlFieldHolder;
		}
		
		private PropertyValueModel buildTargetXmlFieldHolder() {
			return new SimplePropertyValueModel(this.fieldPair.getTargetXmlField());
		}
		
		public MWXmlField targetXmlField() {
			return (MWXmlField) this.targetXmlFieldHolder.getValue();
		}
		
		
		// **************** Target Xpath **************************************
		
		public PropertyValueModel targetXpathHolder() {
			if (this.targetXpathHolder == null) {
				this.targetXpathHolder = this.buildTargetXpathHolder();
			}
			
			return this.targetXpathHolder;
		}
		
		private BufferedPropertyValueModel buildTargetXpathHolder() {
			return new BufferedPropertyValueModel(this.buildInternalTargetXpathHolder(), this.trigger);
		}
		
		private PropertyValueModel buildInternalTargetXpathHolder() {
			return new PropertyAspectAdapter(this.targetXmlFieldHolder(), MWXmlField.XPATH_PROPERTY) {
				protected Object getValueFromSubject() {
					return ((MWXmlField) this.subject).getXpath();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWXmlField) this.subject).setXpath((String) value);
				}
			};
		}
		
		public String targetXpath() {
			return (String) this.targetXpathHolder.getValue();
		}
		
		public boolean targetXpathIsDuplicate() {
			return this.fieldPair.duplicateTargetXpath(this.targetXpath());
		}
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			this.trigger.accept();
		}
	}
}
