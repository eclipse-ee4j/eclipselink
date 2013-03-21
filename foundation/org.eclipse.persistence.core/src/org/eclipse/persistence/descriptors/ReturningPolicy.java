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
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import java.io.Serializable;
import java.util.*;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>:
 * Allows for INSERT or UPDATE operations to return values back into the object being written.
 * This allows for table default values, trigger or stored procedures computed values to be set back
 * into the object.
 * This can be used with generated SQL on the Oracle platform using the RETURNING clause,
 * or through stored procedures on other platforms.
 *
 * @since TopLink 10.1.3
 */
public class ReturningPolicy implements Serializable, Cloneable {
    protected static final int INSERT = 0;
    protected static final int UPDATE = 1;
    protected static final int NUM_OPERATIONS = 2;
    protected static final int RETURN_ONLY = 0;
    protected static final int WRITE_RETURN = 1;
    protected static final int MAPPED = 2;
    protected static final int UNMAPPED = 3;
    protected static final int ALL = 4;
    protected static final int MAIN_SIZE = 5;

    /** owner of the policy */
    protected ClassDescriptor descriptor;

    /**
     * Stores an object of type Info for every call to any of addField.. methods.
     * Should be filled out before initialize() is called:
     * fields added after initialization are ignored.
     */
    protected List<Info> infos = new ArrayList();

    /**
     * The following attributes are initialized by initialize() method.
     * Contains the actual DatabaseFields to be returned.
     * Populated during descriptor initialization using infos.
     * Here's the order:
     * <pre>
     * main[INSERT][RETURN_ONLY]  main[INSERT][WRITE_RETURN]  main[INSERT][MAPPED]	main[INSERT][UNMAPPED]	main[INSERT][ALL]
     * main[UPDATE][RETURN_ONLY]  main[UPDATE][WRITE_RETURN]  main[UPDATE][MAPPED]	main[UPDATE][UNMAPPED]	main[UPDATE][ALL]
     * </pre>
     * After initialization main[UPDATE,WRITE_RETURN] will contain all DatabaseFields that should be
     * returned on Update as read-write.
     * <pre>
     * main[i][RETURN_ONLY] + main[i][WRITE_RETURN] = main[i][MAPPED]
     * main[i][MAPPED] + main[i][UNMAPPED] = main[i][ALL]
     * </pre>
     */
    protected Collection<DatabaseField>[][] main;

    /**
     * maps ClassDescriptor's tables into Vectors of fields to be used for call generation.
     * Lazily initialized array [NUM_OPERATIONS]
     */
    protected Map<DatabaseTable, Vector<DatabaseField>>[] tableToFieldsForGenerationMap;

    /** indicates whether ReturningPolicy is used for generation of the PK. */
    protected boolean isUsedToSetPrimaryKey;

    /** contains all default table the returning fields that are either unmapped or mapped supplied with types. */
    protected Map<DatabaseField, DatabaseField> fieldsNotFromDescriptor_DefaultTable;

    /** contains all the other tables returning fields that are either unmapped or mapped supplied with types. */
    protected Map<DatabaseField, DatabaseField> fieldsNotFromDescriptor_OtherTables;

    public ReturningPolicy() {
        super();
    }

    /**
     * PUBLIC:
     * Return the owner of the policy.
     */
    public ClassDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * INTERNAL:
     */
    protected void fieldIsNotFromDescriptor(DatabaseField field) {
        if (field.getTable().equals(getDescriptor().getDefaultTable())) {
            if (this.fieldsNotFromDescriptor_DefaultTable == null) {
                this.fieldsNotFromDescriptor_DefaultTable = new HashMap();
            }
            this.fieldsNotFromDescriptor_DefaultTable.put(field, field);
        } else {
            if (this.fieldsNotFromDescriptor_OtherTables == null) {
                this.fieldsNotFromDescriptor_OtherTables = new HashMap();
            }
            this.fieldsNotFromDescriptor_OtherTables.put(field, field);
        }
    }

    /**
     * INTERNAL:
     */
    public Vector getFieldsToGenerateInsert(DatabaseTable table) {
        return getVectorOfFieldsToGenerate(INSERT, table);
    }

    /**
     * INTERNAL:
     */
    public Vector getFieldsToGenerateUpdate(DatabaseTable table) {
        return getVectorOfFieldsToGenerate(UPDATE, table);
    }

