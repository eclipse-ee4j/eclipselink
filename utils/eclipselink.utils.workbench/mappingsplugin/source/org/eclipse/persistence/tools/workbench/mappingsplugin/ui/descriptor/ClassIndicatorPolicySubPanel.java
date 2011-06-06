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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledBorder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AccessibleTitledPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorExtractionMethodPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public abstract class ClassIndicatorPolicySubPanel extends AbstractPanel implements RootListener, IndicatorFieldListener {
	
    private ValueModel descriptorHolder;
    
	private ValueModel inheritancePolicyHolder;
	
    private PropertyValueModel classIndicatorPolicyHolder;
    
    private PropertyValueModel classIndicatorFieldPolicyHolder;
  
    private PropertyValueModel useNameModel;
    
    private boolean isRoot;
    
    private boolean isIndicatorField;

	private PropertyValueModel policyTypeModel;
    
	private JRadioButton useClassExtractionMethodRadioButton;

	protected JRadioButton useClassIndicatorFieldRadioButton;
	
	protected JRadioButton useClassNameAsIndicatorRadioButton;

	protected JRadioButton useClassIndicatorDictionaryRadioButton;
	    	
	protected ClassIndicatorPolicySubPanel(PropertyValueModel descriptorHolder, PropertyValueModel inheritancePolicyHolder, WorkbenchContextHolder contextHolder, Collection isRootListeners) {
		super(contextHolder);
		initialize(descriptorHolder, inheritancePolicyHolder, isRootListeners);
	}
	
	protected void initialize(PropertyValueModel descHolder, PropertyValueModel inheritancePolicyHolder, Collection isRootListeners) {
		this.descriptorHolder = descHolder;
		this.inheritancePolicyHolder = inheritancePolicyHolder;
		this.classIndicatorPolicyHolder = buildClassIndicatorPolicyHolder();
        this.classIndicatorFieldPolicyHolder = buildClassIndicatorFieldPolicyHolder();
		this.useNameModel = buildUseClassNameAsIndicatorHolder();
		this.policyTypeModel = buildClassIndicatorPolicyTypeAdapter();
		initializeLayout(isRootListeners);
	}
	
	protected abstract void initializeLayout(Collection isRootListeners);
	
	public void updateRootStatus(boolean newValue) {
		this.isRoot = newValue;
		this.updateEnablementStatus();
	}

	public void updateIndicatorFieldStatus(boolean newValue) {
		this.isIndicatorField = newValue;
		this.updateEnablementStatus();
	}
	
	protected void addExtractionMethodListener(final ExtractionMethodListener listener) {
		getPolicyTypeModel().addPropertyChangeListener( 
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					boolean enabled = evt.getNewValue() == MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE;
					listener.updateExtractionMethodStatus(enabled);
				}
			}
		);
		listener.updateExtractionMethodStatus(getPolicyTypeModel().getValue() == MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE);
	}

	protected void addIndicatorFieldListener(final IndicatorFieldListener listener) {
		getPolicyTypeModel().addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					boolean enabled = evt.getNewValue() == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE;
					listener.updateIndicatorFieldStatus(enabled);
				}
			}
		);
		listener.updateIndicatorFieldStatus(getPolicyTypeModel().getValue() == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE);
	}

	protected void addIndicatorDictionaryListener(final IndicatorDictionaryListener listener) {
		getUseNameModel().addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					boolean enabled = evt.getNewValue() == Boolean.FALSE;
					listener.updateIndicatorDictionaryStatus(enabled);
				}
			}
		);
	}
	
	//children should call super.updateEnablementStatus() when this method is overidden
	protected void updateEnablementStatus() {
		useClassIndicatorFieldRadioButton.setEnabled(this.isRoot());
		useClassExtractionMethodRadioButton.setEnabled(this.isRoot());
		useClassNameAsIndicatorRadioButton.setEnabled(this.isRoot() && this.isIndicatorType());
		useClassIndicatorDictionaryRadioButton.setEnabled(this.isRoot() && this.isIndicatorType());
	}
	
	protected PropertyValueModel buildClassIndicatorPolicyHolder() {
        return new PropertyAspectAdapter(inheritancePolicyHolder, MWDescriptorInheritancePolicy.CLASS_INDICATOR_POLICY_PROPERTY) {
			protected Object getValueFromSubject(){
				return ((MWDescriptorInheritancePolicy)this.subject).getClassIndicatorPolicy();
			}
        };
	}

	private PropertyValueModel buildClassIndicatorPolicyTypeAdapter() {
		return new PropertyAspectAdapter(inheritancePolicyHolder, MWDescriptorInheritancePolicy.CLASS_INDICATOR_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				if (((MWDescriptorInheritancePolicy) this.subject).getClassIndicatorPolicy() == null) {
					return null;
				}
				return ((MWDescriptorInheritancePolicy) this.subject).getClassIndicatorPolicy().getType();
			}
			
			protected void setValueOnSubject(Object value) {
				if ((String) value == MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE) {
					((MWDescriptorInheritancePolicy) this.subject).useClassExtractionMethodIndicatorPolicy();
				}
				else if ((String) value == MWClassIndicatorPolicy.CLASS_INDICATOR_FIELD_TYPE) {
					((MWDescriptorInheritancePolicy) subject).useClassIndicatorFieldPolicy();
				}
			}
		};
	}
	
	private PropertyValueModel buildUseClassNameAsIndicatorHolder() {
		return new PropertyAspectAdapter(getClassIndicatorFieldPolicyHolder(), MWClassIndicatorFieldPolicy.CLASS_NAME_IS_INDICATOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWClassIndicatorFieldPolicy) subject).classNameIsIndicator());
			}
			protected void setValueOnSubject(Object value) {
				((MWClassIndicatorFieldPolicy) subject).setClassNameIsIndicator(((Boolean)value).booleanValue());
			}			
		};
	}
	
	protected RadioButtonModelAdapter buildClassNameAsIndicatorRadioButtonModel(PropertyValueModel useNameHolder) {
		return new RadioButtonModelAdapter(useNameHolder, Boolean.TRUE);
	}
	
	protected RadioButtonModelAdapter buildClassIndicatorDictionaryRadioButtonModel(PropertyValueModel useNameHolder) {
		return new RadioButtonModelAdapter(useNameHolder, Boolean.FALSE);
	}

	protected RadioButtonModelAdapter buildClassIndicatorPolicyRadioButtonModel(String classIndicatorPolicyType) {
		return new RadioButtonModelAdapter(this.policyTypeModel, classIndicatorPolicyType);
	}

	protected JPanel buildUseClassExtractionMethodPanel(Collection isRootListeners) {
		GridBagConstraints constraints = new GridBagConstraints();
	
		//use class extraction panel
		JPanel useClassExtractionMethodPanel = new AccessibleTitledPanel(new GridBagLayout());
		
			// useClassExtractionMethod radio button
			useClassExtractionMethodRadioButton = buildRadioButton("USE_CLASS_EXTRACTION_METHOD", buildClassIndicatorPolicyRadioButtonModel(MWClassIndicatorPolicy.CLASS_EXTRACTION_METHOD_TYPE));
			useClassExtractionMethodPanel.setEnabled(this.isRoot());
			constraints.gridx      = 0;
			constraints.gridy      = 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(0, 0, 0, 0);
		
			useClassExtractionMethodPanel.add(useClassExtractionMethodRadioButton, constraints);
	
			ClassExtractionMethodPanel extractionMethodPanel = new ClassExtractionMethodPanel(getApplicationContext(), buildClassExtractionMethodIndirectionPolicyHolder());
			isRootListeners.add(extractionMethodPanel);
			addExtractionMethodListener(extractionMethodPanel);
			extractionMethodPanel.setBorder(new AccessibleTitledBorder(useClassExtractionMethodRadioButton.getText()));
	
			constraints.gridx      = 0;
			constraints.gridy      = 1;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 1;
			constraints.weighty    = 1;
			constraints.fill       = GridBagConstraints.HORIZONTAL;
			constraints.anchor     = GridBagConstraints.CENTER;
			constraints.insets     = new Insets(0, SwingTools.checkBoxIconWidth(), 0, 0);
	
			useClassExtractionMethodPanel.add(extractionMethodPanel, constraints);
		
		addHelpTopicId(useClassExtractionMethodPanel, helpTopicId() + ".useClassExtraction");
	
		return useClassExtractionMethodPanel;	
	}
    
    private PropertyValueModel buildClassExtractionMethodIndirectionPolicyHolder() {
        return new FilteringPropertyValueModel(this.classIndicatorPolicyHolder) {
            protected boolean accept(Object value) {
                return value instanceof MWClassIndicatorExtractionMethodPolicy;
            }
        };
    }
    
    private PropertyValueModel buildClassIndicatorFieldPolicyHolder() {
        return new FilteringPropertyValueModel(this.classIndicatorPolicyHolder) {
            protected boolean accept(Object value) {
                return value instanceof MWClassIndicatorFieldPolicy;
            }
        };
    }


	protected String helpTopicId() {
		return "descriptor.inheritance.classIndicator";	
	}
	
	protected PropertyValueModel getUseNameModel() {
		return useNameModel;
	}

	protected PropertyValueModel getPolicyTypeModel() {
		return policyTypeModel;
	}

	protected PropertyValueModel getClassIndicatorPolicyHolder() {
		return classIndicatorPolicyHolder;
	}
    
    protected PropertyValueModel getClassIndicatorFieldPolicyHolder() {
        return classIndicatorFieldPolicyHolder;
    }	
    
	protected ValueModel getDescriptorHolder() {
		return descriptorHolder;
	}

	public boolean isRoot() {
		return isRoot;
	}
	
	public boolean isIndicatorType() {
		return isIndicatorField;
	}
    
    

}
