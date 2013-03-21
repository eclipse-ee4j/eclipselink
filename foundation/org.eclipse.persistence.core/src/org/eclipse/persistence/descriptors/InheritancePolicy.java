/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 *     08/18/2011-2.3.1 Guy Pelletier 
 *       - 355093: Add new 'includeCriteria' flag to Multitenant metadata
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.io.*;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.remote.*;

/**
 * <p><b>Purpose</b>: Allows customization of an object's inheritance.
 * The primary supported inheritance model uses a class type indicator
 * column in the table that stores the object's class type.
 * The class-to-type mapping is specified on this policy.
 * The full class name can also be used for the indicator instead of the mapping.
 * <p>Each subclass can either share their parents table, or in addition add their
 * own table(s).
 * <p>For legacy models a customized inheritance class-extractor can be provided.
 * This allows Java code to be used to compute the class type to use for a row.
 * When this customized inheritance model is used an only-instances and with-all-subclasses
 * filter expression may be required for concrete and branch querying.
 */
public class InheritancePolicy extends CoreInheritancePolicy<AbstractRecord, AbstractSession, ClassDescriptor, DatabaseField> implements Serializable, Cloneable {
    protected Class parentClass;
    protected String parentClassName;
    protected ClassDescriptor parentDescriptor;
    protected List<ClassDescriptor> childDescriptors;
    protected DatabaseField classIndicatorField;
    protected transient Map classIndicatorMapping;
    protected Map classNameIndicatorMapping;
    protected transient boolean shouldUseClassNameAsIndicator;
    protected transient Boolean shouldReadSubclasses;
    protected DatabaseTable readAllSubclassesView;
    protected transient List<Object> allChildClassIndicators;
    protected transient Expression onlyInstancesExpression;
    protected transient Expression withAllSubclassesExpression;
    // null if there are no childrenTables, otherwise all tables for reference class plus childrenTables
    protected transient Vector allTables;
    // all tables for all subclasses (subclasses of subclasses included), should be in sync with childrenTablesJoinExpressions.
    protected transient List<DatabaseTable> childrenTables;
    // join expression for each child table, keyed by the table, should be in sync with childrenTables.
    protected transient Map<DatabaseTable, Expression> childrenTablesJoinExpressions;
    // all expressions from childrenTablesJoinExpressions ANDed together
    protected transient Expression childrenJoinExpression;

    /** Allow for class extraction method to be specified. */
    protected String classExtractorName;
    protected transient ClassExtractor classExtractor;
    
    protected ClassDescriptor descriptor;
    protected boolean shouldAlwaysUseOuterJoin = false;

    //CR 4005
    protected boolean useDescriptorsToValidateInheritedObjects = false;
    
    /** Define if an outer join should be used to read subclasses. */
    protected boolean shouldOuterJoinSubclasses = false;

    // used by the entity-mappings XML writer to determine inheritance strategy
    protected boolean isJoinedStrategy;

    /** PERF: Cache root descriptor. */
    protected ClassDescriptor rootParentDescriptor;
    
    protected boolean describesNonPersistentSubclasses = false;
    
    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptors involved in inheritance should have a policy.
     */
    public InheritancePolicy() {
        this.classIndicatorMapping = new HashMap(10);
        this.classNameIndicatorMapping = new HashMap(10);
        this.shouldUseClassNameAsIndicator = false;
        this.allChildClassIndicators = new ArrayList(4);
        this.childDescriptors = new ArrayList(4);
        this.setJoinedStrategy();
    }

    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptors involved in inheritance should have a policy.
     */
    public InheritancePolicy(ClassDescriptor descriptor) {
        this();
        this.descriptor = descriptor;
    }

    /**
     * INTERNAL:
     * Add child descriptor to the parent descriptor.
     */
    public void addChildDescriptor(ClassDescriptor childDescriptor) {
        getChildDescriptors().add(childDescriptor);
    }

    /**
     * INTERNAL:
     * childrenTablesJoinExpressions, childrenTables, allTables and childrenJoinExpression 
     * are created simultaneously and kept in sync.
     */
    protected void addChildTableJoinExpression(DatabaseTable table, Expression expression) {
        if (this.childrenTablesJoinExpressions == null) {
            this.childrenTablesJoinExpressions = new HashMap();
           // childrenTables should've been null, too
            this.childrenTables = new ArrayList();
           // allTables should've been null, too
            this.allTables = new Vector(getDescriptor().getTables());
        }
        // Avoid duplicates as two independent subclasses may have the same table.
        if (!this.childrenTables.contains(table)) {
            this.childrenTables.add(table);
        }
        if (!this.allTables.contains(table)) {
            this.allTables.add(table);
        }
        this.childrenTablesJoinExpressions.put(table, expression);
        this.childrenJoinExpression = expression.and(this.childrenJoinExpression);
    }

    /**
     * INTERNAL:
     * call addChildTableJoinExpression on all parents
     */
    public void addChildTableJoinExpressionToAllParents(DatabaseTable table, Expression expression) {
        ClassDescriptor parentDescriptor = getParentDescriptor();
        while(parentDescriptor != null) {
            InheritancePolicy parentPolicy = parentDescriptor.getInheritancePolicy();
            parentPolicy.addChildTableJoinExpression(table, expression);
            parentDescriptor = parentPolicy.getParentDescriptor();
        }
    }

    /**
     * PUBLIC:
     * Add a class indicator for the root classes subclass.
     * The indicator is used to determine the class to use for a row read from the database,
     * and to query only instances of a class from the database.
     * Every concrete persistent subclass must have a single unique indicator defined for it.
     * If the root class is concrete then it must also define an indicator.
     * Only the root class's descriptor of the entire inheritance hierarchy can define the class indicator mapping.
     */
    public void addClassIndicator(Class childClass, Object typeValue) {
        // Note we should think about supporting null values.
        // Store as key and value for bi-directional lookup.
        getClassIndicatorMapping().put(typeValue, childClass);
        getClassIndicatorMapping().put(childClass, typeValue);
    }

    /**
     * INTERNAL:
     * Add the class name reference by class name, used by the MW.
     */
    public void addClassNameIndicator(String childClassName, Object typeValue) {
        getClassNameIndicatorMapping().put(childClassName, typeValue);
    }

    /**
     * INTERNAL:
     * Add abstract class indicator information to the database row.  This is
     * required when building a row for an insert or an update of a concrete child
     * descriptor.
     * This is only used to build a template row.
     */
    public void addClassIndicatorFieldToInsertRow(AbstractRecord databaseRow) {
        if (hasClassExtractor()) {
            return;
        }

        DatabaseField field = getClassIndicatorField();
        databaseRow.put(field, null);
    }

    /**
     * INTERNAL:
     * Add abstract class indicator information to the database row.  This is
     * required when building a row for an insert or an update of a concrete child
     * descriptor.
     */
    public void addClassIndicatorFieldToRow(AbstractRecord databaseRow) {
        if (hasClassExtractor()) {
            return;
        }

        DatabaseField field = getClassIndicatorField();
        Object value = getClassIndicatorValue();

        databaseRow.put(field, value);
    }

    /**
     * INTERNAL:
     * Post initialize the child descriptors
     */
    protected void addClassIndicatorTypeToParent(Object indicator) {
        ClassDescriptor parentDescriptor = getDescriptor().getInheritancePolicy().getParentDescriptor();

        if (parentDescriptor.getInheritancePolicy().isChildDescriptor()) {
            if (parentDescriptor.getInheritancePolicy().shouldReadSubclasses()) {
                parentDescriptor.getInheritancePolicy().getAllChildClassIndicators().add(indicator);
            }
            parentDescriptor.getInheritancePolicy().addClassIndicatorTypeToParent(indicator);
        }
    }

    /**
     * INTERNAL:
     * Recursively adds fields to all the parents
     */
    protected void addFieldsToParent(Vector fields) {
        if (isChildDescriptor()) {
            if (getParentDescriptor().isInvalid()) {
                return;
            }
            ClassDescriptor parentDescriptor = getParentDescriptor();
            if (parentDescriptor.getInheritancePolicy().shouldReadSubclasses()) {
                Helper.addAllUniqueToVector(parentDescriptor.getAllFields(), fields);
            }
            parentDescriptor.getInheritancePolicy().addFieldsToParent(fields);
        }
    }

