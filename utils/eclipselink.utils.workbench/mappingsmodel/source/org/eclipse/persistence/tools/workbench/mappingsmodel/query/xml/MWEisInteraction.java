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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;

public final class MWEisInteraction 
	extends MWModel
{
	private volatile String functionName;
		public final static String FUNCTION_NAME_PROPERTY = "funtionName";

	private volatile String inputRecordName;
		public final static String INPUT_RECORD_NAME_PROPERTY = "inputRecordName";	
		    
	private volatile String inputRootElementName;
		public final static String INPUT_ROOT_ELEMENT_PROPERTY = "inputRootElementName";
			
	private List inputArguments;
		public final static String INPUT_ARGUMENTS_LIST = "inputArguments";
		
	private List outputArguments;
		public final static String OUTPUT_ARGUMENTS_LIST = "outputArguments";
		
	private List properties;
		public final static String PROPERTIES_LIST = "properties";
	
	private volatile String inputResultPath;
		public final static String INPUT_RESULT_PATH_PROPERTY = "inputResultPath";
		
	private volatile String outputResultPath;
		public final static String OUTPUT_RESULT_PATH_PROPERTY = "outputResultPath";
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWEisInteraction.class);
	
		descriptor.addDirectMapping("functionName", "function-name/text()");
		descriptor.addDirectMapping("inputRecordName","input-record-name/text()");
		descriptor.addDirectMapping("inputRootElementName", "input-root-element-name/text()");
		descriptor.addDirectMapping("inputResultPath", "input-result-path/text()");
		descriptor.addDirectMapping("outputResultPath", "output-result-path/text()");
		
		XMLCompositeCollectionMapping inputArgumentsListMapping = new XMLCompositeCollectionMapping();
		inputArgumentsListMapping.setAttributeName("inputArguments");

		inputArgumentsListMapping.setReferenceClass(MWEisInteraction.ArgumentPair.class);
		inputArgumentsListMapping.setXPath("input-arguments-list");
		descriptor.addMapping(inputArgumentsListMapping);
		
		XMLCompositeCollectionMapping outputArgumentsListMapping = new XMLCompositeCollectionMapping();
		outputArgumentsListMapping.setAttributeName("outputArguments");

		outputArgumentsListMapping.setReferenceClass(MWEisInteraction.ArgumentPair.class);
		outputArgumentsListMapping.setXPath("output-arguments-list");
		descriptor.addMapping(outputArgumentsListMapping);

		XMLCompositeCollectionMapping propertiesListMapping = new XMLCompositeCollectionMapping();
		propertiesListMapping.setAttributeName("properties");
		propertiesListMapping.setReferenceClass(MWEisInteraction.ArgumentPair.class);
		propertiesListMapping.setXPath("properties");
		descriptor.addMapping(propertiesListMapping);

		return descriptor;
	}
	
	
	// **************** Member classes ****************************************
	
	public static class ArgumentPair 
		extends MWModel
	{	
		private volatile String argumentName;
			public final static String ARGUMENT_NAME_PROPERTY = "argumentName";
			
		private volatile String argumentFieldName;
			public final static String ARGUMENT_FIELD_NAME_PROPERTY = "argumentFieldName";
		
		
		// ************ Static methods ****************************************
		
		public final static XMLDescriptor buildDescriptor() {
			XMLDescriptor descriptor = new XMLDescriptor();
			
			descriptor.setJavaClass(MWEisInteraction.ArgumentPair.class);
			
			XMLDirectMapping interactionMapping = (XMLDirectMapping)descriptor.addDirectMapping("argumentName", "@name");
			interactionMapping.setNullValue("");
			
			XMLDirectMapping argumentMapping = (XMLDirectMapping)descriptor.addDirectMapping("argumentFieldName", "@field-name");
			argumentMapping.setNullValue("");
			
			return descriptor;
		}
		
		
		// ************ Constructors ******************************************
		
		private ArgumentPair (){
			super();
		}
		
		ArgumentPair(MWEisInteraction parent, String argumentName, String argumentFieldName){
			super(parent);
			this.argumentName = argumentName;
			this.argumentFieldName = argumentFieldName;
		}
		
		
		// ************ Accessors *********************************************
		
		public String getArgumentName() {
			return this.argumentName;
		}
		
		public void setArgumentName(String newArgumentName) {
			if (newArgumentName.equals("")) {
				newArgumentName = null;
			}
			String oldArgumentName = this.argumentName;
			this.argumentName = newArgumentName;
			this.firePropertyChanged(ARGUMENT_NAME_PROPERTY, oldArgumentName, this.argumentName);
		}
		
		public String getArgumentFieldName() {
			return this.argumentFieldName;
		}
		
		public void setArgumentFieldName(String newArgumentFieldName) 
		{			
			if (newArgumentFieldName.equals("")) {
				newArgumentFieldName = null;
			}
			String oldArgumentFieldName = this.argumentFieldName;
			this.argumentFieldName = newArgumentFieldName;
			this.firePropertyChanged(ARGUMENT_FIELD_NAME_PROPERTY, oldArgumentFieldName, this.argumentFieldName);
		}
		
		public void setArgumentPairNames(String newArgumentName, String newArgumentFieldName){
			this.setArgumentName(newArgumentName);
			this.setArgumentFieldName(newArgumentFieldName);
		}
	}
	
	
	// **************** Constructors ******************************************
	
	private MWEisInteraction(){
		super();
	}
	
	public MWEisInteraction(MWModel parent){
		super(parent);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent){
		super.initialize(parent);
		this.inputArguments = new Vector();
		this.outputArguments = new Vector();
		this.properties = new Vector();
	}
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.model.xml.MWXModel#addChildrenTo(Collection)
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.inputArguments) { children.addAll(this.inputArguments); }
		synchronized (this.outputArguments) { children.addAll(this.outputArguments); }
		synchronized (this.properties) { children.addAll(this.properties); }
	}
	
	//***************** Conversion ********************************************
	
	public EISInteraction runtimeInteraction() {
		if (this.getFunctionName() == null) {
			return null;
		}
		
		XMLInteraction xmlInteraction = new XMLInteraction();
		
		xmlInteraction.setFunctionName(this.functionName);
		xmlInteraction.setInputRecordName((this.inputRecordName == null) ? "" : this.inputRecordName);
		xmlInteraction.setInputRootElementName((this.inputRootElementName == null) ? "" : this.inputRootElementName);
		xmlInteraction.setInputResultPath((this.inputResultPath == null) ? "" : this.inputResultPath);
		xmlInteraction.setOutputResultPath((this.outputResultPath == null) ? "" : this.outputResultPath);
		
		for (Iterator it = this.inputArguments(); it.hasNext(); )
		{	
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.addArgument(argumentPair.getArgumentName() == null ? "" : argumentPair.getArgumentName(),
					argumentPair.getArgumentFieldName() == null ? "" : argumentPair.getArgumentFieldName());
		}
		
		for (Iterator it = this.outputArguments(); it.hasNext(); )
		{
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.addOutputArgument(argumentPair.getArgumentName() == null ? "" : argumentPair.getArgumentName(),
					argumentPair.getArgumentFieldName() == null ? "" : argumentPair.getArgumentFieldName());
		}

		for (Iterator it = this.properties(); it.hasNext(); )
		{
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.setProperty(argumentPair.getArgumentName() == null ? "" : argumentPair.getArgumentName(),
					argumentPair.getArgumentFieldName() == null ? "" : argumentPair.getArgumentFieldName());
		}
		
		return xmlInteraction;	
	
	}
	
		
	// **************** Accessors *********************************************
	
	public String getFunctionName() {
		return this.functionName;
	}
	
	public void setFunctionName(String newFunctionName) {
		if (newFunctionName.equals("")) {
			newFunctionName = null;
		}
		
		String oldFunctionName = this.functionName;
		this.functionName = newFunctionName;
		this.firePropertyChanged(FUNCTION_NAME_PROPERTY, oldFunctionName, this.functionName);
	}
	
	public String getInputRecordName() {
		return this.inputRecordName;
	}
	
	public void setInputRecordName(String newInputRecordName) {
		if (newInputRecordName.equals("")) {
			newInputRecordName = null;
		}
		
		String oldInputRecordName = this.inputRecordName;
		this.inputRecordName = newInputRecordName;
		this.firePropertyChanged(INPUT_RECORD_NAME_PROPERTY, oldInputRecordName, this.inputRecordName);
	}	
	
	
	public String getInputRootElementName() {
		return this.inputRootElementName;
	}
	
	public void setInputRootElementName(String newInputRootElementName) 
	{
		if (newInputRootElementName.equals("")) {
			newInputRootElementName = null;
		}
		
		String oldInputElementName = this.inputRootElementName;
		this.inputRootElementName = newInputRootElementName;
		this.firePropertyChanged(INPUT_ROOT_ELEMENT_PROPERTY, oldInputElementName, this.inputRootElementName);
	}

	public ListIterator properties(){
		return new CloneListIterator(this.properties);
	}
	
	public ArgumentPair getProperty(int index) {
		return (ArgumentPair) this.properties.get(index);
	}
	
	public ArgumentPair addProperty() {
		return this.addProperty("propertyName", "propertyValue");
	}
	
	public ArgumentPair addProperty(String propertyName, String propertyValue) {
		ArgumentPair newPair = new ArgumentPair(this, propertyName, propertyValue);
		this.addProperty(newPair);
		return newPair;
	}
	
	public void addProperty(ArgumentPair property) {
		this.properties.add(property);
		this.fireItemAdded(PROPERTIES_LIST, this.properties.indexOf(property), property);
	}
	
	public void removeProperty(ArgumentPair property){
		int oldIndex = this.properties.indexOf(property);
		this.properties.remove(property);
		this.fireItemRemoved(PROPERTIES_LIST, oldIndex, property);
	}
		
	public ListIterator inputArguments(){
		return new CloneListIterator(this.inputArguments);
	}
	
	public ArgumentPair getInputArgumentPair(int index) {
		return (ArgumentPair) this.inputArguments.get(index);
	}
	
	public ArgumentPair addInputArgument() {
		return this.addInputArgument("argumentName", "argumentFieldName");
	}
	
	public ArgumentPair addInputArgument(String argumentName, String argumentFieldName) {
		ArgumentPair newPair = new ArgumentPair(this, argumentName, argumentFieldName);
		this.addInputArgument(newPair);
		return newPair;
	}
	
	public void addInputArgument(ArgumentPair argumentPair) {
		this.inputArguments.add(argumentPair);
		this.fireItemAdded(INPUT_ARGUMENTS_LIST, this.inputArguments.indexOf(argumentPair), argumentPair);
	}
	
	public void removeInputArgument(ArgumentPair argumentPair){
		int oldIndex = this.inputArguments.indexOf(argumentPair);
		this.inputArguments.remove(argumentPair);
		this.fireItemRemoved(INPUT_ARGUMENTS_LIST, oldIndex, argumentPair);
	}
	
	public ListIterator outputArguments(){
		return new CloneListIterator(this.outputArguments);
	}
	
	public ArgumentPair getOutputArgumentPair(int index) {
		return (ArgumentPair) this.outputArguments.get(index);
	}
	
	public ArgumentPair addOutputArgument() {
		return this.addOutputArgument("argumentName", "argumentFieldName");
	}
	
	public ArgumentPair addOutputArgument(String argumentName, String argumentFieldName) {
		ArgumentPair newPair = new ArgumentPair(this, argumentName, argumentFieldName);
		this.addOutputArgument(newPair);
		return newPair;
	}
	
	public void addOutputArgument(ArgumentPair argumentPair){
		this.outputArguments.add(argumentPair);
		this.fireItemAdded(OUTPUT_ARGUMENTS_LIST, this.outputArguments.indexOf(argumentPair), argumentPair);
	}
	
	public void removeOutputArgument(ArgumentPair argumentPair) {
		int oldIndex = this.outputArguments.indexOf(argumentPair);
		this.outputArguments.remove(argumentPair);
		this.fireItemRemoved(OUTPUT_ARGUMENTS_LIST, oldIndex, argumentPair);
	}
	
	public String getInputResultPath() {
		return this.inputResultPath;
	}
	
	public void setInputResultPath(String newInputResultPath) 
	{
		if (newInputResultPath.equals("")) {
			newInputResultPath = null;
		}
		
		String oldInputResultPath = this.inputResultPath;
		this.inputResultPath = newInputResultPath;
		this.firePropertyChanged(INPUT_RESULT_PATH_PROPERTY, oldInputResultPath, this.inputResultPath);
	}
	
	public String getOutputResultPath() {
		return this.outputResultPath;
	}
	
	public void setOutputResultPath(String newOutputResultPath) 
	{
		if (newOutputResultPath.equals("")) {
			newOutputResultPath = null;
		}
		
		String oldOutputResultPath = this.outputResultPath;
		this.outputResultPath = newOutputResultPath;
		this.firePropertyChanged(OUTPUT_RESULT_PATH_PROPERTY, oldOutputResultPath, this.outputResultPath);
	}

	
	// **************** Queries ***********************************************
	
	public int inputArgumentsSize() {
		return this.inputArguments.size();
	}
	
	public int outputArgumentsSize() {
		return this.outputArguments.size();
	}
	
	public int propertySize() {
		return this.properties.size();
	}
	
	
	// **************** Runtime conversion ************************************
	
	/** If the interaction has no name, it doesn't really exist as a runtime interaction */
	public boolean isSpecified() {
		return ! StringTools.stringIsEmpty(this.functionName);
	}
	
	public void adjustRuntimeDescriptor(DatabaseQuery runtimeQuery) {
		XMLInteraction xmlInteraction = new XMLInteraction();
		
		xmlInteraction.setFunctionName(this.functionName);
		xmlInteraction.setInputRecordName(this.inputRecordName);
		xmlInteraction.setInputRootElementName(this.inputRootElementName);
		xmlInteraction.setInputResultPath(this.inputResultPath);
		xmlInteraction.setOutputResultPath(this.outputResultPath);
		
		for (Iterator it = this.inputArguments(); it.hasNext(); )
		{	
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.addArgument(argumentPair.getArgumentName(), argumentPair.getArgumentFieldName());
		}
		
		for (Iterator it = this.outputArguments(); it.hasNext(); )
		{
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.addOutputArgument(argumentPair.getArgumentName(), argumentPair.getArgumentFieldName());
		}
		
		for (Iterator it = this.properties(); it.hasNext();) {
			ArgumentPair argumentPair = (ArgumentPair) it.next();
			xmlInteraction.setProperty(argumentPair.getArgumentName(), argumentPair.getArgumentFieldName());
		}
		
		runtimeQuery.setCall(xmlInteraction);
	}
}
