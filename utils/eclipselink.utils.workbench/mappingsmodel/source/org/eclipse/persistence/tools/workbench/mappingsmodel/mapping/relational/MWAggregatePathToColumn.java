/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Collator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import deprecated.sdk.SDKAggregateCollectionMapping;

/**
 * This class represents a mapping path from an aggregate mapping to
 * one of its submappings.  Because an aggregate's submappings can be nested
 * several layers deep, this class maintains a reference to a List of mappings
 * which is essentially a tree path to a database column.
 */
public final class MWAggregatePathToColumn extends MWModel {
										  
	/**
	 * List of MWMappings that lead to the column.
	 */
	private List mappingHandles;
	
	private volatile AggregateRuntimeFieldNameGenerator aggregateRuntimeFieldNameGenerator; //transient
		public final static String AGGREGATE_RUNTIME_FIELD_NAME_GENERATOR_PROPERTY = "aggregateRuntimeFieldNameGenerator";
	
	private volatile String fieldDescription;  //persistent
	
	/**
	 * The column that the above path maps to.
	 */
	private MWColumnHandle columnHandle;
		public final static String COLUMN_PROPERTY = "column";

	private String legacyFieldName;
	private String legacyFieldTableName;
	
	private String legacyFieldDescription;

	
	// ************** Constructors ***************	

	/** Default constructor - for TopLink use only */
	private MWAggregatePathToColumn() {
		super();
	}
	
	MWAggregatePathToColumn(MWAggregateMapping mapping) {
		super(mapping);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.mappingHandles = new Vector();
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
	}

	
	// ************** Accessors ***************	

	public MWColumn getColumn() {
		return this.columnHandle.getColumn();
	}

	public void setColumn(MWColumn column) {
		MWColumn old = this.getColumn();
		this.columnHandle.setColumn(column);	
		firePropertyChanged(COLUMN_PROPERTY, old, column);
	}
		
	public AggregateRuntimeFieldNameGenerator getAggregateRuntimeFieldNameGenerator() {
		return this.aggregateRuntimeFieldNameGenerator;
	}

	public void setAggregateRuntimeFieldNameGenerator(AggregateRuntimeFieldNameGenerator aggregateRuntimeFieldNameGenerator) {
		this.aggregateRuntimeFieldNameGenerator = aggregateRuntimeFieldNameGenerator;
	}
	
	void addMappingNode(MWMapping node) {
		if (getMappingNodes().contains(node)) {
			return;
		}
		this.mappingHandles.add(new MWMappingHandle(this, node, NodeReferenceScrubber.NULL_INSTANCE));
	}
	
	private void addMappingAt(int index, MWMapping mapping) {
	    this.mappingHandles.add(index, new MWMappingHandle(this, mapping, NodeReferenceScrubber.NULL_INSTANCE));
	}
	
	/**
	 * Creates a copy of the pathToField:
	 *   points to the same parent mapping
	 *   points to the same field description
	 *   points to the same field
	 */
	MWAggregatePathToColumn copy(MWAggregateMapping parent) {
		MWAggregatePathToColumn copy = new MWAggregatePathToColumn(parent);

		for (Iterator stream = this.getMappingNodes().iterator(); stream.hasNext(); ) {
			copy.addMappingNode((MWMapping) stream.next());
		}
		copy.setAggregateRuntimeFieldNameGenerator(this.getAggregateRuntimeFieldNameGenerator());
		copy.setColumn(this.columnHandle.getColumn());

		return copy;
	}
	
	MWMapping mappingAt(int mappingIndex) {
		return (MWMapping) getMappingNodes().get(mappingIndex);
	}
	
