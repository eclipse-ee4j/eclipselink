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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.db;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWDescriptorGenerator;
import org.eclipse.persistence.tools.workbench.mappingsmodel.generation.MWRelationshipHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.relational.RelationalProjectNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


final class DescriptorGenerationCoordinator {
	
	private WorkbenchContext context;
	
	DescriptorGenerationCoordinator(WorkbenchContext context) {
		super();
		initialize(context);
	}
	
	private void initialize(WorkbenchContext context) {
		this.context = context;
	}

	private Vector calculatePossibleRelationships(Collection tables) {
		Vector possibleRelationships = new Vector();
		for (Iterator tableIt = tables.iterator(); tableIt.hasNext();) {
			for (Iterator references = ((MWTable) tableIt.next()).references(); references.hasNext();) {
				MWReference reference = (MWReference) references.next();
				if ((!reference.isForeignKeyReference()) || (!tables.contains(reference.getTargetTable())))
					continue;
				// add a possible relationship for both directions (the boolean indicates foreign key is in target table)
				possibleRelationships.add(new MWRelationshipHolder(reference, false));
				possibleRelationships.add(new MWRelationshipHolder(reference, true));
			}
		}
		return possibleRelationships;
	}
	
	private boolean checkForProjectSave(RelationalProjectNode projectNode) {
		if (projectNode.isDirty())
			return promptToSaveProject(projectNode);
		else
			return true;
	}
	
	void generateClassDescriptorsForAllTables(RelationalProjectNode projectNode) {
		generateClassDescriptorsForTables(projectNode, CollectionTools.collection(projectNode.getProject().getDatabase().tables()));
	}
	
	void generateClassDescriptorsForSelectedTables(RelationalProjectNode projectNode, Collection selectedTables) {
		generateClassDescriptorsForTables(projectNode, selectedTables);
	}
	
	private void generateClassDescriptorsForTables(RelationalProjectNode projectNode, Collection tables) {
		generateDescriptorsForTables(projectNode, tables);
	}
	
	private void generateDescriptorsForTables(RelationalProjectNode projectNode, Collection tables) {
		if (!checkForProjectSave(projectNode))
			return;
		DescriptorGenerationDialog descriptorDialog = new DescriptorGenerationDialog((MWRelationalProject) projectNode.getProject(), context);
		descriptorDialog.show();
		if (descriptorDialog.wasCanceled()) {
			return;
		}
		
		Vector possibleRelationships = new Vector();
		Collection relationshipsToCreate = new Vector();
		boolean generateBidirectionalRelationships = false;
		if (!possibleRelationships.isEmpty()) {
			RelationshipGenerationDialog relationshipDialog = new RelationshipGenerationDialog(possibleRelationships, context);
			relationshipDialog.show();
			if (relationshipDialog.wasCanceled()) {
				return;
			}
			relationshipsToCreate = relationshipDialog.getRelationshipsToCreate();
			generateBidirectionalRelationships = relationshipDialog.getGenerateBidirectionalRelationships();
		}
		MWDescriptorGenerator generator = new MWDescriptorGenerator();
		generator.setProject((MWRelationalProject) projectNode.getProject());
		generator.setTables(tables);
		generator.setPackageName(descriptorDialog.getPackageName());
		generator.setGenerateMethodAccessors(descriptorDialog.getGenerateAccessors());
		generator.setGenerateBidirectionalRelationships(generateBidirectionalRelationships);
		generator.setRelationshipsToCreate(relationshipsToCreate);
		generator.generateClassesAndDescriptors();
		// show successful dialog
		String successDialogString = "generateClassesAndDescriptors";
			
		JOptionPane.showMessageDialog(
			context.getCurrentWindow(),
			resourceRepository().getString(successDialogString + ".message"), 
			resourceRepository().getString(successDialogString + ".title"),
			JOptionPane.INFORMATION_MESSAGE);
			
	}
		
	private boolean promptToSaveProject(RelationalProjectNode projectNode) {
		int selection = JOptionPane.showConfirmDialog(
			context.getCurrentWindow(),
			resourceRepository().getString("autoGeneratingClassAndDescriptor.message"),
			resourceRepository().getString("saveProject.title"),
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
			

		if (selection == JOptionPane.YES_OPTION) {
			if (projectNode.save(null,this.context)) {
				return true;
			} else {
				JOptionPane.showMessageDialog(
					context.getCurrentWindow(),
					resourceRepository().getString("saveProjectError.message"), 
					resourceRepository().getString("unableToCreateClassesAndDescriptors.title"),
					JOptionPane.INFORMATION_MESSAGE);

				return false;
			}
		} else {
			return selection != JOptionPane.CANCEL_OPTION && selection != JOptionPane.CLOSED_OPTION;
		}
	}
	
	private ResourceRepository resourceRepository() {
		return this.context.getApplicationContext().getResourceRepository();
	}
}
