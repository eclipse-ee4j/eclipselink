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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWClassBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMethodBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.TransformerEditingPanel.TransformerSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;


public final class TransformerEditingDialog 
	extends AbstractValidatingDialog
{
	// **************** Variables *********************************************
	
	private TransformerEditor transformerEditor;
	
	private boolean noError = true;
	
	
	// **************** Static methods ****************************************
	
	public static void promptToEditAttributeTransformer(
		MWTransformationMapping transformationMapping, WorkbenchContext context
	) {
		TransformerEditor editor = new AttributeTransformerEditor(transformationMapping);
		TransformerEditingDialog dialog = new TransformerEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}
	
	public static void promptToAddFieldTransformerAssociationForAggregate(
		MWRelationalTransformationMapping transformationMapping, WorkbenchContext context
	) {
		MWRelationalFieldTransformerAssociation association = 
			transformationMapping.buildEmptyFieldTransformerAssociation();
		TransformerEditor editor = new FieldTransformerEditor(association);
		TransformerEditingDialog dialog = new TransformerEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
			transformationMapping.addFieldTransformerAssociation(association);
		}
	}
	
	public static void promptToEditFieldTransformerAssociationForAggregate(
		MWRelationalFieldTransformerAssociation association, WorkbenchContext context
	) {
		TransformerEditor editor = new FieldTransformerEditor(association);
		TransformerEditingDialog dialog = new TransformerEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}
	
	
	// **************** Constructors ******************************************
	
	private TransformerEditingDialog(TransformerEditor transformerEditor, WorkbenchContext context) {
		super(context);
		this.initialize(transformerEditor);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		this.setTitle(this.resourceRepository().getString("TRANSFORMER_EDITING_DIALOG.TITLE"));
		this.getOKAction().setEnabled(false);
	}
	
	private void initialize(TransformerEditor transformerEditor) {
		this.transformerEditor = transformerEditor;
		this.transformerEditor.transformationMethodHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		this.transformerEditor.transformerClassHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		// doing this last so that the other holders engage their held value holders first
		this.transformerEditor.transformerTypeHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
	}
	
	private PropertyChangeListener buildValidatingListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				TransformerEditingDialog.this.updateErrorMessage();
				TransformerEditingDialog.this.updateOKAction();
			}
		};
	}
	
	private void updateErrorMessage() {
		if (this.transformerEditor.transformerType() == TransformerSpec.NULL_TRANSFORMER) {
			this.setErrorMessageKey("TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMER_ERROR");
		}
		else if (
			this.transformerEditor.transformerType() == TransformerSpec.TRANSFORMER_CLASS
			&& this.transformerEditor.transformerClass() == null
		) {
			this.setErrorMessageKey("TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMER_CLASS_ERROR");
		}
		else if (
			this.transformerEditor.transformerType() == TransformerSpec.TRANSFORMATION_METHOD
			&& this.transformerEditor.transformationMethod() == null
		) {
			this.setErrorMessageKey("TRANSFORMER_EDITING_DIALOG.NULL_TRANSFORMATION_METHOD_ERROR");
		}
		else {
			this.clearErrorMessage();
		}
		
		if (
			this.transformerEditor.transformerType() == TransformerSpec.TRANSFORMATION_METHOD
			&& this.transformerEditor.transformationMethod() != null
			&& ! this.transformerEditor.transformationMethodIsValid()
		) {
			this.setWarningMessageKey("TRANSFORMER_EDITING_DIALOG.INVALID_TRANSFORMATION_METHOD_WARNING");
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
	
	protected Component buildMainPanel() {
		return new TransformerEditingPanel(this.transformerEditor, new DefaultWorkbenchContextHolder(this.getWorkbenchContext()));
	}
	
	protected String helpTopicId() {
		return "dialog.editTransformer";
	}

	protected void prepareToShow() {
		super.prepareToShow();
		setSize(Math.max(500, getWidth()), getHeight());
		
//		pack();
//		setSize(Math.max(500, getWidth()), getHeight());
//		this.setLocationRelativeTo(this.getParent());
	}
	
	// **************** Member classes ****************************************
	
	public static interface TransformerEditor
		extends TransformerSpec
	{
		void commit();
	}
	
	
	static abstract class AbstractTransformerEditor
		implements TransformerEditor
	{
		// **************** Variables *****************************************
		
		private PropertyValueModel transformerTypeHolder;
		
		private BufferedPropertyValueModel methodHolder;
		
		private BufferedPropertyValueModel.Trigger methodTrigger;
		
		private BufferedPropertyValueModel classHolder;
		
		private BufferedPropertyValueModel.Trigger classTrigger;
		
		
		// **************** Constructors **************************************
		
		protected AbstractTransformerEditor() {
			super();
			this.methodTrigger = new BufferedPropertyValueModel.Trigger();
			this.classTrigger = new BufferedPropertyValueModel.Trigger();
		}
		
		
		// **************** Transformer type **********************************
		
		public PropertyValueModel transformerTypeHolder() {
			if (this.transformerTypeHolder == null) {
				this.transformerTypeHolder = this.buildTransformerTypeHolder();
			}
			
			return this.transformerTypeHolder;
		}
		
		protected PropertyValueModel buildTransformerTypeHolder() {
			String transformerType = NULL_TRANSFORMER;
			
			if (this.transformerClass() != null) {
				transformerType = TRANSFORMER_CLASS;
			}
			else if (this.transformationMethod() != null) {
				transformerType = TRANSFORMATION_METHOD;
			}
			
			return new SimplePropertyValueModel(transformerType);
		}
		
		public String transformerType() {
			return (String) this.transformerTypeHolder().getValue();
		}
		
		
		// **************** Transformation method *****************************
		
		public PropertyValueModel transformationMethodHolder() {
			if (this.methodHolder == null) {
				this.methodHolder = this.buildMethodHolder();
			}
			
			return this.methodHolder;
		}
		
		private BufferedPropertyValueModel buildMethodHolder() {
			return new BufferedPropertyValueModel(this.buildInternalMethodHolder(), this.methodTrigger);
		}
		
		protected abstract PropertyValueModel buildInternalMethodHolder();
		
		public MWMethod transformationMethod() {
			return (MWMethod) this.transformationMethodHolder().getValue();
		}
		
		
		// **************** Transformer class *********************************
		
		public PropertyValueModel transformerClassHolder() {
			if (this.classHolder == null) {
				this.classHolder = this.buildClassHolder();
			}
			
			return this.classHolder;
		}
		
		private BufferedPropertyValueModel buildClassHolder() {
			return new BufferedPropertyValueModel(this.buildInternalClassHolder(), this.classTrigger);
		}
		
		protected abstract PropertyValueModel buildInternalClassHolder();
		
		public MWClass transformerClass() {
			return (MWClass) this.transformerClassHolder().getValue();
		}
		
		public MWClassRepository classRepository() {
			return this.transformationMapping().getRepository();
		}
		
		protected abstract MWTransformationMapping transformationMapping();
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			if (this.transformerType() == TRANSFORMATION_METHOD) {
				this.methodTrigger.accept();
			}
			else if (this.transformerType() == TRANSFORMER_CLASS) {
				this.classTrigger.accept();
			}
		}
	}
	
	
	static class AttributeTransformerEditor
		extends AbstractTransformerEditor
	{
		private MWTransformationMapping transformationMapping;
		
		
		private AttributeTransformerEditor(MWTransformationMapping transformationMapping) {
			super();
			this.transformationMapping = transformationMapping;
		}
		
		protected MWTransformationMapping transformationMapping() {
			return this.transformationMapping;
		}
		
		public Iterator candidateTransformationMethods() {
			return this.transformationMapping().candidateAttributeTransformationMethods();
		}
		
		public boolean transformationMethodIsValid() {
			return this.transformationMethod().isCandidateAttributeTransformerMethod();
		}
		
		protected PropertyValueModel buildInternalMethodHolder() {
			return new PropertyAspectAdapter(MWTransformationMapping.ATTRIBUTE_TRANSFORMER_PROPERTY, this.transformationMapping) {
				protected Object getValueFromSubject() {
					MWTransformer transformer = ((MWTransformationMapping) this.subject).getAttributeTransformer();
					
					if (transformer instanceof MWMethodBasedTransformer) {
						return ((MWMethodBasedTransformer) transformer).getMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWTransformationMapping) this.subject).setAttributeTransformer((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalClassHolder() {
			return new PropertyAspectAdapter(MWTransformationMapping.ATTRIBUTE_TRANSFORMER_PROPERTY, this.transformationMapping) {
				protected Object getValueFromSubject() {
					MWTransformer transformer = ((MWTransformationMapping) this.subject).getAttributeTransformer();
					
					if (transformer instanceof MWClassBasedTransformer) {
						return ((MWClassBasedTransformer) transformer).getTransformerClass();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWTransformationMapping) this.subject).setAttributeTransformer((MWClass) value);
				}
			};
		}
	}
	
	
	static class FieldTransformerEditor
		extends AbstractTransformerEditor
	{
		private MWFieldTransformerAssociation association;
		
		
		FieldTransformerEditor(MWFieldTransformerAssociation association) {
			super();
			this.association = association;
		}
		
		protected MWTransformationMapping transformationMapping() {
			return this.association.getMapping();
		}
		
		public Iterator candidateTransformationMethods() {
			return this.transformationMapping().candidateFieldTransformationMethods();
		}
		
		public boolean transformationMethodIsValid() {
			return this.transformationMethod().isCandidateFieldTransformerMethod();
		}
		
		protected PropertyValueModel buildInternalMethodHolder() {
			return new PropertyAspectAdapter(MWFieldTransformerAssociation.FIELD_TRANSFORMER_PROPERTY, this.association) {
				protected Object getValueFromSubject() {
					MWTransformer transformer = ((MWFieldTransformerAssociation) this.subject).getFieldTransformer();
					
					if (transformer instanceof MWMethodBasedTransformer) {
						return ((MWMethodBasedTransformer) transformer).getMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWFieldTransformerAssociation) this.subject).setFieldTransformer((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalClassHolder() {
			return new PropertyAspectAdapter(MWFieldTransformerAssociation.FIELD_TRANSFORMER_PROPERTY, this.association) {
				protected Object getValueFromSubject() {
					MWTransformer transformer = ((MWFieldTransformerAssociation) this.subject).getFieldTransformer();
					
					if (transformer instanceof MWClassBasedTransformer) {
						return ((MWClassBasedTransformer) transformer).getTransformerClass();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWFieldTransformerAssociation) this.subject).setFieldTransformer((MWClass) value);
				}
			};
		}
	}
}
