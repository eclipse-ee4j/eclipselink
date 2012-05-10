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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TreeIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/**
 * This is the factory and repository for all the class "descriptions"
 * (MWClasses) that represent the user's object model.
 * 
 * This "internal" class repository manages all the MWClasses.
 * It uses an "external" class repository to build the MWClasses
 * from external metadata, which are typically derived from Java
 * Class objects or byte codes.
 * 
 * There are several varieties of MWClasses:
 * - "core" classes
 *     for performance reasons, a "core" class (i.e. a JDK or TopLink
 *     classes) is initially only partially populated with state related
 *     to its class declaration (superclass, interfaces, etc.); the
 *     remaining state is lazily populated on demand
 * - "stub" classes
 *     a user class that is only referenced by other user classes is not
 *     populated beyond its name (and whether it might be an interface)
 * - "fully populated" classes
 *     a user class that is mapped, or whose fields or methods are referenced
 *     by descriptors, is fully populated with all its state; this is the only type
 *     of class that is written out to an XML file
 * 
 * To avoid confusion between Java Classes and MWClasses,
 * we refer to MWClasses as "types" whenever possible and
 * to Java Classes as "classes".
 * 
 * NB: We synchronized 'types' whenever types are being added or removed.
 * This is because multiple threads can fault in new types. :-(  ~bjv
 */
