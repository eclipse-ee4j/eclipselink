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
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.ChangeTracking;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.JoinFetchOrBatchRead;
import org.eclipse.persistence.annotations.OrderCorrectionType;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.mappings.*;

public class EmployeeProject extends org.eclipse.persistence.sessions.Project {

    boolean useListOrderField;
    boolean useIndirection;
    boolean isPrivatelyOwned;
    boolean useSecondaryTable;
    boolean useVarcharOrder;
    ChangeTracking changeTracking;
    OrderCorrectionType orderCorrectionType;
    boolean shouldOverrideContainerPolicy; 
    JoinFetchOrBatchRead joinFetchOrBatchRead;
    
    public EmployeeProject(boolean useListOrderField, boolean useIndirection, boolean isPrivatelyOwned, boolean useSecondaryTable, boolean useVarcharOrder, ChangeTracking changeTracking, OrderCorrectionType orderCorrectionType, boolean shouldOverrideContainerPolicy, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useListOrderField = useListOrderField;
        this.useIndirection = useIndirection;
        this.isPrivatelyOwned = isPrivatelyOwned;
        this.useSecondaryTable = useSecondaryTable;
        this.useVarcharOrder = useVarcharOrder;
        this.changeTracking = changeTracking;
        this.orderCorrectionType = orderCorrectionType;
        this.shouldOverrideContainerPolicy = shouldOverrideContainerPolicy;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        
        setName("OL_Employee");
        setDatasourceLogin(new DatabaseLogin());

        addDescriptor(buildChildDescriptor());
        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildLargeProjectDescriptor());
        // currently attribute change tracking is incompatible with AggregateCollectionMapping
        if(changeTracking != ChangeTracking.ATTRIBUTE) {
            addDescriptor(buildPhoneNumberDescriptor());
        }
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildSmallProjectDescriptor());
    }

    public ClassDescriptor buildChildDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Child.class);
        descriptor.addTableName("OL_CHILD");
        descriptor.addTableName("OL_ALLOWANCE");
        descriptor.addPrimaryKeyFieldName("CHILD_ID");
        
        descriptor.addForeignKeyFieldNameForMultipleTable("OL_ALLOWANCE.OWNER_CHILD_ID", "OL_CHILD.CHILD_ID");
        
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(50);
        descriptor.setSequenceNumberFieldName("CHILD_ID");
        descriptor.setSequenceNumberName("OL_CHILD_SEQ");
        descriptor.setAlias("OL_Child");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CHILD_ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping allowanceMapping = new DirectToFieldMapping();
        allowanceMapping.setAttributeName("allowance");
        allowanceMapping.setFieldName("OL_ALLOWANCE.ALLOWANCE");
        descriptor.addMapping(allowanceMapping);

        return descriptor;        
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.addTableName("OL_EMPLOYEE");
        descriptor.addTableName("OL_SALARY");
        descriptor.addPrimaryKeyFieldName("OL_EMPLOYEE.EMP_ID");

        descriptor.addForeignKeyFieldNameForMultipleTable("OL_SALARY.OWNER_EMP_ID", "OL_EMPLOYEE.EMP_ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("OL_EMP_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OL_Employee");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        DirectToFieldMapping salaryMapping = new DirectToFieldMapping();
        salaryMapping.setAttributeName("salary");
        salaryMapping.setFieldName("OL_SALARY.SALARY");
        descriptor.addMapping(salaryMapping);

        DirectCollectionMapping responsibilitiesListMapping = new DirectCollectionMapping();
        responsibilitiesListMapping.setAttributeName("responsibilitiesList");
        if(useListOrderField) {
            // target foreign key and listOrderField must be in the same table, so
            // either specify listOrderField with target foreign key table
//            responsibilitiesListMapping.setListOrderFieldName("OL_RESPONS.RESPONS_ORDER");
            // or don't specify listOrderField's table at all - it will be defaulted to foreign key table.
            if(useVarcharOrder) {
                responsibilitiesListMapping.setListOrderFieldName("RESPONS_ORDER_VARCHAR");
            } else {
                responsibilitiesListMapping.setListOrderFieldName("RESPONS_ORDER");
            }
            responsibilitiesListMapping.setOrderCorrectionType(orderCorrectionType);
        }
        if(useIndirection) {
            responsibilitiesListMapping.useTransparentList();
        } else {
            if(changeTracking == ChangeTracking.ATTRIBUTE) {
                responsibilitiesListMapping.useCollectionClass(IndirectList.class);
            } else {
                responsibilitiesListMapping.useCollectionClass(ArrayList.class);
            }
            responsibilitiesListMapping.dontUseIndirection();
        }
        responsibilitiesListMapping.setReferenceTableName("OL_RESPONS");
        responsibilitiesListMapping.setDirectFieldName("OL_RESPONS.DESCRIP");
        responsibilitiesListMapping.addReferenceKeyFieldName("OL_RESPONS.EMP_ID", "EMP_ID");
        setJoinFetchOrBatchRead(responsibilitiesListMapping);
        descriptor.addMapping(responsibilitiesListMapping);

        // managedEmployees have nothing: no managedEmployees, no children, no projects, no responsibilities, no phones -
        // can't read them using INNER_JOIN
        if(this.joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN) {
            OneToOneMapping managerMapping = new OneToOneMapping();
            managerMapping.setAttributeName("manager");
            managerMapping.setReferenceClass(Employee.class);
            if(useSecondaryTable) {
                managerMapping.addForeignKeyFieldName("OL_SALARY.MANAGER_ID", "EMP_ID");
            } else {
                managerMapping.addForeignKeyFieldName("MANAGER_ID", "EMP_ID");
            }
            descriptor.addMapping(managerMapping);
    
            OneToManyMapping managedEmployeesMapping = new OneToManyMapping();
            managedEmployeesMapping.setAttributeName("managedEmployees");
            managedEmployeesMapping.setReferenceClass(Employee.class);
            if(isPrivatelyOwned) {
                managedEmployeesMapping.privateOwnedRelationship();
            }
            if(useIndirection) {
                managedEmployeesMapping.useTransparentList();
            } else {
                if(changeTracking == ChangeTracking.ATTRIBUTE) {
                    managedEmployeesMapping.useCollectionClass(IndirectList.class);
                } else {
                    managedEmployeesMapping.useCollectionClass(ArrayList.class);
                }
                managedEmployeesMapping.dontUseIndirection();
            }
            if(useListOrderField) {
                // target foreign key and listOrderField must be in the same table, so
                // either specify listOrderField with target foreign key table
/*                if(useSecondaryTable) {
                    managedEmployeesMapping.setListOrderFieldName("OL_SALARY.MANAGED_ORDER");
                } else {
                    managedEmployeesMapping.setListOrderFieldName("OL_EMPLOYEE.MANAGED_ORDER");
                }*/
                // or don't specify listOrderField's table at all - it will be defaulted to foreign key table.
                // If secondary table used then the field is in SALARY table, otherwise in EMPLOYEE table
                // (both tables have MANAGED_ORDER field, only one of them used at a time).
                if(useVarcharOrder) {
                    managedEmployeesMapping.setListOrderFieldName("MANAGED_ORDER_VARCHAR");
                } else {
                    managedEmployeesMapping.setListOrderFieldName("MANAGED_ORDER");
                }
                managedEmployeesMapping.setOrderCorrectionType(orderCorrectionType);
            }
            if(useSecondaryTable) {
                managedEmployeesMapping.addTargetForeignKeyFieldName("OL_SALARY.MANAGER_ID", "EMP_ID");
            } else {
                managedEmployeesMapping.addTargetForeignKeyFieldName("MANAGER_ID", "EMP_ID");
            }
            setJoinFetchOrBatchRead(managedEmployeesMapping);
            descriptor.addMapping(managedEmployeesMapping);
        }
        
        OneToManyMapping childrenMapping = new UnidirectionalOneToManyMapping();
        childrenMapping.setAttributeName("children");
        childrenMapping.setReferenceClass(Child.class);
        if(isPrivatelyOwned) {
            childrenMapping.privateOwnedRelationship();
        }
        if(useListOrderField) {
            // target foreign key and listOrderField must be in the same table, so
            // either specify listOrderField with target foreign key table
/*            if(useSecondaryTable) {
                childrenMapping.setListOrderFieldName("OL_ALLOWANCE.CHILDREN_ORDER");
            } else {
                childrenMapping.setListOrderFieldName("OL_CHILD.CHILDREN_ORDER");
            }*/
            // or don't specify listOrderField's table at all - it will be defaulted to foreign key table.
            // If secondary table used then the field is in ALLOWANCE table, otherwise in CHILD table
            // (both tables have CHILDREN_ORDER field, only one of them used at a time).
            if(useVarcharOrder) {
                childrenMapping.setListOrderFieldName("CHILDREN_ORDER_VARCHAR");
            } else {
                childrenMapping.setListOrderFieldName("CHILDREN_ORDER");
            }
            childrenMapping.setOrderCorrectionType(orderCorrectionType);
        }
        if(useIndirection) {
            childrenMapping.useTransparentList();
        } else {
            if(changeTracking == ChangeTracking.ATTRIBUTE) {
                childrenMapping.useCollectionClass(IndirectList.class);
            } else {
                childrenMapping.useCollectionClass(Vector.class);
            }
            childrenMapping.dontUseIndirection();
        }
        if(useSecondaryTable) {
            childrenMapping.addTargetForeignKeyFieldName("OL_ALLOWANCE.PARENT_ID", "EMP_ID");
        } else {
            childrenMapping.addTargetForeignKeyFieldName("OL_CHILD.PARENT_ID", "EMP_ID");
        }
        setJoinFetchOrBatchRead(childrenMapping);
        descriptor.addMapping(childrenMapping);

        // currently attribute change tracking is incompatible with AggregateCollectionMapping
        if(changeTracking != ChangeTracking.ATTRIBUTE) {
            AggregateCollectionMapping phoneNumbersMapping = new AggregateCollectionMapping();
            phoneNumbersMapping.setAttributeName("phoneNumbers");
            phoneNumbersMapping.setReferenceClass(PhoneNumber.class);
            phoneNumbersMapping.addTargetForeignKeyFieldName("OL_PHONE.OWNER_ID", "OL_EMPLOYEE.EMP_ID");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.OWNER_ID", "OWNER_ID");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.AREA_CODE", "AREA_CODE");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.PNUMBER", "PNUMBER");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.TYPE", "TYPE");
            if(useListOrderField) {
                // target foreign key and listOrderField must be in the same table, so
                // either specify listOrderField with target foreign key table
//                phoneNumbersMapping.setListOrderFieldName("OL_PHONE.PHONE_ORDER");
                // or don't specify listOrderField's table at all - it will be defaulted to foreign key table.
                if(useVarcharOrder) {
                    phoneNumbersMapping.setListOrderFieldName("PHONE_ORDER_VARCHAR");
                } else {
                    phoneNumbersMapping.setListOrderFieldName("PHONE_ORDER");
                }
                phoneNumbersMapping.setOrderCorrectionType(orderCorrectionType);
            }
            if(useIndirection) {
                phoneNumbersMapping.useTransparentList();
            } else {
                if(changeTracking == ChangeTracking.ATTRIBUTE) {
                    phoneNumbersMapping.useCollectionClass(IndirectList.class);
                } else {
                    phoneNumbersMapping.useCollectionClass(ArrayList.class);
                }
                phoneNumbersMapping.dontUseIndirection();
            }
            setJoinFetchOrBatchRead(phoneNumbersMapping);
            descriptor.addMapping(phoneNumbersMapping);
        }

        ManyToManyMapping projectsMapping = new ManyToManyMapping();
        projectsMapping.setAttributeName("projects");
        projectsMapping.setReferenceClass(Project.class);
        if(isPrivatelyOwned) {
            projectsMapping.privateOwnedRelationship();
        }
        if(useIndirection) {
            projectsMapping.useTransparentList();
        } else {
            if(changeTracking == ChangeTracking.ATTRIBUTE) {
                projectsMapping.useCollectionClass(IndirectList.class);
            } else {
                projectsMapping.useCollectionClass(ArrayList.class);
            }
            projectsMapping.dontUseIndirection();
        }
        if(useListOrderField) {
            // target foreign key and listOrderField must be in the same table, so
            // either specify listOrderField with target foreign key table
//            projectsMapping.setListOrderFieldName("OL_PROJ_EMP.PROJ_ORDER");
            // or don't specify listOrderField's table at all - it will be defaulted to foreign key table.
            if(useVarcharOrder) {
                projectsMapping.setListOrderFieldName("PROJ_ORDER_VARCHAR");
            } else {
                projectsMapping.setListOrderFieldName("PROJ_ORDER");
            }
            projectsMapping.setOrderCorrectionType(orderCorrectionType);
        }
        projectsMapping.setRelationTableName("OL_PROJ_EMP");
        projectsMapping.addSourceRelationKeyFieldName("OL_PROJ_EMP.EMP_ID", "EMP_ID");
        projectsMapping.addTargetRelationKeyFieldName("OL_PROJ_EMP.PROJ_ID", "OL_PROJECT.PROJ_ID");
        setJoinFetchOrBatchRead(projectsMapping);
        descriptor.addMapping(projectsMapping);

        // Currently projectsLed is not used in the tests - that breaks INNER_JOIN
