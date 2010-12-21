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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationEditingPanel.FieldTransformerAssociationSpec;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.TransformerEditingPanel.TransformerSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public abstract class FieldTransformerAssociationEditingDialog
	extends AbstractValidatingDialog
{
	// **************** Variables *********************************************
	
	protected FieldTransformerAssociationEditor associationEditor;
	
	protected PropertyChangeListener validatingListener;
	
	private boolean noError = true;
	
	
	// **************** Constructors ******************************************
	
	protected FieldTransformerAssociationEditingDialog(
		FieldTransformerAssociationEditor associationEditor, 
		WorkbenchContext context
	) {
		super(context);
		this.initialize(associationEditor);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.setTitle(this.resourceRepository().getString("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.TITLE"));
		this.getOKAction().setEnabled(false);
	}
	
	protected void initialize(FieldTransformerAssociationEditor associationEditor) {
		this.associationEditor = associationEditor;
		this.validatingListener = this.buildValidatingListener();
		this.transformerSpec().transformationMethodHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
		this.transformerSpec().transformerClassHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
		// doing this last so that the other holders engage their held value holders first
		this.transformerSpec().transformerTypeHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
	}
	
	private PropertyChangeListener buildValidatingListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				FieldTransformerAssociationEditingDialog.this.updateMessage();
				FieldTransformerAssociationEditingDialog.this.updateOKAction();
			}
		};
	}
	
	protected void updateMessage() {
		if (this.transformerSpec().transformerType() == TransformerSpec.NULL_TRANSFORMER) {
			this.setErrorMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMER_ERROR");
		}
		else if (
			this.transformerSpec().transformerType() == TransformerSpec.TRANSFORMER_CLASS
			&& this.transformerSpec().transformerClass() == null
		) {
			this.setErrorMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMER_CLASS_ERROR");
		}
		else if (
			this.transformerSpec().transformerType() == TransformerSpec.TRANSFORMATION_METHOD
			&& this.transformerSpec().transformationMethod() == null
		) {
			this.setErrorMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_TRANSFORMATION_METHOD_ERROR");
		}
		else {
			this.clearErrorMessage();
		}
		
		if (
			this.transformerSpec().transformerType() == TransformerSpec.TRANSFORMATION_METHOD
			&& this.transformerSpec().transformationMethod() != null
			&& ! this.transformerSpec().transformationMethodIsValid()
		) {
			this.setWarningMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.INVALID_TRANSFORMATION_METHOD_WARNING");
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
		return "dialog.editFieldTransformerAssociation";
	}
	
	
	// **************** Convenience *******************************************
	
	private TransformerEditingPanel.TransformerSpec transformerSpec() {
		return this.associationEditor.transformerSpec();
	}
	
	
	// **************** Member classes ****************************************
	
	public static interface FieldTransformerAssociationEditor
		extends FieldTransformerAssociationSpec
	{
		void commit();
	}
	
	
	public static abstract class AbstractFieldTransformerAssociationEditor
		implements FieldTransformerAssociationEditor
	{
		// **************** Variables *****************************************
		
		protected MWFieldTransformerAssociation association;
		
		private TransformerEditingDialog.FieldTransformerEditor transformerEditor;
		
		
		// **************** Constructors **************************************
		
		protected AbstractFieldTransformerAssociationEditor(MWFieldTransformerAssociation association) {
			super();
			this.association = association;
			this.transformerEditor = new TransformerEditingDialog.FieldTransformerEditor(association);
		}
		
		
		// **************** FieldTransformerAssociationSpec impl **************
		
		public TransformerSpec transformerSpec() {
			return this.transformerEditor;
		}
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			this.transformerEditor.commit();
		}
	}
}
