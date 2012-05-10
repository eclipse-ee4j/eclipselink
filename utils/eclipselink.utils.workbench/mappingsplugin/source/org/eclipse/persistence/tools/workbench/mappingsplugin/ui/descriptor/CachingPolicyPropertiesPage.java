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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.CacheCoordinationOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.CacheIsolationOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.CacheTypeOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy.ExistenceCheckingOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy.CacheSizeHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy.CacheTypeHolder;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


public final class CachingPolicyPropertiesPage extends ScrollablePropertiesPage
{
    private PropertyValueModel cachingPolicyHolder;
    private PropertyValueModel nonNullCachingPolicyHolder;
    
    private PropertyValueModel projectDefaultCachingPolicyHolder;
    
    CachingPolicyPropertiesPage(PropertyValueModel valueModel,
                                         WorkbenchContextHolder contextHolder) 
    {
        super(valueModel, contextHolder);
    }
    
    protected String getHelpTopicId() {
        return "descriptor.identity";
    }
    
    protected void initialize(PropertyValueModel selectionNodeHolder) {
        super.initialize(selectionNodeHolder);
        this.cachingPolicyHolder = buildCachingPolicyHolder();
        this.nonNullCachingPolicyHolder = buildNonNullCachingPolicyHolder();
       
        this.projectDefaultCachingPolicyHolder = buildProjectDefaultCachingPolicyHolder();

    }
    
    private PropertyValueModel buildNonNullCachingPolicyHolder() {
        return new FilteringPropertyValueModel(this.cachingPolicyHolder) {
            protected boolean accept(Object value) {
                return (value instanceof MWNullCachingPolicy) ? false : true;
            }
        };
    }
    
    private PropertyValueModel buildCachingPolicyHolder() {
        return new PropertyAspectAdapter(buildTransactionalPolicyHolder()) {
            protected Object getValueFromSubject() {
                return ((MWTransactionalPolicy) this.subject).getCachingPolicy();
            }
        }; 
    }
  
    private PropertyValueModel buildTransactionalPolicyHolder() {
        return new PropertyAspectAdapter(getSelectionHolder()) {
            protected Object getValueFromSubject() {
                return ((MWTransactionalDescriptor) this.subject).getTransactionalPolicy();
            }
        };
    }
    