/*        if(this.joinFetchOrBatchRead != JoinFetchOrBatchRead.INNER_JOIN) {
            OneToManyMapping projectsLedMapping = new OneToManyMapping();
            projectsLedMapping.setAttributeName("projectsLed");
            projectsLedMapping.setReferenceClass(Project.class);
            if(useListOrderField) {
                projectsLedMapping.setListOrderFieldName("OL_PROJECT.PROJECTS_LED_ORDER");
                projectsLedMapping.setOrderCorrectionType(orderCorrectionType);
            }
            if(useIndirection) {
                projectsLedMapping.useTransparentList();
            } else {
                if(changeTracking == ChangeTracking.ATTRIBUTE) {
                    projectsLedMapping.useCollectionClass(IndirectList.class);
                } else {
                    projectsLedMapping.useCollectionClass(ArrayList.class);
                }
                projectsLedMapping.dontUseIndirection();
            }
            projectsLedMapping.addTargetForeignKeyFieldName("OL_PROJECT.LEADER_ID", "EMP_ID");
            setJoinFetchOrBatchRead(projectsLedMapping);
            descriptor.addMapping(projectsLedMapping);
        }*/

        return descriptor;
    }

    public ClassDescriptor buildPhoneNumberDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);
        // table name is overridden in AggregateCollectionMapping
        descriptor.addTableName("OL_AGGREGATE_PHONE");

        // Descriptor Properties.
        descriptor.descriptorIsAggregate();

        // Mappings.
        DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
        areaCodeMapping.setAttributeName("areaCode");
        areaCodeMapping.setFieldName("AREA_CODE");
        descriptor.addMapping(areaCodeMapping);

        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("P_NUMBER");
        descriptor.addMapping(numberMapping);

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setFieldName("TYPE");
        descriptor.addMapping(typeMapping);