    /**
     * INTERNAL:
     */
    public List<Info> getFieldInfos() {
        return infos;
    }

    /**
     * INTERNAL:
     */
    public void setFieldInfos(List<Info> infos) {
        this.infos = infos;
    }

    /**
     * INTERNAL:
     * Used for testing only
     */
    public boolean hasEqualFieldInfos(ReturningPolicy returningPolicyToCompare) {
        return hasEqualFieldInfos(returningPolicyToCompare.getFieldInfos());
    }

    /**
     * INTERNAL:
     * Used for testing only
     */
    public boolean hasEqualFieldInfos(List<Info> infosToCompare) {
        return areCollectionsEqualAsSets(getFieldInfos(), infosToCompare);
    }

    /**
     * INTERNAL:
     * Compares two Collections as sets (ignoring the order of the elements).
     * Note that the passed Collections are cloned.
     * Used for testing only.
     */
    public static boolean areCollectionsEqualAsSets(Collection col1, Collection col2) {
        if (col1 == col2) {
            return true;
        }
        if (col1.size() != col2.size()) {
            return false;
        }
        Collection c1 = new ArrayList(col1);
        Collection c2 = new ArrayList(col2);
        for (Iterator i = c1.iterator(); i.hasNext();) {
            Object o = i.next();
            c2.remove(o);
        }
        return c2.isEmpty();
    }

    /**
     * INTERNAL:
     */
    protected Vector<DatabaseField> getVectorOfFieldsToGenerate(int operation, DatabaseTable table) {
        if (this.main[operation][ALL] == null) {
            return null;
        }
        if (this.tableToFieldsForGenerationMap == null) {
            // the method is called for the first time
            tableToFieldsForGenerationMap = new HashMap[NUM_OPERATIONS];
        }
        if (this.tableToFieldsForGenerationMap[operation] == null) {
            // the method is called for the first time for this operation
            this.tableToFieldsForGenerationMap[operation] = new HashMap();
        }
        Vector<DatabaseField> fieldsForGeneration = this.tableToFieldsForGenerationMap[operation].get(table);
        if (fieldsForGeneration == null) {
            // the method is called for the first time for this operation and this table
            fieldsForGeneration = new NonSynchronizedVector();
            Iterator it = this.main[operation][ALL].iterator();
            while (it.hasNext()) {
                DatabaseField field = (DatabaseField)it.next();
                if (field.getTable().equals(table)) {
                    fieldsForGeneration.add(field);
                }
            }
            this.tableToFieldsForGenerationMap[operation].put(table, fieldsForGeneration);
        }
        return fieldsForGeneration;
    }

    /**
     * INTERNAL:
     */
    public Collection<DatabaseField> getFieldsToMergeInsert() {
        return main[INSERT][MAPPED];
    }

    /**
     * INTERNAL:
     */
    public Collection<DatabaseField> getFieldsToMergeUpdate() {
        return main[UPDATE][MAPPED];
    }

