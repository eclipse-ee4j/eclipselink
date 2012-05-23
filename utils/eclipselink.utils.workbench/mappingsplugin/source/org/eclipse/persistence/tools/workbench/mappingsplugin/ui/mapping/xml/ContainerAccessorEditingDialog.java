package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.framework.context.DefaultWorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractValidatingDialog;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAbstractCompositeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAttributeContainerAccessor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWContainerAccessor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWMethodContainerAccessor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml.AccessorEditingPanel.AccessorSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

public final class ContainerAccessorEditingDialog extends
		AbstractValidatingDialog {

	// **************** Variables *********************************************
	
	private AccessorEditor accessorEditor;
	
	private boolean noError = false;
	
	// **************** Static methods ****************************************
	
	public static void promptToEditContainerAccessor(
		MWAbstractCompositeMapping compositeMapping, WorkbenchContext context
	) {
		AccessorEditor editor = new AttributeAccessorEditor(compositeMapping);
		ContainerAccessorEditingDialog dialog = new ContainerAccessorEditingDialog(editor, context);
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			editor.commit();
		}
	}

	// **************** Constructors ******************************************
	
	private ContainerAccessorEditingDialog(AccessorEditor accessorEditor, WorkbenchContext context) {
		super(context);
		this.initialize(accessorEditor);
	}

	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		this.setTitle(this.resourceRepository().getString("CONTAINER_ACCESSOR_EDITING_DIALOG_TITLE"));
