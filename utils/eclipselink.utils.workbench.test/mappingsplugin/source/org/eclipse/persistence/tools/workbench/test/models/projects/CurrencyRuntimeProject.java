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

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;



public class CurrencyRuntimeProject {
	private Project runtimeProject;

	public CurrencyRuntimeProject() {
		this.runtimeProject = new Project();
		this.runtimeProject.setName("Currency Project");
		applyLogin();

		this.runtimeProject.addDescriptor(buildCommodityPriceDescriptor());
		this.runtimeProject.addDescriptor(buildCurrencyDescriptor());
		this.runtimeProject.addDescriptor(buildCurrencyRangeDescriptor());
		this.runtimeProject.addDescriptor(buildExchangeRateDescriptor());
	}

	public void applyLogin() {
		DatabaseLogin login = new DatabaseLogin();
		login.usePlatform(new MySQLPlatform());
		login.setDriverClassName(TestDatabases.mySQLDriverClassName());
		login.setConnectionString(TestDatabases.mySQLServerURL());
		login.setUserName(TestDatabases.userName());
		login.setPassword(TestDatabases.password());

		// Configuration properties.
		((TableSequence)login.getDefaultSequence()).setTableName("SEQUENCE");
		((TableSequence)login.getDefaultSequence()).setNameFieldName("SEQ_NAME");
		((TableSequence)login.getDefaultSequence()).setCounterFieldName("SEQ_COUNT");
		((TableSequence)login.getDefaultSequence()).setPreallocationSize(50);
		login.setShouldCacheAllStatements(false);
		login.setUsesByteArrayBinding(true);
		login.setUsesStringBinding(false);
		if (login.shouldUseByteArrayBinding()) { // Can only be used with binding.
			login.setUsesStreamsForBinding(false);
		}
		login.setShouldForceFieldNamesToUpperCase(false);
		login.setShouldOptimizeDataConversion(true);
		login.setShouldTrimStrings(true);
		login.setUsesBatchWriting(false);
		if (login.shouldUseBatchWriting()) { // Can only be used with batch writing.
			login.setUsesJDBCBatchWriting(true);
		}
		login.setUsesExternalConnectionPooling(false);
		login.setUsesExternalTransactionController(false);
		this.runtimeProject.setLogin(login);
	}


