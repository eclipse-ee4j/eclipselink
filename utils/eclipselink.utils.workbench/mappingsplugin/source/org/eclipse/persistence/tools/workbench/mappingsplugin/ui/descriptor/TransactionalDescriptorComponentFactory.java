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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;



public class TransactionalDescriptorComponentFactory extends SwingComponentFactory {


	// ************* read only ************	

	public static JCheckBox buildReadOnlyCheckBox(ValueModel transactionalDescriptorHolder, ApplicationContext context) {
		JCheckBox readOnlyCheckBox = buildCheckBox(
					"READ_ONLY_CHECKBOX_TEXT", 
					buildReadOnlyCheckBoxModel(transactionalDescriptorHolder), 
					context.getResourceRepository()
				);
		context.getHelpManager().addTopicID(readOnlyCheckBox, "descriptor.transactional.readOnly");
		
		return readOnlyCheckBox;
	}
	
	private static ButtonModel buildReadOnlyCheckBoxModel(ValueModel transactionalDescriptorHolder) {
		return new CheckBoxModelAdapter(buildReadOnlyAdapter(transactionalDescriptorHolder));
	}
	
	private static PropertyValueModel buildReadOnlyAdapter(ValueModel transactionalDescriptorHolder) {
		return new PropertyAspectAdapter(transactionalDescriptorHolder, MWTransactionalPolicy.READ_ONLY_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWTransactionalDescriptor) subject).getTransactionalPolicy().isReadOnly());
			}

			protected void setValueOnSubject(Object value) {
				((MWTransactionalDescriptor) subject).getTransactionalPolicy().setReadOnly(((Boolean) value).booleanValue());
			}
		};
	}


	// **************** Conform Results In Unit of Work ***********************
	
	public static JCheckBox buildConformResultsInUnitOfWorkCheckBox(PropertyValueModel transactionalDescriptorHolder, ApplicationContext context) {
		JCheckBox checkBox = buildCheckBox(
					"CONFORM_RESULTS_CHECKBOX_TEXT", 
					buildConformResultsInUnitOfWorkCheckBoxModel(transactionalDescriptorHolder), 
					context.getResourceRepository()
				);
		context.getHelpManager().addTopicID(checkBox, "descriptor.transactional.conform");
		return checkBox;
	}
	
	private static ButtonModel buildConformResultsInUnitOfWorkCheckBoxModel(PropertyValueModel transactionalDescriptorHolder) {
		return new CheckBoxModelAdapter(buildConformResultsInUnitOfWorkAdapter(transactionalDescriptorHolder));
	}
	
	private static PropertyValueModel buildConformResultsInUnitOfWorkAdapter(PropertyValueModel transactionalDescriptorHolder) {
		return new PropertyAspectAdapter(transactionalDescriptorHolder, MWTransactionalPolicy.CONFORM_RESULTS_IN_UNIT_OF_WORK_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWTransactionalDescriptor) subject).getTransactionalPolicy().isConformResultsInUnitOfWork());
			}

			protected void setValueOnSubject(Object value) {
				((MWTransactionalDescriptor) subject).getTransactionalPolicy().setConformResultsInUnitOfWork(((Boolean) value).booleanValue());
			}
		};
	}


	
	// **************** Refresh cache policy **********************************
	// **************** (Must have a refresh cache policy to use this) ********
	
	public static RefreshCachePolicyPanel buildRefreshCachePolicyPanel(PropertyValueModel transactionalDescriptorHolder, ApplicationContext context) {
		return new RefreshCachePolicyPanel(context, buildRefreshCachePolicyHolder(transactionalDescriptorHolder));
	}
	
	// **************** Refresh cache policy **********************************
	
	private static PropertyValueModel buildRefreshCachePolicyHolder(PropertyValueModel transactionalDescriptorHolder) {
		return new PropertyAspectAdapter(transactionalDescriptorHolder) {
			protected Object getValueFromSubject() {
				return ((MWTransactionalDescriptor) subject).getTransactionalPolicy().getRefreshCachePolicy();
			}
		};
	}
    
    
    
    // **************** Descriptor Alias **********************************
    
    public static Document buildDescriptorAliasDocumentAdapter(ValueModel mappingDescriptorHolder) {
        return new DocumentAdapter(buildDescriptorAliasHolder(mappingDescriptorHolder));
    }
    
    private static PropertyValueModel buildDescriptorAliasHolder(ValueModel mappingDescriptorHolder) {
        return new PropertyAspectAdapter(buildTransactionalPolicyHolder(mappingDescriptorHolder), MWTransactionalPolicy.DESCRIPTOR_ALIAS_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWTransactionalPolicy) this.subject).getDescriptorAlias();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWTransactionalPolicy) this.subject).setDescriptorAlias((String) value);
            }
        };
    }
    
    private static PropertyValueModel buildTransactionalPolicyHolder(ValueModel mappingDescriptorHolder) {
        return new PropertyAspectAdapter(mappingDescriptorHolder) {
            protected Object getValueFromSubject() {
                return ((MWMappingDescriptor) this.subject).getTransactionalPolicy();
            }
        };
    }
    
}