    private PropertyValueModel buildProjectDefaultCachingPolicyHolder() {
        return new PropertyAspectAdapter(this.nonNullCachingPolicyHolder) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getProject().getDefaultsPolicy().getCachingPolicy();
            }
        };
    }
    
    protected Component buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        
        PropertyValueModel projectCachingTypeHolder =  buildProjectDefaultCachingTypeHolder();

        final JComponent cacheTypeComboBox = 
                buildLabeledComboBox(
                        "CACHING_POLICY_CACHE_TYPE_CHOOSER",
                        new ComboBoxModelAdapter(buildCacheTypeCollectionHolder(), buildCacheTypeHolder()),
                        buildCacheTypeOptionRenderer(projectCachingTypeHolder)
                );
        
        new ComponentEnabler(buildCacheTypeHolderBooleanModel(), cacheTypeComboBox);

        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 5);
        panel.add(cacheTypeComboBox, constraints);
        helpManager().addTopicID(cacheTypeComboBox, "descriptor.cacheType");
        projectCachingTypeHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener((JComboBox) cacheTypeComboBox.getComponent(1)));
        
        
        //cache size
        PropertyValueModel descriptorCacheSizeHolder = buildCacheSizeHolder();
        final SpinnerNumberModel descriptorCacheSizeModel =
            new NumberSpinnerModelAdapter(
                    descriptorCacheSizeHolder, 
                    0,     // Minimum value
                    99999, // Maximum value
                    1      // Step size
            );
        JComponent cacheSizeWidgets = buildLabeledSpinnerNumber(
            "CACHING_POLICY_CACHE_SIZE_SPINNER",
            descriptorCacheSizeModel
        );

        constraints.gridx      = 0;
        constraints.gridy      = 1;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 5);

        panel.add(cacheSizeWidgets, constraints);
        helpManager().addTopicID(cacheSizeWidgets, "descriptor.cacheSize");

        final JSpinner cacheSizeSpinner = (JSpinner) cacheSizeWidgets.getComponent(1);

        
        // Default Cache Size check box
        JCheckBox defaultCacheSizeCheckBox = buildCheckBox(
            "CACHING_POLICY_CACHE_SIZE_DEFAULT_CHECKBOX",
            buildDefaultCacheSizeCheckBoxAdapter()
        );

        constraints.gridx      = 1;
        constraints.gridy      = 1;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 0);

        panel.add(defaultCacheSizeCheckBox, constraints);
        helpManager().addTopicID(defaultCacheSizeCheckBox, "descriptor.cacheSize.default");
        PropertyValueModel projectCacheSizeHolder = buildProjectCacheSizeHolder();
        final SpinnerNumberModel projectCacheSizeModel = 
            new NumberSpinnerModelAdapter(
                projectCacheSizeHolder, 
                0,     // Minimum value
                99999, // Maximum value
                1      // Step size
        );
        defaultCacheSizeCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    cacheSizeSpinner.setModel(projectCacheSizeModel);
                }
                else {
                    cacheSizeSpinner.setModel(descriptorCacheSizeModel);
                }
            }
        });

        new ComponentEnabler(buildCacheSizeHolderBooleanModel(), new Component[] {cacheSizeWidgets, defaultCacheSizeCheckBox});
       
        //cache isolation
        final PropertyValueModel cacheIsolationHolder = buildCacheIsolationHolder();
        final PropertyValueModel projectCachingIsolationHolder =  buildProjectDefaultCacheIsolationHolder();
        final JComponent cacheIsolationComboBox = 
            buildLabeledComboBox(
                    "CACHING_POLICY_CACHE_ISOLATION_CHOOSER",
                    new ComboBoxModelAdapter(buildCacheIsolationCollectionHolder(), cacheIsolationHolder),
                    buildCacheIsolationOptionRenderer(projectCachingIsolationHolder)
            );
        constraints.gridx      = 0;
        constraints.gridy      = 2;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 5);
        panel.add(cacheIsolationComboBox, constraints);
        helpManager().addTopicID(cacheIsolationComboBox, "descriptor.cacheIsolation");
        projectCachingIsolationHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener((JComboBox) cacheIsolationComboBox.getComponent(1)));
        
        //cache coordination
        PropertyValueModel projectCachingCoordinationHolder =  buildProjectDefaultCacheCoordinationHolder();
        final JComponent cacheCoordinationComboBox = 
            buildLabeledComboBox(
                    "CACHING_POLICY_CACHE_COORDINATION_CHOOSER",
                    new ComboBoxModelAdapter(buildCacheCoordinationCollectionHolder(), buildCacheCoordinationHolder()),
                    buildCacheCoordinationOptionRenderer(projectCachingCoordinationHolder)
            );
        constraints.gridx      = 0;
        constraints.gridy      = 3;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 5);
        panel.add(cacheCoordinationComboBox, constraints);
        helpManager().addTopicID(cacheCoordinationComboBox, "descriptor.cacheCoord");
       projectCachingCoordinationHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener((JComboBox) cacheCoordinationComboBox.getComponent(1)));
        cacheIsolationHolder.addPropertyChangeListener(
                ValueModel.VALUE,
                buildUpdateCacheCoordinationComboBox((JComboBox) cacheCoordinationComboBox.getComponent(1), cacheIsolationHolder, projectCachingIsolationHolder));
        projectCachingIsolationHolder.addPropertyChangeListener(
                ValueModel.VALUE,
                buildUpdateCacheCoordinationComboBox((JComboBox) cacheCoordinationComboBox.getComponent(1), cacheIsolationHolder, projectCachingIsolationHolder));
        
  
        //expiry
         CacheExpiryPanel cacheExpiryPanel = 
             new CacheExpiryPanel(
                     getApplicationContext(),
                     this.nonNullCachingPolicyHolder,
                     getHelpTopicId() + ".CacheExpiry"
             );

        constraints.gridx      = 0;
        constraints.gridy      = 4;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(10, 5, 5, 5);

        panel.add(cacheExpiryPanel, constraints);
       
       
        //existence checking
        PropertyValueModel projectCachingExistenceCheckingHolder =  buildProjectDefaultExistenceCheckingHolder();
        final JComponent existenceCheckingComboBox = 
            buildLabeledComboBox(
                    "CACHING_POLICY_EXISTENCE_CHECKING_CHOOSER",
                    new ComboBoxModelAdapter(buildExistenceCheckingCollectionHolder(), buildExistenceCheckingHolder()),
                    buildExistenceCheckingOptionRenderer(projectCachingExistenceCheckingHolder)
            );
        constraints.gridx      = 0;
        constraints.gridy      = 5;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, 5, 0, 5);
        panel.add(existenceCheckingComboBox, constraints);
		  addHelpTopicId(existenceCheckingComboBox, "descriptor.identity.existenceCheck");
        projectCachingExistenceCheckingHolder.addPropertyChangeListener(
                ValueModel.VALUE, 
                buildProjectDefaultListener((JComboBox) existenceCheckingComboBox.getComponent(1)));
 
        
		  addHelpTopicId(panel, getHelpTopicId());
        return panel;      
    }
    
    private ValueModel buildCacheTypeHolderBooleanModel() {
		PropertyValueModel cacheTypeHolderModel = new PropertyAspectAdapter(this.cachingPolicyHolder, MWDescriptorCachingPolicy.CACHE_TYPE_HOLDER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorCachingPolicy) this.subject).getCacheTypeHolder();
			}
		};
		return new TransformationPropertyValueModel(cacheTypeHolderModel) {
			protected Object transform(Object value) {
				if (value == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((CacheTypeHolder) value).typeCanBeSet());
			}
		};
    }
    
    private ValueModel buildCacheSizeHolderBooleanModel() {
		PropertyValueModel cacheSizeHolderModel = new PropertyAspectAdapter(this.cachingPolicyHolder, MWDescriptorCachingPolicy.CACHE_SIZE_HOLDER_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorCachingPolicy) this.subject).getCacheSizeHolder();
			}
		};
		return new TransformationPropertyValueModel(cacheSizeHolderModel) {
			protected Object transform(Object value) {
				if (value == null) {
					return Boolean.FALSE;
				}
				return Boolean.valueOf(((CacheSizeHolder) value).sizeCanBeSet());
			}
		};
    }

    private PropertyChangeListener buildProjectDefaultListener(final JComboBox comboBox) {
       return new PropertyChangeListener(){ 
           public void propertyChange(PropertyChangeEvent evt) {
               //repainting the comboBox because the rendering changes when 
               //the project value is changed.
               comboBox.repaint();           
           }
       };
    }
    
    private PropertyChangeListener buildUpdateCacheCoordinationComboBox(final JComboBox comboBox, final PropertyValueModel cacheIsolationHolder, final PropertyValueModel projectCachingIsolationHolder) {
       return new PropertyChangeListener() {                
            public void propertyChange(PropertyChangeEvent evt) {
                CacheIsolationOption option = (CacheIsolationOption) cacheIsolationHolder.getValue();
                if (option != null) {
                    String mwModelOption = option.getMWModelOption();
                    if (mwModelOption == MWCachingPolicy.CACHE_ISOLATION_PROJECT_DEFAULT) {
                        if ((CacheIsolationOption) projectCachingIsolationHolder.getValue() != null) {
                            mwModelOption = ((CacheIsolationOption) projectCachingIsolationHolder.getValue()).getMWModelOption();
                        }
                    }
                    if (mwModelOption == MWCachingPolicy.CACHE_ISOLATION_ISOLATED) {
                        comboBox.setEnabled(false);
                    }
                    else {
                        comboBox.setEnabled(true);
                    }
                }
            }                
        };
    }
    
    // **************** Cache Type ***************
    
    private PropertyValueModel buildCacheTypeHolder() {
        return new PropertyAspectAdapter(
        		this.nonNullCachingPolicyHolder, 
        		MWCachingPolicy.CACHE_TYPE_PROPERTY, 
        		MWDescriptorCachingPolicy.CACHE_TYPE_HOLDER_PROPERTY, 
        		MWDescriptorCachingPolicy.DESCRIPTOR_INHERITANCE_PROPERTY) 
        {
            protected Object getValueFromSubject() {
            	return ((MWCachingPolicy) this.subject).getCacheType();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCachingPolicy) this.subject).setCacheType((CacheTypeOption) value);
            }
        };
    }
    
    private ListValueModel buildCacheTypeCollectionHolder() {
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWDescriptorCachingPolicy.cacheTypeOptions().toplinkOptions();
            }
        };
    }
    
    private ListCellRenderer buildCacheTypeOptionRenderer(final PropertyValueModel projectCacheTypeHolder) {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                if (((CacheTypeOption) value).getMWModelOption() == MWCachingPolicy.CACHE_TYPE_PROJECT_DEFAULT) {
                    if (projectCacheTypeHolder.getValue() == null) {
                        return resourceRepository().getString(((CacheTypeOption) value).resourceKey());
                    }
                    return resourceRepository().getString(
                            ((CacheTypeOption) value).resourceKey(),
                            resourceRepository().getString(((CacheTypeOption) projectCacheTypeHolder.getValue()).resourceKey())
                            );
                }
                return resourceRepository().getString(((CacheTypeOption) value).resourceKey());
            }
        };
    }
    
    private PropertyValueModel buildProjectDefaultCachingTypeHolder() {
        return new PropertyAspectAdapter(this.projectDefaultCachingPolicyHolder, MWCachingPolicy.CACHE_TYPE_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheType();
            }
        };
    }   
    
    // **************** Cache Size ***************
    
    private PropertyValueModel buildCacheSizeHolder() {
        return new PropertyAspectAdapter(
        		this.nonNullCachingPolicyHolder, 
        		MWCachingPolicy.CACHE_SIZE_PROPERTY,
        		MWDescriptorCachingPolicy.CACHE_TYPE_HOLDER_PROPERTY, 
        		MWDescriptorCachingPolicy.DESCRIPTOR_INHERITANCE_PROPERTY)
        {
            protected Object getValueFromSubject() {
            	return new Integer(((MWCachingPolicy) this.subject).getCacheSize());
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCachingPolicy) this.subject).setCacheSize(((Integer) value).intValue());
            }
        };
    }
    
    private PropertyValueModel buildProjectCacheSizeHolder() {
        return new PropertyAspectAdapter(this.projectDefaultCachingPolicyHolder, MWCachingPolicy.CACHE_SIZE_PROPERTY) {
            protected Object getValueFromSubject() {
                return new Integer(((MWCachingPolicy) this.subject).getCacheSize());
            }
            protected void setValueOnSubject(Object value) {
                if (((Integer) value).intValue() !=  ((MWDescriptorCachingPolicy) cachingPolicyHolder.getValue()).getProject().getDefaultsPolicy().getCachingPolicy().getCacheSize()) {                         
                    ((MWDescriptorCachingPolicy) cachingPolicyHolder.getValue()).setDontUseProjectDefaultCacheSize(((Integer) value).intValue());
                }
            }
        };
    }
    
    private CheckBoxModelAdapter buildDefaultCacheSizeCheckBoxAdapter() {
        return new CheckBoxModelAdapter(buildDefaultCacheSizeHolder());
    }
    
    private PropertyValueModel buildDefaultCacheSizeHolder() {
        return new PropertyAspectAdapter(
        		this.nonNullCachingPolicyHolder, 
        		MWDescriptorCachingPolicy.USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY,
           		MWDescriptorCachingPolicy.CACHE_SIZE_HOLDER_PROPERTY, 
        		MWDescriptorCachingPolicy.DESCRIPTOR_INHERITANCE_PROPERTY) 
        {
            protected Object getValueFromSubject() {
            	return Boolean.valueOf(((MWDescriptorCachingPolicy) this.subject).getCacheSizeHolder().usesProjectDefaultCacheSize());
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWDescriptorCachingPolicy) this.subject).setUseProjectDefaultCacheSize(((Boolean) value).booleanValue());
            }
        };        
    }
    
    // **************** Cache Isolation ***************

    private PropertyValueModel buildCacheIsolationHolder() {
        return new PropertyAspectAdapter(this.nonNullCachingPolicyHolder, MWCachingPolicy.CACHE_ISOLATION_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheIsolation();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCachingPolicy) this.subject).setCacheIsolation((CacheIsolationOption) value);
            }
        };
    }
    
    private ListValueModel buildCacheIsolationCollectionHolder() {
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWDescriptorCachingPolicy.cacheIsolationOptions().toplinkOptions();
            }
        };
    }
    
    private ListCellRenderer buildCacheIsolationOptionRenderer(final PropertyValueModel projectCacheIsolationHolder) {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                if (((CacheIsolationOption) value).getMWModelOption() == MWCachingPolicy.CACHE_ISOLATION_PROJECT_DEFAULT) {
                    if (projectCacheIsolationHolder.getValue() == null) {
                        return resourceRepository().getString(((CacheIsolationOption) value).resourceKey());
                    }
                    return resourceRepository().getString(
                            ((CacheIsolationOption) value).resourceKey(),
                            resourceRepository().getString(((CacheIsolationOption) projectCacheIsolationHolder.getValue()).resourceKey())
                            );
                }
                return resourceRepository().getString(((CacheIsolationOption) value).resourceKey());
            }
        };
    }
    
    private PropertyValueModel buildProjectDefaultCacheIsolationHolder() {
        return new PropertyAspectAdapter(this.projectDefaultCachingPolicyHolder, MWCachingPolicy.CACHE_ISOLATION_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheIsolation();
            }
        };
    } 
  
    
    // **************** Cache Coordination ***************

    private PropertyValueModel buildCacheCoordinationHolder() {
        return new PropertyAspectAdapter(this.nonNullCachingPolicyHolder, MWCachingPolicy.CACHE_COORDINATION_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheCoordination();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCachingPolicy) this.subject).setCacheCoordination((CacheCoordinationOption) value);
            }
        };
    }
    
    private ListValueModel buildCacheCoordinationCollectionHolder() {
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWDescriptorCachingPolicy.cacheCoordinationOptions().toplinkOptions();
            }
        };  
    }
    
    private ListCellRenderer buildCacheCoordinationOptionRenderer(final PropertyValueModel projectCacheCoordinationHolder) {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                if (((CacheCoordinationOption) value).getMWModelOption() == MWCachingPolicy.CACHE_COORDINATION_PROJECT_DEFAULT) {
                    if (projectCacheCoordinationHolder.getValue() == null) {
                        return resourceRepository().getString(((CacheCoordinationOption) value).resourceKey());
                    }
                    return resourceRepository().getString(
                            ((CacheCoordinationOption) value).resourceKey(),
                            resourceRepository().getString(((CacheCoordinationOption) projectCacheCoordinationHolder.getValue()).resourceKey())
                            );
                }
                return resourceRepository().getString(((CacheCoordinationOption) value).resourceKey());
            }
        };
    }
    
    private PropertyValueModel buildProjectDefaultCacheCoordinationHolder() {
        return new PropertyAspectAdapter(this.projectDefaultCachingPolicyHolder, MWCachingPolicy.CACHE_COORDINATION_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getCacheCoordination();
            }
        };
    } 

    
    // **************** Existence Checking ***************

    private PropertyValueModel buildExistenceCheckingHolder() {
        return new PropertyAspectAdapter(this.nonNullCachingPolicyHolder, MWCachingPolicy.EXISTENCE_CHECKING_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getExistenceChecking();
            }
            
            protected void setValueOnSubject(Object value) {
                ((MWCachingPolicy) this.subject).setExistenceChecking((ExistenceCheckingOption) value);
            }
        };
    }
    
    private ListValueModel buildExistenceCheckingCollectionHolder() {
        return new AbstractReadOnlyListValueModel() {
            public Object getValue() {
                return MWDescriptorCachingPolicy.existenceCheckingOptions().toplinkOptions();
            }
        };  
    }
    
    private ListCellRenderer buildExistenceCheckingOptionRenderer(final PropertyValueModel projectExistenceCheckingHolder) {
        return new SimpleListCellRenderer() {
            protected String buildText(Object value) {
                if (((ExistenceCheckingOption) value).getMWModelOption() == MWCachingPolicy.EXISTENCE_CHECKING_PROJECT_DEFAULT) {
                    if (projectExistenceCheckingHolder.getValue() == null) {
                        return resourceRepository().getString(((ExistenceCheckingOption) value).resourceKey());
                    }
                    return resourceRepository().getString(
                            ((ExistenceCheckingOption) value).resourceKey(),
                            resourceRepository().getString(((ExistenceCheckingOption) projectExistenceCheckingHolder.getValue()).resourceKey())
                            );
                }
                return resourceRepository().getString(((ExistenceCheckingOption) value).resourceKey());
            }
        };
    }
    
    private PropertyValueModel buildProjectDefaultExistenceCheckingHolder() {
        return new PropertyAspectAdapter(this.projectDefaultCachingPolicyHolder, MWCachingPolicy.EXISTENCE_CHECKING_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWCachingPolicy) this.subject).getExistenceChecking();
            }
        };
    }
    
    private MWDescriptor rootDescriptor() {
        return ((MWMappingDescriptor) getSelectionHolder().getValue()).getInheritancePolicy().getRootDescriptor();
    }
}