	public RelationalDescriptor buildCommodityPriceDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.currency.CommodityPrice.class.getName());

		descriptor.addTableName("COMMODITY_PRICE");
		descriptor.addPrimaryKeyFieldName("COMMODITY_PRICE.ID");

		// Descriptor properties.
		descriptor.setIsIsolated(false);
		descriptor.setAlias("CommodityPrice");

		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("COMMODITY_PRICE.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping typeMapping = new DirectToFieldMapping();
		typeMapping.setAttributeName("type");
		typeMapping.setFieldName("COMMODITY_PRICE.TYPE");
		descriptor.addMapping(typeMapping);

		DirectToFieldMapping periodLengthMapping = new DirectToFieldMapping();
		periodLengthMapping.setAttributeName("periodLength");
		periodLengthMapping.setFieldName("COMMODITY_PRICE.PERIOD");
		descriptor.addMapping(periodLengthMapping);


		AggregateObjectMapping currencyRangeMapping = new AggregateObjectMapping();
		currencyRangeMapping.setAttributeName("currencyRange");
		currencyRangeMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange.class.getName());
		currencyRangeMapping.setIsNullAllowed(false);
		currencyRangeMapping.addFieldNameTranslation("COMMODITY_PRICE.HIGH_UNIT", "highLimit_unit->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("COMMODITY_PRICE.TREND", "trend->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("COMMODITY_PRICE.LOW_VALUE", "lowLimit_value->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("COMMODITY_PRICE.HIGH_VALUE", "highLimit_value->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("COMMODITY_PRICE.LOW_UNIT", "lowLimit_unit->DIRECT");
		descriptor.addMapping(currencyRangeMapping);

		return descriptor;
	}

	public RelationalDescriptor buildCurrencyDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.currency.Currency.class.getName());

		// Descriptor properties.
		descriptor.setAlias("Currency");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping unitMapping = new DirectToFieldMapping();
		unitMapping.setAttributeName("unit");
		unitMapping.setFieldName("unit->DIRECT");
		descriptor.addMapping(unitMapping);

		DirectToFieldMapping valueMapping = new DirectToFieldMapping();
		valueMapping.setAttributeName("value");
		valueMapping.setFieldName("value->DIRECT");
		descriptor.addMapping(valueMapping);

		return descriptor;
	}

	public RelationalDescriptor buildCurrencyRangeDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange.class.getName());

		// Descriptor properties.
		descriptor.setAlias("CurrencyRange");

		// Query manager.
		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping trendMapping = new DirectToFieldMapping();
		trendMapping.setAttributeName("trend");
		trendMapping.setFieldName("trend->DIRECT");
		descriptor.addMapping(trendMapping);

		AggregateObjectMapping highLimitMapping = new AggregateObjectMapping();
		highLimitMapping.setAttributeName("highLimit");
		highLimitMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.currency.Currency.class.getName());
		highLimitMapping.setIsNullAllowed(false);
		highLimitMapping.addFieldNameTranslation("highLimit_unit->DIRECT", "unit->DIRECT");
		highLimitMapping.addFieldNameTranslation("highLimit_value->DIRECT", "value->DIRECT");
		descriptor.addMapping(highLimitMapping);

		AggregateObjectMapping lowLimitMapping = new AggregateObjectMapping();
		lowLimitMapping.setAttributeName("lowLimit");
		lowLimitMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.currency.Currency.class.getName());
		lowLimitMapping.setIsNullAllowed(false);
		lowLimitMapping.addFieldNameTranslation("lowLimit_unit->DIRECT", "unit->DIRECT");
		lowLimitMapping.addFieldNameTranslation("lowLimit_value->DIRECT", "value->DIRECT");
		descriptor.addMapping(lowLimitMapping);

		return descriptor;
	}

	public RelationalDescriptor buildExchangeRateDescriptor() {
		RelationalDescriptor descriptor = new RelationalDescriptor();
		descriptor.setJavaClassName(org.eclipse.persistence.tools.workbench.test.models.currency.ExchangeRate.class.getName());
		descriptor.addTableName("EXCHANGE_RATE");
		descriptor.addPrimaryKeyFieldName("EXCHANGE_RATE.ID");

		// Descriptor properties.
		descriptor.useFullIdentityMap();
		descriptor.setIdentityMapSize(405);
		descriptor.setAlias("ExchangeRate");
		descriptor.setIsIsolated(false);
		
		// Query manager.
		descriptor.getDescriptorQueryManager().checkDatabaseForDoesExist();

		//Named Queries

		// Event manager.

		// Mappings.
		DirectToFieldMapping idMapping = new DirectToFieldMapping();
		idMapping.setAttributeName("id");
		idMapping.setFieldName("EXCHANGE_RATE.ID");
		descriptor.addMapping(idMapping);

		DirectToFieldMapping periodLengthMapping = new DirectToFieldMapping();
		periodLengthMapping.setAttributeName("periodLength");
		periodLengthMapping.setFieldName("EXCHANGE_RATE.PERIOD");
		descriptor.addMapping(periodLengthMapping);

		AggregateObjectMapping currencyRangeMapping = new AggregateObjectMapping();
		currencyRangeMapping.setAttributeName("currencyRange");
		currencyRangeMapping.setReferenceClassName(org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange.class.getName());
		currencyRangeMapping.setIsNullAllowed(false);
		currencyRangeMapping.addFieldNameTranslation("EXCHANGE_RATE.HI_UNIT", "highLimit_unit->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("EXCHANGE_RATE.TREND", "trend->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("EXCHANGE_RATE.LO_VALUE", "lowLimit_value->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("EXCHANGE_RATE.HI_VALUE", "highLimit_value->DIRECT");
		currencyRangeMapping.addFieldNameTranslation("EXCHANGE_RATE.LO_UNIT", "lowLimit_unit->DIRECT");
		descriptor.addMapping(currencyRangeMapping);

		return descriptor;
	}


	public Project getRuntimeProject() {
		return this.runtimeProject;
	}
}