    /**
     * INTERNAL:
     * Normally cloned when not yet initialized.
     * If initialized ReturningPolicy cloned then the clone should be re-initialized.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception exception) {
            throw new InternalError("clone failed");
        }
    }

    /**
     * INTERNAL:
     */
    public void setDescriptor(ClassDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     */
    public void addFieldForInsert(String qualifiedName) {
        addFieldForInsert(qualifiedName, null);
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     * The type may be required to bind the output parameter if not known by the mapping.
     */
    public void addFieldForInsert(String qualifiedName, Class type) {
        addFieldForInsert(createField(qualifiedName, type));
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     */
    public void addFieldForInsert(DatabaseField field) {
        addField(field, true, false, false);
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     * A field added with addFieldForInsertReturnOnly method
     * is excluded from INSERT clause during SQL generation.
     */
    public void addFieldForInsertReturnOnly(String qualifiedName) {
        addFieldForInsertReturnOnly(qualifiedName, null);
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     * A field added with addFieldForInsertReturnOnly method
     * is excluded from INSERT clause during SQL generation.
     * The type may be required to bind the output parameter if not known by the mapping.
     */
    public void addFieldForInsertReturnOnly(String qualifiedName, Class type) {
        addFieldForInsertReturnOnly(createField(qualifiedName, type));
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an insert operation.
     * A field added with addFieldForInsertReturnOnly method
     * is excluded from INSERT clause during SQL generation.
     */
    public void addFieldForInsertReturnOnly(DatabaseField field) {
        addField(field, true, true, false);
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an update operation.
     */
    public void addFieldForUpdate(String qualifiedName) {
        addFieldForUpdate(qualifiedName, null);
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an update operation.
     * The type may be required to bind the output parameter if not known by the mapping.
     */
    public void addFieldForUpdate(String qualifiedName, Class type) {
        addFieldForUpdate(createField(qualifiedName, type));
    }

    /**
     * PUBLIC:
     * Define that the field will be returned from an update operation.
     */
    public void addFieldForUpdate(DatabaseField field) {
        addField(field, false, false, true);
    }

    /**
     * INTERNAL:
     */
    protected void addField(DatabaseField field, boolean isInsert, boolean isInsertModeReturnOnly, boolean isUpdate) {
        getFieldInfos().add(new Info(field, isInsert, isInsertModeReturnOnly, isUpdate));
    }

    /**
     * INTERNAL:
     */
    public static class Info implements Cloneable {
        private DatabaseField field;
        private boolean isInsert;
        private boolean isInsertModeReturnOnly;
        private boolean isUpdate;
        private Class referenceClass;
        private String referenceClassName;

        Info() {
            super();
        }

        Info(DatabaseField field, boolean isInsert, boolean isInsertModeReturnOnly, boolean isUpdate) {
            this.field = field;
            if (field != null) {
                if (field.getType() != null) {
                    setReferenceClass(field.getType());
                }
            }
            this.isInsert = isInsert;
            this.isInsertModeReturnOnly = isInsertModeReturnOnly;
            this.isUpdate = isUpdate;
        }

        public DatabaseField getField() {
            return field;
        }

        public void setField(DatabaseField field) {
            this.field = field;
            if ((field.getType() == null) && (referenceClass != null)) {
                field.setType(referenceClass);
            }
        }

        public boolean isInsert() {
            return isInsert;
        }

        public void setIsInsert(boolean isInsert) {
            this.isInsert = isInsert;
        }

        public boolean isInsertModeReturnOnly() {
            return isInsertModeReturnOnly;
        }

        public void setIsInsertModeReturnOnly(boolean isInsertModeReturnOnly) {
            this.isInsertModeReturnOnly = isInsertModeReturnOnly;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void setIsUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        public Class getReferenceClass() {
            return referenceClass;
        }

        public void setReferenceClass(Class referenceClass) {
            this.referenceClass = referenceClass;
            if (referenceClass != null) {
                this.referenceClassName = referenceClass.getName();
            }
        }

        public String getReferenceClassName() {
            return referenceClassName;
        }

        public void setReferenceClassName(String referenceClassName) {
            this.referenceClassName = referenceClassName;
        }

        // operation is INSERT or UPDATE (0 or 1)
        boolean is(int operation, int stateToCheck) {
            if (operation == INSERT) {
                if (isInsert) {
                    if (stateToCheck == RETURN_ONLY) {
                        return isInsertModeReturnOnly;
                    } else {
                        return !isInsertModeReturnOnly;
                    }
                }
            } else {
                if (isUpdate) {
                    return stateToCheck == WRITE_RETURN;
                }
            }
            return false;
        }

        // operation is INSERT or UPDATE (0 or 1)
        boolean is(int operation) {
            if (operation == INSERT) {
                return isInsert;
            } else {
                return isUpdate;
            }
        }

        /**
         * INTERNAL:
         */
        public Object clone() {
            try {
                return super.clone();
            } catch (Exception exception) {
                throw new InternalError("clone failed");
            }
        }

        public boolean equals(Object objectToCompare) {
            if (objectToCompare instanceof Info) {
                return equals((Info)objectToCompare);
            } else {
                return false;
            }
        }

        boolean equals(Info infoToCompare) {
            if (this == infoToCompare) {
                return true;
            }
            if (!getField().equals(infoToCompare.getField())) {
                return false;
            }
            if ((getField().getType() == null) && (infoToCompare.getField().getType() != null)) {
                return false;
            }
            if ((getField().getType() != null) && !getField().getType().equals(infoToCompare.getField().getType())) {
                return false;
            }
            if (isInsert() != infoToCompare.isInsert()) {
                return false;
            }
            if (isInsertModeReturnOnly() != infoToCompare.isInsertModeReturnOnly()) {
                return false;
            }
            if (isUpdate() != infoToCompare.isUpdate()) {
                return false;
            }
            return true;
        }
    }

    /**
     * INTERNAL:
     */
    // precondition: info1.field.equals(info2.field);
    static Info mergeInfos(Info info1, Info info2, AbstractSession session, ClassDescriptor descriptor) {
        boolean ok = true;

        DatabaseField fieldMerged = info1.getField();

        if (info2.getField().getType() != null) {
            if (info1.getField().getType() == null) {
                fieldMerged = info2.field;
            } else if (!info1.getField().getType().equals(info2.getField().getType())) {
                session.getIntegrityChecker().handleError(DescriptorException.returningPolicyFieldTypeConflict(info1.getField().getName(), info1.getField().getType().getName(), info2.getField().getType().getName(), descriptor));
                ok = false;
            }
        }

        boolean isInsertMerged = false;
        boolean isInsertModeReturnOnlyMerged = false;
        if (info1.isInsert() && !info2.isInsert()) {
            isInsertMerged = true;
            isInsertModeReturnOnlyMerged = info1.isInsertModeReturnOnly();
        } else if (!info1.isInsert() && info2.isInsert()) {
            isInsertMerged = true;
            isInsertModeReturnOnlyMerged = info2.isInsertModeReturnOnly();
        } else if (info1.isInsert() && info2.isInsert()) {
            isInsertMerged = true;
            isInsertModeReturnOnlyMerged = info1.isInsertModeReturnOnly();
            if (info1.isInsertModeReturnOnly() != info2.isInsertModeReturnOnly()) {
                session.getIntegrityChecker().handleError(DescriptorException.returningPolicyFieldInsertConflict(info1.getField().getName(), descriptor));
                ok = false;
            }
        }

        if (ok) {
            // merging
            boolean isUpdateMerged = info1.isUpdate() || info2.isUpdate();
            return new Info(fieldMerged, isInsertMerged, isInsertModeReturnOnlyMerged, isUpdateMerged);
        } else {
            // there is a problem - can't merge
            return null;
        }
    }

    /**
     * INTERNAL:
     */

    // used only on equal fields: field1.equals(field2)
    static protected boolean isThereATypeConflict(DatabaseField field1, DatabaseField field2) {
        return (field1.getType() != null) && (field2.getType() != null) && !field1.getType().equals(field2.getType());
    }

    /**
     * INTERNAL:
     */
    protected DatabaseField createField(String qualifiedName, Class type) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(type);
        return field;
    }

    /**
     * INTERNAL:
     */
    protected Collection createCollection() {
        return new HashSet();
    }

    // precondition field != null
    protected void addFieldToMain(int operation, int state, DatabaseField field) {
        if (main[operation][state] == null) {
            main[operation][state] = createCollection();
        }
        main[operation][state].add(field);
    }

    protected void addCollectionToMain(int operation, int state, Collection collection) {
        if ((collection == null) || collection.isEmpty()) {
            return;
        }
        if (main[operation][state] == null) {
            main[operation][state] = createCollection();
        }
        main[operation][state].addAll(collection);
    }

    protected void addMappedFieldToMain(DatabaseField field, Info info) {
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            for (int state = RETURN_ONLY; state <= WRITE_RETURN; state++) {
                if (info.is(operation, state)) {
                    addFieldToMain(operation, state, field);
                    addFieldToMain(operation, MAPPED, field);
                    addFieldToMain(operation, ALL, field);
                }
            }
        }
    }

    protected void addUnmappedFieldToMain(DatabaseField field, Info info) {
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            if (info.is(operation)) {
                addFieldToMain(operation, UNMAPPED, field);
                addFieldToMain(operation, ALL, field);
            }
        }
    }

    protected Hashtable removeDuplicateAndValidateInfos(AbstractSession session) {
        Hashtable infoHashtable = new Hashtable();
        for (int i = 0; i < infos.size(); i++) {
            Info info1 = infos.get(i);
            info1 = (Info)info1.clone();
            DatabaseField descField = getDescriptor().buildField(info1.getField()); 
            if(info1.getField().getType() == null) {
                info1.setField(descField);
            } else {
                // keep the original type if specified
                info1.getField().setName(descField.getName()); 
                info1.getField().setTableName(getDescriptor().getDefaultTable().getQualifiedNameDelimited(session.getPlatform()));
            }
            Info info2 = (Info)infoHashtable.get(info1.getField());
            if (info2 == null) {
                infoHashtable.put(info1.getField(), info1);
            } else {
                Info infoMerged = mergeInfos(info1, info2, session, getDescriptor());
                if (infoMerged != null) {
                    // substitute info2 with infoMerged
                    infoHashtable.put(infoMerged.getField(), infoMerged);
                } else {
                    // couldn't merge info1 and info2 due to a conflict.
                    // substitute info2 with info1
                    infoHashtable.put(info1.getField(), info1);
                }
            }
        }
        return infoHashtable;
    }

    /**
     * INTERNAL:
     */
    public void initialize(AbstractSession session) {
        clearInitialization();
        main = new Collection[NUM_OPERATIONS][MAIN_SIZE];

        // The order of descriptor initialization guarantees initialization of Parent before children.
        // main array is copied from Parent's ReturningPolicy
        if (getDescriptor().isChildDescriptor()) {
            ClassDescriptor parentDescriptor = getDescriptor().getInheritancePolicy().getParentDescriptor();
            if (parentDescriptor.hasReturningPolicy()) {
                copyMainFrom(parentDescriptor.getReturningPolicy());
            }
        }

        if (!infos.isEmpty()) {
            Hashtable infoHashtable = removeDuplicateAndValidateInfos(session);
            Hashtable infoHashtableUnmapped = (Hashtable)infoHashtable.clone();
            for (Enumeration fields = getDescriptor().getFields().elements();
                     fields.hasMoreElements();) {
                DatabaseField field = (DatabaseField)fields.nextElement();
                Info info = (Info)infoHashtableUnmapped.get(field);
                if (info != null) {
                    infoHashtableUnmapped.remove(field);
                    if (verifyFieldAndMapping(session, field)) {
                        if (info.getField().getType() == null) {
                            addMappedFieldToMain(field, info);
                        } else {
                            addMappedFieldToMain(info.getField(), info);
                            fieldIsNotFromDescriptor(info.getField());
                        }
                    }
                }
            }

            if (!infoHashtableUnmapped.isEmpty()) {
                Enumeration fields = infoHashtableUnmapped.keys();
                while (fields.hasMoreElements()) {
                    DatabaseField field = (DatabaseField)fields.nextElement();
                    Info info = (Info)infoHashtableUnmapped.get(field);
                    if (verifyField(session, field, getDescriptor())) {
                        if (field.getType() != null) {
                            addUnmappedFieldToMain(field, info);
                            fieldIsNotFromDescriptor(field);
                            session.log(SessionLog.FINEST, SessionLog.QUERY, "added_unmapped_field_to_returning_policy", info.toString(), getDescriptor().getJavaClassName());
                        } else {
                            if (getDescriptor().isReturnTypeRequiredForReturningPolicy()) {
                                session.getIntegrityChecker().handleError(DescriptorException.returningPolicyUnmappedFieldTypeNotSet(field.getName(), getDescriptor()));
                            }
                        }
                    }
                }
            }
        }

        initializeIsUsedToSetPrimaryKey();
    }

    protected void copyMainFrom(ReturningPolicy policy) {
        Collection[][] mainToCopy = policy.main;
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            for (int state = RETURN_ONLY; state < MAIN_SIZE; state++) {
                addCollectionToMain(operation, state, mainToCopy[operation][state]);
            }
        }
    }

    /**
     * INTERNAL:
     * Both ReturningPolicies should be initialized
     */
    public boolean hasEqualMains(ReturningPolicy policy) {
        Collection[][] mainToCompare = policy.main;
        if (main == mainToCompare) {
            return true;
        }
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            for (int state = RETURN_ONLY; state < MAIN_SIZE; state++) {
                if ((main[operation][state] == null) && (mainToCompare[operation][state] != null)) {
                    return false;
                }
                if ((main[operation][state] != null) && (mainToCompare[operation][state] == null)) {
                    return false;
                }
                if (!main[operation][state].equals(mainToCompare[operation][state])) {
                    return false;
                }
            }
        }

        // now compare types
        Hashtable allFields = new Hashtable();
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            if (main[operation][ALL] != null) {
                Iterator it = main[operation][ALL].iterator();
                while (it.hasNext()) {
                    DatabaseField field = (DatabaseField)it.next();
                    allFields.put(field, field);
                }
            }
        }
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            if (mainToCompare[operation][ALL] != null) {
                Iterator it = mainToCompare[operation][ALL].iterator();
                while (it.hasNext()) {
                    DatabaseField fieldToCompare = (DatabaseField)it.next();
                    DatabaseField field = (DatabaseField)allFields.get(fieldToCompare);
                    if (!field.getType().equals(fieldToCompare.getType())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * INTERNAL:
     */
    public void trimModifyRowForInsert(AbstractRecord modifyRow) {
        trimModifyRow(modifyRow, INSERT);
    }

    // operation should be either INSERT or UPDATE
    protected void trimModifyRow(AbstractRecord modifyRow, int operation) {
        if ((modifyRow == null) || modifyRow.isEmpty()) {
            return;
        }
        Collection fields = main[operation][RETURN_ONLY];
        if ((fields == null) || fields.isEmpty()) {
            return;
        }
        for (int i = modifyRow.size() - 1; i >= 0; i--) {
            DatabaseField field = modifyRow.getFields().get(i);
            if (fields.contains(field)) {
                modifyRow.remove(field);
            }
        }
    }

    /**
     * PUBLIC:
     */
    public boolean isUsedToSetPrimaryKey() {
        return isUsedToSetPrimaryKey;
    }

    // only infos is filled out
    protected void clearInitialization() {
        this.main = null;
        this.tableToFieldsForGenerationMap = null;
        this.fieldsNotFromDescriptor_DefaultTable = null;
        this.fieldsNotFromDescriptor_OtherTables = null;
    }

    protected void initializeIsUsedToSetPrimaryKey() {
        this.isUsedToSetPrimaryKey = false;
        if ((main[INSERT][MAPPED] == null) || main[INSERT][MAPPED].isEmpty()) {
            return;
        }
        List primaryKeys = getDescriptor().getPrimaryKeyFields();
        for (int index = 0; (index < primaryKeys.size()) && !isUsedToSetPrimaryKey; index++) {
            this.isUsedToSetPrimaryKey = main[INSERT][MAPPED].contains(primaryKeys.get(index));
        }
    }

    protected boolean verifyFieldAndMapping(AbstractSession session, DatabaseField field) {
        boolean ok = true;
        verifyField(session, field, getDescriptor());
        DatabaseMapping mapping;
        List readOnlyMappings = getDescriptor().getObjectBuilder().getReadOnlyMappingsForField(field);
        if (readOnlyMappings != null) {
            for (int j = 0; j < readOnlyMappings.size(); j++) {
                mapping = (DatabaseMapping)readOnlyMappings.get(j);
                ok &= verifyFieldAndMapping(session, field, getDescriptor(), mapping);
            }
        }
        mapping = getDescriptor().getObjectBuilder().getMappingForField(field);
        if (mapping != null) {
            ok &= verifyFieldAndMapping(session, field, getDescriptor(), mapping);
        }
        return ok;
    }

    protected static boolean verifyFieldAndMapping(AbstractSession session, DatabaseField field, ClassDescriptor descriptor, DatabaseMapping mapping) {
        verifyField(session, field, descriptor);
        while (mapping.isAggregateObjectMapping()) {
            ClassDescriptor referenceDescriptor = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
            mapping = referenceDescriptor.getObjectBuilder().getMappingForField(field);
            verifyFieldAndMapping(session, field, referenceDescriptor, mapping);
        }
        if (!mapping.isDirectToFieldMapping() && !mapping.isTransformationMapping()) {
            String mappingTypeName = Helper.getShortClassName(mapping);
            session.getIntegrityChecker().handleError(DescriptorException.returningPolicyMappingNotSupported(field.getName(), mappingTypeName, mapping));
            return false;
        } else {
            return true;
        }
    }

    protected static boolean verifyField(AbstractSession session, DatabaseField field, ClassDescriptor descriptor) {
        boolean ok = true;
        if (field.equals(descriptor.getSequenceNumberField())) {
            ok = false;
            session.getIntegrityChecker().handleError(DescriptorException.returningPolicyFieldNotSupported(field.getName(), descriptor));
        } else if (descriptor.hasInheritance() && field.equals(descriptor.getInheritancePolicy().getClassIndicatorField())) {
            ok = false;
            session.getIntegrityChecker().handleError(DescriptorException.returningPolicyFieldNotSupported(field.getName(), descriptor));
        } else if (descriptor.usesOptimisticLocking()) {
            OptimisticLockingPolicy optimisticLockingPolicy = descriptor.getOptimisticLockingPolicy();
            if (optimisticLockingPolicy instanceof VersionLockingPolicy) {
                VersionLockingPolicy versionLockingPolicy = (VersionLockingPolicy)optimisticLockingPolicy;
                if (field.equals(versionLockingPolicy.getWriteLockField())) {
                    ok = false;
                    session.getIntegrityChecker().handleError(DescriptorException.returningPolicyFieldNotSupported(field.getName(), descriptor));
                }
            }
        }
        return ok;
    }

    /**
     * INTERNAL:
     */
    public void validationAfterDescriptorInitialization(AbstractSession session) {
        Hashtable mapped = new Hashtable();
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            if ((main[operation][MAPPED] != null) && !main[operation][MAPPED].isEmpty()) {
                Iterator it = main[operation][MAPPED].iterator();
                while (it.hasNext()) {
                    DatabaseField field = (DatabaseField)it.next();
                    mapped.put(field, field);
                }
            }
        }
        if (!mapped.isEmpty()) {
            for (Enumeration fields = getDescriptor().getFields().elements();
                     fields.hasMoreElements();) {
                DatabaseField fieldInDescriptor = (DatabaseField)fields.nextElement();
                DatabaseField fieldInMain = (DatabaseField)mapped.get(fieldInDescriptor);
                if (fieldInMain != null) {
                    if (fieldInMain.getType() == null) {
                        if (getDescriptor().isReturnTypeRequiredForReturningPolicy()) {
                            session.getIntegrityChecker().handleError(DescriptorException.returningPolicyMappedFieldTypeNotSet(fieldInMain.getName(), getDescriptor()));
                        }
                    } else if (isThereATypeConflict(fieldInMain, fieldInDescriptor)) {
                        session.getIntegrityChecker().handleError(DescriptorException.returningPolicyAndDescriptorFieldTypeConflict(fieldInMain.getName(), fieldInMain.getType().getName(), fieldInDescriptor.getType().getName(), getDescriptor()));
                    }
                }
            }
        }
        if (!(session.getDatasourcePlatform() instanceof DatabasePlatform)) {
            // don't attempt further diagnostics on non-relational platforms
            return;
        }
        WriteObjectQuery[] query = { getDescriptor().getQueryManager().getInsertQuery(), getDescriptor().getQueryManager().getUpdateQuery() };
        String[] queryTypeName = { "InsertObjectQuery", "UpdateObjectQuery" };
        for (int operation = INSERT; operation <= UPDATE; operation++) {
            if ((main[operation][ALL] != null) && !main[operation][ALL].isEmpty()) {
                // this operation requires some fields to be returned
                if ((query[operation] == null) || (query[operation].getDatasourceCall() == null)) {
                    if (!session.getPlatform().canBuildCallWithReturning()) {
                        session.getIntegrityChecker().handleError(DescriptorException.noCustomQueryForReturningPolicy(queryTypeName[operation], Helper.getShortClassName(session.getPlatform()), getDescriptor()));
                    }
                } else if (query[operation].getDatasourceCall() instanceof StoredProcedureCall) {
                    // SQLCall with custom SQL calculates its outputRowFields later (in prepare() method) -
                    // that's why SQLCall can't be verified here.
                    DatabaseCall customCall = (DatabaseCall)query[operation].getDatasourceCall();
                    Enumeration outputRowFields = customCall.getOutputRowFields().elements();
                    Collection notFoundInOutputRow = createCollection();
                    notFoundInOutputRow.addAll(main[operation][ALL]);
                    while (outputRowFields.hasMoreElements()) {
                        notFoundInOutputRow.remove(outputRowFields.nextElement());
                    }
                    if (!notFoundInOutputRow.isEmpty()) {
                        Iterator it = notFoundInOutputRow.iterator();
                        while (it.hasNext()) {
                            DatabaseField field = (DatabaseField)it.next();
                            session.getIntegrityChecker().handleError(DescriptorException.customQueryAndReturningPolicyFieldConflict(field.getName(), queryTypeName[operation], getDescriptor()));
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * Returns an equal field held by ReturningPolicy, or null.
     */
    public DatabaseField getField(DatabaseField field) {
        DatabaseField foundField = null;
        if (this.fieldsNotFromDescriptor_DefaultTable != null) {
            foundField = this.fieldsNotFromDescriptor_DefaultTable.get(field);
        }
        if ((foundField == null) && (this.fieldsNotFromDescriptor_OtherTables != null)) {
            foundField = this.fieldsNotFromDescriptor_OtherTables.get(field);
        }
        return foundField;
    }
}
