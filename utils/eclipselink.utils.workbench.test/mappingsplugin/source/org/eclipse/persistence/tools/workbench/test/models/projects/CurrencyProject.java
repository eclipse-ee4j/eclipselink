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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class CurrencyProject extends RelationalTestProject {

	
	public CurrencyProject() {
		super();
	}

	public static MWRelationalProject emptyProject() {
		return new MWRelationalProject("Currency Project", spiManager(), mySqlPlatform());
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
	
	@Override
	protected void initializeProject() {
		super.initializeProject();
	
		// Defaults policy
		getProject().getDefaultsPolicy().getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		getProject().getDefaultsPolicy().getCachingPolicy().setCacheSize(405);
		getProject().getDefaultsPolicy().getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		getProject().getDefaultsPolicy().setMethodAccessing(true);
	}

	protected void initializeCommodityPriceDescriptor() {
		MWClass commodityPriceClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.CommodityPrice");
	
		MWTableDescriptor descriptor;
		try {
			descriptor = (MWTableDescriptor) getProject().addDescriptorForType(commodityPriceClass);
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}

		MWTable table = tableNamed("COMMODITY_PRICE");
		descriptor.setPrimaryTable(table);
		descriptor.setActive(true);
	
		// Direct to field mappings
		addDirectMapping(descriptor, "id", table, "ID");
		addDirectMapping(descriptor, "type", table, "TYPE");
		addDirectMapping(descriptor, "periodLength", table, "PERIOD");
	
		// Aggregate mappings
		MWAggregateMapping currencyRangeMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("currencyRange"));
		MWClass currencyRangeClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange");
		currencyRangeMapping.setReferenceDescriptor(getProject().descriptorForType(currencyRangeClass));
		
		Iterator fieldAssociations = CollectionTools.sort(currencyRangeMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"HIGH_UNIT", "HIGH_VALUE", "LOW_UNIT", "LOW_VALUE", "TREND"};
		for (int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(table.columnNamed(fieldNames[i]));
		}
	}
	
	protected void initializeCommodityPriceTable() {
		MWTable table = database().addTable("COMMODITY_PRICE");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "TYPE", "varchar", 20);
		addField(table, "PERIOD", "integer");
		addField(table, "LOW_UNIT", "varchar", 20);
		addField(table, "LOW_VALUE", "integer");
		addField(table, "HIGH_UNIT", "varchar", 20);
		addField(table, "HIGH_VALUE", "integer");
		addField(table, "TREND", "varchar", 50);
	}
	
	protected void initializeCurrencyDescriptor() {
		MWAggregateDescriptor descriptor = this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.Currency");
	
		// Direct to field mappings
		addDirectMapping(descriptor, "unit");
		addDirectMapping(descriptor, "value");
	}
	
	protected void initializeCurrencyRangeDescriptor() {
	
		MWAggregateDescriptor descriptor = this.addAggregateDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange");
		descriptor.setActive(true);
	
		// Direct to field mappings
		addDirectMapping(descriptor, "trend");
	
		// Aggregate mappings
		MWClass currencyClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.Currency");
		
		// lowLimit aggregate mapping
		MWAggregateMapping lowLimitMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("lowLimit"));
		lowLimitMapping.setReferenceDescriptor(getProject().descriptorForType(currencyClass));
		
		//highLimit aggregate mapping
		MWAggregateMapping highLimitMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("highLimit"));
		highLimitMapping.setReferenceDescriptor(getProject().descriptorForType(currencyClass));
	}
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();
		this.initializeSequenceTable();
		this.initializeExchangeRateTable();
		this.initializeCommodityPriceTable();
	}

	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();
		initializeCurrencyDescriptor();
		initializeCurrencyRangeDescriptor();
		initializeExchangeRateDescriptor();
		initializeCommodityPriceDescriptor();
	}

	protected void initializeExchangeRateDescriptor()  {
		MWClass exchangeRateClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.ExchangeRate");
		MWTable exchangeRateTable = getProject().getDatabase().tableNamed("EXCHANGE_RATE");
		MWTableDescriptor descriptor;
		try {
			descriptor = (MWTableDescriptor) getProject().addDescriptorForType(exchangeRateClass);
		} catch (InterfaceDescriptorCreationException e) {
			throw new RuntimeException(e);
		}

		descriptor.setPrimaryTable(exchangeRateTable);
	
	
		// Caching Policy
		descriptor.getCachingPolicy().setCacheSize(405);
		descriptor.getCachingPolicy().setCacheType(MWCachingPolicy.CACHE_TYPE_FULL);
		descriptor.getCachingPolicy().setExistenceChecking(MWCachingPolicy.EXISTENCE_CHECKING_CHECK_DATABASE);
		
		
		// Direct to field mappings
		
		addDirectMapping(descriptor, "id", exchangeRateTable, "ID");	
		addDirectMapping(descriptor, "periodLength", exchangeRateTable, "PERIOD");	
	
		// Aggregate mappings
		MWAggregateMapping currencyRangeMapping = descriptor.addAggregateMapping(descriptor.getMWClass().attributeNamed("currencyRange"));
		MWClass currencyRangeClass = refreshedTypeNamed("org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange");
		MWAggregateDescriptor currencyRangeDescriptor = (MWAggregateDescriptor) getProject().descriptorForType(currencyRangeClass);
		currencyRangeMapping.setReferenceDescriptor(currencyRangeDescriptor);
	
		Iterator fieldAssociations = CollectionTools.sort(currencyRangeMapping.pathsToFields()).iterator();
		String[] fieldNames = new String[] {"HI_UNIT", "HI_VALUE", "LO_UNIT", "LO_VALUE", "TREND"};
		for (int i=0; i<fieldNames.length; i++) {
			MWAggregatePathToColumn association = (MWAggregatePathToColumn) fieldAssociations.next();
			association.setColumn(exchangeRateTable.columnNamed(fieldNames[i]));
		}
	}
	
	protected void initializeExchangeRateTable() 
	{
		MWTable table = database().addTable("EXCHANGE_RATE");
		addPrimaryKeyField(table, "ID", "integer");
		addField(table, "PERIOD", "integer");
		addField(table, "LO_UNIT", "varchar", 20);
		addField(table, "LO_VALUE", "integer");
		addField(table, "HI_UNIT", "varchar", 20);
		addField(table, "HI_VALUE", "integer");
		addField(table, "TREND", "varchar", 50);
	}
	
	public MWTableDescriptor getExchangeRateDescriptor() {
		return tableDescriptorWithShortName("ExchangeRate");
	}
}