    /**
     * INTERNAL:
     * Return a select statement that will be used to query the class indicators required to query.
     * This is used in the abstract-multiple read.
     */
    public SQLSelectStatement buildClassIndicatorSelectStatement(ObjectLevelReadQuery query) {
        SQLSelectStatement selectStatement;
        selectStatement = new SQLSelectStatement();
        selectStatement.useDistinct();
        selectStatement.addTable(classIndicatorField.getTable());
        selectStatement.addField(getClassIndicatorField());
        // 2612538 - the default size of Map (32) is appropriate
        Map clonedExpressions = new IdentityHashMap();
        selectStatement.setWhereClause(((ExpressionQueryMechanism)query.getQueryMechanism()).buildBaseSelectionCriteria(false, clonedExpressions));
        appendWithAllSubclassesExpression(selectStatement);
        selectStatement.setTranslationRow(query.getTranslationRow());
        if (query.isReadAllQuery() && ((ReadAllQuery)query).hasHierarchicalExpressions()) {
            ReadAllQuery readAllQuery = (ReadAllQuery)query;
            selectStatement.setHierarchicalQueryExpressions(readAllQuery.getStartWithExpression(), readAllQuery.getConnectByExpression(), readAllQuery.getOrderSiblingsByExpressions());
        }
        selectStatement.setHintString(query.getHintString());

        selectStatement.normalize(query.getSession(), getDescriptor(), clonedExpressions);

        return selectStatement;
    }

    /**
     * INTERNAL:
     * Append the branch with all subclasses expression to the statement.
     */
    public void appendWithAllSubclassesExpression(SQLSelectStatement selectStatement) {
        if (getWithAllSubclassesExpression() != null) {
            // For Flashback: Must always rebuild with simple expression on right.
            if (selectStatement.getWhereClause() == null) {
                selectStatement.setWhereClause((Expression)getWithAllSubclassesExpression().clone());
            } else {
                selectStatement.setWhereClause(selectStatement.getWhereClause().and(getWithAllSubclassesExpression()));
            }
        }
    }

    /**
     * INTERNAL:
     * Build a select statement for all subclasses on the view using the same
     * selection criteria as the query.
     */
    public SQLSelectStatement buildViewSelectStatement(ObjectLevelReadQuery query) {
        // 2612538 - the default size of Map (32) is appropriate
        Map clonedExpressions = new IdentityHashMap();
        ExpressionQueryMechanism mechanism = (ExpressionQueryMechanism)query.getQueryMechanism();

        // CR#3166555 - Have the mechanism build the statement to avoid duplicating code and ensure that lock-mode, hints, hierarchical, etc. are set.
        SQLSelectStatement selectStatement = mechanism.buildBaseSelectStatement(false, clonedExpressions);
        selectStatement.setTables(org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(1));
        selectStatement.addTable(getReadAllSubclassesView());

        // Case, normal read for branch inheritance class that reads subclasses all in its own table(s).
        if (getWithAllSubclassesExpression() != null) {
            Expression branchIndicator = (Expression)getWithAllSubclassesExpression().clone();
            if (branchIndicator != null) {
                selectStatement.setWhereClause(branchIndicator.and(selectStatement.getWhereClause()));
            }
        }

        selectStatement.setFields(mechanism.getSelectionFields(selectStatement, true));
        selectStatement.normalizeForView(query.getSession(), getDescriptor(), clonedExpressions);
        // Allow for joining indexes to be computed to ensure distinct rows
        if (query.hasJoining()) {
            query.getJoinedAttributeManager().computeJoiningMappingIndexes(false, query.getSession(), 0);
        }

        return selectStatement;
    }

    /**
     * INTERNAL:
     * This method is invoked only for the abstract descriptors.
     */
    @Override
    public Class classFromRow(AbstractRecord rowFromDatabase, AbstractSession session) throws DescriptorException {
        if (hasClassExtractor()) {
            return getClassExtractor().extractClassFromRow(rowFromDatabase, session);
        }

        Object classFieldValue = session.getDatasourcePlatform().getConversionManager().convertObject(rowFromDatabase.get(getClassIndicatorField()), getClassIndicatorField().getType());

        if (classFieldValue == null) {
            throw DescriptorException.missingClassIndicatorField(rowFromDatabase, getDescriptor());
        }

        return classFromValue(classFieldValue, session);
    }
    
    /**
     * INTERNAL:
     * This method is used to turn the a raw database field value classFieldValue into a Class object.  Used to determine
     * which class objects to build from database results, and for class type expression
     */
    public Class classFromValue(Object classFieldValue, AbstractSession session) throws DescriptorException {
        Class concreteClass;
        if (!shouldUseClassNameAsIndicator()) {
            concreteClass = (Class)getClassIndicatorMapping().get(classFieldValue);
            if (concreteClass == null) {
                throw DescriptorException.missingClassForIndicatorFieldValue(classFieldValue, getDescriptor());
            }
        } else {
            try {
                String className = (String)classFieldValue;
                //PWK 2.5.1.7 can not use class for name, must go through conversion manager.
                //Should use the root ClassDescriptor's classloader to avoid loading from a loader other
                //than the one that loaded the project
                concreteClass = getDescriptor().getJavaClass().getClassLoader().loadClass(className);
                if (concreteClass == null) {
                    throw DescriptorException.missingClassForIndicatorFieldValue(classFieldValue, getDescriptor());
                }
            } catch (ClassNotFoundException e) {
                throw DescriptorException.missingClassForIndicatorFieldValue(classFieldValue, getDescriptor());
            } catch (ClassCastException e) {
                throw DescriptorException.missingClassForIndicatorFieldValue(classFieldValue, getDescriptor());
            }
        }

        return concreteClass;
    }

