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

import java.awt.BorderLayout;
import java.util.Iterator;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWEisReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MappingCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.*;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


//TODO this should not have been copied directly from the ui.mapping.relational bundle!
final class MaintainsBidirectionalRelationshipPanel extends AbstractSubjectPanel
{
	MaintainsBidirectionalRelationshipPanel(ValueModel subjectHolder,
														 WorkbenchContextHolder context)
	{
		super(new BorderLayout(), subjectHolder, context);
	}

	private ButtonModel buildMaintainsBidirectionalityRelationshipCheckBoxAdapter()
	{
		return new CheckBoxModelAdapter(buildMaintainsBidirectionalityRelationshipHolder());
	}

	private PropertyValueModel buildMaintainsBidirectionalityRelationshipHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWEisReferenceMapping.MAINTAINS_BIDIRECTIONAL_RELATIONSHIP_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWEisReferenceMapping mapping = (MWEisReferenceMapping) subject;
				return Boolean.valueOf(mapping.maintainsBidirectionalRelationship());
			}

			protected void setValueOnSubject(Object value)
			{
				MWEisReferenceMapping mapping = (MWEisReferenceMapping) subject;
				mapping.setMaintainsBidirectionalRelationship(Boolean.TRUE.equals(value));
			}
		};
	}

	private ListChooser buildRelationshipPartnerChooser() {
		ListChooser chooser = 
			new DefaultListChooser(this.buildRelationshipPartnerComboAdapter(), this.getWorkbenchContextHolder());
		chooser.setRenderer(new AdaptableListCellRenderer(new MappingCellRendererAdapter(this.resourceRepository())));
		return chooser;
	}

	private CollectionValueModel buildRelationshipPartnerCollectionHolder()
	{
		PropertyAspectAdapter referenceDescriptorHolder = new PropertyAspectAdapter(getSubjectHolder(), MWEisReferenceMapping.REFERENCE_DESCRIPTOR_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWEisReferenceMapping mapping = (MWEisReferenceMapping) subject;
				return mapping.getReferenceDescriptor();
			}
		};

		return new CollectionAspectAdapter(referenceDescriptorHolder, MWMappingDescriptor.MAPPINGS_COLLECTION)
		{
			protected Iterator getValueFromSubject()
			{
				return ((MWMappingDescriptor) subject).mappings();
			}

			protected int sizeFromSubject()
			{
				return ((MWMappingDescriptor) subject).mappingsSize();
			}
		};
	}

	private ComboBoxModel buildRelationshipPartnerComboAdapter()
	{
		return new ComboBoxModelAdapter(buildRelationshipPartnerListModel(),
												  buildRelationshipPartnerHolder());
	}

	private PropertyValueModel buildRelationshipPartnerHolder()
	{
		return new PropertyAspectAdapter(getSubjectHolder(), MWEisReferenceMapping.RELATIONSHIP_PARTNER_MAPPING_PROPERTY)
		{
			protected Object getValueFromSubject()
			{
				MWEisReferenceMapping mapping = (MWEisReferenceMapping) subject;
				return mapping.getRelationshipPartnerMapping();
			}

			protected void setValueOnSubject(Object value)
			{
				MWEisReferenceMapping mapping = (MWEisReferenceMapping) subject;
				mapping.setRelationshipPartnerMapping((MWMapping) value);
			}
		};
	}

	private ListValueModel buildRelationshipPartnerListModel()
	{
		return new SortedListValueModelAdapter
		(
			new FilteringCollectionValueModel(buildRelationshipPartnerCollectionHolder())
			{
				protected boolean accept(Object value)
				{
					return value != subject();
				}
			}
		);
	}

	protected void initializeLayout()
	{
		JCheckBox maintainsBidiRelationshipCheckBox = buildCheckBox
		(
			"MAINTAINS_BIDI_RELATIONSHIP_CHECK_BOX",
			buildMaintainsBidirectionalityRelationshipCheckBoxAdapter()
		);

		JComponent relationshipPartnerWidgets = buildLabeledComponent
		(
			"MAINTAINS_BIDI_RELATIONSHIP_RELATIONSHIP_PARTNER_CHOOSER",
			buildRelationshipPartnerChooser()
		);

		new ComponentEnabler(buildMaintainsBidirectionalityRelationshipHolder(), relationshipPartnerWidgets.getComponents());

		GroupBox groupBox = new GroupBox(maintainsBidiRelationshipCheckBox, relationshipPartnerWidgets);
		add(groupBox, BorderLayout.CENTER);
		addHelpTopicId(this, "mapping.maintainsBidirectionalRelationship");
	}
}
