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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import java.awt.Component;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.FieldTransformerAssociationEditingDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


final class RelationalFieldTransformerAssociationEditingDialog
	extends FieldTransformerAssociationEditingDialog
{	
	// **************** Static creators ***************************************
	
	static void promptToAddFieldTransformerAssociation(
		MWRelationalTransformationMapping transformationMapping, WorkbenchContext context
	) {
		MWRelationalFieldTransformerAssociation association = 
			transformationMapping.buildEmptyFieldTransformerAssociation();
		RelationalFieldTransformerAssociationEditor editor = 
			new RelationalFieldTransformerAssociationEditor(association);
		RelationalFieldTransformerAssociationEditingDialog dialog 
			= new RelationalFieldTransformerAssociationEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
			transformationMapping.addFieldTransformerAssociation(association);
		}
	}
	
	static void promptToEditFieldTransformerAssociation(
		MWRelationalFieldTransformerAssociation association, WorkbenchContext context
	) {
		RelationalFieldTransformerAssociationEditor editor = 
			new RelationalFieldTransformerAssociationEditor(association);
		RelationalFieldTransformerAssociationEditingDialog dialog 
			= new RelationalFieldTransformerAssociationEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}
	
	
	// **************** Constructors ******************************************
	
	private RelationalFieldTransformerAssociationEditingDialog(
		RelationalFieldTransformerAssociationEditor associationEditor, 
		WorkbenchContext context
	) {
		super(associationEditor, context);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(FieldTransformerAssociationEditor associationEditor) {
		super.initialize(associationEditor);
		this.associationEditor().fieldHolder().addPropertyChangeListener(ValueModel.VALUE, this.validatingListener);
	}
	
	protected Component buildMainPanel() {
		return new RelationalFieldTransformerAssociationEditingPanel(this.associationEditor(), this.getWorkbenchContext());
	}
	
	protected void updateMessage() {
		super.updateMessage();
		
		if (this.associationEditor().field() == null) {
			this.setErrorMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.NULL_FIELD_ERROR");
		}
		
		if (this.associationEditor().fieldIsDuplicate()) {
			this.setWarningMessageKey("FIELD_TRANSFORMER_ASSOCIATION_EDITING_DIALOG.DUPLICATE_FIELD_WARNING");
		}
	}
	
	// **************** Behavior **********************************************
	
	protected void prepareToShow() {
		pack();
		setSize(Math.max(600, getWidth()), getHeight());
		this.setLocationRelativeTo(this.getParent());
	}
	
	
	// **************** Convenience *******************************************
	
	private RelationalFieldTransformerAssociationEditor associationEditor() {
		return (RelationalFieldTransformerAssociationEditor) this.associationEditor;
	}
	
	
	// **************** Member classes ****************************************
	
	private static class RelationalFieldTransformerAssociationEditor
		extends AbstractFieldTransformerAssociationEditor
		implements RelationalFieldTransformerAssociationEditingPanel.RelationalFieldTransformerAssociationSpec
	{
		// **************** Variables *****************************************
		
		private BufferedPropertyValueModel fieldHolder;
		
		private BufferedPropertyValueModel.Trigger fieldTrigger;
		
		
		// **************** Constructors **************************************
		
		protected RelationalFieldTransformerAssociationEditor(MWRelationalFieldTransformerAssociation association) {
			super(association);
			this.fieldTrigger = new BufferedPropertyValueModel.Trigger();
		}
		
		
		// **************** Convenience ***************************************
		
		private MWRelationalFieldTransformerAssociation association() {
			return (MWRelationalFieldTransformerAssociation) this.association;
		}
		
		private MWRelationalTransformationMapping transformationMapping() {
			return (MWRelationalTransformationMapping) this.association().getMapping();
		}
		
		
		// **************** "Field" *******************************************
		
		public PropertyValueModel fieldHolder() {
			if (this.fieldHolder == null) {
				this.fieldHolder = this.buildFieldHolder();
			}
			
			return this.fieldHolder;
		}
		
		private BufferedPropertyValueModel buildFieldHolder() {
			return new BufferedPropertyValueModel(this.buildInternalFieldHolder(), this.fieldTrigger);
		}
		
		private PropertyValueModel buildInternalFieldHolder() {
			return new PropertyAspectAdapter(MWRelationalFieldTransformerAssociation.FIELD_PROPERTY, this.association()) {
				protected Object getValueFromSubject() {
					return ((MWRelationalFieldTransformerAssociation) this.subject).getColumn();
				}
				
				protected void setValueOnSubject(Object value) {
					((MWRelationalFieldTransformerAssociation) this.subject).setColumn((MWColumn) value);
				}
			};
		}
		
		protected MWColumn field() {
			return (MWColumn) this.fieldHolder().getValue();
		}
		
		public Iterator candidateFields() {
			return this.transformationMapping().candidateColumns();
		}
		
		public boolean fieldIsDuplicate() {
			return this.association().duplicateField(this.field());
		}
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			super.commit();
			this.fieldTrigger.accept();
		}
	}
}