//		this.getOKAction().setEnabled(this.noError);
	}
	
	private void initialize(AccessorEditor accessorEditor) {
		this.accessorEditor = accessorEditor;
		this.accessorEditor.accessorGetMethodHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		this.accessorEditor.accessorSetMethodHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		this.accessorEditor.accessorAttributeHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		// doing this last so that the other holders engage their held value holders first
		this.accessorEditor.accessorTypeHolder().addPropertyChangeListener(ValueModel.VALUE, this.buildValidatingListener());
		updateOKAction();
	}
	
	private PropertyChangeListener buildValidatingListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ContainerAccessorEditingDialog.this.updateErrorMessage();
				ContainerAccessorEditingDialog.this.updateOKAction();
			}
		};
	}
	
	private void updateErrorMessage() {
		if (this.accessorEditor.accessorType() == AccessorSpec.NULL_ACCESSOR) {
			this.setErrorMessageKey("ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_ERROR");
		}
		else if (this.accessorEditor.accessorType() == AccessorSpec.ACCESSOR_ATTRIBUTE && this.accessorEditor.accessorAttribute() == null) {
			this.setErrorMessageKey("ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_ATTRIBUTE_ERROR");
		}
		else if (this.accessorEditor.accessorType() == AccessorSpec.ACCESSOR_METHODS) {
			if (this.accessorEditor.accessorGetMethod() == null) {
				this.setErrorMessageKey("ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_GET_METHOD_ERROR");
			} else if (this.accessorEditor.accessorSetMethod() == null) {
				this.setErrorMessageKey("ACCESSOR_EDITING_DIALOG.NULL_ACCESSOR_SET_METHOD_ERROR");				
			} else {
				this.clearErrorMessage();
			}
		}
		else {
			this.clearErrorMessage();
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
		return "dialog.editAccessor";
	}

	protected void prepareToShow() {
		super.prepareToShow();
		setSize(Math.max(500, getWidth()), getHeight());
		updateErrorMessage();
		updateOKAction();
	}


	@Override
	protected Component buildMainPanel() {
		return new AccessorEditingPanel(this.accessorEditor, new DefaultWorkbenchContextHolder(this.getWorkbenchContext()));
	}

	// **************** Member classes ****************************************
	
	public static interface AccessorEditor
		extends AccessorSpec
	{
		void commit();
	}
	
	
	static abstract class AbstractAccessorEditor
		implements AccessorEditor
	{
		// **************** Variables *****************************************
		
		private PropertyValueModel accessorTypeHolder;
		
		private BufferedPropertyValueModel getMethodHolder;
		
		private BufferedPropertyValueModel.Trigger getMethodTrigger;
		
		private BufferedPropertyValueModel setMethodHolder;
		
		private BufferedPropertyValueModel.Trigger setMethodTrigger;

		private BufferedPropertyValueModel attributeHolder;
		
		private BufferedPropertyValueModel.Trigger attributeTrigger;
		
		
		// **************** Constructors **************************************
		
		protected AbstractAccessorEditor() {
			super();
			this.getMethodTrigger = new BufferedPropertyValueModel.Trigger();
			this.setMethodTrigger = new BufferedPropertyValueModel.Trigger();
			this.attributeTrigger = new BufferedPropertyValueModel.Trigger();
		}
		
		
		// **************** Accessor type **********************************
		
		public PropertyValueModel accessorTypeHolder() {
			if (this.accessorTypeHolder == null) {
				this.accessorTypeHolder = this.buildAccessorTypeHolder();
			}
			
			return this.accessorTypeHolder;
		}
		
		protected PropertyValueModel buildAccessorTypeHolder() {
			String accessorType = NULL_ACCESSOR;
			
			if (this.accessorAttribute() != null) {
				accessorType = ACCESSOR_ATTRIBUTE;
			}
			else if (this.accessorGetMethod() != null && this.accessorSetMethod() != null) {
				accessorType = ACCESSOR_METHODS;
			}
			
			return new SimplePropertyValueModel(accessorType);
		}
		
		public String accessorType() {
			return (String) this.accessorTypeHolder().getValue();
		}
		
		
		// **************** Accessor get method *****************************
		
		public PropertyValueModel accessorGetMethodHolder() {
			if (this.getMethodHolder == null) {
				this.getMethodHolder = this.buildGetMethodHolder();
			}
			
			return this.getMethodHolder;
		}
		
		private BufferedPropertyValueModel buildGetMethodHolder() {
			return new BufferedPropertyValueModel(this.buildInternalGetMethodHolder(), this.getMethodTrigger);
		}
		
		protected abstract PropertyValueModel buildInternalGetMethodHolder();
		
		public MWMethod accessorGetMethod() {
			return (MWMethod) this.accessorGetMethodHolder().getValue();
		}		
		
		// **************** Accessor set method *****************************
		
		public PropertyValueModel accessorSetMethodHolder() {
			if (this.setMethodHolder == null) {
				this.setMethodHolder = this.buildSetMethodHolder();
			}
			
			return this.setMethodHolder;
		}
		
		private BufferedPropertyValueModel buildSetMethodHolder() {
			return new BufferedPropertyValueModel(this.buildInternalSetMethodHolder(), this.setMethodTrigger);
		}
		
		protected abstract PropertyValueModel buildInternalSetMethodHolder();
		
		public MWMethod accessorSetMethod() {
			return (MWMethod) this.accessorSetMethodHolder().getValue();
		}
		
		// **************** Accessor attribute *********************************
		
		public PropertyValueModel accessorAttributeHolder() {
			if (this.attributeHolder == null) {
				this.attributeHolder = this.buildAttributeHolder();
			}
			
			return this.attributeHolder;
		}
		
		private BufferedPropertyValueModel buildAttributeHolder() {
			return new BufferedPropertyValueModel(this.buildInternalAttributeHolder(), this.attributeTrigger);
		}
		
		protected abstract PropertyValueModel buildInternalAttributeHolder();
		
		public MWClassAttribute accessorAttribute() {
			return (MWClassAttribute) this.accessorAttributeHolder().getValue();
		}
		
//		public MWClassRepository classRepository() {
//			return this.compositeMapping().getRepository();
//		}
//		
//		protected abstract MWTransformationMapping compositeMapping();
		
		
		// **************** Editing *******************************************
		
		public void commit() {
			if (this.accessorType() == ACCESSOR_METHODS) {
				this.getMethodTrigger.accept();
				this.setMethodTrigger.accept();
			}
			else if (this.accessorType() == ACCESSOR_ATTRIBUTE) {
				this.attributeTrigger.accept();
			}
		}
	}
	
	
	static class AttributeAccessorEditor
		extends AbstractAccessorEditor
	{
		private MWAbstractCompositeMapping compositeMapping;
		
		private AttributeAccessorEditor(MWAbstractCompositeMapping compositeMapping) {
			super();
			this.compositeMapping = compositeMapping;
		}
		
		protected MWAbstractCompositeMapping compositeMapping() {
			return this.compositeMapping;
		}
		
		public Iterator candidateAccessorMethods() {
			return this.compositeMapping().candidateAccessorMethods();
		}
		
		public Iterator candidateAccessorAttributes() {
			return this.compositeMapping().candidateAccessorAttributes();
		}
		
		protected PropertyValueModel buildInternalGetMethodHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWMethodContainerAccessor) {
						return ((MWMethodContainerAccessor) accessor).getAccessorGetMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorGetMethod((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalSetMethodHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWMethodContainerAccessor) {
						return ((MWMethodContainerAccessor) accessor).getAccessorSetMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorSetMethod((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalAttributeHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWAttributeContainerAccessor) {
						return ((MWAttributeContainerAccessor) accessor).getAccessorAttribute();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorAttribute(((MWClassAttribute) value));
				}
			};
		}
	}
	
	
	static class MethodsAccessorEditor
		extends AbstractAccessorEditor
	{		
		
		private MWAbstractCompositeMapping compositeMapping;

		MethodsAccessorEditor(MWAbstractCompositeMapping compositeMapping) {
			super();
			this.compositeMapping = compositeMapping;
		}
		
		protected MWAbstractCompositeMapping compositeMapping() {
			return this.compositeMapping;
		}
		
		public Iterator candidateAccessorMethods() {
			return this.compositeMapping().candidateAccessorMethods();
		}
		
		public Iterator candidateAccessorAttributes() {
			return this.compositeMapping().candidateAccessorAttributes();
		}
		
		protected PropertyValueModel buildInternalGetMethodHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWMethodContainerAccessor) {
						return ((MWMethodContainerAccessor) accessor).getAccessorGetMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorGetMethod((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalSetMethodHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWMethodContainerAccessor) {
						return ((MWMethodContainerAccessor) accessor).getAccessorSetMethod();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorSetMethod((MWMethod) value);
				}
			};
		}
		
		protected PropertyValueModel buildInternalAttributeHolder() {
			return new PropertyAspectAdapter(MWAbstractCompositeMapping.CONTAINER_ACCESSOR_PROPERTY, this.compositeMapping) {
				protected Object getValueFromSubject() {
					MWContainerAccessor accessor = ((MWAbstractCompositeMapping) this.subject).getContainerAccessor();
					
					if (accessor instanceof MWAttributeContainerAccessor) {
						return ((MWAttributeContainerAccessor) accessor).getAccessorAttribute();
					}
					else {
						return null;
					}
				}
				
				protected void setValueOnSubject(Object value) {
					((MWAbstractCompositeMapping) this.subject).setContainerAccessorAttribute(((MWClassAttribute) value));
				}
			};
		}
	}

}
