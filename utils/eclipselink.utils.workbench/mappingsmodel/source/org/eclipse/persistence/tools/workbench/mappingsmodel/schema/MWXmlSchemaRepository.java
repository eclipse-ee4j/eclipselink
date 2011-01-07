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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWXmlProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;

public final class MWXmlSchemaRepository
	extends MWModel
	implements ProjectSubFileComponentContainer
{
	// queried reflectively by I/O Manager
	private static final String SUB_DIRECTORY_NAME = "schemas";

	
	// **************** Instance Variables ************************************
	
	/** keyed by schema name */
	private Map schemas;
		public final static String SCHEMAS_COLLECTION = "schemas";

	/**
	 * these schema names are read in by TopLink and are then
	 * used and managed by the IOManager;
	 * DO NOT use them for anything else  ~bjv
	 */
	private Collection schemaNames;
		private final static String SCHEMA_NAMES_COLLECTION = "schemaNames";
	
	
	// **************** Constructors ******************************************
	
	private MWXmlSchemaRepository() {
		super();
	}
	
	public MWXmlSchemaRepository(MWXmlProject parent) {
		super(parent);
	}
	
	
	// **************** Initialization ****************************************
	
	protected /*private-protected*/ void initialize(Node parent) {
		super.initialize(parent);
		this.schemas = new Hashtable();
		this.schemaNames = new HashSet();
	}
	
	protected /*private-protected*/ void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.schemas) { children.addAll(this.schemas.values()); }
	}
	
	
	// **************** Schema Management *************************************
	
	public Iterator schemas() {
		return this.schemas.values().iterator();
	}
	
	public int schemasSize() {
		return this.schemas.size();
	}
	
	public MWXmlSchema getSchema(String schemaName) {
		return (MWXmlSchema) this.schemas.get(schemaName);
	}
	
	public MWXmlSchema createSchemaFromFile(String name, String file) 
		throws ResourceException
	{
		return this.addSchema(MWXmlSchema.createFromFile(this, name, file));
	}
	
	public MWXmlSchema createSchemaFromUrl(String name, String url) 
		throws ResourceException
	{
		return this.addSchema(MWXmlSchema.createFromUrl(this, name, url));
	}
	
	public MWXmlSchema createSchemaFromClasspath(String name, String resource) 
		throws ResourceException
	{
		return this.addSchema(MWXmlSchema.createFromClasspath(this, name, resource));
	}
	
	private MWXmlSchema addSchema(MWXmlSchema schema)
		throws ResourceException
	{
		schema.reload();
		this.checkSchemaName(schema.getName(), schema);
		this.schemas.put(schema.getName(), schema);
		this.fireItemAdded(SCHEMAS_COLLECTION, schema);
		return schema;
	}

	private void checkSchemaName(String schemaName, MWXmlSchema schema) {
		if (this.getSchema(schemaName) != null) {
			throw new IllegalArgumentException("Schema with name already exists.");
		}
		MWXmlSchema matchIgnoreCase = this.getSchemaIgnoreCase(schemaName);
		if ((matchIgnoreCase != null) && (matchIgnoreCase != schema)) {
			throw new IllegalArgumentException("Schema with name, but different case, already exists: \"" + matchIgnoreCase.getName() + "\".");
		}
	}
	
	public boolean containsSchemaIgnoreCase(String name) {
		return this.getSchemaIgnoreCase(name) != null;
	}

	public MWXmlSchema getSchemaIgnoreCase(String name) {
		for (Iterator stream = this.schemas.values().iterator(); stream.hasNext(); ) {
			MWXmlSchema schema = (MWXmlSchema) stream.next();
			if (schema.getName().equalsIgnoreCase(name)) {
				return schema;
			}
		}
		return null;
	}

	public void removeSchema(MWXmlSchema schema) {
		if (this.getSchema(schema.getName()) == null)
			throw new IllegalArgumentException("Schema with name does not exist.");
		
		this.schemas.remove(schema.getName());
		this.fireItemRemoved(SCHEMAS_COLLECTION, schema);
		getProject().nodeRemoved(schema);
	}

	void schemaRenamed(String oldName, String newName, MWXmlSchema schema) {
		synchronized (this.schemas) {
			this.checkSchemaName(newName, schema);
			this.schemas.put(newName, this.schemas.remove(oldName));
		}
		// if a schema has been renamed, we need to fire an "internal"
		// change event so the repository is marked dirty
		this.fireCollectionChanged(SCHEMA_NAMES_COLLECTION);
	}

	/**
	 * performance tuning: override this method and assume
	 * the repository's descendants have NO references (handles)
	 * to any models other than other descendants of the repository
	 */
	public void nodeRemoved(Node node) {
		if (node.isDescendantOf(this)) {
			super.nodeRemoved(node);
		}
	}
	
	/**
	 * performance tuning: override this method and assume
	 * the repository's descendants have NO references (handles)
	 * to any models other than other descendants of the repository
	 */
	public void nodeRenamed(Node node) {
		if (node.isDescendantOf(this)) {
			super.nodeRenamed(node);
			// we handle a renamed schema directly in #schemaRenamed(String, String)
		}
	}
	
	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to mappings in the schema repository or its descendants
	 */
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		// do nothing
	}

	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to descriptors in the database or its descendants
	 */
	public void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor) {
		// do nothing
	}

	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to mappings in the schema repository or its descendants
	 */
	public void descriptorUnmapped(Collection mappings) {
		// do nothing
	}
	

	// ********** SubComponentContainer implementation **********

	public Iterator projectSubFileComponents() {
		return this.schemas();
	}

	public void setProjectSubFileComponents(Collection subComponents) {
		this.schemas = new Hashtable(subComponents.size());
		for (Iterator stream = subComponents.iterator(); stream.hasNext(); ) {
			MWXmlSchema schema = (MWXmlSchema) stream.next();
			this.schemas.put(schema.getName(), schema);
		}
	}

	public Iterator originalProjectSubFileComponentNames() {
		return this.schemaNames.iterator();
	}

	public void setOriginalProjectSubFileComponentNames(Collection originalSubComponentNames) {
		this.schemaNames = originalSubComponentNames;
	}

	public boolean hasChangedMainProjectSaveFile() {
		if (this.isDirty()) {
			// the repository itself is dirty
			return true;
		}
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			if (this.childHasChangedTheProjectSaveFile(stream.next())) {
				return true;
			}
		}
		// the schemas might be dirty
		return false;
	}

	/**
	 * return whether the specified child of the repository is dirty AND
	 * is written to the .mwp file
	 */
	private boolean childHasChangedTheProjectSaveFile(Object child) {
		if (this.schemas.containsValue(child)) {
			// tables are written to separate files
			return false;
		}
		// the child is NOT a table,
		// so all of its state is written to the .mwp file
		return ((Node) child).isDirtyBranch();
	}


	//******************** TopLink methods ************************

	public static XMLDescriptor buildDescriptor(){
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlSchemaRepository.class);
		
		XMLCompositeDirectCollectionMapping schemaNamesMapping = new XMLCompositeDirectCollectionMapping();
		schemaNamesMapping.setAttributeName("schemaNames");
		schemaNamesMapping.setSetMethodName("setSchemaNamesForTopLink");
		schemaNamesMapping.setGetMethodName("getSchemaNamesForTopLink");
		schemaNamesMapping.useCollectionClass(HashSet.class);
		schemaNamesMapping.setXPath("schema-names/name/text()");
		descriptor.addMapping(schemaNamesMapping);

		return descriptor;
	}
	
	/**
	 * sort the schema names for TopLink
	 */
	private Collection getSchemaNamesForTopLink() {
		List names = new ArrayList(this.schemas.size());
		synchronized (this.schemas) {
			for (Iterator stream = this.schemas.keySet().iterator(); stream.hasNext(); ) {
				names.add(stream.next());
			}
		}
		return CollectionTools.sort(names, Collator.getInstance());
	}

	/**
	 * TopLink sets this value, which is then used by the
	 * ProjectIOManager to read in the actual schemata
	 */
	private void setSchemaNamesForTopLink(Collection schemaNames) {
		this.schemaNames = schemaNames;
	}

}