	List getMappingNodes() {
		List result = new Vector(this.mappingHandles.size());
		for (Iterator stream = new CloneIterator(this.mappingHandles); stream.hasNext(); ) {
		    result.add(((MWMappingHandle) stream.next()).getMapping());
		}
		return result;
	}
		
	
	/**
	 * Inserts a root mapping node into the collection of
	 * mapping nodes.
	 **/
	void insertRootMappingNode(MWMapping node) {
		addMappingAt(0, node);
	}
	
	
	// ************** Containment hierarchy ***************	

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.mappingHandles) { children.addAll(this.mappingHandles); }
		children.add(this.columnHandle);
	}

	private NodeReferenceScrubber buildColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAggregatePathToColumn.this.setColumn(null);
			}
			public String toString() {
				return "MWAggregatePathToColumn.buildColumnScrubber()";
			}
		};
	}

	// we don't worry about 'mappings' here because they only ever
	// reference mappings in a MWAggregateDescriptor; and the 'mappings'
	// will be rebuilt whenever a mapping is removed from a MWAggregateDescriptor;
	// see MWAggregateDescriptor.removeMapping(MWMapping) and
	// MWProject.recalculateAggregatePathsToColumn(MWMappingDescriptor)
	// ~bjv (per kfm)
	
	public void mappingReplaced(MWMapping oldMapping, MWMapping newMapping) {
		super.mappingReplaced(oldMapping, newMapping);
		// like #nodeRemoved(Node), above, we don't worry about 'mappingHandles' here;
		// they will be rebuilt when the new mapping is added to the aggregate descriptor;
		// see MWAggregateDescriptor.addMapping(MWMapping) and
		// MWProject.recalculateAggregatePathsToColumn(MWMappingDescriptor)
		// ~bjv (per kfm)
	}
	

	// ************** Problem Handling ***************	

	boolean isPathReadOnly(){
		Iterator mappingNodes = getMappingNodes().iterator();
	
		while (mappingNodes.hasNext()){
			MWMapping mapping = (MWMapping) mappingNodes.next();
	        
			// short circuit
			if (mapping.isReadOnly())
				return true;
		}
		return false;
	}
	
	boolean fieldIsWritten() {
		if (isPathReadOnly()) {
			return false;
		}
		return this.aggregateRuntimeFieldNameGenerator.fieldIsWritten();
	}
	

	// ************** display methods ***************	

	public String getPathDescription() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		Iterator i = getMappingNodes().iterator();
		if (i.hasNext()) {
			pw.print(mappingAt(0).getParentDescriptor().getMWClass().shortName());
		} else {
			pw.print(this.aggregateRuntimeFieldNameGenerator.owningDescriptor().getMWClass().shortName());
		}
		
		while (i.hasNext()) {
			pw.print(".");
			pw.print(((MWMapping) i.next()).getName());
		}
		return sw.getBuffer().toString();
	}
	
	public void toString(StringBuffer sb) {
		sb.append(this.getPathDescription());
		sb.append(" -> ");
		if (this.getColumn() == null) {
			sb.append("null");
		} else {
			sb.append(this.getColumn().qualifiedName());
		}
	}
	
	public String description() {
		StringBuffer sb = new StringBuffer();
		this.toString(sb);
		return sb.toString();
	}
	
	public int compareTo(Object o) {
		MWAggregatePathToColumn otherPathFieldAssociation = (MWAggregatePathToColumn) o;
		return Collator.getInstance().compare(getPathDescription() + getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime(), 
												otherPathFieldAssociation.getPathDescription() + otherPathFieldAssociation.getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime());
	}
	

	//	**************** Containment Hierarchy ************
	
	private MWAggregateMapping getAggregateMapping() {
		return (MWAggregateMapping) getParent();
	}
	
	private MWRelationalDescriptor getParentDescriptor() {
		return (MWRelationalDescriptor) getAggregateMapping().getParentDescriptor();
	}
	
	
	//**************** Runtime Conversion ************
	
	void adjustRuntimeMapping(AggregateObjectMapping runtimeMapping) {
		if (!getParentDescriptor().isAggregateDescriptor()) {
			if (getColumn() != null) {
				runtimeMapping.addFieldNameTranslation(getColumn().qualifiedName(), calculateAggregateFieldName());
			}
		}
		else {
			runtimeMapping.addFieldNameTranslation(calculateSourceFieldName(), calculateAggregateFieldName());
		}
	}

	private String calculateAggregateFieldName() {
		String fieldName = "";
		for (Iterator mappingNodes = getMappingNodes().listIterator(); mappingNodes.hasNext(); ) {
			MWMapping mapping = (MWMapping) mappingNodes.next();
			if (fieldName.equals("")) {
				fieldName += mapping.getName();
			}
			else {
				fieldName += "_" + mapping.getName();
			}		
		}
		
		if (!fieldName.equals("")) {
			fieldName += "->";
		}
		
		fieldName += getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime();
		
		return fieldName;
	}
	
	private String calculateSourceFieldName() {
		return ((MWAggregateMapping) getParent()).getName() + "_" + calculateAggregateFieldName();
	}
	

	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWAggregatePathToColumn.class);
		
		XMLDirectMapping fieldDescriptionMapping = new XMLDirectMapping();
		fieldDescriptionMapping.setAttributeName("aggregateRuntimeFieldNameGenerator");
		fieldDescriptionMapping.setGetMethodName("getFieldDescriptionForTopLink");
		fieldDescriptionMapping.setSetMethodName("setFieldDescriptionForTopLink");
		fieldDescriptionMapping.setXPath("field-description/text()");
		descriptor.addMapping(fieldDescriptionMapping);

		XMLCompositeObjectMapping columnHandleMapping = new XMLCompositeObjectMapping();
		columnHandleMapping.setAttributeName("columnHandle");
		columnHandleMapping.setGetMethodName("getColumnHandleForTopLink");
		columnHandleMapping.setSetMethodName("setColumnHandleForTopLink");
		columnHandleMapping.setReferenceClass(MWColumnHandle.class);
		columnHandleMapping.setXPath("column-handle");
		descriptor.addMapping(columnHandleMapping);

		XMLCompositeCollectionMapping mappingHandlesMapping = new XMLCompositeCollectionMapping();
		mappingHandlesMapping.setAttributeName("mappingHandles");
		mappingHandlesMapping.setGetMethodName("getMappingHandlesForTopLink");
		mappingHandlesMapping.setSetMethodName("setMappingHandlesForTopLink");
		mappingHandlesMapping.setReferenceClass(MWMappingHandle.class);
		mappingHandlesMapping.setXPath("mapping-handles/mapping-handle");
		descriptor.addMapping(mappingHandlesMapping);

		return descriptor;
	}
	
	private String getFieldDescriptionForTopLink() {
		return getAggregateRuntimeFieldNameGenerator().fieldNameForRuntime();
	}	
	private void setFieldDescriptionForTopLink(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	private List getMappingHandlesForTopLink() {
		return this.mappingHandles;
	}
	private void setMappingHandlesForTopLink(List handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWMappingHandle) stream.next()).setScrubber(NodeReferenceScrubber.NULL_INSTANCE);
		}
		this.mappingHandles = handles;
	}

	/**
	 * check for null
	 */
	private MWColumnHandle getColumnHandleForTopLink() {
		return (this.columnHandle.getColumn() == null) ? null : this.columnHandle;
	}
	private void setColumnHandleForTopLink(MWColumnHandle columnHandle) {
		NodeReferenceScrubber scrubber = this.buildColumnScrubber();
		this.columnHandle = ((columnHandle == null) ? new MWColumnHandle(this, scrubber) : columnHandle.setScrubber(scrubber));
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		if (this.fieldDescription == null) {
			return; //legacy cases are handled in the legacyXXPostPostProjectBuild methods
		}
		Iterator aggregateFieldNameGenerators;
		if (this.mappingHandles.size() == 0) {
			aggregateFieldNameGenerators = ((MWRelationalDescriptor) getAggregateMapping().getReferenceDescriptor()).buildAggregateFieldNameGenerators().iterator();
		}
		else {
			MWMappingHandle mappingHandle = (MWMappingHandle) this.mappingHandles.get(this.mappingHandles.size() - 1);
			aggregateFieldNameGenerators = mappingHandle.getMapping().aggregateFieldNameGenerators();
			if (!aggregateFieldNameGenerators.hasNext()) {
				aggregateFieldNameGenerators = ((MWRelationalDescriptor) ((MWAggregateMapping) mappingHandle.getMapping()).getReferenceDescriptor()).buildAggregateFieldNameGenerators().iterator();
			}
		}
		while (aggregateFieldNameGenerators.hasNext()) {
			AggregateRuntimeFieldNameGenerator aggregateFieldNameGenerator = (AggregateRuntimeFieldNameGenerator) aggregateFieldNameGenerators.next();
			if (this.fieldDescription.equals(aggregateFieldNameGenerator.fieldNameForRuntime())) {
				setAggregateRuntimeFieldNameGenerator(aggregateFieldNameGenerator);
				break;
			}
		}
		this.fieldDescription = null;
	}
	
	public static ClassDescriptor legacy50BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();

		descriptor.setJavaClass(MWAggregatePathToColumn.class);
		descriptor.setTableName("aggregate-path-to-field");

		descriptor.addDirectMapping("legacyFieldDescription", "field-description");
		descriptor.addDirectMapping("legacyFieldName", "legacyGetFieldName", "legacySetFieldName", "field-name");

		OneToOneMapping fieldTableMapping = new OneToOneMapping();
		fieldTableMapping.setAttributeName("fieldTable");
		fieldTableMapping.setGetMethodName("legacyGetFieldTable");
		fieldTableMapping.setSetMethodName("legacySetFieldTable");
		fieldTableMapping.setReferenceClass(MWTable.class);
		fieldTableMapping.setForeignKeyFieldName("field-table");
		fieldTableMapping.dontUseIndirection();
		descriptor.addMapping(fieldTableMapping);

		SDKAggregateCollectionMapping mappingHandlesMapping = new SDKAggregateCollectionMapping();
		mappingHandlesMapping.setAttributeName("mappingHandles");
		mappingHandlesMapping.setGetMethodName("getMappingHandlesForTopLink");
		mappingHandlesMapping.setSetMethodName("setMappingHandlesForTopLink");
		mappingHandlesMapping.setReferenceClass(MWMappingHandle.class);
		mappingHandlesMapping.setFieldName("mapping-handles");
		descriptor.addMapping(mappingHandlesMapping);

		return descriptor;
	}

	protected void legacy50PostBuild(DescriptorEvent event) {
		if (this.legacyFieldDescription.equals("direct field")) {
			this.legacyFieldDescription = "DIRECT";
		}
		else if (this.legacyFieldDescription.equals("Class Indicator Field")) {
			this.legacyFieldDescription = "CLASS_INDICATOR_FIELD";
		}
		else if (this.legacyFieldDescription.indexOf(", field set by method") != -1) {
			this.legacyFieldDescription = "METHOD_TRANSFORMER " + this.legacyFieldDescription.substring(this.legacyFieldDescription.lastIndexOf(' ') + 1, this.legacyFieldDescription.length() - 3);
		}
		else if (this.legacyFieldDescription.indexOf("field used as an association to query key") != -1) {
			this.legacyFieldDescription = StringTools.removeAllOccurrences(this.legacyFieldDescription, '"');
			this.legacyFieldDescription =  "QUERY_KEY " + this.legacyFieldDescription.substring(this.legacyFieldDescription.lastIndexOf(' ') + 1, this.legacyFieldDescription.length());
		}
		else if (this.legacyFieldDescription.indexOf("in reference") != -1) {
			this.legacyFieldDescription = this.legacyFieldDescription.replaceFirst(" in reference ", "_IN_REFERENCE_");
		}
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
		super.legacy50PostBuild(event);
	}
			
	public void legacy50PostPostProjectBuild() {
		legacyResolveField();		
		super.legacy50PostPostProjectBuild();
	}
	
	public void legacy50FixAggregatePathToFields() {
		super.legacy50FixAggregatePathToFields();

		MWMappingHandle mappingHandle = (MWMappingHandle) this.mappingHandles.get(this.mappingHandles.size() - 1);
		if (mappingHandle.getMapping() == null) {
			//did not have aptfs for query keys in previous versions
			MWDescriptor referenceDescriptor = ((MWAggregateMapping) getParent()).getReferenceDescriptor();
			if (referenceDescriptor.canHaveInheritance()) {
				setAggregateRuntimeFieldNameGenerator((MWRelationalClassIndicatorFieldPolicy) ((MWMappingDescriptor) referenceDescriptor).getInheritancePolicy().getRootDescriptor().getInheritancePolicy().getClassIndicatorPolicy());
			}
			this.mappingHandles.remove(0);
			this.legacyFieldDescription = null;
			return;
		}
		Iterator aggregateFieldNameGenerators = mappingHandle.getMapping().aggregateFieldNameGenerators();
		if (!aggregateFieldNameGenerators.hasNext()) {
			if (mappingHandle.getMapping() instanceof MWAggregateMapping) {
				aggregateFieldNameGenerators = ((MWRelationalDescriptor) ((MWAggregateMapping) mappingHandle.getMapping()).getReferenceDescriptor()).buildAggregateFieldNameGenerators().iterator();
			}
		}
		
		while (aggregateFieldNameGenerators.hasNext()) {
			AggregateRuntimeFieldNameGenerator aggregateFieldNameGenerator = (AggregateRuntimeFieldNameGenerator) aggregateFieldNameGenerators.next();
			if (this.legacyFieldDescription.equals(aggregateFieldNameGenerator.fieldNameForRuntime())) {
				setAggregateRuntimeFieldNameGenerator(aggregateFieldNameGenerator);
				break;
			}
		}
		this.legacyFieldDescription = null;
	}

	private void legacyResolveField() {
		if(this.legacyFieldName != null && this.legacyFieldTableName != null) {
			MWColumn field = this.getProject().getDatabase().tableNamed(this.legacyFieldTableName).columnNamed(this.legacyFieldName);
			this.columnHandle.setColumn(field);
		}
		this.legacyFieldName = null;
		this.legacyFieldTableName = null;
	}

	private MWTable legacyGetFieldTable() {
		throw new UnsupportedOperationException();
	}	
	private void legacySetFieldTable(MWTable table) {
		if(table != null) {
			this.legacyFieldTableName = table.getName();
		}
	}
	
	private String legacyGetFieldName() {
		throw new UnsupportedOperationException();
	}	
	private void legacySetFieldName(String newValue) {
		this.legacyFieldName = newValue;
	}

	public static ClassDescriptor legacy45BuildDescriptor() {
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.setJavaClass(MWAggregatePathToColumn.class);
		descriptor.setTableName("AggregatePathToField");
		descriptor.descriptorIsAggregate();

		descriptor.addDirectMapping("legacyFieldDescription", "fieldDescription");

		descriptor.addDirectMapping("legacyFieldName", "legacyGetFieldName", "legacySetFieldName", "fieldName");

		OneToOneMapping fieldTableMapping = new OneToOneMapping();
		fieldTableMapping.setAttributeName("fieldTable");
		fieldTableMapping.setGetMethodName("legacyGetFieldTable");
		fieldTableMapping.setSetMethodName("legacySetFieldTable");
		fieldTableMapping.setReferenceClass(MWTable.class);
		fieldTableMapping.setForeignKeyFieldName("fieldTable");
		fieldTableMapping.dontUseIndirection();
		descriptor.addMapping(fieldTableMapping);

		SDKAggregateCollectionMapping mappingHandlesMapping = new SDKAggregateCollectionMapping();
		mappingHandlesMapping.setAttributeName("mappingHandles");
		mappingHandlesMapping.setGetMethodName("getMappingHandlesForTopLink");
		mappingHandlesMapping.setSetMethodName("setMappingHandlesForTopLink");
		mappingHandlesMapping.setReferenceClass(MWMappingHandle.class);
		mappingHandlesMapping.setFieldName("mappingHandles");
		descriptor.addMapping(mappingHandlesMapping);

		return descriptor;
	}

	protected void legacy45PostBuild(DescriptorEvent event) {
		if (this.legacyFieldDescription.equals("direct field")) {
			this.legacyFieldDescription = "DIRECT";
		}
		else if (this.legacyFieldDescription.equals("Class Indicator Field")) {
			this.legacyFieldDescription = "CLASS_INDICATOR_FIELD";
		}
		else if (this.legacyFieldDescription.indexOf(", field set by method") != -1) {
			this.legacyFieldDescription = "METHOD_TRANSFORMER " + this.legacyFieldDescription.substring(this.legacyFieldDescription.lastIndexOf(' ') + 1, this.legacyFieldDescription.length() - 2);
		}
		else if (this.legacyFieldDescription.indexOf("field used as an association to query key") != -1) {
			this.legacyFieldDescription = StringTools.removeAllOccurrences(this.legacyFieldDescription, '"');
			this.legacyFieldDescription =  "QUERY_KEY " + this.legacyFieldDescription.substring(this.legacyFieldDescription.lastIndexOf(' ') + 1, this.legacyFieldDescription.length());
		}
		else if (this.legacyFieldDescription.indexOf("in reference") != -1) {
			this.legacyFieldDescription = this.legacyFieldDescription.replaceFirst(" in reference ", "_IN_REFERENCE_");
		}
		this.columnHandle = new MWColumnHandle(this, this.buildColumnScrubber());
		super.legacy45PostBuild(event);
	}
	
	public void legacy45PostPostProjectBuild() {
		legacyResolveField();
		super.legacy45PostPostProjectBuild();
	}	
	
	public void legacy45FixAggregatePathToFields() {
		super.legacy45FixAggregatePathToFields();

		MWMappingHandle mappingHandle = (MWMappingHandle) this.mappingHandles.get(this.mappingHandles.size() - 1);
		if (mappingHandle.getMapping() == getParent()) {
			this.mappingHandles.remove(0);
			MWDescriptor referenceDescriptor = ((MWAggregateMapping) getParent()).getReferenceDescriptor();
			if (referenceDescriptor.canHaveInheritance()) {
				setAggregateRuntimeFieldNameGenerator((MWRelationalClassIndicatorFieldPolicy) ((MWMappingDescriptor) referenceDescriptor).getInheritancePolicy().getRootDescriptor().getInheritancePolicy().getClassIndicatorPolicy());
			}
			this.legacyFieldDescription = null;
			return;
		}
		Iterator aggregateFieldNameGenerators = mappingHandle.getMapping().aggregateFieldNameGenerators();
		if (!aggregateFieldNameGenerators.hasNext()) {
			if (mappingHandle.getMapping() instanceof MWAggregateMapping) {
				aggregateFieldNameGenerators = ((MWRelationalDescriptor) ((MWAggregateMapping) mappingHandle.getMapping()).getReferenceDescriptor()).buildAggregateFieldNameGenerators().iterator();
			}
		}
		while (aggregateFieldNameGenerators.hasNext()) {
			AggregateRuntimeFieldNameGenerator aggregateFieldNameGenerator = (AggregateRuntimeFieldNameGenerator) aggregateFieldNameGenerators.next();
			if (this.legacyFieldDescription.equals(aggregateFieldNameGenerator.fieldNameForRuntime())) {
				setAggregateRuntimeFieldNameGenerator(aggregateFieldNameGenerator);
				break;
			}
		}
		this.legacyFieldDescription = null;
	}

}
