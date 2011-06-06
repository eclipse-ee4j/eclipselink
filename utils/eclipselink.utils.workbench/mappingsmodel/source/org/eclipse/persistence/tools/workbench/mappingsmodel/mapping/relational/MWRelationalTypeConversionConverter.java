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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

import org.eclipse.persistence.platform.database.oracle.NCharacter;
import org.eclipse.persistence.platform.database.oracle.NClob;
import org.eclipse.persistence.platform.database.oracle.NString;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWRelationalTypeConversionConverter extends MWTypeConversionConverter {
	
	// **************** Constructors ******************
	
	/** Default constructor - for TopLink use only */
	private MWRelationalTypeConversionConverter() {
		super();
	}

	protected MWRelationalTypeConversionConverter(MWConverterMapping parent) {
		super(parent);
	}

	//only used for legacy projects
	public MWRelationalTypeConversionConverter(MWConverterMapping parent, Map legacyValueMap) {
		super(parent);
		this.legacyInitialize(legacyValueMap);
	}


	// **************** Methods ******************

	public Set getBasicTypes() {
		Set types = super.getBasicTypes();
		this.addNTypeDatabaseTypeDeclarationsTo(types);
		this.addOracleSpecificDatabaseTypeDeclarationsTo(types);
		return types;
	}

	private static final Class[] N_TYPE_CLASSES = new Class[] {
		//TODO: replace with newer versions once newer TL is finalized
		NCharacter.class,
		NString.class,
		NClob.class
	};

	private Iterator nTypes() {
		return new TransformationIterator(new ArrayIterator(N_TYPE_CLASSES)) {
			protected Object transform(Object next) {
				return MWRelationalTypeConversionConverter.this.typeFor((Class) next);
			}
		};
	}

	private void addNTypeDatabaseTypeDeclarationsTo(Collection typeDeclarations) {
		for (Iterator stream = this.nTypes(); stream.hasNext(); ) {
			typeDeclarations.add(new MWTypeDeclaration(this, (MWClass) stream.next()));
		}
	}

	private static final String[] ORACLE_SPECIFIC_TYPE_NAMES = new String[] {
		"oracle.sql.TIMESTAMP",
		"oracle.sql.TIMESTAMPLTZ",
		"oracle.sql.TIMESTAMPTZ"
	};

	private Iterator oracleSpecificTypes() {
		return new TransformationIterator(new ArrayIterator(ORACLE_SPECIFIC_TYPE_NAMES)) {
			protected Object transform(Object next) {
				return MWRelationalTypeConversionConverter.this.typeNamed((String) next);
			}
		};
	}

	private void addOracleSpecificDatabaseTypeDeclarationsTo(Collection typeDeclarations) {
		for (Iterator stream = this.oracleSpecificTypes(); stream.hasNext(); ) {
			typeDeclarations.add(new MWTypeDeclaration(this, (MWClass) stream.next()));
		}
	}


	// **************** Problem Handling *****************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		if (this.getDataType() != null) {
			this.checkNType(currentProblems);
			this.checkOracleSpecificType(currentProblems);
		}
	}

	private void checkNType(List currentProblems) {
		if (this.dataTypeIsNType()
				&& ! this.getDatabase().getDatabasePlatform().containsDatabaseTypeNamed("NCHAR")) {
			currentProblems.add(this.buildProblem(ProblemConstants.MAPPING_NTYPE_NOT_SUPPORTED_ON_PLATFORM));
		}
	}
	
	private void checkOracleSpecificType(List currentProblems) {
		if (this.dataTypeIsOracleSpecificType()
				&& ! this.getDatabase().getDatabasePlatform().containsDatabaseTypeNamed("TIMESTAMP WITH LOCAL TIME ZONE")) {
			currentProblems.add(buildProblem(ProblemConstants.MAPPING_ORACLE_SPECIFIC_TYPE_NOT_SUPPORTED_ON_PLATFORM));
		}
	}
	
	private boolean dataTypeIsNType() {
		return CollectionTools.contains(this.nTypes(), this.getDataType().getType());
	}
	
	private boolean dataTypeIsOracleSpecificType() {
		return CollectionTools.contains(this.oracleSpecificTypes(), this.getDataType().getType());
	}


	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWRelationalTypeConversionConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWTypeConversionConverter.class);
		return descriptor;
	}	
	
}
