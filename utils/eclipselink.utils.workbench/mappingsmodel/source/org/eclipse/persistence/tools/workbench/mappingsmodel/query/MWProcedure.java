/*
 * Copyright (c) 2006, 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStoredProcedureQueryFormat;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
@SuppressWarnings("nls")
public final class MWProcedure extends MWModel
{

	private String name;
		public static final String NAME_PROPERTY = "name";

	private List<MWProcedureNamedOutputArgument> namedOutputArguments;
		public static final String NAMED_OUTPUT_ARGUMENT_LIST = "namedOutputArguments";

	private List<MWProcedureNamedInOutputArgument> namedInOuputArguments;
		public static final String NAMED_INOUTPUT_ARGUMENT_LIST = "namedInOutputArguments";

	private List<MWProcedureNamedInArgument> namedInArguments;
		public static final String NAMED_ARGUMENT_LIST = "namedArguments";

	private List<MWProcedureUnamedInOutputArgument> unamedInOuputArguments;
		public static final String UNAMED_INOUTPUT_ARGUMENT_LIST = "unamedInOutputArguments";

	private List<MWProcedureUnamedInArgument> unamedInArguments;
		public static final String UNAMED_ARGUMENT_LIST = "unamedArguments";

	private List<MWProcedureUnamedOutputArgument> unamedOutputArguments;
		public static final String UNAMED_OUTPUT_ARGUMENT_LIST = "unamedOutputArguments";
		
		public static final String ARGUMENT_COLLECTION = "arguments";
		
	private Boolean useUnamedCursorOutput;
		public static final String USE_UNAMED_CURSOR_OUTPUT = "useUnamedCursorOutput";
		
	private String cursorOutputName;
		public static final String CURSOR_OUTPUT_NAME = "cursorOutputName";

	/**
	 * Default constructor - for TopLink use only
	 */
	@SuppressWarnings("unused")
	private MWProcedure() {
		super();
	}

	public MWProcedure(MWStoredProcedureQueryFormat format) {
		super(format);
	}

	@Override
	protected void initialize() {
		 super.initialize();
		 this.name = "STR_PROC_1";
		 this.namedInArguments = new ArrayList<MWProcedureNamedInArgument>();
		 this.namedOutputArguments = new ArrayList<MWProcedureNamedOutputArgument>();
		 this.namedInOuputArguments = new ArrayList<MWProcedureNamedInOutputArgument>();
		 this.unamedInArguments = new ArrayList<MWProcedureUnamedInArgument>();
		 this.unamedOutputArguments = new ArrayList<MWProcedureUnamedOutputArgument>();
		 this.unamedInOuputArguments = new ArrayList<MWProcedureUnamedInOutputArgument>();
		 this.useUnamedCursorOutput = Boolean.FALSE;
		 this.cursorOutputName = "";
	 }

	@Override
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.namedInArguments) { children.addAll(this.namedInArguments); }
		synchronized (this.namedOutputArguments) { children.addAll(this.namedOutputArguments); }
		synchronized (this.namedInOuputArguments) { children.addAll(this.namedInOuputArguments); }
		synchronized (this.unamedInArguments) { children.addAll(this.unamedInArguments); }
		synchronized (this.unamedOutputArguments) { children.addAll(this.unamedOutputArguments); }
		synchronized (this.unamedInOuputArguments) { children.addAll(this.unamedInOuputArguments); }
	}

	//	Persistence
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWProcedure.class);

		// DTFs
		XMLDirectMapping nameMapping = new XMLDirectMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setGetMethodName("getNameForTopLink");
		nameMapping.setSetMethodName("setNameForTopLink");
		nameMapping.setXPath("name/text()");
		descriptor.addMapping(nameMapping);
		
		XMLDirectMapping useUnamedCursorOutputMapping = new XMLDirectMapping();
		useUnamedCursorOutputMapping.setAttributeName("useUnamedCursorOutput");
		useUnamedCursorOutputMapping.setGetMethodName("getUseUnamedCursorOutputForTopLink");
		useUnamedCursorOutputMapping.setSetMethodName("setUseUnamedCursorOutputForTopLink");
		useUnamedCursorOutputMapping.setNullValue(Boolean.TRUE);
		useUnamedCursorOutputMapping.setXPath("use-unamed-cursor-output");
		descriptor.addMapping(useUnamedCursorOutputMapping);
		
		descriptor.addDirectMapping("cursorOutputName", "getCursorOutputNameForTopLink", "setCursorOutputNameForTopLink", "cursor-output-name/text()");

		// Aggregate collection - namedOutputArguments
		XMLCompositeCollectionMapping namedOutputArgumentsMapping = new XMLCompositeCollectionMapping();
		namedOutputArgumentsMapping.setAttributeName("namedOutputArguments");
		namedOutputArgumentsMapping.setGetMethodName("getNamedOutputArgumentsForTopLink");
		namedOutputArgumentsMapping.setSetMethodName("setNamedOutputArgumentsForTopLink");
		namedOutputArgumentsMapping.setReferenceClass(MWProcedureNamedOutputArgument.class);
		namedOutputArgumentsMapping.setXPath("named-output-arguments/named-output-argument");
		descriptor.addMapping(namedOutputArgumentsMapping);

		// Aggregate collection - unamedOutputArguments
		XMLCompositeCollectionMapping unamedOutputArgumentsMapping = new XMLCompositeCollectionMapping();
		unamedOutputArgumentsMapping.setAttributeName("unamedOutputArguments");
		unamedOutputArgumentsMapping.setGetMethodName("getUnamedOutputArgumentsForTopLink");
		unamedOutputArgumentsMapping.setSetMethodName("setUnamedOutputArgumentsForTopLink");
		unamedOutputArgumentsMapping.setReferenceClass(MWProcedureUnamedOutputArgument.class);
		unamedOutputArgumentsMapping.setXPath("unamed-output-arguments/unamed-output-argument");
		descriptor.addMapping(unamedOutputArgumentsMapping);

		// Aggregate collection - namedInArguments
		XMLCompositeCollectionMapping namedInArgumentsMapping = new XMLCompositeCollectionMapping();
		namedInArgumentsMapping.setAttributeName("namedInArguments");
		namedInArgumentsMapping.setGetMethodName("getNamedInArgumentsForTopLink");
		namedInArgumentsMapping.setSetMethodName("setNamedInArgumentsForTopLink");
		namedInArgumentsMapping.setReferenceClass(MWProcedureNamedInArgument.class);
		namedInArgumentsMapping.setXPath("named-in-arguments/named-in-argument");
		descriptor.addMapping(namedInArgumentsMapping);

		// Aggregate collection - unamedInArguments
		XMLCompositeCollectionMapping unamedInArgumentsMapping = new XMLCompositeCollectionMapping();
		unamedInArgumentsMapping.setAttributeName("unamedInArguments");
		unamedInArgumentsMapping.setGetMethodName("getUnamedInArgumentsForTopLink");
		unamedInArgumentsMapping.setSetMethodName("setUnamedInArgumentsForTopLink");
		unamedInArgumentsMapping.setReferenceClass(MWProcedureUnamedInArgument.class);
		unamedInArgumentsMapping.setXPath("unamed-in-arguments/unamed-in-argument");
		descriptor.addMapping(unamedInArgumentsMapping);

		// Aggregate collection - namedArguments
		XMLCompositeCollectionMapping namedInOutputArgumentsMapping = new XMLCompositeCollectionMapping();
		namedInOutputArgumentsMapping.setAttributeName("namedInOutputArguments");
		namedInOutputArgumentsMapping.setGetMethodName("getNamedInOutputArgumentsForTopLink");
		namedInOutputArgumentsMapping.setSetMethodName("setNamedInOutputArgumentsForTopLink");
		namedInOutputArgumentsMapping.setReferenceClass(MWProcedureNamedInOutputArgument.class);
		namedInOutputArgumentsMapping.setXPath("named-inout-arguments/named-inout-argument");
		descriptor.addMapping(namedInOutputArgumentsMapping);

		// Aggregate collection - unamedArguments
		XMLCompositeCollectionMapping unamedInOutputArgumentsMapping = new XMLCompositeCollectionMapping();
		unamedInOutputArgumentsMapping.setAttributeName("unamedInOutputArguments");
		unamedInOutputArgumentsMapping.setGetMethodName("getUnamedInOutputArgumentsForTopLink");
		unamedInOutputArgumentsMapping.setSetMethodName("setUnamedInOutputArgumentsForTopLink");
		unamedInOutputArgumentsMapping.setReferenceClass(MWProcedureUnamedInOutputArgument.class);
		unamedInOutputArgumentsMapping.setXPath("unamed-inout-arguments/unamed-inout-argument");
		descriptor.addMapping(unamedInOutputArgumentsMapping);

		return descriptor;
	}

	public String getName() {
		if (this.name == null) {
			return "";
		} else {
			return this.name;
		}
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, oldName, this.name);
	}

	@SuppressWarnings("unused")
	private void setNameForTopLink(String name) {
		this.name = name;
	}

	@SuppressWarnings("unused")
	private String getNameForTopLink() {
		return this.name;
	}

	public Boolean getUseUnamedCursorOutput() {
		return this.useUnamedCursorOutput;
	}
	
	public void setUseUnamedCursorOutput(Boolean newValue) {
		Boolean old = this.useUnamedCursorOutput;
		this.useUnamedCursorOutput = newValue;
		this.firePropertyChanged(USE_UNAMED_CURSOR_OUTPUT, old, this.useUnamedCursorOutput);
	}
	
	@SuppressWarnings("unused")
	private Boolean getUseUnamedCursorOutputForTopLink() {
		return this.useUnamedCursorOutput;
	}
	
	@SuppressWarnings("unused")
	private void setUseUnamedCursorOutputForTopLink(Boolean newValue) {
		this.useUnamedCursorOutput = newValue;
	}
	
	public String getCursorOutputName() {
		return this.cursorOutputName;
	}
	
	public void setCursorOutputName(String newValue) {
		String old = this.cursorOutputName;
		this.cursorOutputName = newValue;
		this.firePropertyChanged(CURSOR_OUTPUT_NAME, old, newValue);
	}
	
	@SuppressWarnings("unused")
	private String getCursorOutputNameForTopLink() {
		return this.cursorOutputName;
	}

	@SuppressWarnings("unused")
	private void setCursorOutputNameForTopLink(String newValue) {
		this.cursorOutputName = newValue;
	}
	
	public MWQuery getQuery() {
		return ((MWQueryFormat)getParent()).getQuery();
	}
	
	//argument aggregate collection
	public int argumentsSize() {
		return namedInArgumentsSize() + namedOutputArgumentsSize() + namedInOutputArgumentsSize() + unamedInArgumentsSize() + unamedOutputArgumentsSize() + unamedInOutputArgumentsSize();
	}
	
	public Iterator<MWAbstractProcedureArgument> getAllArguments() {
		Collection<MWAbstractProcedureArgument> argumentList = new ArrayList<MWAbstractProcedureArgument>();
		
		CollectionTools.addAll(argumentList, this.namedInArguments());
		CollectionTools.addAll(argumentList, this.namedOutputArguments());
		CollectionTools.addAll(argumentList, this.namedInOutputArguments());
		CollectionTools.addAll(argumentList, this.unamedInArguments());
		CollectionTools.addAll(argumentList, this.unamedOutputArguments());
		CollectionTools.addAll(argumentList, this.unamedInOutputArguments());
		
		return argumentList.iterator();
	}
	
	public MWProcedureNamedInArgument addNamedInArgument(String name) {
		MWProcedureNamedInArgument namedArgument = new MWProcedureNamedInArgument(this, name);
		this.addItemToList(namedArgument, namedInArguments, NAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return namedArgument;
	}

	public void removeArgument(MWAbstractProcedureArgument argument) {
		if(argument.isNamedIn()) {
			this.removeNamedArgument((MWProcedureNamedInArgument)argument);
		} else if (argument.isNamedOut()) {
			this.removeNamedOutputArgument((MWProcedureNamedOutputArgument)argument);
		} else if (argument.isNamedInOut()) {
			this.removeNamedInOutputArgument((MWProcedureNamedInOutputArgument)argument);
		} else if (argument.isUnnamedIn()) {
			this.removeUnamedArgument((MWProcedureUnamedInArgument)argument);
		} else if (argument.isUnnamedOut()) {
			this.removeUnamedOuputArgument((MWProcedureUnamedOutputArgument)argument);
		} else if (argument.isUnnamedInOut()) {
			this.removeUnamedInOuputArgument((MWProcedureUnamedInOutputArgument)argument);
		}
	}
	
	public void removeNamedArgument(MWProcedureNamedInArgument argument) {
		this.removeItemFromList(argument, this.namedInArguments, NAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setNamedInArgumentsForTopLink(List<MWProcedureNamedInArgument> namedArguments) {
		this.namedInArguments = namedArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureNamedInArgument> getNamedInArgumentsForTopLink() {
		return CollectionTools.sort(this.namedInArguments);
	}

	public int namedInOutputArgumentsSize() {
		return namedInOuputArguments.size();
	}

	public MWProcedureNamedInOutputArgument addNamedInOutputArgument(String name) {
		MWProcedureNamedInOutputArgument namedInOutputArgument = new MWProcedureNamedInOutputArgument(this, name);
		this.addItemToList(namedInOutputArgument, namedInOuputArguments, NAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return namedInOutputArgument;
	}

	public void removeNamedInOutputArgument(MWProcedureNamedInOutputArgument argument) {
		this.removeItemFromList(argument, this.namedInOuputArguments, NAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setNamedInOutputArgumentsForTopLink(List<MWProcedureNamedInOutputArgument> namedInOutputArguments) {
		this.namedInOuputArguments = namedInOutputArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureNamedInOutputArgument> getNamedInOutputArgumentsForTopLink() {
		return CollectionTools.sort(this.namedInOuputArguments);
	}

	public int namedOutputArgumentsSize() {
		return namedOutputArguments.size();
	}

	public MWProcedureNamedOutputArgument addNamedOutputArgument(String name) {
		MWProcedureNamedOutputArgument namedOutputArgument = new MWProcedureNamedOutputArgument(this, name);
		this.addItemToList(namedOutputArgument, namedOutputArguments, NAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return namedOutputArgument;
	}

	public void removeNamedOutputArgument(MWProcedureNamedOutputArgument argument) {
		this.removeItemFromList(argument, this.namedOutputArguments, NAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setNamedOutputArgumentsForTopLink(List<MWProcedureNamedOutputArgument> namedOutputArguments) {
		this.namedOutputArguments = namedOutputArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureNamedOutputArgument> getNamedOutputArgumentsForTopLink() {
		return CollectionTools.sort(this.namedOutputArguments);
	}

	public MWProcedureUnamedInArgument addUnamedInArgument() {
		MWProcedureUnamedInArgument unamedArgument = new MWProcedureUnamedInArgument(this);
		this.addItemToList(unamedArgument, unamedInArguments, UNAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return unamedArgument;
	}

	public void removeUnamedArgument(MWProcedureUnamedInArgument argument) {
		this.removeItemFromList(argument, this.unamedInArguments, UNAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setUnamedInArgumentsForTopLink(List<MWProcedureUnamedInArgument> unamedArguments) {
		this.unamedInArguments = unamedArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureUnamedInArgument> getUnamedInArgumentsForTopLink() {
		return CollectionTools.sort(this.unamedInArguments);
	}

	public int unamedInOutputArgumentsSize() {
		return unamedInOuputArguments.size();
	}

	public MWProcedureUnamedInOutputArgument addUnamedInOutputArgument() {
		MWProcedureUnamedInOutputArgument unamedInOutputArgument = new MWProcedureUnamedInOutputArgument(this);
		this.addItemToList(unamedInOutputArgument, unamedInOuputArguments, UNAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return unamedInOutputArgument;
	}

	public void removeUnamedInOuputArgument(MWProcedureUnamedInOutputArgument argument) {
		this.removeItemFromList(argument, this.unamedInOuputArguments, UNAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setUnamedInOutputArgumentsForTopLink(List<MWProcedureUnamedInOutputArgument> unamedInOutputArguments) {
		this.unamedInOuputArguments = unamedInOutputArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureUnamedInOutputArgument> getUnamedInOutputArgumentsForTopLink() {
		return CollectionTools.sort(this.unamedInOuputArguments);
	}

	public int unamedOutputArgumentsSize() {
		return unamedOutputArguments.size();
	}

	public MWProcedureUnamedOutputArgument addUnamedOutputArgument() {
		MWProcedureUnamedOutputArgument unamedOutputArgument = new MWProcedureUnamedOutputArgument(this);
		this.addItemToList(unamedOutputArgument, unamedOutputArguments, UNAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
		return unamedOutputArgument;
	}

	public void removeUnamedOuputArgument(MWProcedureUnamedOutputArgument argument) {
		this.removeItemFromList(argument, this.unamedOutputArguments, UNAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	@SuppressWarnings("unused")
	private void setUnamedOutputArgumentsForTopLink(List<MWProcedureUnamedOutputArgument> unamedOutputArguments) {
		this.unamedOutputArguments = unamedOutputArguments;
	}

	@SuppressWarnings("unused")
	private List<MWProcedureUnamedOutputArgument> getUnamedOutputArgumentsForTopLink() {
		return CollectionTools.sort(this.unamedOutputArguments);
	}

	public Iterator<String> namedInArgumentNames() {
		return new TransformationIterator(namedInArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureNamedInArgument)value).getArgumentName();
			}
		};
	}

	public Iterator<String> namedInOutputArgumentNames() {
		return new TransformationIterator(namedInOutputArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureNamedInOutputArgument)value).getArgumentName();
			}
		};
	}

	public Iterator<String> namedOutputArgumentNames() {
		return new TransformationIterator(namedOutputArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureNamedOutputArgument)value).getArgumentName();
			}
		};
	}

	public Iterator<String> unamedOutputArgumentNames() {
		return new TransformationIterator(unamedOutputArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureUnamedOutputArgument)value).getArgumentName();
			}
		};
	}

	public Iterator<String> unamedInOutputArgumentNames() {
		return new TransformationIterator(unamedInOutputArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureUnamedInOutputArgument)value).getArgumentName();
			}
		};
	}

	public Iterator<String> unamedInArgumentNames() {
		return new TransformationIterator(unamedInArguments()) {
			@Override
			protected String transform(Object value) {
				return ((MWProcedureUnamedInArgument)value).getArgumentName();
			}
		};
	}

	public ListIterator<MWProcedureNamedInArgument> namedInArguments() {
		return new CloneListIterator(this.namedInArguments);
	}

	public ListIterator<MWProcedureNamedInOutputArgument> namedInOutputArguments() {
		return new CloneListIterator(this.namedInOuputArguments);
	}

	public ListIterator<MWProcedureNamedOutputArgument> namedOutputArguments() {
		return new CloneListIterator(this.namedOutputArguments);
	}

	public ListIterator<MWProcedureUnamedInArgument> unamedInArguments() {
		return new CloneListIterator(this.unamedInArguments);
	}

	public ListIterator<MWProcedureUnamedInOutputArgument> unamedInOutputArguments() {
		return new CloneListIterator(this.unamedInOuputArguments);
	}

	public ListIterator<MWProcedureUnamedOutputArgument> unamedOutputArguments() {
		return new CloneListIterator(this.unamedOutputArguments);
	}

	public int namedInArgumentsSize() {
		return this.namedInArguments.size();
	}

	public int unamedInArgumentsSize() {
		return this.unamedInArguments.size();
	}

	@Override
	public String displayString() {
		return this.getName();
	}

	public void removeNamedArgumentAt(int index) {
		removeItemFromList(index, this.namedInArguments, NAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	public void removeNamedInOutputArgumentAt(int index) {
		removeItemFromList(index, this.namedInOuputArguments, NAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	public void removeNamedOutputArgumentAt(int index) {
		removeItemFromList(index, this.namedOutputArguments, NAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	public void removeUnamedArgumentAt(int index) {
		removeItemFromList(index, this.unamedInArguments, UNAMED_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	public void removeUnamedInOutputArgumentAt(int index) {
		removeItemFromList(index, this.unamedInOuputArguments, UNAMED_INOUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	public void removeUnamedOutputArgumentAt(int index) {
		removeItemFromList(index, this.unamedOutputArguments, UNAMED_OUTPUT_ARGUMENT_LIST);
		fireCollectionChanged(ARGUMENT_COLLECTION);
	}

	//******************** Runtime Conversions ******************************
	
	public org.eclipse.persistence.queries.StoredProcedureCall buildRuntimeCall() {
		
		StoredProcedureCall runtimeProcedure = new StoredProcedureCall();
		
		runtimeProcedure.setProcedureName(getName());
		
		if (this.useUnamedCursorOutput) {
			runtimeProcedure.useUnnamedCursorOutputAsResultSet();
		} else {
			if (this.getCursorOutputName() == null) {
				runtimeProcedure.useNamedCursorOutputAsResultSet("");
			} else {
				runtimeProcedure.useNamedCursorOutputAsResultSet(this.getCursorOutputName());
			}
		}
				
		//TODO: add support for class/database field addition when RT support is completed in bug #7510411.

		for (Iterator<MWProcedureNamedInArgument> it = this.namedInArguments(); it.hasNext(); ) {
			MWProcedureNamedInArgument namedArgument = it.next();
			namedArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}

		for (Iterator<MWProcedureNamedInOutputArgument> it = this.namedInOutputArguments(); it.hasNext(); ) {
			MWProcedureNamedInOutputArgument namedInOutputArgument = it.next();
			namedInOutputArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}

		for (Iterator<MWProcedureNamedOutputArgument> it = this.namedOutputArguments(); it.hasNext(); ) {
			MWProcedureNamedOutputArgument namedOutputArgument = it.next();
			namedOutputArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}

		for (Iterator<MWProcedureUnamedInArgument> it = this.unamedInArguments(); it.hasNext(); ) {
			MWProcedureUnamedInArgument unamedArgument = it.next();
			unamedArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}

		for (Iterator<MWProcedureUnamedInOutputArgument> it = this.unamedInOutputArguments(); it.hasNext(); ) {
			MWProcedureUnamedInOutputArgument unamedInOutputArgument = it.next();
			unamedInOutputArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}

		for (Iterator<MWProcedureUnamedOutputArgument> it = this.unamedOutputArguments(); it.hasNext(); ) {
			MWProcedureUnamedOutputArgument unamedOutputArgument = it.next();
			unamedOutputArgument.addRuntimeEclipseLinkArgument(runtimeProcedure);
		}
		
		return runtimeProcedure;

	}
	
	public static MWProcedure convertFromEclipseLinkRuntime(MWStoredProcedureQueryFormat format, org.eclipse.persistence.queries.StoredProcedureCall call) {
		MWProcedure procedure = new MWProcedure(format);
	
		procedure.setName(call.getProcedureName());
		
		boolean unamedCursor = true;

		Iterator<String> argumentNames = (Iterator<String>)call.getProcedureArgumentNames().iterator();
		Iterator<Object> parameters = (Iterator<Object>)call.getParameters().iterator();
		Iterator<Integer> parameterTypes = (Iterator<Integer>)call.getParameterTypes().iterator();
		
		while (argumentNames.hasNext()) {
			String name = argumentNames.next();
			Integer type = parameterTypes.next();
			MWAbstractProcedureArgument arg = null;
			org.eclipse.persistence.internal.helper.DatabaseField field = null;
			org.eclipse.persistence.internal.helper.DatabaseField outField = null;
			Object parameter = parameters.next();
			
			if (parameter instanceof org.eclipse.persistence.internal.helper.DatabaseField) {
				field = (org.eclipse.persistence.internal.helper.DatabaseField)parameter;
			} else {
				field = (org.eclipse.persistence.internal.helper.DatabaseField)((Object[])parameter)[0];
				outField = (org.eclipse.persistence.internal.helper.DatabaseField)((Object[])parameter)[1];
			}

			if (name == null) {			
				if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN)) {
					arg = procedure.addUnamedInArgument();
				} else if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT)) {
					arg = procedure.addUnamedInOutputArgument();
					((MWAbstractProcedureInOutputArgument)arg).setOutFieldName(outField.getName());
				} else if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT)) {
					arg = procedure.addUnamedOutputArgument();
				}
			} else {				
				if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN)) {
					arg = procedure.addNamedInArgument(name);
				} else if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT)) {
					arg = procedure.addNamedInOutputArgument(name);
					((MWAbstractProcedureInOutputArgument)arg).setOutFieldName(outField.getName());
				} else if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT)) {
					arg = procedure.addNamedOutputArgument(name);
				}  else if (type.equals(org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT_CURSOR)) {  
					unamedCursor = false;
					procedure.setCursorOutputName(name);
					procedure.setUseUnamedCursorOutput(Boolean.FALSE);
				}
			}
			arg.setFieldName(field.getName());
			arg.setFieldSqlTypeName(MWAbstractProcedureArgument.jdbcTypeNameFor(field.getSqlType()));
			if (field instanceof org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField) {
				org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField orField = (org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField)field;
				arg.setFieldSubTypeName(orField.getSqlTypeName());
				if (orField.getType() != null) {
					arg.setFieldJavaClassName(orField.getType().getName());
				}
				if (orField.getNestedTypeField() != null) {
					arg.setNestedTypeFieldName(orField.getNestedTypeField().getName());
				}
			}
			
		}

		if (unamedCursor) {
			procedure.setUseUnamedCursorOutput(Boolean.TRUE);
			procedure.setCursorOutputName(null);
		}
		
		return procedure;
	}
	
}