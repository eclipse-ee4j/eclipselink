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
package org.eclipse.persistence.tools.workbench.mappingsmodel.db;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNominative;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalForeignKeyColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.ExternalTableDescription;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.utility.NameTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public final class MWTable
	extends MWModel
	implements MWNominative
{
	/** the catalog should never be empty - it can be null, but not empty */
	private volatile String catalog;
		public static final String CATALOG_PROPERTY = "catalog";

	/** the schema should never be empty - it can be null, but not empty */
	private volatile String schema;
		public static final String SCHEMA_PROPERTY = "schema";

	/** the short name should never be null OR empty */
	private volatile String shortName;
		public static final String SHORT_NAME_PROPERTY = "shortName";

		public static final String QUALIFIED_NAME_PROPERTY = "qualifiedName";

	/** this will be null if it is not known */
	private Date lastRefreshTimestamp;
		public static final String LAST_REFRESH_TIMESTAMP_PROPERTY = "lastRefreshTimestamp";

	private Collection columns;
		public static final String COLUMNS_COLLECTION = "columns";

	private Collection references;
		public static final String REFERENCES_COLLECTION = "references";

	private boolean legacyIsFullyQualified;

	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWTable() {
		super();
	}

	MWTable(MWDatabase database, String catalog, String schema, String shortName) {
		super(database);
		this.catalog = catalog;
		this.schema = schema;
		this.shortName = shortName;
	}


	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.lastRefreshTimestamp = null;	// if the table is built by hand, this is left null
		this.columns = new Vector();
		this.references = new Vector();
	}


	// ********** name: catalog, schema, shortName **********

	public String getCatalog() {
		return this.catalog;
	}
  
	/**
	 * private - @see #rename(String, String, String)
	 */
	private void setCatalog(String catalog) {
		Object old = this.catalog;
		this.catalog = catalog;
		if (this.attributeValueHasChanged(old, catalog)) {
			this.firePropertyChanged(CATALOG_PROPERTY, old, catalog);
			this.qualifiedNameChanged();
		}
	}

	public String getSchema() {
		return this.schema;
	}  
	
	/**
	 * private - @see #rename(String, String, String)
	 */
	private void setSchema(String schema) {
		Object old = this.schema;
		this.schema = schema;
		if (this.attributeValueHasChanged(old, schema)) {
			this.firePropertyChanged(SCHEMA_PROPERTY, old, schema);
			this.qualifiedNameChanged();
		}
	}

	public String getShortName() {
		return this.shortName;
	}

	/**
	 * private - @see #rename(String, String, String)
	 */
	private void setShortName(String shortName) {
		Object old = this.shortName;
		this.shortName = shortName;
		if (this.attributeValueHasChanged(old, shortName)) {
			this.firePropertyChanged(SHORT_NAME_PROPERTY, old, shortName);
			this.qualifiedNameChanged();
		}
	}

	private void qualifiedNameChanged() {
		this.firePropertyChanged(QUALIFIED_NAME_PROPERTY, this.qualifiedName());
		this.getProject().nodeRenamed(this);
		for (Iterator stream = this.columns(); stream.hasNext(); ) {
			((MWColumn) stream.next()).qualifiedNameChanged();
		}		
	}
	

	// ********** columns **********

	/**
	 * this will be null if it is not known
	 */
	public Date getLastRefreshTimestamp() {
		return this.lastRefreshTimestamp;
	}

	/**
	 * PRIVATE - this can only be set internally
	 */
	private void setLastRefreshTimestamp(Date lastRefreshTimestamp) {
		Object old = this.lastRefreshTimestamp;
		this.lastRefreshTimestamp = lastRefreshTimestamp;
		this.firePropertyChanged(LAST_REFRESH_TIMESTAMP_PROPERTY, old, lastRefreshTimestamp);
	}
	

	// ********** columns **********

	public Iterator columns() {
		return new CloneIterator(this.columns) {
			protected void remove(Object current) {
				MWTable.this.removeColumn((MWColumn) current);
			}
		};
	}

	public int columnsSize() {
		return this.columns.size();
	}

	public MWColumn addColumn(String name) {
		this.checkColumnName(name);
		return this.addColumn(new MWColumn(this, name));
	}

	private MWColumn addColumn(MWColumn column) {
		this.addItemToCollection(column, this.columns, COLUMNS_COLLECTION);
		return column;
	}

	public void removeColumn(MWColumn column) {
		this.removeNodeFromCollection(column, this.columns, COLUMNS_COLLECTION);
	}

	public void removeColumns(Iterator cols) {
		while (cols.hasNext()) {
			this.removeColumn((MWColumn) cols.next());
		}
	}

	public void removeColumns(Collection cols) {
		this.removeColumns(cols.iterator());
	}

	public boolean containsColumnNamed(String columnName) {
		return this.columnNamed(columnName) != null;
	}

	/**
	 * only used for unqualified column names
	 * @see #columnWithQualifiedName(String qualifiedName)
	 */
	public MWColumn columnNamed(String unqualifiedColumnName) {
		synchronized (this.columns) {
			for (Iterator stream = this.columns.iterator(); stream.hasNext(); ) {
				MWColumn column = (MWColumn) stream.next();
				if (column.getName().equals(unqualifiedColumnName)) {
					return column;
				}
			}
		}
		return null;
	}

	/**
	 * return the column with the specified "qualified" name
	 */
	public MWColumn columnWithQualifiedName(String name) {
		if ( ! MWColumn.parseTableNameFromQualifiedName(name).equals(this.getName())) {
			throw new IllegalArgumentException();
		}
		return this.columnNamed(MWColumn.parseColumnNameFromQualifiedName(name));
	}

	public Iterator columnNames() {
		return new TransformationIterator(this.columns()) {
			protected Object transform(Object next) {
				return ((MWColumn) next).getName();
			}
		};
	}

	public int primaryKeyColumnsSize() {
		int size = 0;
		synchronized (this.columns) {
			for (Iterator stream = this.columns.iterator(); stream.hasNext(); ) {
				if (((MWColumn) stream.next()).isPrimaryKey()) {
					size++;
				}
			}
		}
		return size;
	}

	public Iterator primaryKeyColumns() {
		return new FilteringIterator(this.columns()) {
			protected boolean accept(Object o) {
				return ((MWColumn) o).isPrimaryKey();
			}
		};
	}

	public Iterator primaryKeyColumnNames() {
		return new TransformationIterator(this.primaryKeyColumns()) {
			protected Object transform(Object next) {
				return ((MWColumn) next).getName();
			}
		};
	}

	public Iterator nonPrimaryKeyColumns() {
		return new FilteringIterator(this.columns()) {
			protected boolean accept(Object o) {
				return ! ((MWColumn) o).isPrimaryKey();
			}
		};
	}

	/**
	 * used by table generation
	 */
	public MWColumn addColumnLike(MWColumn original) {
		MWColumn copy = this.addColumn(original.getName());
		copy.copySettingsFrom(original);
		return copy;
	}


	// ********** references **********

	public Iterator references() {
		return new CloneIterator(this.references) {
			protected void remove(Object current) {
				MWTable.this.removeReference((MWReference) current);
			}
		};
	}

	public int referencesSize() {
		return this.references.size();
	}

	public MWReference addReference(String name, MWTable targetTable) {
		this.checkReferenceName(name);
		return this.addReference(new MWReference(this, name, targetTable));
	}

	private MWReference addReference(MWReference reference) {
		this.addItemToCollection(reference, this.references, REFERENCES_COLLECTION);
		return reference;
	}	

	public void removeReference(MWReference reference) {
		this.removeNodeFromCollection(reference, this.references, REFERENCES_COLLECTION);
	}

	public void removeReferences(Iterator refs) {
		while (refs.hasNext()) {
			this.removeReference((MWReference) refs.next());
		}
	}

	public void removeReferences(Collection refs) {
		this.removeReferences(refs.iterator());
	}

	/**
	 * remove only the references among those specified
	 * that are defined on the database; leave any "virtual"
	 * user-defined references
	 */
	private void removeDatabaseReferences(Iterator refs) {
		while (refs.hasNext()) {
			MWReference ref = (MWReference) refs.next();
			if (ref.isOnDatabase()) {
				this.removeReference(ref);
			}
		}
	}

	/**
	 * remove only the references among those specified
	 * that are defined on the database; leave any "virtual"
	 * user-defined references
	 */
	private void removeDatabaseReferences(Collection refs) {
		this.removeDatabaseReferences(refs.iterator());
	}

	public boolean containsReferenceNamed(String referenceName) {
		return this.referenceNamed(referenceName) != null;
	}

	public MWReference referenceNamed(String referenceName) {
		synchronized (this.references) {
			for (Iterator stream = this.references.iterator(); stream.hasNext(); ) {
				MWReference reference = (MWReference) stream.next();
				if (reference.getName().equals(referenceName)) {
					return reference;
				}
			}
			return null;
		}
	}

	public Iterator referenceNames(){
		return new TransformationIterator(this.references()) {
			protected Object transform(Object next) {
				return ((MWReference) next).getName();
			}
		};
	}

	/**
	 * return the references that are actually present
	 * as constraints on the database
	 */
	public Iterator databaseReferences() {
		return new FilteringIterator(this.references()) {
			protected boolean accept(Object o) {
				return ((MWReference) o).isOnDatabase();
			}
		};
	}

	/**
	 * return all the references between the table and the
	 * specified table (either table can be the source and/or target)
	 */
	public Iterator referencesBetween(MWTable table) {
		return new CompositeIterator(this.referencesTo(table), table.referencesTo(this));
	}

	/**
	 * return all the references with the table as the source and the
	 * specified table as the target
	 */
	public Iterator referencesTo(final MWTable targetTable) {
		return new FilteringIterator(this.references()) {
			protected boolean accept(Object o) {
				return ((MWReference) o).getTargetTable() == targetTable;
			}
		};
	}


	// ********** Nominative implementation **********

	/**
	 * return the appropriately-qualified name
	 */
	public String getName() {
		return this.qualifiedName();
	}
	
	//  ********** queries **********

	public DatabasePlatform databasePlatform() {
		 return this.getDatabase().getDatabasePlatform();
	}

	boolean nameMatches(String cat, String sch, String sn) {
		return this.valuesAreEqual(this.catalog, cat) &&
			this.valuesAreEqual(this.schema, sch) &&
			this.valuesAreEqual(this.shortName, sn);
	}

	boolean nameMatchesIgnoreCase(String cat, String sch, String sn) {
		return StringTools.stringsAreEqualIgnoreCase(this.catalog, cat) &&
			StringTools.stringsAreEqualIgnoreCase(this.schema, sch) &&
			StringTools.stringsAreEqualIgnoreCase(this.shortName, sn);
	}

	/**
	 * if either the 'catalog' or 'schema' are specified,
	 * the table's name is "qualified"
	 */
	public boolean nameIsQualified() {
		if (this.catalog != null) {
			return true;
		}
		if (this.schema != null) {
			return true;
		}
		return false;
	}

	public boolean nameIsUnqualified() {
		return ! this.nameIsQualified();
	}

	public String qualifiedName() {
		return NameTools.buildQualifiedDatabaseObjectName(this.catalog, this.schema, this.shortName);
	}

	public String unqualifiedName() {
		return this.shortName;
	}

	/**
	 * used for table generation:
	 *     catalog.schema
	 *     catalog.
	 *     schema
	 */
	private String qualifier() {
		if (this.nameIsUnqualified()) {
			return "";
		}
		StringBuffer sb = new StringBuffer(100);
		if (this.catalog != null) {
			sb.append(this.catalog);
		}
		if (this.schema != null) {
			if (this.catalog != null) {
			sb.append('.');
		}
			sb.append(this.schema);
		}
		return sb.toString();
	}


	// ********** miscellaneous behavior **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.columns) { children.addAll(this.columns); }
		synchronized (this.references) { children.addAll(this.references); }
	}

	public void rename(String newCatalog, String newSchema, String newShortName) {
		if (this.nameMatches(newCatalog, newSchema, newShortName)) {
			// if someone is tryng to rename a table to its existing name, ignore it
			return;
		}
		this.getDatabase().checkTableName(newCatalog, newSchema, newShortName, this);
		this.setCatalog(newCatalog);
		this.setSchema(newSchema);
		this.setShortName(newShortName);
		this.getDatabase().tableRenamed();	// we must notify the database of a name change
	}

	/**
	 * disallow duplicate column names
	 */
	void checkColumnName(String columnName) {
		if ((columnName == null) || (columnName.length() == 0)) {
			throw new IllegalArgumentException();
		}
		if (this.containsColumnNamed(columnName)) {
			throw new IllegalArgumentException("duplicate column name: " + columnName);
		}
	}

	/**
	 * disallow duplicate reference names
	 */
	void checkReferenceName(String referenceName) {
		if ((referenceName == null) || (referenceName.length() == 0)) {
			throw new IllegalArgumentException();
		}
		if (this.containsReferenceNamed(referenceName)) {
			throw new IllegalArgumentException("duplicate reference name: " + referenceName);
		}
	}

	void databasePlatformChanged() {
		synchronized (this.columns) {
			for (Iterator stream = this.columns.iterator(); stream.hasNext(); ) {
				((MWColumn) stream.next()).databasePlatformChanged();
			}
		}
	}


	// ********** problems **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);

		int identityTypeCount = 0;
		synchronized (this.columns) {
			for (Iterator stream = this.columns.iterator(); stream.hasNext(); ) {
				MWColumn column = (MWColumn) stream.next();
				if (column.isIdentity()) {
					identityTypeCount++;
				}
			}
		}
		if (identityTypeCount > 1) {
			currentProblems.add(this.buildProblem(ProblemConstants.TABLE_TOO_MANY_IDENTITY_COLUMNS));
		}
	}


	// ********** importing/refreshing **********

	/**
	 * return the "external" table descriptions that share the table's name;
	 * typically this will return multiple entries only when the table's name
	 * is unqualified
	 */
	public Iterator matchingExternalTableDescriptions() {
		return this.getDatabase().externalTableDescriptions(this.catalog, this.schema, this.shortName, null);
	}

	/**
	 * refresh the table's columns (but not the table's references - that
	 * must be performed separately);
	 */
	void refreshColumns(ExternalTable externalTable) {
		// after we have looped through the external columns,
		// 'removedColumns' will be left with the columns that need to be removed
		Collection removedColumns;
		synchronized (this.columns) {
			removedColumns = new HashSet(this.columns);
		}
		ExternalColumn[] externalColumns = externalTable.getColumns();
		for (int i = externalColumns.length; i-- > 0; ) {
			this.refreshColumn(externalColumns[i], removedColumns);
		}
		this.removeColumns(removedColumns);
		this.setLastRefreshTimestamp(new Date());
	}

	/**
	 * refresh the column corresponding to the specified "external" column
	 */
	private void refreshColumn(ExternalColumn externalColumn, Collection removedColumns) {
		MWColumn existingColumn = this.columnNamed(externalColumn.getName());
		if (existingColumn == null) {
			// we have a new column
			existingColumn = this.addColumn(externalColumn.getName());
		} else {
			// retain the existing column
			removedColumns.remove(existingColumn);
		}
		existingColumn.refresh(externalColumn);
	}

	/**
	 * refresh the table's references - this should be called after
	 * the table's columns have been refreshed and any target tables
	 * have had their columns refreshed - this will allow us to build
	 * the references properly
	 */
	void refreshReferences(ExternalTable externalTable) {
		// after we have looped through the foreign keys,
		// 'removedReferences' will be left with the references that need to be removed
		Collection removedReferences;
		synchronized (this.references) {
			removedReferences = new HashSet(this.references);
		}
		ExternalForeignKey[] externalForeignKeys = externalTable.getForeignKeys();
		for (int i = externalForeignKeys.length; i-- > 0; ) {
			this.refreshReference(externalForeignKeys[i], removedReferences);
		}

		// remove *only* the remaining references that were originally defined on the database;
		// leave the "virtual" user-defined references intact
		this.removeDatabaseReferences(removedReferences);
	}

	/**
	 * refresh the reference corresponding to the specified "external" foreign key;
	 * search the 'removedReferences' so that we don't ever
	 * refresh the same reference twice; first search for a match based
	 * on name, then search for a match based on the column pairs - this
	 * should prevent us from getting a match based on columns that has
	 * the same name as another reference with different columns that
	 * is further down the list
	 */
	private void refreshReference(ExternalForeignKey externalForeignKey, Collection removedReferences) {
		// first, find the target table
		ExternalTableDescription ttd = externalForeignKey.getTargetTableDescription();
		MWTable targetTable = this.getDatabase().tableNamed(ttd.getCatalogName(), ttd.getSchemaName(), ttd.getName());
		if (targetTable == null) {
			// the target table may have been imported without
			// a fully-qualified name, so try that also
			targetTable = this.getDatabase().tableNamed(null, null, ttd.getName());
		}
		// if we don't have the target table, we can't build a reference to it
		if (targetTable == null) {
			return;
		}

		// look for a match based on name
		for (Iterator stream = removedReferences.iterator(); stream.hasNext(); ) {
			MWReference ref = (MWReference) stream.next();
			if (ref.getName().equals(externalForeignKey.getName())) {
				ref.setTargetTable(targetTable);
				ref.refreshColumnPairs(externalForeignKey);
				ref.setOnDatabase(true);
				removedReferences.remove(ref);
				return;
			}
		}

		// look for a match based on column pairs
		for (Iterator stream = removedReferences.iterator(); stream.hasNext(); ) {
			MWReference ref = (MWReference) stream.next();
			if (ref.matchesColumnPairs(externalForeignKey)) {
				ref.setName(externalForeignKey.getName());
				ref.setTargetTable(targetTable);
				ref.setOnDatabase(true);
				removedReferences.remove(ref);
				return;
			}
		}

		// no match - we have a new reference
		MWReference ref = this.addReference(externalForeignKey.getName(), targetTable);
		ExternalForeignKeyColumnPair[] pairs = externalForeignKey.getColumnPairs();
		for (int i = pairs.length; i-- > 0; ) {
			ref.addColumnPair(this.column(pairs[i].getSourceColumn()), targetTable.column(pairs[i].getTargetColumn()));
		}
		ref.setOnDatabase(true);
	}

	/**
	 * return the column with the same name as the specified "external" column
	 */
	MWColumn column(ExternalColumn externalColumn) {
		return (externalColumn == null) ? null : this.columnNamed(externalColumn.getName());
	}


 	// ********** runtime conversion ***********

	/**
	 * return a run-time table definition corresponding to the table
	 */
	TableDefinition buildRuntimeTableDefinition() {
		TableDefinition td = new TableDefinition();
		td.setName(this.shortName);
		td.setQualifier(this.qualifier());
		synchronized (this.columns) {
			for (Iterator stream = this.columns.iterator(); stream.hasNext(); ) {
				td.addField(((MWColumn) stream.next()).buildRuntimeFieldDefinition());
			}
		}
		// only add the references that are defined on the database
		synchronized (this.references) {
			for (Iterator stream = this.references.iterator(); stream.hasNext(); ) {
				MWReference ref = (MWReference) stream.next();
				if (ref.isOnDatabase()) {
					td.addForeignKeyConstraint(ref.buildRuntimeConstraint());
				}
			}
		}
		return td;
	}


	// ********** printing and displaying **********

	public void toString(StringBuffer sb) {
		sb.append(this.qualifiedName());
	}

	public String displayString() {
		return this.qualifiedName();
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWTable.class);

		descriptor.setDefaultRootElement("table");

		descriptor.addDirectMapping("catalog", "catalog/text()");
		descriptor.addDirectMapping("schema", "schema/text()");
		descriptor.addDirectMapping("shortName", "short-name/text()");

		descriptor.addDirectMapping("lastRefreshTimestamp", "last-refresh-timestamp/text()");

		XMLCompositeCollectionMapping columnsMapping = new XMLCompositeCollectionMapping();
		columnsMapping.setAttributeName("columns");
		columnsMapping.setGetMethodName("getColumnsForTopLink");
		columnsMapping.setSetMethodName("setColumnsForTopLink");
		columnsMapping.setReferenceClass(MWColumn.class);
		columnsMapping.setXPath("columns/column");
		descriptor.addMapping(columnsMapping);
		
		XMLCompositeCollectionMapping referencesMapping = new XMLCompositeCollectionMapping();
		referencesMapping.setAttributeName("references");
		referencesMapping.setGetMethodName("getReferencesForTopLink");
		referencesMapping.setSetMethodName("setReferencesForTopLink");
		referencesMapping.setReferenceClass(MWReference.class);
		referencesMapping.setXPath("references/table-association");
		descriptor.addMapping(referencesMapping);
				
		return descriptor;
	}

	/**
	 * sort the columns for TopLink
	 */
	private Collection getColumnsForTopLink() {
		synchronized (this.columns) {
			return new TreeSet(this.columns);
		}
	}
	private void setColumnsForTopLink(Collection columns) {
		this.columns = columns;
	}  

	/**
	 * sort the references for TopLink
	 */
	private Collection getReferencesForTopLink() {
		synchronized (this.references) {
			return new TreeSet(this.references);
		}
	}
	private void setReferencesForTopLink(Collection references) {
		 this.references = references;
	}
	
}