/*        OneToOneMapping ownerMapping = new OneToOneMapping();
        ownerMapping.setAttributeName("owner");
        ownerMapping.setReferenceClass(Employee.class);
        ownerMapping.useBasicIndirection();
        ownerMapping.addForeignKeyFieldName("OWNER_ID", "OL_EMPLOYEE.EMP_ID");
        descriptor.addMapping(ownerMapping);*/

        return descriptor;
    }

    public ClassDescriptor buildProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Project.class);
        descriptor.addTableName("OL_PROJECT");
        descriptor.addPrimaryKeyFieldName("OL_PROJECT.PROJ_ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PROJ_TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(SmallProject.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(LargeProject.class, "L");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("PROJ_ID");
        descriptor.setSequenceNumberName("OL_PROJ_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OL_Project");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("DESCRIP");
        descriptionMapping.setNullValue("");
        descriptor.addMapping(descriptionMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("PROJ_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("PROJ_NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        OneToOneMapping teamLeaderMapping = new OneToOneMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setReferenceClass(Employee.class);
        teamLeaderMapping.useBasicIndirection();
        teamLeaderMapping.addForeignKeyFieldName("LEADER_ID", "OL_EMPLOYEE.EMP_ID");
        descriptor.addMapping(teamLeaderMapping);

        // ReadOnly - mapped employees attribute on Project is not automatically updated,
        // but only in case of privatelyOwned mapping it fails (many) tests:
        // that happens because compare operates differently on privately owned objects -
        // it compares objects (instead of comparing just their pks in non privately owned case).
        //
        // Also excluded this mapping from inner and outer joins and batch reading configurations.
        // If the mapping is enabled, in case of outer joins SimpleIndexTest keeps running out of memory;
        // inner joins and batch reading configuration generate more queries as well (but not as bad as outer join case).
        //
        // May be this mapping should be excluded for all configurations.
        if(!isPrivatelyOwned && this.joinFetchOrBatchRead == JoinFetchOrBatchRead.NONE) {
            ManyToManyMapping employeesMapping = new ManyToManyMapping();
            employeesMapping.setAttributeName("employees");
            employeesMapping.setReferenceClass(Employee.class);
            if(useIndirection) {
                employeesMapping.useTransparentList();
            } else {
                if(changeTracking == ChangeTracking.ATTRIBUTE) {
                    employeesMapping.useCollectionClass(IndirectList.class);
                } else {
                    employeesMapping.useCollectionClass(ArrayList.class);
                }
                employeesMapping.dontUseIndirection();
            }
            employeesMapping.readOnly();
            employeesMapping.setRelationTableName("OL_PROJ_EMP");
            employeesMapping.addSourceRelationKeyFieldName("OL_PROJ_EMP.PROJ_ID", "PROJ_ID");
            employeesMapping.addTargetRelationKeyFieldName("OL_PROJ_EMP.EMP_ID", "OL_EMPLOYEE.EMP_ID");
            descriptor.addMapping(employeesMapping);
        }

        return descriptor;
    }

    public ClassDescriptor buildSmallProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SmallProject.class);

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OL_SmallProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        return descriptor;
    }
    
    public ClassDescriptor buildLargeProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(LargeProject.class);
        descriptor.addTableName("OL_LPROJECT");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OL_LargeProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping budgetMapping = new DirectToFieldMapping();
        budgetMapping.setAttributeName("budget");
        budgetMapping.setFieldName("OL_LPROJECT.BUDGET");
        descriptor.addMapping(budgetMapping);

        DirectToFieldMapping milestoneVersionMapping = new DirectToFieldMapping();
        milestoneVersionMapping.setAttributeName("milestoneVersion");
        milestoneVersionMapping.setFieldName("OL_LPROJECT.MILESTONE");
        descriptor.addMapping(milestoneVersionMapping);

        return descriptor;
    }
    
    void setJoinFetchOrBatchRead(ForeignReferenceMapping mapping) {
        if(joinFetchOrBatchRead == JoinFetchOrBatchRead.INNER_JOIN) {
            mapping.setJoinFetch(ForeignReferenceMapping.INNER_JOIN);
        } else if(joinFetchOrBatchRead == JoinFetchOrBatchRead.OUTER_JOIN) {
            mapping.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
        } else if(joinFetchOrBatchRead == JoinFetchOrBatchRead.BATCH_READ) {
            mapping.setUsesBatchReading(true);
        }
    }
    
    List<CollectionMapping> getListOrderMappings() {
        List<CollectionMapping> list = new ArrayList();
        Iterator<ClassDescriptor> it = this.getDescriptors().values().iterator();
        while(it.hasNext()) {
            list.addAll(getListOrderMappings(it.next()));
        }
        return list;
    }

    static List<CollectionMapping> getListOrderMappings(DatabaseSession session) {
        List<CollectionMapping> list = new ArrayList();
        list.addAll(getListOrderMappings(session.getDescriptor(Employee.class)));
        list.addAll(getListOrderMappings(session.getDescriptor(Project.class)));
        return list;
    }

    static List<CollectionMapping> getListOrderMappings(ClassDescriptor desc) {
        List<CollectionMapping> list = new ArrayList();
        List<DatabaseMapping> mappings = desc.getMappings();
        for(int i=0; i < mappings.size(); i++) {
            if(mappings.get(i).isCollectionMapping()) {
                CollectionMapping collectionMapping = (CollectionMapping)mappings.get(i); 
                if(collectionMapping.getListOrderField() != null) {
                    list.add(collectionMapping);                    
                }
             }
        }
        return list;
    }
}