public final class MWClassRepository
	extends MWModel
	implements ProjectSubFileComponentContainer
{

	/**
	 * The MWClasses, which includes "user" types, "stub" types,
	 * and "core" types, keyed by name. We need to carefully
	 * synchronize access to this map since we will fault in types
	 * whenever they are referenced. This means any number of
	 * methods can cause an addition to this map.
	 */
	private Map types;
		// 'types' is NOT a public property, but 'userTypes' is

	/**
	 * The names of the types contained in 'types', above,
	 * keyed by the name converted to lower-case.
	 * This must be kept in synch with 'types'.
	 * @see #addType(String, MWClass)
	 * @see #removeTypeNamed(String)
	 */
	private Map typeNames;

	/**
	 * this holds only the current set of "user" types and must
	 * be maintained by tracking changes to the types themselves (yuck);
	 * @see #typeChanged(MWClass)
	 * @see MWClass#aspectChanged(String)
	 */
	private Set userTypes;
		public static final String USER_TYPES_COLLECTION = "userTypes";

	/**
	 * these user type names are read in by TopLink and are then
	 * used and managed by the IOManager;
	 * DO NOT use them for anything else  ~bjv
	 */
	private Collection userTypeNames;
		private static final String USER_TYPE_NAMES_COLLECTION = "userTypeNames";

	/**
	 * the "external" class repository that supplies the
	 * "external" types/classes used to build MWClasses
	 */
	private volatile ExternalClassRepository externalClassRepository;

	/**
	 * classpath used by the "external" class repository, if necessary, to
	 * search for the "external" classes used to build MWClasses;
	 * it is a list of strings
	 */
	private List classpathEntries;
		public static final String CLASSPATH_ENTRIES_LIST = "classpathEntries";

	/** these are the keys used to find the classpath entries for the "core" classes */
	private static final Class[] CORE_KEYS =
		new Class[] {
			java.lang.Object.class,		// rt.jar
			//some custom jvms have the classes divided between more than one jar which can cause problems
			//associated with bug #222471
			java.util.List.class,
			java.util.Map.class,
			java.util.Collection.class,
			java.util.Set.class,
			org.eclipse.persistence.indirection.ValueHolderInterface.class		// eclipselink.jar
		};

	/** the "core" classes: the names of the MWClasses that are never written out */
	private static Set coreClassNames;

	/**
	 * the "core" classes: the names of the MWClasses that are never written out,
	 * keyed by the name converted to lowercase
	 */
	private static Map coreClassNamesLowerCase;

	// queried reflectively by I/O Manager
	private static final String SUB_DIRECTORY_NAME = "classes";
	
	//not peristed
	private boolean persistLastRefresh;
		public static final String PERSIST_LAST_REFRESH_PREFERENCE = "last refresh";
		public static final boolean PERSIST_LAST_REFRESH_PREFERENCE_DEFAULT = true;
	

	// ********** constructors **********
	
	/**
	 * default constructor - for TopLink use only
	 */
	private MWClassRepository() {
		super();
	}
	
	/**
	 * public constructor:
	 * this is the starting point for all the classes in the meta package...
	 */
	public MWClassRepository(MWProject project) {
		super(project);
	}


	// ********** initialization **********
	
	/**
	 * initialize transient state
	 */
	protected void initialize() {
		super.initialize();
	}
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.types = new Hashtable();
		this.typeNames = new Hashtable();
		this.userTypes = Collections.synchronizedSet(new HashSet());
		this.classpathEntries = new Vector();
		this.userTypeNames = new HashSet();
		this.persistLastRefresh = true;
	}
	

	// ********** accessors **********

	/**
	 * PRIVATE - called by I/O Manager (ProjectReader);
	 * 'types' should only hold "user" types after it is initialized by
	 * the I/O Manager
	 */
	private void setTypes(Collection types) {
		this.types = new Hashtable(types.size());
		this.typeNames = new Hashtable(types.size());
		for (Iterator stream = types.iterator(); stream.hasNext(); ) {
			this.addType((MWClass) stream.next());
		}
		this.userTypes = Collections.synchronizedSet(new HashSet(types));
	}

	private void addType(MWClass type) {
		this.addType(type.getName(), type);
	}

	/**
	 * keep 'types' and 'typeNames' in synch
	 */
	private void addType(String typeName, MWClass type) {
		// don't add the type to 'userTypes' - it will be added when the type becomes a "non-stub" type
		Object previousType = this.types.put(typeName, type);
		if (previousType != null) {
			this.types.put(typeName, previousType);		// restore 'types'
			throw new IllegalArgumentException("duplicate types: " + previousType + " vs. " + type);
		}

		Object previousTypeName = this.typeNames.put(typeName.toLowerCase(), typeName);
		if (previousTypeName != null) {
			this.types.remove(typeName);		// restore 'types'
			this.typeNames.put(typeName.toLowerCase(), previousTypeName);		// restore 'typeNames'
			throw new IllegalArgumentException("type names cannot differ only by case: " + previousTypeName + " vs. " + typeName);
		}
	}

	/**
	 * keep 'types', 'typeNames', and 'userTypes' in synch
	 */
	private void removeType(MWClass type) {
		this.removeTypeNamed(type.getName());
		// 'userTypes' may or may not contain the type - it doesn't matter
		this.removeUserType(type);
	}

	/**
	 * keep 'types', and 'typeNames' in synch; but leave 'userTypes' unchanged(!)
	 */
	private MWClass removeTypeNamed(String typeName) {
		Object previousTypeName = this.typeNames.remove(typeName.toLowerCase());
		if (previousTypeName == null) {
			throw new IllegalArgumentException("missing type name: " + typeName);
		}
		if ( ! previousTypeName.equals(typeName)) {
			this.typeNames.put(typeName.toLowerCase(), previousTypeName);		// restore 'typeNames'???
			throw new IllegalArgumentException("inconsistent type names: " + previousTypeName + " vs. " + typeName);
		}

		MWClass previousType = (MWClass) this.types.remove(typeName);
		if (previousType == null) {
			this.typeNames.put(typeName.toLowerCase(), previousTypeName);		// restore 'typeNames'???
			throw new IllegalArgumentException("missing type: " + typeName);
		}

		return previousType;
	}


	/**
	 * Return the "user" types; i.e. all the types that are neither
	 * "stub" types nor "core" types.
	 */
	public Iterator userTypes() {
		return new CloneIterator(this.userTypes);
	}

	public int userTypesSize() {
		return this.userTypes.size();
	}

	private void addUserType(MWClass userType) {
		this.addItemToCollection(userType, this.userTypes, USER_TYPES_COLLECTION);
	}

	private void removeUserType(MWClass userType) {
		this.removeItemFromCollection(userType, this.userTypes, USER_TYPES_COLLECTION);
	}

	/**
	 * PRIVATE - no one should need direct access to the external
	 * class repository;
	 * lazy initialize so we don't have to propagate exceptions any
	 * more than necessary
	 */
	private ExternalClassRepository getExternalClassRepository() {
		if (this.externalClassRepository == null) {
			this.externalClassRepository = this.buildExternalClassRepository();
		}
		return this.externalClassRepository;
	}
	
	private ExternalClassRepository buildExternalClassRepository() {
		return this.externalClassRepositoryFactory().buildClassRepository(this.buildExternalClassRepositoryClasspath());
	}
	

	/** NOTE: Classpath entries are Strings */

	public ListIterator classpathEntries() {
		return new CloneListIterator(this.classpathEntries);
	}

	public int classpathEntriesSize() {
		return this.classpathEntries.size();
	}

	public String getClasspathEntry(int index) {
		return (String) this.classpathEntries.get(index);
	}
	
	public void addClasspathEntry(int index, String entry) {
		this.addItemToList(index, entry, this.classpathEntries, CLASSPATH_ENTRIES_LIST);
		this.externalClassRepository = null;
	}
	
	public void addClasspathEntry(String entry) {
		this.addClasspathEntry(this.classpathEntriesSize(), entry);
	}

	public void addClasspathEntries(int index, List entries) {
		this.addItemsToList(index, entries, this.classpathEntries, CLASSPATH_ENTRIES_LIST);
		this.externalClassRepository = null;
	}

	public void addClasspathEntries(List entries) {
		this.addClasspathEntries(this.classpathEntriesSize(), entries);
	}

	public void addClasspathEntries(ListIterator entries) {
		this.addClasspathEntries(CollectionTools.list(entries));
	}

	public String removeClasspathEntry(int index) {
		String result = (String) this.removeItemFromList(index, this.classpathEntries, CLASSPATH_ENTRIES_LIST);
		this.externalClassRepository = null;
		return result;
	}

	public List removeClasspathEntries(int index, int length) {
		List result = this.removeItemsFromList(index, length, this.classpathEntries, CLASSPATH_ENTRIES_LIST);
		this.externalClassRepository = null;
		return result;
	}

	public String replaceClasspathEntry(int index, String newEntry) {
		String result = (String) this.setItemInList(index, newEntry, this.classpathEntries, CLASSPATH_ENTRIES_LIST);
		this.externalClassRepository = null;
		return result;
	}


	// ********** queries **********

	/**
	 * the external class repository factory is supplied by client code
	 */
	private ExternalClassRepositoryFactory externalClassRepositoryFactory() {
		return this.getProject().getSPIManager().getExternalClassRepositoryFactory();
	}
	
	/**
	 * return an iterator on all the external class descriptions
	 * available in the external class repository
	 */
	public Iterator externalClassDescriptions() {
		return new ArrayIterator(this.getExternalClassRepository().getClassDescriptions());
	}

	/**
	 * return an iterator on all the external "reference" class
	 * descriptions available in the external class repository;
	 * this will *not* include void or the primitives
	 */
	public Iterator externalReferenceClassDescriptions() {
		return this.referenceTypes(this.externalClassDescriptions());
	}
	
	/**
	 * return the first encountered external class description with the specified name,
	 * as defined by the external class repository implementation;
	 * the external class repository may contain more than
	 * one external class description with the specified name...
	 */
	private ExternalClassDescription externalClassDescriptionNamed(String name) {
		return this.getExternalClassRepository().getClassDescription(name);
	}
	
	/**
	 * return an iterator on all the external class descriptions
	 * available in the external class repository
	 * with the specified name
	 */
	Iterator externalClassDescriptionsNamed(final String name) {
		return new FilteringIterator(this.externalClassDescriptions()) {
			public boolean accept(Object next) {
				return ((ExternalClassDescription) next).getName().equals(name);
			}
			public String toString() {
				return "MWClassRepository.externalClassDescriptionsNamed(String)";
			}
		};
	}
	
	/**
	 * return an iterator on all the external class descriptions
	 * available in the external class repository
	 * combined with all the internal types in the repository
	 */
	public Iterator combinedTypes() {
		return new CompositeIterator(new CloneIterator(this.userTypes), this.externalClassDescriptions());
	}
	
	/**
	 * return an iterator on all the external "reference" types
	 * available in the external class repository
	 * combined with all the internal "reference" types in
	 * the repository;
	 * this will *not* include void or the primitives
	 */
	public Iterator combinedReferenceTypes() {
		return this.referenceTypes(this.combinedTypes());
	}

	/**
	 * return an iterator on all the external class descriptions
	 * available in the external class repository
	 * combined with all the internal types in
	 * the repository, eliminating duplicates
	 */
	public Iterator uniqueCombinedTypes() {
		return this.uniqueTypes(this.combinedTypes());
	}
	
	/**
	 * return an iterator on all the external "reference" types
	 * available in the external class repository
	 * combined with all the internal "reference" types in
	 * the repository, eliminating duplicates;
	 * this will *not* include void or the primitives
	 */
	public Iterator uniqueCombinedReferenceTypes() {
		return this.uniqueTypes(this.combinedReferenceTypes());
	}

	/**
	 * filter the specified iterator of class descriptions to
	 * return only a unique set of types, based on the type names
	 */
	private Iterator uniqueTypes(Iterator originalTypes) {
		return new FilteringIterator(originalTypes) {
			private Set usedNames = new HashSet(10000);	// this will be big...
			protected boolean accept(Object next) {
				// @see java.util.Set#add(Object)
				return this.usedNames.add(((ClassDescription) next).getName());
			}
			public String toString() {
				return "MWClassRepository.uniqueTypes(Iterator)";
			}
		};
	}
	
	/**
	 * filter the specified iterator of class descriptions to
	 * return only "reference" types;
	 * this will *not* include void or the primitives (int, char, etc.)
	 */
	private Iterator referenceTypes(Iterator originalTypes) {
		return new FilteringIterator(originalTypes) {
			protected boolean accept(Object next) {
				return ! MWClass.nonReferenceClassNamesContains(((ClassDescription) next).getName());
			}
			public String toString() {
				return "MWClassRepository.referenceTypes(Iterator)";
			}
		};
	}
	
	/**
	 * return the directory used to convert relative
	 * classpath entries to fully qualified files
	 */
	File classpathBaseDirectory() {
		return this.getProject().getSaveDirectory();
	}
	
	/**
	 * return the classpath with the entries converted to
	 * fully qualified files (any relative entries will be
	 * resolved relative to the project save directory)
	 */
	private File[] buildExternalClassRepositoryClasspath() {
		List<String> coreFiles = buildCoreClassLocations();
		List files = new ArrayList(this.classpathEntriesSize() + coreFiles.size());
		CollectionTools.addAll(files, this.fullyQualifiedClasspathFiles());
		ListIterator<String> coreFileIter = coreFiles.listIterator();
		// hard-code the "core" classes at the back of the classpath
		// so they can be overridden by the client
		while (coreFileIter.hasNext()) {
			files.add(new File(coreFileIter.next()));
		}
		return (File[]) files.toArray(new File[files.size()]);
	}
	
	/**
	 * return the classpath with the entries converted to
	 * fully qualified files (any relative entries will be
	 * resolved relative to the project save directory)
	 */
	private Iterator fullyQualifiedClasspathFiles() {
		return new TransformationIterator(this.classpathEntries()) {
			protected Object transform(Object next) {
				File file = new File((String) next);
				if ( ! file.isAbsolute()) {
					file = new File(MWClassRepository.this.classpathBaseDirectory(), file.getPath());
				}
				return file;
			}
			public String toString() {
				return "MWClassRepository.fullyQualifiedClasspathFiles()";
			}
		};
	}
	
	/**
	 * Return the entries with path resolved to project save directory.
	 */
	public Iterator fullyQualifiedClasspathEntries() {
		return new TransformationIterator(this.fullyQualifiedClasspathFiles()) {
			protected Object transform(Object next) {
				return ((File) next).getAbsolutePath();
			}
			public String toString() {
				return "MWClassRepository.fullyQualifiedClasspathEntries()";
			}
		};
	}

	/**
	 * return the immediate [loaded] subclasses of the specified type
	 */
	Iterator subclassesOf(MWClass type) {
		Collection subclasses = new ArrayList();
		synchronized (this.types) {
			for (Iterator stream = this.types.values().iterator(); stream.hasNext(); ) {
				MWClass next = (MWClass) stream.next();
				if (next.getSuperclass() == type) {
					subclasses.add(next);
				}
			}
		}
		return subclasses.iterator();
	}
	
	/**
	 * return the immediate [loaded] subclasses of the class,
	 * all their [loaded] subclasses, and so on
	 */
	public Iterator allSubclassesOf(MWClass type) {
		return new TreeIterator(type) {
			protected Iterator children(Object next) {
				return ((MWClass) next).subclasses();
			}
			public String toString() {
				return "MWClassRepository.allSubclassesOf(MWClass)";
			}
		};
	}

	/**
	 * return the specified type if it has been loaded;
	 * return null if the specified type is not loaded
	 * (or has been "garbage-collected")
	 */
	public MWClass typeNamedIgnoreCase(String typeName) {
		synchronized (this.types) {
			return this.typeNamedIgnoreCase2(typeName);
		}
	}
	
	/**
	 * unsynchronized version of #typeNamedIgnoreCase(String)
	 */
	private MWClass typeNamedIgnoreCase2(String typeName) {
		typeName = (String) this.typeNames.get(typeName.toLowerCase());
		if (typeName == null) {
			return null;
		}
		MWClass type = (MWClass) this.types.get(typeName);
		return this.typeIsGarbage(type) ? null : type;
	}
	
	/**
	 * return whether the specified type is "garbage";
	 * i.e. there are no longer any references to it;
	 * if the type is garbage, it will be removed as a side-effect
	 */
	private boolean typeIsGarbage(MWClass type) {
		for (Iterator stream = this.getProject().branchReferences(); stream.hasNext(); ) {
			Reference ref = (Reference) stream.next();
			if (ref.getTarget().isDescendantOf(type)) {
				// something is still holding on to the type or one of
				// its descendants, so we can't remove it yet
				return false;
			}
		}
		this.removeType(type);
		return true;
	}

	/**
	 * If we already have the specified type, return it;
	 * otherwise, return a newly-built stub (i.e. we will
	 * never return null from this method).
	 * 
	 * We will *not* attempt to build up a fully-populated type;
	 * but we will *partially* build up certain, core types (e.g.
	 * Collection, ValueHolderInterface); and some of these
	 * core types will always be fully-populated, whether you
	 * like it or not (e.g. Object).
	 * 
	 * Typically, this is the best method to call when you just need
	 * a class (as opposed to an attribute or method). If you need
	 * a fully-populated type, call this method to get the type,
	 * check whether the type is a stub, and, if it is a stub, refresh it.
	 * 
	 * You may NOT always want to refresh a type, since that might
	 * drop any changes entered manually by the user since the
	 * last refresh; but it should be OK to refresh a stub.
	 * 
	 * If you need to refresh a type from a new "external" definition
	 * (e.g. a freshly compiled set of bytecodes),
	 * call the method #refreshExternalClassDescriptions() before refreshing
	 * the type. This will cause the external class repository to be
	 * rebuilt and should allow the new "external" class definition
	 * to be used during the refresh.
	 * 
	 * This method should only be called by MWModel or MWHandle; other
	 * classes should use MWModel#typeNamed(String) or
	 * MWHandle#typeNamed(String).
	 */
	public MWClass typeNamedInternal(String typeName) {
		synchronized (this.types) {
			return this.typeNamedInternal2(typeName);
		}
	}

	/**
	 * unsynchronized version of #typeNamedInternal(String)
	 */
	private MWClass typeNamedInternal2(String typeName) {
		if (typeName == null) {
			throw new NullPointerException();
		}
		typeName = typeName.trim();
		MWClass type = (MWClass) this.types.get(typeName);
		if (type != null) {
			return type;
		}

		boolean coreType = this.checkTypeName(typeName);
		type = new MWClass(this, typeName, coreType);
		this.addType(typeName, type);
		type.initializeNameDependentState();

		// core types must be, at least, partially populated...
		if (coreType) {
			this.buildCoreType(type);
		}
		return type;
	}

	/**
	 * validate the specified type name and return whether
	 * it is the name of a "core" type;
	 * the return value isn't really consistent with the purpose of
	 * the method, but it provides us with a slight performance
	 * improvement
	 */
	private boolean checkTypeName(String typeName) {
		if (typeName.length() == 0) {
			throw new IllegalArgumentException("empty type name");
		}
		if (ClassTools.classNamedIsArray(typeName)) {
			throw new IllegalArgumentException("use MWTypeDeclaration for array types");
		}
		if (coreClassNamesContains(typeName)) {
			return true;
		}
		// "java.lang.Object" is OK, but "Java.Lang.OBJECT" is not
		String collision = coreClassNameIgnoreCase(typeName);
		if (collision != null) {
			throw new IllegalArgumentException("case-insensitive name collision with \"core\" type: " + collision);
		}
		return false;
	}

	/**
	 * return the primitive types (e.g. int, char);
	 * does *not* include void
	 */
	public Iterator primitiveTypes() {
		return new TransformationIterator(MWClass.primitiveClassNames()) {
			protected Object transform(Object next) {
				// the primitives are always fully populated
				return MWClassRepository.this.typeNamedInternal((String) next);
			}
			public String toString() {
				return "MWClassRepository.primitiveTypes()";
			}
		};
	}
	
	/**
	 * return the type corresponding to the void keyword
	 */
	public MWClass voidType() {
		// void is always fully populated
		return this.typeNamedInternal(MWClass.voidClassName());
	}
	
	/**
	 * Look for the specified resource on the project classpath.
	 */
	public URL findResource(String resourceName) {
		Classpath cp;
		synchronized (this.classpathEntries) {
			cp = new Classpath(this.classpathEntries);
		}
		return new URLClassLoader(cp.urls()).findResource(resourceName);
	}


	// ********** behavior **********

	/**
	 * containment hierarchy
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.types) { children.addAll(this.types.values()); }
	}

	/**
	 * One of the types has changed; determine whether the change
	 * affects our "user types" collection.
	 */	
	void typeChanged(MWClass type) {
		if (type.isCoreType()) {
			return;
		}
		if (type.isStub()) {
			this.removeUserType(type);
		} else {
			this.addUserType(type);
		}
	}

	/**
	 * a type has been renamed - validate the new name and rehash our maps
	 */
	void typeRenamed(String oldName, String newName) {
		if (this.checkTypeName(newName)) {
			throw new IllegalArgumentException("type cannot be renamed to a \"core\" type");
		}
		MWClass type;
		synchronized (this.types) {
			// leave the type in 'userTypes' if it's there
			type = this.removeTypeNamed(oldName);
			this.addType(newName, type);
		}
		if (this.userTypes.contains(type)) {
			// if a user type has been renamed, we need to fire an "internal"
			// change event so the repository is marked dirty
			this.fireCollectionChanged(USER_TYPE_NAMES_COLLECTION);
		}
	}

	/** 
	 * refresh the specified type using a default refresh policy
	 * @see #refresh(MWClass, MWClassRefreshPolicy)
	 */
	void refreshType(MWClass type) throws ExternalClassNotFoundException {
		this.refreshType(type, DefaultMWClassRefreshPolicy.instance());
	}

	/**
	 * refresh the specified type from the current set of external class descriptions;
	 * if you would like to force the type to be refreshed from a newly-built
	 * set of external class descriptions, call #refreshExternalClassDescriptions() first;
	 * the type will be refreshed with the "default" external class description returned by
	 * the external class repository
	 * @see ExternalClassRepository#getExternalClassDescription(String)
	 */
	void refreshType(MWClass type, MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		ExternalClassDescription exClassDescription = this.externalClassDescriptionNamed(type.getName());
		if (exClassDescription == null) {
			throw new ExternalClassNotFoundException(type.getName());
		}
		type.refresh(exClassDescription.getExternalClass(), refreshPolicy);
	}

	/**
	 * refresh the specified type's members from the current set of external class descriptions;
	 * if you would like to force the type to be refreshed from a newly-built
	 * set of external class descriptions, call #refreshExternalClassDescriptions() first;
	 * the type's members will be refreshed with the "default" external class description returned by
	 * the external class repository;
	 * this method is used to finish the "lazy refresh" of a "core" type
	 * @see ExternalClassRepository#getExternalClassDescription(String)
	 */
	void refreshTypeMembers(MWClass type, MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		ExternalClassDescription exClassDescription = this.externalClassDescriptionNamed(type.getName());
		if (exClassDescription == null) {
			throw new ExternalClassNotFoundException(type.getName());
		}
		type.refreshMembers(exClassDescription.getExternalClass(), refreshPolicy);
	}

	/**
	 * refresh the collection of external class descriptions by forcing
	 * a rebuild of the external class repository; the next
	 * call to #externalClassDescriptions() will return an iterator on
	 * the refreshed collection
	 */
	public void refreshExternalClassDescriptions() {
		this.externalClassRepository = null;
	}

	/**
	 * refresh the specified type with the "default" external class description;
	 * throw an exception if we can't load the corresponding metadata
	 */
	public void refreshTypeNamed(String typeName) throws ExternalClassNotFoundException {
		this.typeNamedInternal(typeName).refresh();
	}

	/**
	 * refresh the type corresponding to the specified external class description;
	 * throw an exception if we can't load the corresponding metadata
	 */
	public void refreshTypeFor(ExternalClassDescription externalClassDescription) throws ExternalClassNotFoundException {
		this.typeNamedInternal(externalClassDescription.getName()).refresh(externalClassDescription.getExternalClass());
	}

	/**
	 * refresh the types corresponding to the specified external class descriptions;
	 * notify the specified listener for each corresponding
	 * chunk of metadata we can't load
	 */
	public void refreshTypesFor(Iterator externalClassDescriptions, ExternalClassLoadFailureListener listener) {
		while (externalClassDescriptions.hasNext()) {
			ExternalClassDescription externalClassDescription = (ExternalClassDescription) externalClassDescriptions.next();
			try {
				this.refreshTypeFor(externalClassDescription);
			} catch (ExternalClassNotFoundException ex) {
				listener.externalClassLoadFailure(new ExternalClassLoadFailureEvent(this, externalClassDescription.getName(), ex));
			}
		}
	}

	/**
	 * refresh the types corresponding to the specified external class descriptions;
	 * return the failures
	 */
	public ExternalClassLoadFailureContainer refreshTypesFor(Iterator externalClassDescriptions) {
		ExternalClassLoadFailureContainer listener = new ExternalClassLoadFailureContainer();
		this.refreshTypesFor(externalClassDescriptions, listener);
		return listener;
	}
	
	/**
	 * refresh the specified MWClass types;
	 * notify the specified listener for each corresponding
	 * chunk of metadata we can't load
	 */
	public void refreshTypes(Iterator refreshTypes, ExternalClassLoadFailureListener listener) {
		while (refreshTypes.hasNext()) {
			MWClass type = (MWClass) refreshTypes.next();
			try {
				this.refreshType(type);
			} catch (ExternalClassNotFoundException ecnfe) {
				listener.externalClassLoadFailure(new ExternalClassLoadFailureEvent(this, type.getName(), ecnfe));
			}
		}
	}
	
	/**
	 * refresh the specified MWClass types;
	 * return the failures
	 */
	public ExternalClassLoadFailureContainer refreshTypes(Iterator refreshTypes) {
		ExternalClassLoadFailureContainer listener = new ExternalClassLoadFailureContainer();
		this.refreshTypes(refreshTypes, listener);
		return listener;
	}
	
	/**
	 * core types are partially-populated
	 */
	private void buildCoreType(MWClass coreType) {
		try {
			// for now, assume that *any* version of the external class description is OK; so just take the default one
			ExternalClassDescription externalClassDescription = this.externalClassDescriptionNamed(coreType.getName());
			ExternalClass externalClass = externalClassDescription.getExternalClass();
			coreType.refreshDeclaration(externalClass);
		} catch (ExternalClassNotFoundException ex) {
			// if we can't load a "core" type, we are in serious trouble...
			throw new RuntimeException(coreType.getName(), ex);
		}
	}

	/**
	 * notify the branch of classes under the specified type that
	 * their superclasses have changed
	 */
	void hierarchyChanged(MWClass type) {
		for (Iterator stream = this.allSubclassesOf(type); stream.hasNext(); ) {
			((MWClass) stream.next()).superclassesChanged();
		}
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
			// we handle a renamed type directly in #typeRenamed(String, String)
		}
	}
	
	/**
	 * performance tuning: ignore this method - assume there are no
	 * references to mappings in the class repository or its descendants
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
	 * references to mappings in the class repository or its descendants
	 */
	public void descriptorUnmapped(Collection mappings) {
		// do nothing
	}
	
	
	// ********** displaying and printing **********
	
	public void toString(StringBuffer sb) {
		sb.append(this.types.size());
		sb.append(" types/");
		sb.append(this.userTypes.size());
		sb.append(" user types");
	}


	// ********** SubComponentContainer implementation **********

	public Iterator projectSubFileComponents() {
		return this.userTypes();
	}

	public void setProjectSubFileComponents(Collection subComponents) {
		this.setTypes(subComponents);
	}

	public Iterator originalProjectSubFileComponentNames() {
		return this.userTypeNames.iterator();
	}

	public void setOriginalProjectSubFileComponentNames(Collection originalSubComponentNames) {
		this.userTypeNames = originalSubComponentNames;
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
		// the types might be dirty
		return false;
	}

	/**
	 * return whether the specified child of the repository is dirty AND
	 * is written to the .mwp file
	 */
	private boolean childHasChangedTheProjectSaveFile(Object child) {
		if (this.types.containsValue(child)) {
			// types are written to separate files
			return false;
		}
		// the child is NOT a type,
		// so all of its state is written to the .mwp file
		return ((Node) child).isDirtyBranch();
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWClassRepository.class);

		XMLCompositeDirectCollectionMapping classpathMapping = new XMLCompositeDirectCollectionMapping();
		classpathMapping.setAttributeName("classpathEntries");
		classpathMapping.setSetMethodName("setClasspathEntriesForTopLink");
		classpathMapping.setGetMethodName("getClasspathEntriesForTopLink");
		classpathMapping.setXPath("classpath-entries/entry/text()");
		descriptor.addMapping(classpathMapping);

		XMLCompositeDirectCollectionMapping userTypeNamesMapping = new XMLCompositeDirectCollectionMapping();
		userTypeNamesMapping.setAttributeName("userTypeNames");
		userTypeNamesMapping.setSetMethodName("setUserTypeNamesForTopLink");
		userTypeNamesMapping.setGetMethodName("getUserTypeNamesForTopLink");
		userTypeNamesMapping.useCollectionClass(HashSet.class);
		userTypeNamesMapping.setXPath("user-type-names/name/text()");
		descriptor.addMapping(userTypeNamesMapping);

		return descriptor;
	}

	/**
	 * sort and return only the names of the "user" types
	 * (non-core and non-stub types)
	 */
	private Collection getUserTypeNamesForTopLink() {
		List names = new ArrayList(this.userTypes.size());
		synchronized (this.userTypes) {
			for (Iterator stream = this.userTypes.iterator(); stream.hasNext(); ) {
				names.add(((MWClass) stream.next()).getName());
			}
		}
		return CollectionTools.sort(names, Collator.getInstance());
	}

	/**
	 * TopLink sets this value, which is then used by the
	 * ProjectIOManager to read in the actual types
	 */
	private void setUserTypeNamesForTopLink(Collection userTypeNames) {
		this.userTypeNames = userTypeNames;
	}
	
	/**
	 * convert to platform-independent representation
	 */
	private List getClasspathEntriesForTopLink() {
		List result = new ArrayList(this.classpathEntries.size());
		for (Iterator stream = this.classpathEntries.iterator(); stream.hasNext(); ) {
			result.add(((String) stream.next()).replace('\\', '/'));
		}
		return result;
	}
	
	/**
	 * convert to platform-specific representation
	 */
	private void setClasspathEntriesForTopLink(List entries) {
		this.classpathEntries = this.convertToClasspathEntries(entries);
	}

	/**
	 * convert to platform-specific representation
	 */
	private List convertToClasspathEntries(List entries) {
		List result = new Vector(entries.size());
		for (Iterator stream = entries.iterator(); stream.hasNext(); ) {
			result.add(new File((String) stream.next()).getPath());
		}
		return result;
	}

	/**
	 * reset the "stub" interfaces because they are faulted in during
	 * #postProjectBuild() as "normal" classes;
	 */
	public void postProjectBuild() {
		super.postProjectBuild();
		this.configureImpliedStubInterfaces();
	}

	private void configureImpliedStubInterfaces() {
		// no need to synchronize 'types' during a read
		for (Iterator stream = this.types.values().iterator(); stream.hasNext(); ) {
			((MWClass) stream.next()).configureImpliedStubInterfaces();
		}
	}
	
	/**
	 * Return whether the specified type is a "user" type;
	 * i.e. it is neither a stub nor a "core" type.
	 */
	private boolean typeIsUserSupplied(MWClass type) {
		if (type.isStub()) {
			return false;
		}
		if (coreClassNamesContains(type.getName())) {
			return false;
		}
		return true;
	}

	// ********** static methods **********
	
	/**
	 * return the names of the "core" classes,
	 * i.e. those that are never written out to XML files
	 */
	public static Iterator coreClassNames() {
		return getCoreClassNames().iterator();
	}
	
	/**
	 * return whether the specified name is among the
	 * "core" classes, i.e. those classes that are never
	 * written out to XML files
	 */
	public static boolean coreClassNamesContains(String typeName) {
		return getCoreClassNames().contains(typeName);
	}
	
	private static synchronized Set getCoreClassNames() {
		if (coreClassNames == null) {
			coreClassNames = buildCoreClassNames();
		}
		return coreClassNames;
	}

	private static Set buildCoreClassNames() {
		Set result = new HashSet(10000);
		
		// void, boolean, int, float, etc.
		CollectionTools.addAll(result, MWClass.nonReferenceClassNames());

		List locations = buildCoreClassLocations();		
		Classpath cp = new Classpath(locations);
		cp.addClassNamesTo(result);
	
		return result;
	}

	private static List<String> buildCoreClassLocations() {
		List<String> locations = new ArrayList<String>();
		for (int i = 0; i < CORE_KEYS.length; i++) {
			String classpath = Classpath.locationFor(CORE_KEYS[i]);
			if (!locations.contains(classpath)) {
				locations.add(classpath);
			}
		}
		return locations;
	}

	/**
	 * return the specified name is among the
	 * "core" classes, i.e. those classes that are never
	 * written out to XML files
	 */
	public static String coreClassNameIgnoreCase(String typeName) {
		return (String) getCoreClassNamesLowerCase().get(typeName.toLowerCase());
	}
	
	private static synchronized Map getCoreClassNamesLowerCase() {
		if (coreClassNamesLowerCase == null) {
			coreClassNamesLowerCase = buildCoreClassNamesLowerCase();
		}
		return coreClassNamesLowerCase;
	}

	private static Map buildCoreClassNamesLowerCase() {
		Set names = getCoreClassNames();
		Map result = new HashMap(names.size());
		for (Iterator stream = names.iterator(); stream.hasNext(); ) {
			String name = (String) stream.next();
			result.put(name.toLowerCase(), name);
		}
		return result;
	}

	public boolean isPersistLastRefresh() {
		return persistLastRefresh;
	}

	public void setPersistLastRefresh(boolean persistLastRefresh) {
		//must assure all classes get re-written next save if value changes from true to false
		if (!persistLastRefresh) {
			Iterator userTypeIter = this.userTypes();
			while(userTypeIter.hasNext()) {
				((MWClass)userTypeIter.next()).markBranchDirty();
			}
		}
		this.persistLastRefresh = persistLastRefresh;
		
	}

}
