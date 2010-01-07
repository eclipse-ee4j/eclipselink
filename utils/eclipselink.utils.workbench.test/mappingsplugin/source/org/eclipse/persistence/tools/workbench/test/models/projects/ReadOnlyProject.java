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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWManyToManyMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



public class ReadOnlyProject extends RelationalTestProject {


	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("ReadOnly", spiManager(), oraclePlatform());

		// Defaults policy  
		project.getDefaultsPolicy().getCachingPolicy().setCacheSize(100);
		project.getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE);
   		project.getDefaultsPolicy().setMethodAccessing(false);
		return project;
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	public MWTableDescriptor getActorDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Actor");
	}

	public MWTableDescriptor getAddressDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Address");
	}

	public MWTableDescriptor getCountryDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Country");
	}

	public MWTableDescriptor getMovieDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Movie");
	}

	public MWTableDescriptor getPromoterDescriptor() {
		return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Promoter");
	}

	public MWAggregateDescriptor getStudioDescriptor() {
		return (MWAggregateDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Studio");
	}

	public void initializeActorDescriptor() {
		
		MWTableDescriptor descriptor = getActorDescriptor();
		MWTable table = tableNamed("RO_ACTOR");
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ACT_ID"));
		
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//direct to fields
		addDirectMapping(descriptor, "id", table, "ACT_ID");
		addDirectMapping(descriptor, "minimumSalary", table, "MIN_SAL");
		addDirectMapping(descriptor, "name", table, "ACT_NAME");
		
		//many to many	
		MWManyToManyMapping moviesMapping = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("movies"));
		moviesMapping.setReadOnly(true);
		moviesMapping.setUseNoIndirection();
		moviesMapping.setReferenceDescriptor(getMovieDescriptor());
		MWTable relationTable = tableNamed("ACT_MOV");
		moviesMapping.setRelationTable(relationTable);
		moviesMapping.setSourceReference(relationTable.referenceNamed("ACT_MOV_RO_ACTOR"));
		moviesMapping.setTargetReference(relationTable.referenceNamed("ACT_MOV_RO_MOVIE"));	
	}

	public void initializeActMovTable() {
		MWTable table = database().addTable("ACT_MOV");
		
		addPrimaryKeyField(table,"ACT_ID", "decimal", 15);
		addPrimaryKeyField(table,"MOV_ID", "decimal", 15);
	}

	public void initializeAddressDescriptor() {
		
		MWTableDescriptor descriptor = getAddressDescriptor();
		MWTable table = tableNamed("RO_ADDR");
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("ADD_SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("ADD_ID"));
		
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_PROJECT_DEFAULT);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//direct to fields
		addDirectMapping(descriptor, "city", table, "CITY");
		addDirectMapping(descriptor, "id", table, "ADD_ID");
		addDirectMapping(descriptor, "streetAddress", table, "STREET");
		addDirectMapping(descriptor, "zipCode", table, "ZIP");

		
		//one to one
		MWOneToOneMapping countryMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("country"));
		countryMapping.setReferenceDescriptor(getCountryDescriptor());
		countryMapping.setReference(tableNamed("RO_ADDR").referenceNamed("RO_ADDR_COUNTRY"));	
	}

	public void initializeCountryDescriptor() {
		
		MWTableDescriptor descriptor = getCountryDescriptor();
		MWTable table = tableNamed("COUNTRY");
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("COUNTRY_SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("COUNTRY_ID"));
		
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//direct to fields
		addDirectMapping(descriptor, "id", table, "COUNTRY_ID");
		addDirectMapping(descriptor, "name", table, "NAME");
	}

	public void initializeCountryTable() {
		MWTable table = database().addTable("COUNTRY");
		
		addPrimaryKeyField(table,"COUNTRY_ID", "decimal", 15);
		addField(table,"NAME", "varchar", 50);
	}

	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeActMovTable();
		this.initializeCountryTable();
		this.initializeRoActorTable();
		this.initializeRoAddrTable();
		this.initializeRoMovieTable();
		this.initializeRoPromoTable();
		
		this.initializeTableReferences();
	}
	
	public void initializeTableReferences() {
		MWTable actMovTable = tableNamed("ACT_MOV");
		MWTable roActorTable = tableNamed("RO_ACTOR");
		MWTable roMovieTable = tableNamed("RO_MOVIE");
		MWTable countryTable = tableNamed("COUNTRY");
		MWTable roAddrTable = tableNamed("RO_ADDR");
		MWTable roPromoTable = tableNamed("RO_PROMO");

			
		this.addReferenceOnDB("ACT_MOV_RO_ACTOR", actMovTable, roActorTable, "ACT_ID", "ACT_ID");
		this.addReferenceOnDB("ACT_MOV_RO_ACTOR2", actMovTable, roActorTable, "ACT_ID", "ACT_ID");
		this.addReferenceOnDB("ACT_MOV_RO_MOVIE", actMovTable, roMovieTable, "MOV_ID", "MOV_ID");
		this.addReferenceOnDB("ACT_MOV_RO_MOVIE2", actMovTable, roMovieTable, "MOV_ID", "MOV_ID");
		this.addReferenceOnDB("RO_ADDR_COUNTRY", roAddrTable, countryTable, "COUNTRY_ID", "COUNTRY_ID");
		this.addReferenceOnDB("RO_MOVIE_RO_ADDR", roMovieTable, roAddrTable, "STD_ADD", "ADD_ID");
		this.addReferenceOnDB("RO_MOVIE_RO_PROMO", roMovieTable, roPromoTable, "PROMO_ID", "PROMO_ID");
	}

	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Actor");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Address");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Country");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Movie");
		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Promoter");

		this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.readonly.Studio");
		
		initializeActorDescriptor();
		initializeAddressDescriptor();
		initializeCountryDescriptor();
		initializePromoterDescriptor();
		initializeStudioDescriptor();
		initializeMovieDescriptor();
	}

	public void initializeMovieDescriptor() {
		
		MWTableDescriptor descriptor = getMovieDescriptor();
		MWTable table = tableNamed("RO_MOVIE");
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("MOV_SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("MOV_ID"));
		
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//direct to fields
		addDirectMapping(descriptor, "id", table, "MOV_ID");
		addDirectMapping(descriptor, "title", table, "TITLE");

		//1-1
		MWOneToOneMapping promoterMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("promoter"));
		promoterMapping.setReferenceDescriptor(getPromoterDescriptor());
		promoterMapping.setReference(table.referenceNamed("RO_MOVIE_RO_PROMO"));	
		
		//many to many	
		MWManyToManyMapping actorsMapping = descriptor.addManyToManyMapping(descriptor.getMWClass().attributeNamed("actors"));
		actorsMapping.setPrivateOwned(true);
		actorsMapping.setUseNoIndirection();
		actorsMapping.setReferenceDescriptor(getActorDescriptor());
		MWTable relationTable = tableNamed("ACT_MOV");
		actorsMapping.setRelationTable(relationTable);
		actorsMapping.setSourceReference(relationTable.referenceNamed("ACT_MOV_RO_MOVIE2"));
		actorsMapping.setTargetReference(relationTable.referenceNamed("ACT_MOV_RO_ACTOR2"));	
			
		//aggregate mapping
		MWAggregateMapping studioMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("studio"));
		studioMapping.setReferenceDescriptor(getStudioDescriptor());
		studioMapping.setReadOnly(true);
		
		Iterator fieldAssociations = CollectionTools.sort(studioMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"STD_ADD", "STD_NAME", "STD_OWN"};
		for(int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}
		
		
	}
	
	public void initializePromoterDescriptor() {
		
		MWTableDescriptor descriptor = getPromoterDescriptor();
		MWTable table = tableNamed("RO_PROMO");
		descriptor.setPrimaryTable(table);

		//sequencing
		descriptor.setUsesSequencing(true);
		descriptor.setSequenceNumberName("PROMO_SEQ");
		descriptor.setSequenceNumberTable(table);
		descriptor.setSequenceNumberColumn(table.columnNamed("PROMO_ID"));
		
		//caching policy
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
        descriptor.getCachingPolicy().setCacheSize(100);
	
		//direct to fields
		addDirectMapping(descriptor, "id", table, "PROMO_ID");
		addDirectMapping(descriptor, "name", table, "NAME");
	}
	public void initializeRoActorTable() {
		MWTable table = database().addTable("RO_ACTOR");
		
		addPrimaryKeyField(table,"ACT_ID", "decimal", 15);
		addField(table,"ACT_NAME", "varchar", 50);
		addField(table,"MIN_SAL", "decimal", 19);
	}

	public void initializeRoAddrTable() {
		MWTable table = database().addTable("RO_ADDR");
		
		addPrimaryKeyField(table,"ADD_ID", "decimal", 15);
		addField(table,"CITY", "varchar", 30);
		addField(table,"COUNTRY_ID", "decimal", 15);
		addField(table,"STREET", "varchar", 30);
		addField(table,"ZIP", "varchar", 10);
	}

	public void initializeRoMovieTable() {
		MWTable table = database().addTable("RO_MOVIE");
		
		addPrimaryKeyField(table,"MOV_ID", "decimal", 15);
		addField(table,"PROMO_ID", "decimal", 15);
		addField(table,"STD_ADD", "decimal", 15);
		addField(table,"STD_NAME", "varchar", 50);
		addField(table,"STD_OWN", "varchar", 50);
		addField(table,"TITLE", "varchar", 50);
	}

	public void initializeRoPromoTable() {
		MWTable table = database().addTable("RO_PROMO");
		
		addField(table,"NAME", "varchar", 20);
		addPrimaryKeyField(table,"PROMO_ID", "decimal", 15);
	}	

	public void initializeStudioDescriptor() {
		MWAggregateDescriptor descriptor = getStudioDescriptor();

	
		//direct to fields
		addDirectMapping(descriptor, "name");
		addDirectMapping(descriptor, "owner");
		
		//1-1
		MWOneToOneMapping addressMapping = descriptor.addOneToOneMapping(descriptor.getMWClass().attributeNamed("address"));
		addressMapping.setPrivateOwned(true);
		addressMapping.setReferenceDescriptor(getAddressDescriptor());
		addressMapping.setReference(tableNamed("RO_MOVIE").referenceNamed("RO_MOVIE_RO_ADDR"));
	}

}
