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
import java.awt.Insets;
import java.util.Collections;
import java.util.ListIterator;

import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorExtractionMethodPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class ClassExtractionMethodPanel extends AbstractSubjectPanel implements RootListener, ExtractionMethodListener {

	private boolean isRoot;
	
	private boolean isExtractionMethod;

	private ListChooser classExtractionMethodListChooser;
	
	ClassExtractionMethodPanel(ApplicationContext context, PropertyValueModel classExtractionMethodIndicatorPolicy) {
		super(classExtractionMethodIndicatorPolicy, context);
	}

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();

		this.classExtractionMethodListChooser = buildClassExtractionMethodChooser();
        this.classExtractionMethodListChooser.putClientProperty("labeledBy", new JLabel(" "));

		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(this.classExtractionMethodListChooser, constraints);

		new ComponentEnabler(buildUseClassExtractionMethodHolder(), Collections.singleton(this.classExtractionMethodListChooser));
	}

	private ValueModel buildMWClassHolder() {
		return new PropertyAspectAdapter(getSubjectHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorInheritancePolicy) ((MWClassIndicatorPolicy) this.subject).getParent()).getOwningDescriptor().getMWClass();
			}
		};
	}
    
    // **************** Class Extraction Method chooser initialization *************************
    
    private ListChooser buildClassExtractionMethodChooser() {
        ListChooser listChooser = 
            new DefaultListChooser(
                this.buildExtendedClassExtractionMethodComboBoxModel(),
                this.getWorkbenchContextHolder(),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()),
                this.buildPostLoadMethodListDialogBuilder()
            );
        listChooser.setRenderer(DescriptorComponentFactory.buildMethodRenderer(resourceRepository()));
        
        return listChooser;
    }
    
    private CachingComboBoxModel buildExtendedClassExtractionMethodComboBoxModel() {
        return new ExtendedComboBoxModel(this.buildClassExtractionMethodComboBoxModel());
    }
    
    private CachingComboBoxModel buildClassExtractionMethodComboBoxModel() {
        return new IndirectComboBoxModel(this.buildClassExtractionMethodChooserPropertyAdapter(), this.buildMWClassHolder()) {
            protected ListIterator listValueFromSubject(Object subject) {
                return ClassExtractionMethodPanel.this.orderedClassExtractionMethodChoices((MWClass) subject);
            }
        };
    }
    
    ListIterator orderedClassExtractionMethodChoices(MWClass mwClass) {
        return CollectionTools.sort(mwClass.candidateClassExtractionMethods()).listIterator();
    }
    
    private DefaultListChooserDialog.Builder buildPostLoadMethodListDialogBuilder() {
        DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
        builder.setTitleKey("CLASS_EXTRACTION_METHOD_LIST_BROWSER_DIALOG.title");
        builder.setListBoxLabelKey("CLASS_EXTRACTION_METHOD_LIST_BROWSER_DIALOG.listLabel");
        builder.setStringConverter(DescriptorComponentFactory.buildMethodStringConverter());
        return builder;
    }

	private PropertyValueModel buildClassExtractionMethodChooserPropertyAdapter() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWClassIndicatorExtractionMethodPolicy.METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassIndicatorExtractionMethodPolicy) this.subject).getClassExtractionMethod();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWClassIndicatorExtractionMethodPolicy) this.subject).setClassExtractionMethod((MWMethod) value);
			}	
		};	
	}


	private ValueModel buildUseClassExtractionMethodHolder() {
		return new PropertyAspectAdapter(getSubjectHolder()) {
			protected Object buildValue() {
				return this.subject == null ? Boolean.FALSE : Boolean.TRUE;
			}
		};
	}
	
	public void updateRootStatus(boolean newValue) {
		this.isRoot = newValue;
		this.updateEnablementStatus();
	}
	
	public void updateExtractionMethodStatus(boolean newValue) {
		this.isExtractionMethod = newValue;
		this.updateEnablementStatus();
	}
	
	protected void updateEnablementStatus() {
        this.classExtractionMethodListChooser.setEnabled(this.isRoot() && this.isExtractionMethod());
	}

	public boolean isRoot() {
		return this.isRoot;
	}

	public boolean isExtractionMethod() {
		return this.isExtractionMethod;
	}

}