    /**
     * INTERNAL:
     * Clone the policy
     */
    public Object clone() {
        InheritancePolicy clone = null;

        try {
            clone = (InheritancePolicy)super.clone();
            if (hasClassIndicator()) {
                clone.setClassIndicatorField(clone.getClassIndicatorField().clone());
            }
        } catch (Exception exception) {
            throw new InternalError("clone failed");
        }

        return clone;
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this InheritancePolicy to actual class-based settings.
     * This method is used when converting a project that has been built with class names to a project with classes.
     * It will also convert referenced classes to the versions of the classes from the classLoader.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        Iterator keysEnum = getClassNameIndicatorMapping().keySet().iterator();
        Iterator valuesEnum = getClassNameIndicatorMapping().values().iterator();
        // Clear old classes (after class names have been lazy initialized).
        classIndicatorMapping = new HashMap();
        while (keysEnum.hasNext()) {
            Object key = keysEnum.next();
            Object value = valuesEnum.next();
            
            Class theClass = convertClassNameToClass((String) key, classLoader);
            classIndicatorMapping.put(theClass, value);
            classIndicatorMapping.put(value, theClass);
        }
        
        // Initialize the parent class name.
        if (getParentClassName() != null){
            setParentClass(convertClassNameToClass(getParentClassName(), classLoader));
        }
        
        // Initialize the class extractor name.
        if (classExtractorName != null) {
            Class classExtractorClass = convertClassNameToClass(classExtractorName, classLoader);
            
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    try {
                        setClassExtractor((ClassExtractor) AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(classExtractorClass)));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(classExtractorName, exception.getException());
                    }
                } else {
                    setClassExtractor((ClassExtractor) PrivilegedAccessHelper.newInstanceFromClass(classExtractorClass));
                }
            } catch (IllegalAccessException ex) {
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(classExtractorName, ex);
            } catch (InstantiationException e) {
                throw ValidationException.reflectiveExceptionWhileCreatingClassInstance(classExtractorName, e);   
            }
        }
    }

    /**
     * INTERNAL:
     * Convert the given className to an actual class.
     */
    protected Class convertClassNameToClass(String className, ClassLoader classLoader) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class) AccessController.doPrivileged(new PrivilegedClassForName(className, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.classNotFoundWhileConvertingClassNames(className, (Exception)exception.getCause());
                }
            } else {
                return PrivilegedAccessHelper.getClassForName(className, true, classLoader);
            }
        } catch (ClassNotFoundException exc){
            throw ValidationException.classNotFoundWhileConvertingClassNames(className, exc);
        }
    }
    
    /**
     * PUBLIC:
     * Set the descriptor to only read instance of itself when queried.
     * This is used with inheritance to configure the result of queries.
     * By default this is true for root inheritance descriptors, and false for all others.
     */
    public void dontReadSubclassesOnQueries() {
        setShouldReadSubclasses(false);
    }

    /**
     * PUBLIC:
     * Set the descriptor not to use the class' full name as the indicator.
     * The class indicator is used with inheritance to determine the class from a row.
     * By default a class indicator mapping is required, this can be set to true if usage of the class name is desired.
     * The field must be of a large enough size to store the fully qualified class name.
     */
    public void dontUseClassNameAsIndicator() {
        setShouldUseClassNameAsIndicator(false);
    }

    /**
     * INTERNAL:
     * Stores class indicators for all child and children's children.
     * Used for queries on branch classes only.
     */
    protected List<Object> getAllChildClassIndicators() {
        if (allChildClassIndicators == null) {
            allChildClassIndicators = new ArrayList(4);
        }
        return allChildClassIndicators;
    }

    /**
     * INTERNAL:
     * Returns all the child descriptors, even descriptors for subclasses of
     * subclasses.
     * Required for bug 3019934.
     */
    @Override
    public List<ClassDescriptor> getAllChildDescriptors() {
        // Guess the number of child descriptors...
        List<ClassDescriptor> allChildDescriptors = new ArrayList(this.getAllChildClassIndicators().size());
        return getAllChildDescriptors(allChildDescriptors);
    }

    /**
     * INTERNAL:
     * Recursive subroutine of getAllChildDescriptors.
     */
    protected List<ClassDescriptor> getAllChildDescriptors(List<ClassDescriptor> allChildDescriptors) {
        for (ClassDescriptor childDescriptor : getChildDescriptors()) {
            allChildDescriptors.add(childDescriptor);
            childDescriptor.getInheritancePolicyOrNull().getAllChildDescriptors(allChildDescriptors);
        }
        return allChildDescriptors;
    }

    /**
     * INTERNAL:
     * if reads subclasses, all tables for all read subclasses (indirect included).
     */
    public List<DatabaseTable> getChildrenTables() {
        return childrenTables;
    }
    
    /**
     * INTERNAL:
     * join expression for each child table, keyed by the table
     */
    public Map<DatabaseTable, Expression> getChildrenTablesJoinExpressions() {
        return childrenTablesJoinExpressions;
    }
    
    /**
     * INTERNAL:
     * all expressions from childrenTablesJoinExpressions ANDed together
     */
    public Expression getChildrenJoinExpression() {
        return childrenJoinExpression;
    }
    
    /**
     * INTERNAL:
     * all tables for reference class plus childrenTables
     */
    public Vector getAllTables() {
        if (allTables == null) {
            return this.getDescriptor().getTables();
        } else {
            return allTables;
        }
    }
    
    /**
     * INTERNAL:
     * Return all the immediate child descriptors.  Only descriptors from
     * direct subclasses are returned.
     */
    public List<ClassDescriptor> getChildDescriptors() {
        return childDescriptors;
    }

    /**
     * INTERNAL:
     * Return all the classExtractionMethod
     */
    protected Method getClassExtractionMethod() {
        if (classExtractor instanceof MethodClassExtractor) {
            return ((MethodClassExtractor)classExtractor).getClassExtractionMethod();
        } else {
            return null;
        }
    }

    /**
     * ADVANCED:
     * A class extraction method can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for a user defined class indicator in place of providing an explicit class indicator field.
     * The method registered must be a static method on the class which has that descriptor. The method must take a 
     * Record as an argument (for example, a DatabaseRecord), and must return the class to use for that record. 
     * This method will be used to decide which class to instantiate when reading from the database. 
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the record. 
     * If this method is used, then the class indicator field and mapping cannot be used, and in addition, 
     * the descriptor's withAllSubclasses and onlyInstances expressions must also be setup correctly.
     *
     * @see #setWithAllSubclassesExpression(Expression)
     * @see #setOnlyInstancesExpression(Expression)
     */
    public String getClassExtractionMethodName() {
        if (classExtractor instanceof MethodClassExtractor) {
            return ((MethodClassExtractor)classExtractor).getClassExtractionMethodName();
        } else {
            return null;
        }
    }

    /**
     * ADVANCED:
     * A class extractor can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for a user defined class indicator in place of providing an explicit class indicator field.
     * The instance registered must extend the ClassExtractor class and implement the extractClass(Map) method.
     * The method must take database row (a Record/Map) as an argument and must return the class to use for that row.
     * This method will be used to decide which class to instantiate when reading from the database.
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the row, such as usage of a direct or transformation mapping for the type fields.
     * If this method is used then the class indicator field and mapping cannot be used, and in addition, 
     * the descriptor's withAllSubclasses and onlyInstances expressions must also be setup correctly.
     *
     * @see #setWithAllSubclassesExpression(Expression)
     * @see #setOnlyInstancesExpression(Expression)
     */
    public ClassExtractor getClassExtractor() {
        return classExtractor;
    }

    /**
     * ADVANCED:
     * A class extractor can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for a user defined class indicator in place of providing an explicit class indicator field.
     * The instance registered must extend the ClassExtractor class and implement the extractClass(Map) method.
     * The method must take database row (a Record/Map) as an argument and must return the class to use for that row.
     * This method will be used to decide which class to instantiate when reading from the database.
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the row, such as usage of a direct or transformation mapping for the type fields.
     * If this method is used then the class indicator field and mapping cannot be used, and in addition, 
     * the descriptor's withAllSubclasses and onlyInstances expressions must also be setup correctly.
     *
     * @see #setWithAllSubclassesExpression(Expression)
     * @see #setOnlyInstancesExpression(Expression)
     */
    public void setClassExtractor(ClassExtractor classExtractor) {
        this.classExtractor = classExtractor;
    }

    /**
     * ADVANCED:
     * Set the class extractor class name. At descriptor initialize time this
     * class will be converted to a Class and set as the ClassExtractor. This
     * method is called from JPA.
     * 
     * @see setClassExtractor for more information on the ClassExtractor class.
     */
    public void setClassExtractorName(String classExtractorName) {
        this.classExtractorName = classExtractorName;
    }
    
    /**
     * INTERNAL:
     * Return the class indicator associations for XML.
     * List of class-name/value associations.
     */
    public Vector getClassIndicatorAssociations() {
        Vector associations = new Vector(getClassNameIndicatorMapping().size() / 2);
        Iterator classesEnum = getClassNameIndicatorMapping().keySet().iterator();
        Iterator valuesEnum = getClassNameIndicatorMapping().values().iterator();
        while (classesEnum.hasNext()) {
            Object className = classesEnum.next();

            // If the project was built in runtime is a class, MW is a string.
            if (className instanceof Class) {
                className = ((Class)className).getName();
            }
            Object value = valuesEnum.next();
            associations.addElement(new TypedAssociation(className, value));
        }

        return associations;
    }

    /**
     * INTERNAL:
     * Returns field that the class type indicator is store when using inheritance.
     */
    @Override
    public DatabaseField getClassIndicatorField() {
        return classIndicatorField;
    }

    /**
     * PUBLIC:
     * Return the class indicator field name.
     * This is the name of the field in the table that stores what type of object this is.
     */
    public String getClassIndicatorFieldName() {
        if (getClassIndicatorField() == null) {
            return null;
        } else {
            return getClassIndicatorField().getQualifiedName();
        }
    }

    /**
     * INTERNAL:
     * Return the association of indicators and classes using specified ConversionManager
     */
    @Override
    public Map getClassIndicatorMapping() {
        if (classIndicatorMapping == null) {
            classIndicatorMapping = new HashMap(10);
        }
        return classIndicatorMapping;
    }

    /**
     * INTERNAL:
     * Return the mapping from class name to indicator, used by MW.
     */
    public Map getClassNameIndicatorMapping() {
        if (classNameIndicatorMapping.isEmpty() && classIndicatorMapping!=null && !classIndicatorMapping.isEmpty()) {
            Iterator keysEnum = classIndicatorMapping.keySet().iterator();
            Iterator valuesEnum = classIndicatorMapping.values().iterator();
            while (keysEnum.hasNext()) {
                Object key = keysEnum.next();
                Object value = valuesEnum.next();
                if (key instanceof Class) {
                    String className = ((Class)key).getName();
                    classNameIndicatorMapping.put(className, value);
                }
            }
        }

        return classNameIndicatorMapping;
    }

    /**
     * INTERNAL:
     * Returns value of the abstract class indicator for the Java class.
     */
    protected Object getClassIndicatorValue() {
        return getClassIndicatorValue(getDescriptor().getJavaClass());
    }

    /**
     * INTERNAL:
     * Returns the indicator field value for the given class
     * If no abstract indicator mapping is specified, use the class name.
     */
    protected Object getClassIndicatorValue(Class javaClass) {
        if (shouldUseClassNameAsIndicator()) {
            return javaClass.getName();
        } else {
            return getClassIndicatorMapping().get(javaClass);
        }
    }

    /**
     * INTERNAL:
     * Returns the descriptor which the policy belongs to.
     */
    @Override
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * ADVANCED:
     * Determines whether the descriptors using this inheritance policy
     * should be used as descriptors for subclasses of the classes they
     * describe if those subclasses do not have their own descriptor
     * 
     * e.g. If Employee.class has a descriptor and EmployeeSubClass does
     * not have a descriptor, if describesNonPersistenceSubclasses is true
     * Employee's descriptor will be used as the descriptor for Employee
     */
    public boolean getDescribesNonPersistentSubclasses(){
        return describesNonPersistentSubclasses;
    }
    
    /**
     * ADVANCED:
     * Return the 'only instances expression'.
     */
    public Expression getOnlyInstancesExpression() {
        return onlyInstancesExpression;
    }

    /**
     * PUBLIC:
     * Return the parent class.
     */
    public Class getParentClass() {
        return parentClass;
    }

    /**
     * INTERNAL:
     * Return the parent class name.
     */
    public String getParentClassName() {
        if ((parentClassName == null) && (parentClass != null)) {
            parentClassName = parentClass.getName();
        }
        return parentClassName;
    }

    /**
     * INTERNAL:
     * Return the parent descriptor.
     */
    public ClassDescriptor getParentDescriptor() {
        return parentDescriptor;
    }

    /**
     * INTERNAL:
     * The view can be used to optimize/customize the query for all subclasses where they have multiple tables.
     * This view can do the outer join, we require the view because we cannot generate dynamic platform independent SQL
     * for outer joins (i.e. not possible to do so either).
     */
    public DatabaseTable getReadAllSubclassesView() {
        return readAllSubclassesView;
    }

    /**
     * ADVANCED:
     * The view can be used to optimize/customize the query for all subclasses where they have multiple tables.
     * This view can use outer joins or unions to combine the results of selecting from all of the subclass tables.
     * If a view is not given then TopLink must make an individual call for each subclass.
     */
    public String getReadAllSubclassesViewName() {
        if (getReadAllSubclassesView() == null) {
            return null;
        }
        return getReadAllSubclassesView().getName();
    }

    /**
     * INTERNAL:
     * Return the root parent descriptor
     */
    public ClassDescriptor getRootParentDescriptor() {
        if (this.rootParentDescriptor == null) {
            if (isRootParentDescriptor()) {
                this.rootParentDescriptor = getDescriptor();
            } else {
                this.rootParentDescriptor = getParentDescriptor().getInheritancePolicy().getRootParentDescriptor();
            }
        }
        return rootParentDescriptor;
    }

    /**
     * INTERNAL:
     * use aggregate in inheritance
     */
    public ClassDescriptor getSubclassDescriptor(Class theClass) {
        if (hasChildren()) {
            for (Iterator enumtr = getChildDescriptors().iterator(); enumtr.hasNext();) {
                ClassDescriptor childDescriptor = (ClassDescriptor)enumtr.next();
                if (childDescriptor.getJavaClass().equals(theClass)) {
                    return childDescriptor;
                } else {
                    ClassDescriptor descriptor = childDescriptor.getInheritancePolicy().getSubclassDescriptor(theClass);
                    if (descriptor != null) {
                        return descriptor;
                    }
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Returns descriptor corresponding to the class owning the policy or its subclass - otherwise null.
     */
    public ClassDescriptor getDescriptor(Class theClass) {
        if(getDescriptor().getJavaClass().equals(theClass)) {
            return getDescriptor();
        } else {
            return getSubclassDescriptor(theClass);
        }
    }

    /**
     * INTERNAL:
     * return if we should use the descriptor inheritance to determine
     * if an object can be returned from the identity map or not.
     */
    public boolean getUseDescriptorsToValidateInheritedObjects() {
        return useDescriptorsToValidateInheritedObjects;
    }

    /**
     * ADVANCED:
     * Return the Expression which gets all subclasses.
     */
    public Expression getWithAllSubclassesExpression() {
        return withAllSubclassesExpression;
    }

    /**
     * INTERNAL:
     * Check if descriptor has children
     */
    public boolean hasChildren() {
        return !getChildDescriptors().isEmpty();
    }

    /**
     * INTERNAL:
     */
    public boolean hasClassExtractor() {
        return getClassExtractor() != null;
    }

    /**
     * INTERNAL:
     * Checks if the class is involved in inheritance
     */
    public boolean hasClassIndicator() {
        return getClassIndicatorField() != null;
    }

    /**
     * INTERNAL:
     * Return if any children of this descriptor require information from another table
     * not specified at the parent level.
     */
    public boolean hasMultipleTableChild() {
        return childrenTables != null;
    }

    /**
     * INTERNAL:
     * Return if a view is used for inheritance reads.
     */
    public boolean hasView() {
        return getReadAllSubclassesView() != null;
    }

    /**
     * INTERNAL:
     * Initialize the inheritance properties of the descriptor once the mappings are initialized.
     * This is done before formal postInitialize during the end of mapping initialize.
     */
    public void initialize(AbstractSession session) {
        // Must reset this in the case that a child thinks it wants to read its subclasses.
        if ((this.shouldReadSubclasses == null) || shouldReadSubclasses()) {
            setShouldReadSubclasses(!getChildDescriptors().isEmpty());
        }

        if (isChildDescriptor()) {
            getDescriptor().setMappings(Helper.concatenateVectors(getParentDescriptor().getMappings(), getDescriptor().getMappings()));
            getDescriptor().setQueryKeys(Helper.concatenateMaps(getParentDescriptor().getQueryKeys(), getDescriptor().getQueryKeys()));
            addFieldsToParent(getDescriptor().getFields());
            // Parents fields must be first for indexing to work.
            Vector parentsFields = (Vector)getParentDescriptor().getFields().clone();

            //bug fix on Oracle duplicate field SQL using "order by"
            Helper.addAllUniqueToVector(parentsFields, getDescriptor().getFields());
            getDescriptor().setFields(parentsFields);

            if (getClassIndicatorValue() != null) {
                if (shouldReadSubclasses()) {
                    getAllChildClassIndicators().add(getClassIndicatorValue());
                }
                addClassIndicatorTypeToParent(getClassIndicatorValue());
            }

            initializeOptimisticLocking();

            if (!getDescriptor().hasReturningPolicy() && getParentDescriptor().hasReturningPolicy()) {
                getDescriptor().setReturningPolicy(new ReturningPolicy());
            }

            // create CMPPolicy on child if parent has one and it does not.  Then copy individual fields
            CMPPolicy parentCMPPolicy = getDescriptor().getInheritancePolicy().getParentDescriptor().getCMPPolicy();
            if (parentCMPPolicy != null) {
                CMPPolicy cmpPolicy = getDescriptor().getCMPPolicy();
                if (cmpPolicy == null) {
                    cmpPolicy = new CMPPolicy();
                    getDescriptor().setCMPPolicy(cmpPolicy);
                }

                //copy pessimistic locking policy from the parent if this child does not have one
                if (parentCMPPolicy.hasPessimisticLockingPolicy() && !cmpPolicy.hasPessimisticLockingPolicy()) {
                    cmpPolicy.setPessimisticLockingPolicy((PessimisticLockingPolicy)parentCMPPolicy.getPessimisticLockingPolicy().clone());
                }

                // copy forceUpdate value if not set in child
                if (cmpPolicy.internalGetForceUpdate() == null) {
                    cmpPolicy.internalSetForceUpdate(parentCMPPolicy.internalGetForceUpdate());
                }

                // copy updateAllFields value if not set in child
                if (cmpPolicy.internalGetUpdateAllFields() == null) {
                    cmpPolicy.internalSetUpdateAllFields(parentCMPPolicy.internalGetUpdateAllFields());
                }
            }
            
            // Inherit the native connection requirement.
            if (getParentDescriptor().isNativeConnectionRequired()) {
                getDescriptor().setIsNativeConnectionRequired(true);
            }
            
            if (getDescriptor().getPartitioningPolicy() == null) {
                getDescriptor().setPartitioningPolicy(getParentDescriptor().getPartitioningPolicy());
            }
        }

        initializeOnlyInstancesExpression();
        initializeWithAllSubclassesExpression();
        if (hasView()) {
            // Set the table qualifier on the inheritance view.
            if ((session.getDatasourcePlatform().getTableQualifier().length() != 0) && (getReadAllSubclassesView().getTableQualifier().length() == 0)) {
                getReadAllSubclassesView().setTableQualifier(session.getDatasourcePlatform().getTableQualifier());
            }
        }
    }

    /**
     * INTERNAL:
     * Setup the default classExtractionMethod, or if one was specified by the user make sure it is valid.
     */
    protected void initializeClassExtractor(AbstractSession session) throws DescriptorException {
        if (getClassExtractor() == null) {
            if (isChildDescriptor()) {
                setClassExtractor(getParentDescriptor().getInheritancePolicy().getClassExtractor());
            }
        } else {
            getClassExtractor().initialize(getDescriptor(), session);
        }
    }

    /**
     * INTERNAL:
     * Initialize the expression to use to check the specific type field.
     */
    protected void initializeOnlyInstancesExpression() throws DescriptorException {
        if (getOnlyInstancesExpression() == null) {
            if (hasClassExtractor()) {
                return;
            }
            Object typeValue = getClassIndicatorValue();
            if (typeValue == null) {
                if (shouldReadSubclasses()) {
                    return;// No indicator is allowed in this case.
                }

                throw DescriptorException.valueNotFoundInClassIndicatorMapping(getParentDescriptor(), getDescriptor());
            }

            DatabaseField typeField = getClassIndicatorField();
            if (typeField == null) {
                throw DescriptorException.classIndicatorFieldNotFound(getParentDescriptor(), getDescriptor());
            }

            // cr3546
            if (shouldAlwaysUseOuterJoin()) {
                setOnlyInstancesExpression(new ExpressionBuilder().getField(typeField).equalOuterJoin(typeValue));
            } else {
                setOnlyInstancesExpression(new ExpressionBuilder().getField(typeField).equal(typeValue));
            }
        }

        // If subclasses are read, this is anded dynamically.
        if (!shouldReadSubclasses()) {
            getDescriptor().getQueryManager().setAdditionalJoinExpression(getOnlyInstancesExpression().and(getDescriptor().getQueryManager().getAdditionalJoinExpression()));
        }
    }


    /**
     * INTERNAL:
     * Potentially override the optimistic locking behavior
     */
    protected void initializeOptimisticLocking(){
        // CR#3214106, do not override if specified in subclass.
        if (!getDescriptor().usesOptimisticLocking() && getParentDescriptor().usesOptimisticLocking()) {
            getDescriptor().setOptimisticLockingPolicy((OptimisticLockingPolicy)getParentDescriptor().getOptimisticLockingPolicy().clone());
            getDescriptor().getOptimisticLockingPolicy().setDescriptor(getDescriptor());
        }
    }
    
    /**
     * INTERNAL:
     * Potentially override the cache invalidation behavior
     */
    protected void initializeCacheInvalidationPolicy() {
        CacheInvalidationPolicy parentPolicyClone = (CacheInvalidationPolicy)getParentDescriptor().getCacheInvalidationPolicy().clone(); 
        getDescriptor().setCacheInvalidationPolicy(parentPolicyClone);
    }
    
    /**
     * INTERNAL:
     * Initialize the expression to use for queries to the class and its subclasses.
     */
    protected void initializeWithAllSubclassesExpression() throws DescriptorException {
        if (getWithAllSubclassesExpression() == null) {
            if (hasClassExtractor()) {
                return;
            }
            if (isChildDescriptor() && shouldReadSubclasses()) {
                setWithAllSubclassesExpression(new ExpressionBuilder().getField(getClassIndicatorField()).in(getAllChildClassIndicators()));
            }
        }
    }
    
    /**
     * INTERNAL:
     * Check if it is a child descriptor.
     */
    public boolean isChildDescriptor() {
        return getParentClassName() != null;
    }
    
    /**
     * INTERNAL:
     * Indicate whether a single table or joined inheritance strategy is being used.  Since we currently do
     * not support TABLE_PER_CLASS, indicating either joined/not joined is sufficient.
     * 
     * @return isJoinedStrategy value
     */
    public boolean isJoinedStrategy() {
        return isJoinedStrategy;
    }
    
    /**
     * INTERNAL:
     * Return whether or not is root parent descriptor
     */
    public boolean isRootParentDescriptor() {
        return getParentDescriptor() == null;
    }

    /**
     * INTERNAL:
     * Initialized the inheritance properties that cannot be initialized
     * until after the mappings have been.
     */
    public void postInitialize(AbstractSession session) {
        if (isChildDescriptor()) {
            ClassDescriptor parent = getParentDescriptor();
            if (getDescriptor().shouldAcquireCascadedLocks()){
                parent.setShouldAcquireCascadedLocks(true);
            }
            if (getDescriptor().hasRelationships()){
                parent.setHasRelationships(true);
            }
            while (parent != null) {
                if (parent.hasMultipleTableConstraintDependecy()) {
                    getDescriptor().setHasMultipleTableConstraintDependecy(true);
                    break;
                }
                parent = parent.getInheritancePolicy().getParentDescriptor();
            }            
        }
    }

    /**
     * INTERNAL:
     * Allow the inheritance properties of the descriptor to be initialized.
     * The descriptor's parent must first be initialized.
     */
    public void preInitialize(AbstractSession session) throws DescriptorException {
        // Make sure that parent is already preinitialized.
        if (isChildDescriptor()) {
            updateTables();
            
            // Clone the multitenant policy and set on child descriptor.
            if (getParentDescriptor().hasMultitenantPolicy()) {
                MultitenantPolicy clonedMultitenantPolicy = getParentDescriptor().getMultitenantPolicy().clone(getDescriptor());
                getDescriptor().setMultitenantPolicy(clonedMultitenantPolicy);
            }
            
            setClassIndicatorMapping(getParentDescriptor().getInheritancePolicy().getClassIndicatorMapping());
            setShouldUseClassNameAsIndicator(getParentDescriptor().getInheritancePolicy().shouldUseClassNameAsIndicator());

            // Initialize properties.
            getDescriptor().setPrimaryKeyFields(getParentDescriptor().getPrimaryKeyFields());
            getDescriptor().setAdditionalTablePrimaryKeyFields(Helper.concatenateMaps(getParentDescriptor().getAdditionalTablePrimaryKeyFields(), getDescriptor().getAdditionalTablePrimaryKeyFields()));

            Expression localExpression = getDescriptor().getQueryManager().getMultipleTableJoinExpression();
            Expression parentExpression = getParentDescriptor().getQueryManager().getMultipleTableJoinExpression();

            if (localExpression != null) {
                getDescriptor().getQueryManager().setInternalMultipleTableJoinExpression(localExpression.and(parentExpression));
            } else if (parentExpression != null) {
                getDescriptor().getQueryManager().setInternalMultipleTableJoinExpression(parentExpression);
            }

            Expression localAdditionalExpression = getDescriptor().getQueryManager().getAdditionalJoinExpression();
            Expression parentAdditionalExpression = getParentDescriptor().getQueryManager().getAdditionalJoinExpression();

            if (localAdditionalExpression != null) {
                getDescriptor().getQueryManager().setAdditionalJoinExpression(localAdditionalExpression.and(parentAdditionalExpression));
            } else if (parentAdditionalExpression != null) {
                getDescriptor().getQueryManager().setAdditionalJoinExpression(parentAdditionalExpression);
            }

            setClassIndicatorField(getParentDescriptor().getInheritancePolicy().getClassIndicatorField());

            //if child has sequencing setting, do not bother to call the parent
            if (!getDescriptor().usesSequenceNumbers()) {
                getDescriptor().setSequenceNumberField(getParentDescriptor().getSequenceNumberField());
                getDescriptor().setSequenceNumberName(getParentDescriptor().getSequenceNumberName());
            }
        } else {
            // This must be done now before any other initialization occurs. 
            getDescriptor().setInternalDefaultTable();
        }

        initializeClassExtractor(session);

        if (!isChildDescriptor()) {
            // build abstract class indicator field.
            if ((getClassIndicatorField() == null) && (!hasClassExtractor())) {
                session.getIntegrityChecker().handleError(DescriptorException.classIndicatorFieldNotFound(getDescriptor(), getDescriptor()));
            }
            if (getClassIndicatorField() != null) {
                setClassIndicatorField(getDescriptor().buildField(getClassIndicatorField()));
                // Determine and set the class indicator classification.
                if (shouldUseClassNameAsIndicator()) {
                    getClassIndicatorField().setType(ClassConstants.STRING);
                } else if (!getClassIndicatorMapping().isEmpty()) {
                    Class type = null;
                    Iterator fieldValuesEnum = getClassIndicatorMapping().values().iterator();
                    while (fieldValuesEnum.hasNext() && (type == null)) {
                        Object value = fieldValuesEnum.next();
                        if (value.getClass() != getClass().getClass()) {
                            type = value.getClass();
                        }
                    }
                    getClassIndicatorField().setType(type);
                }
                getDescriptor().getFields().addElement(getClassIndicatorField());
            }
        }
    }

    /**
     * PUBLIC:
     * Set the descriptor to read instance of itself and its subclasses when queried.
     * This is used with inheritance to configure the result of queries.
     * By default this is true for root inheritance descriptors, and false for all others.
     */
    public void readSubclassesOnQueries() {
        setShouldReadSubclasses(true);
    }
    
    /**
     * INTERNAL:
     * Used to initialize a remote descriptor.
     */
    public void remoteInitialization(DistributedSession session) {
        if (isChildDescriptor()) {
            if (session.hasCorrespondingDescriptor(getParentDescriptor())) {
                setParentDescriptor(session.getDescriptor(getParentClass()));
            } else {
                session.privilegedAddDescriptor(getParentDescriptor());
                getParentDescriptor().remoteInitialization(session);
            }
        }

        Vector tempChildren = new Vector(getChildDescriptors().size());
        for (ClassDescriptor childDescriptor : getChildDescriptors()) {
            if (session.hasCorrespondingDescriptor(childDescriptor)) {
                tempChildren.addElement(session.getDescriptor(childDescriptor.getJavaClass()));
            } else {
                session.privilegedAddDescriptor(childDescriptor);
                childDescriptor.remoteInitialization(session);
                tempChildren.addElement(childDescriptor);
            }
        }
        setChildDescriptors(tempChildren);
    }

    /**
     * INTERNAL:
     * Return if this descriptor has children that define additional tables and needs to read them.
     * This case requires a special read, because the query cannot be done through a single SQL call with normal joins.
     */
    public boolean requiresMultipleTableSubclassRead() {
        return hasMultipleTableChild() && shouldReadSubclasses();
    }

    /**
     * INTERNAL:
     * Select all rows from a abstract table descriptor.
     * This is accomplished by selecting for all of the concrete classes and then merging the rows.
     * This does not optimize using type select, as the type information is not known.
     * @return vector containing database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    protected Vector selectAllRowUsingCustomMultipleTableSubclassRead(ObjectLevelReadQuery query) throws DatabaseException {
        Vector rows = new Vector();
        // CR#3701077, it must either have a filter only instances expression, or not have subclasses.
        // This method recurses, so even though this is only called when shouldReadSubclasses is true, it may be false for subclasses.
        if ((getOnlyInstancesExpression() != null)  || (! shouldReadSubclasses())) {
            ObjectLevelReadQuery concreteQuery = (ObjectLevelReadQuery)query.clone();
            concreteQuery.setReferenceClass(getDescriptor().getJavaClass());
            concreteQuery.setDescriptor(getDescriptor());

            Vector concreteRows = ((ExpressionQueryMechanism)concreteQuery.getQueryMechanism()).selectAllRowsFromConcreteTable();
            rows = Helper.concatenateVectors(rows, concreteRows);
        }

        // Recursively collect all rows from all concrete children and their children.
        // If this descriptor did not have a child with its own table, then the concrete select
        // would have selected them all.
        if (hasMultipleTableChild() || !shouldReadSubclasses()) {
            for (ClassDescriptor concreteDescriptor : getChildDescriptors()) {
                Vector concreteRows = concreteDescriptor.getInheritancePolicy().selectAllRowUsingCustomMultipleTableSubclassRead(query);
                rows = Helper.concatenateVectors(rows, concreteRows);
            }
        }

        return rows;
    }

    /**
     * INTERNAL:
     * Select all rows from a abstract table descriptor.
     * This is accomplished by selecting for all of the concrete classes and then merging the rows.
     * @return vector containing database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    protected Vector selectAllRowUsingDefaultMultipleTableSubclassRead(ObjectLevelReadQuery query) throws DatabaseException, QueryException {
        // Get all rows for the given class indicator field
        // The indicator select is prepared in the original query, so can just be executed.
        List<AbstractRecord> classIndicators = ((ExpressionQueryMechanism)query.getQueryMechanism()).selectAllRowsFromTable();

        List<Class> classes = new ArrayList<Class>();
        Set<Class> uniqueClasses = new HashSet<Class>();
        for (AbstractRecord row : classIndicators) {
            Class concreteClass = classFromRow(row, query.getSession());
            if (!uniqueClasses.contains(concreteClass)) { // Ensure unique (a distinct is used, but may have been disabled)
                uniqueClasses.add(concreteClass);
                classes.add(concreteClass);
            }
        }

        Vector rows = new Vector();
        // joinedMappingIndexes contains Integer indexes corresponding to the number of fields
        // to which the query reference class is mapped, for instance:
        // referenceClass = SmallProject => joinedMappingIndexes(0) = 6;
        // referenceClass = LargeProject => joinedMappingIndexes(0) = 8;
        // This information should be preserved in the main query against the parent class,
        // therefore in this case joinedMappedIndexes contains a Map of classes to Integers:
        // referenceClass = Project => joinedMappingIndexes(0) = Map {SmallProject -> 6; LargeProject -> 8}.
        // These maps are populated in the loop below, and set into the main query joinedMappingIndexes.
        HashMap joinedMappingIndexes = null;
        if (query.hasJoining()) {
            joinedMappingIndexes = new HashMap();
        }
        ClassDescriptor rootDescriptor = query.getDescriptor();
        for (Class concreteClass : classes) {            
            if (!uniqueClasses.contains(concreteClass)) {
                continue;
            }
            query.getSession().logMessage("" + concreteClass);
            Set<Class> subclasses = new HashSet<Class>();
            uniqueClasses.remove(concreteClass);
            subclasses.add(concreteClass);
            ClassDescriptor concreteDescriptor = getDescriptor(concreteClass);
            if (concreteDescriptor == null) {
                throw QueryException.noDescriptorForClassFromInheritancePolicy(query, concreteClass);
            }
            InheritancePolicy concretePolicy = concreteDescriptor.getInheritancePolicy();
            ClassDescriptor parentDescriptor = concretePolicy.getParentDescriptor();
            // Find the root most parent with its own table.
            while ((parentDescriptor != null) && (parentDescriptor != rootDescriptor)
                    && !parentDescriptor.getInheritancePolicy().hasMultipleTableChild() && parentDescriptor.getInheritancePolicy().shouldReadSubclasses()) {
                concreteDescriptor = parentDescriptor;
                concreteClass = concreteDescriptor.getJavaClass();
                uniqueClasses.remove(concreteClass);
                subclasses.add(concreteClass);
                concretePolicy = concreteDescriptor.getInheritancePolicy();
                parentDescriptor = concretePolicy.getParentDescriptor();
                query.getSession().logMessage("Parent:" + concreteClass);
            }
            // If this class has children select them all.
            if (concretePolicy.hasChildren() && !concretePolicy.hasMultipleTableChild()) {
                removeChildren(concreteDescriptor, uniqueClasses, subclasses);
            }
            ObjectLevelReadQuery concreteQuery = (ObjectLevelReadQuery)query.clone();
            concreteQuery.setReferenceClass(concreteClass);
            concreteQuery.setDescriptor(concreteDescriptor);
            Vector concreteRows = ((ExpressionQueryMechanism)concreteQuery.getQueryMechanism()).selectAllRowsFromConcreteTable();
            rows = Helper.concatenateVectors(rows, concreteRows);
            
            if (joinedMappingIndexes != null) {
                // Need to set mapping index for each select, as each row size is different.
                for (Map.Entry entry : concreteQuery.getJoinedAttributeManager().getJoinedMappingIndexes_().entrySet()) {
                    HashMap mappingIndexes = (HashMap)joinedMappingIndexes.get(entry.getKey());
                    if (mappingIndexes == null) {
                        mappingIndexes = new HashMap(classes.size());
                        joinedMappingIndexes.put(entry.getKey(), mappingIndexes);
                    }
                    for (Class subclass : subclasses) {
                        mappingIndexes.put(subclass, entry.getValue());
                    }
                }
            }
        }
        if (joinedMappingIndexes != null) {
            query.getJoinedAttributeManager().setJoinedMappingIndexes_(joinedMappingIndexes);
        }

        return rows;
    }

    /**
     * Remove all of the subclasses (and so on) from the set of classes.
     */
    protected void removeChildren(ClassDescriptor descriptor, Set<Class> classes, Set<Class> subclasses) {
        for (ClassDescriptor childDescriptor : descriptor.getInheritancePolicy().getChildDescriptors()) {
            classes.remove(childDescriptor.getJavaClass());
            subclasses.add(childDescriptor.getJavaClass());
            removeChildren(childDescriptor, classes, subclasses);
        }
    }

    /**
     * INTERNAL:
     * Select all rows from a abstract table descriptor.
     * This is accomplished by selecting for all of the concrete classes and then merging the rows.
     * @return vector containing database rows.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    public Vector selectAllRowUsingMultipleTableSubclassRead(ObjectLevelReadQuery query) throws DatabaseException {
        if (hasClassExtractor()) {
            return selectAllRowUsingCustomMultipleTableSubclassRead(query);
        } else {
            return selectAllRowUsingDefaultMultipleTableSubclassRead(query);
        }
    }

    /**
     * INTERNAL:
     * Select one rows from a abstract table descriptor.
     * This is accomplished by selecting for all of the concrete classes until a row is found.
     * This does not optimize using type select, as the type information is not known.
     * @exception  DatabaseException - an error has occurred on the database.
     */
    protected AbstractRecord selectOneRowUsingCustomMultipleTableSubclassRead(ReadObjectQuery query) throws DatabaseException {
        // CR#3701077, it must either have a filter only instances expression, or not have subclasses.
        // This method recurses, so even though this is only called when shouldReadSubclasses is true, it may be false for subclasses.
        if ((getOnlyInstancesExpression() != null)  || (! shouldReadSubclasses())) {
            ReadObjectQuery concreteQuery = (ReadObjectQuery)query.clone();
            concreteQuery.setReferenceClass(getDescriptor().getJavaClass());
            concreteQuery.setDescriptor(getDescriptor());

            AbstractRecord row = ((ExpressionQueryMechanism)concreteQuery.getQueryMechanism()).selectOneRowFromConcreteTable();

            if (row != null) {
                return row;
            }
        }

        // Recursively collect all rows from all concrete children and their children.
        for (ClassDescriptor concreteDescriptor : getChildDescriptors()) {
            AbstractRecord row = concreteDescriptor.getInheritancePolicy().selectOneRowUsingCustomMultipleTableSubclassRead(query);
            if (row != null) {
                return row;
            }
        }

        return null;
    }

    /**
     * INTERNAL:
     * Select one row of any concrete subclass,
     * This must use two selects, the first retrieves the type field only.
     */
    protected AbstractRecord selectOneRowUsingDefaultMultipleTableSubclassRead(ReadObjectQuery query) throws DatabaseException, QueryException {
        // Get the row for the given class indicator field
        // The indicator select is prepared in the original query, so can just be executed.
        AbstractRecord typeRow = ((ExpressionQueryMechanism)query.getQueryMechanism()).selectOneRowFromTable();

        if (typeRow == null) {
            return null;
        }

        Class concreteClass = classFromRow(typeRow, query.getSession());
        ClassDescriptor concreteDescriptor = getDescriptor(concreteClass);
        if (concreteDescriptor == null) {
            throw QueryException.noDescriptorForClassFromInheritancePolicy(query, concreteClass);
        }

        ReadObjectQuery concreteQuery = (ReadObjectQuery)query.clone();
        concreteQuery.setReferenceClass(concreteClass);
        concreteQuery.setDescriptor(concreteDescriptor);

        AbstractRecord resultRow = ((ExpressionQueryMechanism)concreteQuery.getQueryMechanism()).selectOneRowFromConcreteTable();

        return resultRow;
    }

    /**
     * INTERNAL:
     * Select one row of any concrete subclass,
     * This must use two selects, the first retrieves the type field only.
     */
    public AbstractRecord selectOneRowUsingMultipleTableSubclassRead(ReadObjectQuery query) throws DatabaseException, QueryException {
        if (hasClassExtractor()) {
            return selectOneRowUsingCustomMultipleTableSubclassRead(query);
        } else {
            return selectOneRowUsingDefaultMultipleTableSubclassRead(query);
        }
    }

    /**
     * INTERNAL:
     */
    protected void setAllChildClassIndicators(Vector allChildClassIndicators) {
        this.allChildClassIndicators = allChildClassIndicators;
    }

    /**
     * INTERNAL:
     */
    public void setChildDescriptors(List<ClassDescriptor> childDescriptors) {
        this.childDescriptors = childDescriptors;
    }

    /**
     * ADVANCED:
     * A class extraction method can be registered with the descriptor to override the default inheritance mechanism.
     * This allows for a user defined class indicator in place of providing an explicit class indicator field.
     * The method registered must be a static method on the class which has that descriptor. The method must take Record 
     * as an argument (for example, a DatabaseRecord), and must return the class to use for that record.
     * This method will be used to decide which class to instantiate when reading from the database.
     * It is the application's responsibility to populate any typing information in the database required
     * to determine the class from the record.
     * If this method is used then the class indicator field and mapping cannot be used, and in addition, 
     * the descriptor's withAllSubclasses and onlyInstances expressions must also be set up correctly.
     *
     * @see #setWithAllSubclassesExpression(Expression)
     * @see #setOnlyInstancesExpression(Expression)
     */
    public void setClassExtractionMethodName(String staticClassClassExtractionMethod) {
        if ((staticClassClassExtractionMethod == null) || (staticClassClassExtractionMethod.length() == 0)) {
            return;
        }
        if (!(getClassExtractor() instanceof MethodClassExtractor)) {
            setClassExtractor(new MethodClassExtractor());
        }
        ((MethodClassExtractor)getClassExtractor()).setClassExtractionMethodName(staticClassClassExtractionMethod);
    }

    /**
     * INTERNAL:
     * Set the class indicator associations from reading the deployment XML.
     */
    public void setClassIndicatorAssociations(Vector classIndicatorAssociations) {
        setClassNameIndicatorMapping(new HashMap(classIndicatorAssociations.size() + 1));
        setClassIndicatorMapping(new HashMap((classIndicatorAssociations.size() * 2) + 1));
        for (Iterator iterator = classIndicatorAssociations.iterator(); iterator.hasNext();) {
            Association association = (Association)iterator.next();
            Object key = association.getKey();
            // Allow for 904 format which stored class name, may not use correct class loader.
            if (key instanceof String) {
                key = ConversionManager.getDefaultManager().convertClassNameToClass((String)key);



            }
            addClassIndicator((Class)key, association.getValue());
        }
    }

    /**
     * ADVANCED:
     * To set the class indicator field.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    @Override
    public void setClassIndicatorField(DatabaseField classIndicatorField) {
        this.classIndicatorField = classIndicatorField;
    }

    /**
     * PUBLIC:
     * To set the class indicator field name.
     * This is the name of the field in the table that stores what type of object this is.
     */
    public void setClassIndicatorFieldName(String fieldName) {
        if (fieldName == null) {
            setClassIndicatorField(null);
        } else {
            setClassIndicatorField(new DatabaseField(fieldName));
        }
    }

    /**
     * PUBLIC:
     * Set the association of indicators and classes.
     * This may be desired to be used by clients in strange inheritance models.
     */
    @Override
    public void setClassIndicatorMapping(Map classIndicatorMapping) {
        this.classIndicatorMapping = classIndicatorMapping;
    }

    /**
     * INTERNAL:
     * Set the class name indicator mapping, used by the MW.
     */
    public void setClassNameIndicatorMapping(Map classNameIndicatorMapping) {
        this.classNameIndicatorMapping = classNameIndicatorMapping;
    }

    /**
     * INTERNAL:
     * Set the descriptor.
     */
    @Override
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * ADVANCED:
     * Determines whether the descriptors using this inheritance policy
     * should be used as descriptors for subclasses of the classes they
     * describe if those subclasses do not have their own descriptor
     * 
     * e.g. If Employee.class has a descriptor and EmployeeSubClass does
     * not have a descriptor, if describesNonPersistenceSubclasses is true
     * Employee's descriptor will be used as the descriptor for Employee
     * 
     * @param describesNonPersistenceSubclasses
     */
    public void setDescribesNonPersistentSubclasses(boolean describesNonPersistentSubclasses){
        this.describesNonPersistentSubclasses = describesNonPersistentSubclasses;
    }
    
    /**
     * INTERNAL:
     * Used to indicate a JOINED inheritance strategy.
     * 
     */
    public void setJoinedStrategy() {
        isJoinedStrategy = true;
    }

    /**
     * ADVANCED:
     * Sets the expression used to select instance of the class only. Can be used to customize the
     * inheritance class indicator expression.
     */
    public void setOnlyInstancesExpression(Expression onlyInstancesExpression) {
        this.onlyInstancesExpression = onlyInstancesExpression;
    }

    /**
     * PUBLIC:
     * Set the parent class.
     * A descriptor can inherit from another descriptor through defining it as its parent.
     * The root descriptor must define a class indicator field and mapping.
     * All children must share the same table as their parent but can add additional tables.
     * All children must share the root descriptor primary key.
     */
    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
        if (parentClass != null) {
            setParentClassName(parentClass.getName());
        }
    }

    /**
     * INTERNAL:
     * Set the parent class name, used by MW to avoid referencing the real class for
     * deployment XML generation.
     */
    public void setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
    }

    /**
     * INTERNAL:
     */
    public void setParentDescriptor(ClassDescriptor parentDescriptor) {
        this.parentDescriptor = parentDescriptor;
    }

    /**
     * INTERNAL:
     * The view can be used to optimize/customize the query for all subclasses where they have multiple tables.
     * This view can do the outer join, we require the view because we cannot generate dynamic platform independent SQL
     * for outer joins (i.e. not possible to do so either).
     */
    protected void setReadAllSubclassesView(DatabaseTable readAllSubclassesView) {
        this.readAllSubclassesView = readAllSubclassesView;
    }

    /**
     * ADVANCED:
     * The view can be used to optimize/customize the query for all subclasses where they have multiple tables.
     * This view can use outer joins or unions to combine the results of selecting from all of the subclass tables.
     * If a view is not given then TopLink must make an individual call for each subclass.
     */
    public void setReadAllSubclassesViewName(String readAllSubclassesViewName) {
        if (readAllSubclassesViewName == null) {
            setReadAllSubclassesView(null);
        } else {
            setReadAllSubclassesView(new DatabaseTable(readAllSubclassesViewName));
        }
    }

    /**
     * INTERNAL:
     * Set the descriptor to read instance of itself and its subclasses when queried.
     * This is used with inheritance to configure the result of queries.
     * By default this is true for root inheritance descriptors, and false for all others.
     */
    public void setShouldReadSubclasses(Boolean shouldReadSubclasses) {
        this.shouldReadSubclasses = shouldReadSubclasses;
    }

    /**
     * PUBLIC:
     * Set the descriptor to read instance of itself and its subclasses when queried.
     * This is used with inheritance to configure the result of queries.
     * By default this is true for root inheritance descriptors, and false for all others.
     */
    public void setShouldReadSubclasses(boolean shouldReadSubclasses) {
        this.shouldReadSubclasses = Boolean.valueOf(shouldReadSubclasses);
    }

    /**
     * PUBLIC:
     * Set if the descriptor uses the classes fully qualified name as the indicator.
     * The class indicator is used with inheritance to determine the class from a row.
     * By default a class indicator mapping is required, this can be set to true if usage of the class
     * name is desired.
     * The field must be of a large enough size to store the fully qualified class name.
     */
    public void setShouldUseClassNameAsIndicator(boolean shouldUseClassNameAsIndicator) {
        this.shouldUseClassNameAsIndicator = shouldUseClassNameAsIndicator;
    }

    /**
     * PUBLIC:
     * Sets the inheritance policy to always use an outer join when querying across a relationship of class.
     * used when using getAllowingNull(), or anyOfAllowingNone()
     */

    // cr3546
    public void setAlwaysUseOuterJoinForClassType(boolean choice) {
        this.shouldAlwaysUseOuterJoin = choice;
    }

    /**
     * INTERNAL:
     * Used to indicate a SINGLE_TABLE inheritance strategy.  Since only JOINED and SINGLE_TABLE
     * strategies are supported at this time (no support for TABLE_PER_CLASS) using a 
     * !isJoinedStrategy an an indicator for SINGLE_TABLE is sufficient.
     * 
     */
    public void setSingleTableStrategy() {
        isJoinedStrategy = false;
    }

    /**
     * INTERNAL:
     * Sets if we should use the descriptor inheritance to determine
     * if an object can be returned from the identity map or not.
     */
    public void setUseDescriptorsToValidateInheritedObjects(boolean useDescriptorsToValidateInheritedObjects) {
        //CR 4005
        this.useDescriptorsToValidateInheritedObjects = useDescriptorsToValidateInheritedObjects;
    }

    /**
     * ADVANCED:
     * Sets the expression to be used for querying for a class and all its subclasses. Can be used
     * to customize the inheritance class indicator expression.
     */
    public void setWithAllSubclassesExpression(Expression withAllSubclassesExpression) {
        this.withAllSubclassesExpression = withAllSubclassesExpression;
    }

    /**
     * PUBLIC:
     * Return true if this descriptor should read instances of itself and subclasses on queries.
     */
    public boolean shouldReadSubclasses() {
        if (shouldReadSubclasses == null) {
            return true;
        }
        return shouldReadSubclasses.booleanValue();
    }

    /**
     * INTERNAL:
     * Return true if this descriptor should read instances of itself and subclasses on queries.
     */
    public Boolean shouldReadSubclassesValue() {
        return shouldReadSubclasses;
    }

    /**
     * PUBLIC:
     * returns if the inheritance policy will always use an outerjoin when selecting class type
     */

    // cr3546
    public boolean shouldAlwaysUseOuterJoin() {
        return this.shouldAlwaysUseOuterJoin;
    }

    
    /**
     * PUBLIC:
     * Return if an outer join should be used to read subclasses.
     * By default a separate query is done for each subclass when querying for
     * a root or branch inheritance class that has subclasses that span multiple tables.
     */
    public boolean shouldOuterJoinSubclasses() {
        return shouldOuterJoinSubclasses;
    }
            
    /**
     * PUBLIC:
     * Set if an outer join should be used to read subclasses.
     * By default a separate query is done for each subclass when querying for
     * a root or branch inheritance class that has subclasses that span multiple tables.
     */
    public void setShouldOuterJoinSubclasses(boolean shouldOuterJoinSubclasses) {
        this.shouldOuterJoinSubclasses = shouldOuterJoinSubclasses;
    }
    
    /**
     * PUBLIC:
     * Return true if the descriptor use the classes full name as the indicator.
     * The class indicator is used with inheritance to determine the class from a row.
     * By default a class indicator mapping is required, this can be set to true if usage of the class
     * name is desired.
     * The field must be of a large enough size to store the fully qualified class name.
     */
    public boolean shouldUseClassNameAsIndicator() {
        return shouldUseClassNameAsIndicator;
    }

    /**
     * INTERNAL:
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "(" + getDescriptor() + ")";
    }

    /**
     * INTERNAL:
     * set the tables on the child descriptor 
     * overridden in org.eclipse.persistence.internal.oxm.QNameInheritancePolicy
     */
    protected void updateTables(){
        // Unique is required because the builder can add the same table many times.
        Vector<DatabaseTable> childTables = getDescriptor().getTables();
        Vector<DatabaseTable> parentTables = getParentDescriptor().getTables();
        Vector<DatabaseTable> uniqueTables = Helper.concatenateUniqueVectors(parentTables, childTables);
        getDescriptor().setTables(uniqueTables);
        
        // After filtering out any duplicate tables, set the default table
        // if one is not already set. This must be done now before any other
        // initialization occurs. In a joined strategy case, the default 
        // table will be at an index greater than 0. Which is where
        // setDefaultTable() assumes it is. Therefore, we need to send the 
        // actual default table instead.
        if (childTables.isEmpty()) {
            getDescriptor().setInternalDefaultTable();
        } else {
            getDescriptor().setInternalDefaultTable(uniqueTables.get(uniqueTables.indexOf(childTables.get(0))));
        }
    }
    
    
    /**
     * PUBLIC:
     * Set the descriptor to use the classes full name as the indicator.
     * The class indicator is used with inheritance to determine the class from a row.
     * By default a class indicator mapping is required, this can be set to true if usage of the class
     * name is desired.
     * The field must be of a large enough size to store the fully qualified class name.
     */
    public void useClassNameAsIndicator() {
        setShouldUseClassNameAsIndicator(true);
    }
}
