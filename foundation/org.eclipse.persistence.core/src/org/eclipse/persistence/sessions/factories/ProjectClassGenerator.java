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
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/  
package org.eclipse.persistence.sessions.factories;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.descriptors.copying.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.indirection.*;
import org.eclipse.persistence.internal.expressions.ExpressionJavaPrinter;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.factories.DirectToXMLTypeMappingHelper;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.mappings.querykeys.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.codegen.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.sequencing.*;
import org.eclipse.persistence.indirection.IndirectMap;

/**
 * <p><b>Purpose</b>: Allow for a class storing a TopLink project's descriptors (meta-data) to be generated.
 * This class can then be used at runtime to deploy the TopLink descriptor's instead of XML files.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public class ProjectClassGenerator {
    protected String className;
    protected String packageName;
    protected String outputPath;
    protected String outputFileName;
    protected Writer outputWriter;
    protected Project project;
    protected Hashtable descriptorMethodNames;

    /**
     * PUBLIC:
     * Create a new generator.
     */
    public ProjectClassGenerator() {
        this.outputPath = "";
        this.outputFileName = "TopLinkProject.java";
        this.className = "TopLinkProject";
        this.packageName = "";
        this.descriptorMethodNames = new Hashtable();
    }

    /**
     * PUBLIC:
     * Create a new generator to output the project.
     */
    public ProjectClassGenerator(Project project) {
        this();
        this.project = project;
    }

    /**
     * PUBLIC:
     * Create a new generator to output to the writer.
     */
    public ProjectClassGenerator(Project project, String projectClassName, Writer outputWriter) {
        this(project);
        this.outputWriter = outputWriter;
        setClassName(projectClassName);
    }

    /**
     * PUBLIC:
     * Create a new generator to output to the file.
     */
    public ProjectClassGenerator(Project project, String projectClassName, String fileName) {
        this(project);
        setClassName(projectClassName);
        setOutputFileName(fileName);
    }

    protected void addAggregateCollectionMappingLines(NonreflectiveMethodDefinition method, String mappingName, AggregateCollectionMapping mapping) {
        Enumeration targetKeysEnum = mapping.getTargetForeignKeyFields().elements();
        Enumeration sourceKeysEnum = mapping.getSourceKeyFields().elements();
        while (sourceKeysEnum.hasMoreElements()) {
            DatabaseField sourceField = (DatabaseField)sourceKeysEnum.nextElement();
            DatabaseField targetField = (DatabaseField)targetKeysEnum.nextElement();
            method.addLine(mappingName + ".addTargetForeignKeyFieldName(\"" + targetField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
        }
    }

    protected void addAggregateObjectMappingLines(NonreflectiveMethodDefinition method, String mappingName, AggregateObjectMapping mapping) {
        if (mapping.getReferenceClassName() != null) {
            method.addLine(mappingName + ".setReferenceClass(" + mapping.getReferenceClassName() + ".class);");
        }
        method.addLine(mappingName + ".setIsNullAllowed(" + mapping.isNullAllowed() + ");");

        for (Iterator<String> fieldTranslationEnum = mapping.getAggregateToSourceFields().keySet().iterator();
                 fieldTranslationEnum.hasNext();) {
            String aggregateFieldName = fieldTranslationEnum.next();
            DatabaseField sourceField = mapping.getAggregateToSourceFields().get(aggregateFieldName);
            //may need to account for delimiting on the sourceField in the future
            method.addLine(mappingName + ".addFieldNameTranslation(\"" + sourceField.getQualifiedName() + "\", \"" + aggregateFieldName + "\");");
        }
    }

    protected void addCacheInvalidationPolicyLines(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        CacheInvalidationPolicy policy = descriptor.getCacheInvalidationPolicy();
        if (policy instanceof NoExpiryCacheInvalidationPolicy) {
            if (policy.shouldUpdateReadTimeOnUpdate()) {
                method.addLine("// Cache Invalidation Policy");
                method.addLine("NoExpiryCacheInvalidationPolicy policy = new NoExpiryCacheInvalidationPolicy();");
                method.addLine("policy.setShouldUpdateReadTimeOnUpdate(" + policy.shouldUpdateReadTimeOnUpdate() + ");");
                method.addLine("descriptor.setCacheInvalidationPolicy(policy);");
            }
        } else if (policy instanceof TimeToLiveCacheInvalidationPolicy) {
            method.addLine("// Cache Invalidation Policy");
            method.addLine("TimeToLiveCacheInvalidationPolicy policy = new TimeToLiveCacheInvalidationPolicy(" + ((TimeToLiveCacheInvalidationPolicy)policy).getTimeToLive() + ");");
            method.addLine("policy.setShouldUpdateReadTimeOnUpdate(" + policy.shouldUpdateReadTimeOnUpdate() + ");");
            method.addLine("descriptor.setCacheInvalidationPolicy(policy);");
        } else if (policy instanceof DailyCacheInvalidationPolicy) {
            Calendar calendar = ((DailyCacheInvalidationPolicy)policy).getExpiryTime();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int millisecond = calendar.get(Calendar.MILLISECOND);
            method.addLine("// Cache Invalidation Policy");
            method.addLine("DailyCacheInvalidationPolicy policy = new DailyCacheInvalidationPolicy(" + hour + ", " + minute + ", " + second + ", " + millisecond + ");");
            method.addLine("policy.setShouldUpdateReadTimeOnUpdate(" + policy.shouldUpdateReadTimeOnUpdate() + ");");
            method.addLine("descriptor.setCacheInvalidationPolicy(policy);");
        }
    }

    protected void addCMPPolicyLines(NonreflectiveMethodDefinition method, CMPPolicy sourceCMPPolicy) {
        if (sourceCMPPolicy == null) {
            return;
        }
        method.addLine("");
        method.addLine("// CMP Policy");

        method.addLine("CMPPolicy cmpPolicy = new CMPPolicy();");
        method.addLine("cmpPolicy.setDeferModificationsUntilCommit(" + sourceCMPPolicy.getDeferModificationsUntilCommit() + ");");
        method.addLine("cmpPolicy.setForceUpdate(" + sourceCMPPolicy.getForceUpdate() + ");");
        method.addLine("cmpPolicy.setNonDeferredCreateTime( " + sourceCMPPolicy.getNonDeferredCreateTime() + ");");
        method.addLine("cmpPolicy.setUpdateAllFields(" + sourceCMPPolicy.getUpdateAllFields() + ");");

        if (sourceCMPPolicy.hasPessimisticLockingPolicy()) {
            method.addLine("");
            method.addLine("// Pessimistic Locking Policy");
            method.addLine("cmpPolicy.setPessimisticLockingPolicy(new PessimisticLockingPolicy());");

            if (ObjectLevelReadQuery.LOCK == sourceCMPPolicy.getPessimisticLockingPolicy().getLockingMode()) {
                method.addLine("cmpPolicy.getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK);");
            } else {
                method.addLine("cmpPolicy.getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK_NOWAIT);");
            }
        }
        method.addLine("descriptor.setCMPPolicy(cmpPolicy);");
    }

    protected void addDescriptorPropertyLines(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        method.addLine("// ClassDescriptor Properties.");

        // Identity map
        if ((!descriptor.isChildDescriptor()) && !descriptor.isDescriptorTypeAggregate() && (!descriptor.isDescriptorForInterface())) {
            if (descriptor.shouldUseFullIdentityMap()) {
                method.addLine("descriptor.useFullIdentityMap();");
            } else if (descriptor.shouldUseCacheIdentityMap()) {
                method.addLine("descriptor.useCacheIdentityMap();");
            } else if (descriptor.shouldUseSoftCacheWeakIdentityMap()) {
                method.addLine("descriptor.useSoftCacheWeakIdentityMap();");
            } else if (descriptor.shouldUseHardCacheWeakIdentityMap()) {
                method.addLine("descriptor.useHardCacheWeakIdentityMap();");
            } else if (descriptor.shouldUseWeakIdentityMap()) {
                method.addLine("descriptor.useWeakIdentityMap();");
            } else if (descriptor.shouldUseSoftIdentityMap()) {
                method.addLine("descriptor.useSoftIdentityMap();");
            } else if (descriptor.shouldUseNoIdentityMap()) {
                method.addLine("descriptor.useNoIdentityMap();");
            }

            method.addLine("descriptor.setIdentityMapSize(" + descriptor.getIdentityMapSize() + ");");
            // Remote
            if (descriptor.shouldUseRemoteFullIdentityMap()) {
                method.addLine("descriptor.useRemoteFullIdentityMap();");
            } else if (descriptor.shouldUseRemoteCacheIdentityMap()) {
                method.addLine("descriptor.useRemoteCacheIdentityMap();");
            } else if (descriptor.shouldUseRemoteSoftCacheWeakIdentityMap()) {
                method.addLine("descriptor.useRemoteSoftCacheWeakIdentityMap();");
            } else if (descriptor.shouldUseRemoteHardCacheWeakIdentityMap()) {
                method.addLine("descriptor.useRemoteHardCacheWeakIdentityMap();");
            } else if (descriptor.shouldUseRemoteWeakIdentityMap()) {
                method.addLine("descriptor.useRemoteWeakIdentityMap();");
            } else if (descriptor.shouldUseRemoteNoIdentityMap()) {
                method.addLine("descriptor.useRemoteNoIdentityMap();");
            }

            method.addLine("descriptor.setRemoteIdentityMapSize(" + descriptor.getRemoteIdentityMapSize() + ");");
        }

        // Sequencing
        if (descriptor.usesSequenceNumbers()) {
            method.addLine("descriptor.setSequenceNumberFieldName(\"" + descriptor.getSequenceNumberField().getQualifiedName() + "\");");
            if (descriptor.getSequenceNumberName() != null) {
                method.addLine("descriptor.setSequenceNumberName(\"" + descriptor.getSequenceNumberName() + "\");");
            }
        }

        // Returning
        if (descriptor.hasReturningPolicy()) {
            addReturningPolicyLines(method, descriptor.getReturningPolicy());
        }

        // Locking
        if (descriptor.usesOptimisticLocking()) {
            addOptimisticLockingLines(method, descriptor.getOptimisticLockingPolicy());
        }

        // Always Conform Results in Unit of Work
        if (descriptor.shouldAlwaysConformResultsInUnitOfWork()) {
            method.addLine("descriptor.alwaysConformResultsInUnitOfWork();");
        }

        // Is Read Only
        if (descriptor.shouldBeReadOnly()) {
            method.addLine("descriptor.setReadOnly();");
        }

        // Isolated Session support
        if (descriptor.getCachePolicy().isIsolated()) {
            method.addLine("descriptor.setIsIsolated(true);");
        }

        // Refreshing
        if (descriptor.getCachePolicy().shouldAlwaysRefreshCache()) {
            method.addLine("descriptor.alwaysRefreshCache();");
        }
        if (descriptor.getCachePolicy().shouldAlwaysRefreshCacheOnRemote()) {
            method.addLine("descriptor.alwaysRefreshCacheOnRemote();");
        }
        if (descriptor.getCachePolicy().shouldDisableCacheHits()) {
            method.addLine("descriptor.disableCacheHits();");
        }
        if (descriptor.getCachePolicy().shouldDisableCacheHitsOnRemote()) {
            method.addLine("descriptor.disableCacheHitsOnRemote();");
        }
        if (descriptor.getCachePolicy().shouldOnlyRefreshCacheIfNewerVersion()) {
            method.addLine("descriptor.onlyRefreshCacheIfNewerVersion();");
        }

        // ClassDescriptor alias
        if (descriptor.getAlias() != null) {
            method.addLine("descriptor.setAlias(\"" + descriptor.getAlias() + "\");");
        }

        // Copying
        if ((descriptor.getCopyPolicy() instanceof CloneCopyPolicy) && (((CloneCopyPolicy)descriptor.getCopyPolicy()).getMethodName() != null)) {
            method.addLine("descriptor.useCloneCopyPolicy(\"" + ((CloneCopyPolicy)descriptor.getCopyPolicy()).getMethodName() + "\");");
        }

        // Instantiation
        if (!descriptor.getInstantiationPolicy().isUsingDefaultConstructor()) {
            if (descriptor.getInstantiationPolicy().getFactoryClassName() != null) {
                if (descriptor.getInstantiationPolicy().getFactoryMethodName() != null) {
                    method.addLine("descriptor.useFactoryInstantiationPolicy(" + descriptor.getInstantiationPolicy().getFactoryClassName() + ".class, \"" + descriptor.getInstantiationPolicy().getMethodName() + "\", \"" + descriptor.getInstantiationPolicy().getFactoryMethodName() + "\");");
                } else {
                    method.addLine("descriptor.useFactoryInstantiationPolicy(" + descriptor.getInstantiationPolicy().getFactoryClassName() + ".class, \"" + descriptor.getInstantiationPolicy().getMethodName() + "\");");
                }
            } else {
                method.addLine("descriptor.useMethodInstantiationPolicy(\"" + descriptor.getInstantiationPolicy().getMethodName() + "\");");
            }
        }

        // Amendment
        if (descriptor.getAmendmentClassName() != null) {
            method.addLine("descriptor.setAmendmentClass(" + descriptor.getAmendmentClassName() + ".class);");
        }
        if (descriptor.getAmendmentMethodName() != null) {
            method.addLine("descriptor.setAmendmentMethodName(\"" + descriptor.getAmendmentMethodName() + "\");");
        }

        if (descriptor.getCachePolicy().getCacheSynchronizationType() != CachePolicy.UNDEFINED_OBJECT_CHANGE_BEHAVIOR) {
            StringBuffer lineToAdd = new StringBuffer("descriptor.setCacheSynchronizationType(");
            if (descriptor.getCachePolicy().getCacheSynchronizationType() == CachePolicy.INVALIDATE_CHANGED_OBJECTS) {
                lineToAdd.append("ClassDescriptor.INVALIDATE_CHANGED_OBJECTS");
            } else if (descriptor.getCachePolicy().getCacheSynchronizationType() == CachePolicy.DO_NOT_SEND_CHANGES) {
                lineToAdd.append("ClassDescriptor.DO_NOT_SEND_CHANGES");
            } else if (descriptor.getCachePolicy().getCacheSynchronizationType() == CachePolicy.SEND_NEW_OBJECTS_WITH_CHANGES) {
                lineToAdd.append("ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES");
            } else if (descriptor.getCachePolicy().getCacheSynchronizationType() == CachePolicy.SEND_OBJECT_CHANGES) {
                lineToAdd.append("ClassDescriptor.SEND_OBJECT_CHANGES");
            }
            lineToAdd.append(");");
            method.addLine(lineToAdd.toString());
        }

        // CMP descriptor
        addCMPPolicyLines(method, descriptor.getCMPPolicy());
    }

    protected void addDirectCollectionMappingLines(NonreflectiveMethodDefinition method, String mappingName, DirectCollectionMapping mapping) {
        if (mapping.getReferenceTable() != null) {
            method.addLine(mappingName + ".setReferenceTableName(\"" + mapping.getReferenceTable().getQualifiedName() + "\");");
        }

        method.addLine(mappingName + ".setDirectFieldName(\"" + mapping.getDirectFieldName() + "\");");

        Enumeration sourceKeysEnum = mapping.getSourceKeyFields().elements();
        Enumeration referenceKeysEnum = mapping.getReferenceKeyFields().elements();
        while (referenceKeysEnum.hasMoreElements()) {
            DatabaseField sourceField = (DatabaseField)sourceKeysEnum.nextElement();
            DatabaseField referenceField = (DatabaseField)referenceKeysEnum.nextElement();
            method.addLine(mappingName + ".addReferenceKeyFieldName(\"" + referenceField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
        }

        Converter converter = mapping.getValueConverter();
        if (converter != null) {
            addConverterLines(method, mappingName + "Converter", converter);
            method.addLine(mappingName + ".setValueConverter(" + mappingName + "Converter" + ");");
        }

        addHistoryPolicyLines(method, mapping, mappingName);
    }

    protected void addDirectMapMappingLines(NonreflectiveMethodDefinition method, String mappingName, DirectMapMapping mapping) {
        DatabaseField  directKeyField = mapping.getDirectKeyField();
        if(directKeyField != null) {
            method.addLine(mappingName + ".setDirectKeyFieldName(\"" + directKeyField.getQualifiedName() + "\");");
        }

        Converter converter = mapping.getKeyConverter();
        if (converter != null) {
            addConverterLines(method, mappingName + "KeyConverter", converter);
            method.addLine(mappingName + ".setKeyConverter(" + mappingName + "KeyConverter" + ");");
        }
    }

    protected void addFetchGroupManagerLine(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        if (descriptor.getFetchGroupManager() == null) {
            return;
        }
        method.addLine("//Fetch groups");
        method.addLine("descriptor.setFetchGroupManager(new FetchGroupManager());");

        Map namedFetchGroups = descriptor.getFetchGroupManager().getFetchGroups();

        if (descriptor.getFetchGroupManager().getDefaultFetchGroup() != null) {
            String defaultFetchGroupIdentifier = descriptor.getFetchGroupManager().getDefaultFetchGroup().getName() + "FetchGroup";
            method.addLine("");
            method.addLine("//Default fetch group -- " + defaultFetchGroupIdentifier);
            method.addLine("descriptor.getFetchGroupManager().setDefaultFetchGroup(" + defaultFetchGroupIdentifier + ");");
        }

        if (namedFetchGroups.isEmpty()) {
            return;
        }
        for (Iterator namedFetchGroupIter = namedFetchGroups.values().iterator();
                 namedFetchGroupIter.hasNext();) {
            FetchGroup namedFetchGroup = (FetchGroup)namedFetchGroupIter.next();
            String fetchGroupIdentifier = namedFetchGroup.getName() + "FetchGroup";
            method.addLine("");
            method.addLine("//Named fetch group -- " + fetchGroupIdentifier);
            addFetchGroupLines(method, namedFetchGroup, fetchGroupIdentifier);
            method.addLine("descriptor.getFetchGroupManager().addFetchGroup(" + fetchGroupIdentifier + ");");
        }
    }

    // TODO-dclarke: Needs to be enhanced to handle nested FetchGroup and FetchItem properties
    // OR throw an exception when a nested FetchGroup is encountered since the MW does not
    // supported nested FetchGroup and FetchItem properties
    protected void addFetchGroupLines(NonreflectiveMethodDefinition method, FetchGroup fetchGroup, String fetchGroupIdentifier) {
        method.addLine("FetchGroup " + fetchGroupIdentifier + " = new FetchGroup();");
        if (!fetchGroup.getName().equals("")) {
            method.addLine(fetchGroupIdentifier + ".setName(\"" + fetchGroup.getName() + "\");");
        }
        for (String attribute: fetchGroup.getAttributeNames()) {
            method.addLine(fetchGroupIdentifier + ".addAttribute(\"" + attribute + "\");");
        }
    }

    protected void addEventManagerPropertyLines(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        method.addLine("// Event Manager.");

        // Method events
        if (descriptor.getEventManager().getAboutToInsertSelector() != null) {
            method.addLine("descriptor.getEventManager().setAboutToInsertSelector(\"" + descriptor.getEventManager().getAboutToInsertSelector() + "\");");
        }
        if (descriptor.getEventManager().getAboutToUpdateSelector() != null) {
            method.addLine("descriptor.getEventManager().setAboutToUpdateSelector(\"" + descriptor.getEventManager().getAboutToUpdateSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostBuildSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostBuildSelector(\"" + descriptor.getEventManager().getPostBuildSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostCloneSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostCloneSelector(\"" + descriptor.getEventManager().getPostCloneSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostDeleteSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostDeleteSelector(\"" + descriptor.getEventManager().getPostDeleteSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostInsertSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostInsertSelector(\"" + descriptor.getEventManager().getPostInsertSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostMergeSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostMergeSelector(\"" + descriptor.getEventManager().getPostMergeSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostRefreshSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostRefreshSelector(\"" + descriptor.getEventManager().getPostRefreshSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostUpdateSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostUpdateSelector(\"" + descriptor.getEventManager().getPostUpdateSelector() + "\");");
        }
        if (descriptor.getEventManager().getPostWriteSelector() != null) {
            method.addLine("descriptor.getEventManager().setPostWriteSelector(\"" + descriptor.getEventManager().getPostWriteSelector() + "\");");
        }
        if (descriptor.getEventManager().getPreDeleteSelector() != null) {
            method.addLine("descriptor.getEventManager().setPreDeleteSelector(\"" + descriptor.getEventManager().getPreDeleteSelector() + "\");");
        }
        if (descriptor.getEventManager().getPreInsertSelector() != null) {
            method.addLine("descriptor.getEventManager().setPreInsertSelector(\"" + descriptor.getEventManager().getPreInsertSelector() + "\");");
        }
        if (descriptor.getEventManager().getPreUpdateSelector() != null) {
            method.addLine("descriptor.getEventManager().setPreUpdateSelector(\"" + descriptor.getEventManager().getPreUpdateSelector() + "\");");
        }
        if (descriptor.getEventManager().getPreWriteSelector() != null) {
            method.addLine("descriptor.getEventManager().setPreWriteSelector(\"" + descriptor.getEventManager().getPreWriteSelector() + "\");");
        }
    }

    protected void addForeignReferenceMappingLines(NonreflectiveMethodDefinition method, String mappingName, ForeignReferenceMapping mapping) {
        if (mapping.getReferenceClassName() != null) {
            method.addLine(mappingName + ".setReferenceClass(" + mapping.getReferenceClassName() + ".class);");
        }

        if (mapping.getRelationshipPartnerAttributeName() != null) {
            method.addLine(mappingName + ".setRelationshipPartnerAttributeName(\"" + mapping.getRelationshipPartnerAttributeName() + "\");");
        }

        IndirectionPolicy policy = mapping.getIndirectionPolicy();
        if (policy instanceof ContainerIndirectionPolicy) {
            String containerClassName = ((ContainerIndirectionPolicy)policy).getContainerClassName();
            method.addLine(mappingName + ".useContainerIndirection(" + containerClassName + ".class);");
        //Bug#4251902 used in ObjectReferenceMapping
        } else if (policy instanceof ProxyIndirectionPolicy) {
            method.addLine(mappingName + ".useProxyIndirection();");
        } else if (policy instanceof BasicIndirectionPolicy) {
            method.addLine(mappingName + ".useBasicIndirection();");
        } else if (policy instanceof NoIndirectionPolicy) {
            method.addLine(mappingName + ".dontUseIndirection();");
        }

        if (mapping.shouldUseBatchReading()) {
            method.addLine(mappingName + ".useBatchReading();");
        }
        if (mapping.isJoinFetched()) {
            if (mapping.isInnerJoinFetched()) {
                method.addLine(mappingName + ".useInnerJoinFetch();");
            } else if (mapping.isOuterJoinFetched()) {
                method.addLine(mappingName + ".useOuterJoinFetch();");                
            }
        }

        if ((!mapping.isDirectCollectionMapping()) && mapping.isPrivateOwned()) {
            method.addLine(mappingName + ".privateOwnedRelationship();");
        }

        if (mapping.isCollectionMapping()) {
            CollectionMapping collectionMapping = (CollectionMapping)mapping;
            String collectionClassName = collectionMapping.getContainerPolicy().getContainerClassName();
            if (mapping.getContainerPolicy().isCollectionPolicy()) {
                if (policy instanceof TransparentIndirectionPolicy) {
                    method.addLine(mappingName + ".useTransparentCollection();");
                }
                if (!collectionClassName.equals(Vector.class.getName())) {
                    method.addLine(mappingName + ".useCollectionClass(" + collectionClassName + ".class);");
                }
            } else if (collectionMapping.isDirectMapMapping()) {
                if (policy instanceof TransparentIndirectionPolicy) {
                    method.addLine(mappingName + ".useTransparentMap();");
                    if (!collectionClassName.equals(IndirectMap.class.getName())) {
                        method.addLine(mappingName + ".useMapClass(" + collectionClassName + ".class);");
                    }
                } else {
                    method.addLine(mappingName + ".useMapClass(" + collectionClassName + ".class);");
                }
            } else if (collectionMapping.getContainerPolicy().isMapPolicy()) {
                String keyMethodName = ((org.eclipse.persistence.internal.queries.MapContainerPolicy)collectionMapping.getContainerPolicy()).getKeyName();
                if (policy instanceof TransparentIndirectionPolicy) {
                    method.addLine(mappingName + ".useTransparentMap(\"" + keyMethodName + "\");");
                    if (!collectionClassName.equals(IndirectMap.class.getName())) {
                        method.addLine(mappingName + ".useMapClass(" + collectionClassName + ".class, \"" + keyMethodName + "\");");
                    }
                } else {
                    method.addLine(mappingName + ".useMapClass(" + collectionClassName + ".class, \"" + keyMethodName + "\");");
                }
            }

            // Ordering.
            Iterator queryKeyExpressions = collectionMapping.getOrderByQueryKeyExpressions().iterator();
            while (queryKeyExpressions.hasNext()) {
                FunctionExpression expression = (FunctionExpression) queryKeyExpressions.next();
                String queryKeyName = expression.getBaseExpression().getName();
                
                if (expression.getOperator().getSelector() == ExpressionOperator.Descending) {
                    method.addLine(mappingName + ".addDescendingOrdering(\"" + queryKeyName + "\");");    
                } else {
                    method.addLine(mappingName + ".addAscendingOrdering(\"" + queryKeyName + "\");");    
                }
            }
        }
    }

    protected void addHistoryPolicyLines(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        if (descriptor.getHistoryPolicy() == null) {
            return;
        }
        addHistoryPolicyLines(method, descriptor.getHistoryPolicy(), "historyPolicy");
        method.addLine("descriptor.setHistoryPolicy(historyPolicy);");
    }

    protected void addHistoryPolicyLines(NonreflectiveMethodDefinition method, DirectCollectionMapping mapping, String mappingName) {
        if (mapping.getHistoryPolicy() == null) {
            return;
        }
        String policyName = mapping.getAttributeName() + "HistoryPolicy";
        addHistoryPolicyLines(method, mapping.getHistoryPolicy(), policyName);
        method.addLine(mappingName + ".setHistoryPolicy(" + policyName + ");");
    }

    protected void addHistoryPolicyLines(NonreflectiveMethodDefinition method, ManyToManyMapping mapping, String mappingName) {
        if (mapping.getHistoryPolicy() == null) {
            return;
        }
        String policyName = mapping.getAttributeName() + "HistoryPolicy";
        addHistoryPolicyLines(method, mapping.getHistoryPolicy(), policyName);
        method.addLine(mappingName + ".setHistoryPolicy(" + policyName + ");");
    }

    protected void addHistoryPolicyLines(NonreflectiveMethodDefinition method, HistoryPolicy policy, String policyName) {
        method.addLine("");
        method.addLine("// History Policy");

        method.addLine("HistoryPolicy " + policyName + " = new HistoryPolicy();");
        for (DatabaseTable table : policy.getHistoricalTables()) {
            String sourceName = null;
            if (table.getTableQualifier().equals("")) {
                sourceName = table.getName();
            } else {
                sourceName = table.getTableQualifier() + "." + table.getName();
            }
            String historyName = table.getQualifiedName();
            method.addLine(policyName + ".addHistoryTableName(\"" + sourceName + "\", \"" + historyName + "\");");
        }
        for (DatabaseField field : policy.getStartFields()) {
            method.addLine(policyName + ".addStartFieldName(\"" + field.getQualifiedName() + "\");");

            // Field classifications don't seem to be supported in workbench integration.
            //method.addLine(policyName + ".setStartFieldType(\"" + field.getQualifiedName() + "\", " + field.getType().getName() + ".class);");
        }
        for (DatabaseField field : policy.getEndFields()) {
            method.addLine(policyName + ".addEndFieldName(\"" + field.getQualifiedName() + "\");");
            //method.addLine(policyName + ".setEndFieldType(\"" + field.getQualifiedName() + "\", " + field.getType().getName() + ".class);");
        }
        method.addLine(policyName + ".setShouldHandleWrites(" + (policy.shouldHandleWrites() ? "true" : "false") + ");");
        if (policy.shouldUseLocalTime()) {
            method.addLine(policyName + ".useLocalTime();");
        } else {
            method.addLine(policyName + ".useDatabaseTime();");
        }
    }

    protected void addInheritanceLines(NonreflectiveMethodDefinition method, InheritancePolicy policy) {
        method.addLine("// Inheritance Properties.");
        if (policy.isChildDescriptor()) {
            method.addLine("descriptor.getInheritancePolicy().setParentClass(" + policy.getParentClassName() + ".class);");
        } else {
            if (policy.getClassExtractionMethodName() != null) {
                method.addLine("descriptor.getInheritancePolicy().setClassExtractionMethodName(\"" + policy.getClassExtractionMethodName() + "\");");
            } else if (policy.getClassIndicatorField() != null) {
                method.addLine("descriptor.getInheritancePolicy().setClassIndicatorFieldName(\"" + policy.getClassIndicatorField().getQualifiedName() + "\");");

                if (policy.shouldUseClassNameAsIndicator()) {
                    method.addLine("descriptor.getInheritancePolicy().useClassNameAsIndicator();");
                } else {
                    for (Iterator indicatorsEnum = policy.getClassNameIndicatorMapping().keySet().iterator();
                             indicatorsEnum.hasNext();) {
                        String className = (String)indicatorsEnum.next();
                        Object value = policy.getClassNameIndicatorMapping().get(className);
                        method.addLine("descriptor.getInheritancePolicy().addClassIndicator(" + className + ".class, " + printString(value) + ");");
                    }
                }
            }
        }
        // Subclasses view
        if (policy.getReadAllSubclassesView() != null) {
            method.addLine("descriptor.getInheritancePolicy().setReadAllSubclassesViewName(\"" + policy.getReadAllSubclassesViewName() + "\");");
        }
        // Join-subclasses
        if (policy.shouldOuterJoinSubclasses()) {
            method.addLine("descriptor.getInheritancePolicy().setShouldOuterJoinSubclasses(true);");
        }

        if (!policy.shouldReadSubclasses()) {
            method.addLine("descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();");
        }
    }

    protected void addInterfaceLines(NonreflectiveMethodDefinition method, InterfacePolicy policy) {
        method.addLine("// Interface Properties.");
        if (policy.isInterfaceChildDescriptor()) {
            for (Enumeration interfacesEnum = policy.getParentInterfaceNames().elements();
                     interfacesEnum.hasMoreElements();) {
                String parentInterfaceName = (String)interfacesEnum.nextElement();
                method.addLine("descriptor.getInterfacePolicy().addParentInterface(" + parentInterfaceName + ".class);");
            }
        }
    }

    protected void addManyToManyMappingLines(NonreflectiveMethodDefinition method, String mappingName, ManyToManyMapping mapping) {
        if (mapping.getRelationTable() != null) {
            method.addLine(mappingName + ".setRelationTableName(\"" + mapping.getRelationTable().getQualifiedName() + "\");");
        }

        Enumeration sourceRelationKeysEnum = mapping.getSourceRelationKeyFields().elements();
        Enumeration sourceKeysEnum = mapping.getSourceKeyFields().elements();
        while (sourceRelationKeysEnum.hasMoreElements()) {
            DatabaseField sourceField = (DatabaseField)sourceKeysEnum.nextElement();
            DatabaseField relationField = (DatabaseField)sourceRelationKeysEnum.nextElement();
            method.addLine(mappingName + ".addSourceRelationKeyFieldName(\"" + relationField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
        }

        Enumeration targetRelationKeysEnum = mapping.getTargetRelationKeyFields().elements();
        Enumeration targetKeysEnum = mapping.getTargetKeyFields().elements();
        while (targetRelationKeysEnum.hasMoreElements()) {
            DatabaseField targetField = (DatabaseField)targetKeysEnum.nextElement();
            DatabaseField relationField = (DatabaseField)targetRelationKeysEnum.nextElement();
            method.addLine(mappingName + ".addTargetRelationKeyFieldName(\"" + relationField.getQualifiedName() + "\", \"" + targetField.getQualifiedName() + "\");");
        }

        addHistoryPolicyLines(method, mapping, mappingName);
    }

    protected void addMappingLines(NonreflectiveMethodDefinition method, DatabaseMapping mapping) {
        String mappingName = mapping.getAttributeName() + "Mapping";
        String mappingClassName = mapping.getClass().getName();
        String packageName = mappingClassName.substring(0, mappingClassName.lastIndexOf('.'));
        if (packageName.equals("org.eclipse.persistence.mappings")) {
            mappingClassName = Helper.getShortClassName(mapping);
        }
        method.addLine(mappingClassName + " " + mappingName + " = new " + mappingClassName + "();");
        if (!mapping.isWriteOnly()) {
            method.addLine(mappingName + ".setAttributeName(\"" + mapping.getAttributeName() + "\");");
            if (mapping.getGetMethodName() != null) {
                method.addLine(mappingName + ".setGetMethodName(\"" + mapping.getGetMethodName() + "\");");
            }
            if (mapping.getSetMethodName() != null) {
                method.addLine(mappingName + ".setSetMethodName(\"" + mapping.getSetMethodName() + "\");");
            }
        }
        if (mapping.isAbstractDirectMapping()) {
            AbstractDirectMapping directMapping = (AbstractDirectMapping)mapping;
            if (mapping.getDescriptor().isAggregateDescriptor()) {
                method.addLine(mappingName + ".setFieldName(\"" + directMapping.getField().getName() + "\");");
            } else {
                method.addLine(mappingName + ".setFieldName(\"" + directMapping.getField().getQualifiedName() + "\");");
            }
            if (directMapping.getNullValue() != null) {
                method.addLine(mappingName + ".setNullValue(" + printString(directMapping.getNullValue()) + ");");
            }
            Converter converter = directMapping.getConverter();
            if (converter != null) {
                addConverterLines(method, mappingName + "Converter", converter);
                method.addLine(mappingName + ".setConverter(" + mappingName + "Converter" + ");");
            }
        } else if (mapping.isForeignReferenceMapping()) {
            addForeignReferenceMappingLines(method, mappingName, (ForeignReferenceMapping)mapping);
        }

        if (mapping.isReadOnly()) {
            method.addLine(mappingName + ".readOnly();");
        }

        if (mapping.isAggregateObjectMapping()) {
            addAggregateObjectMappingLines(method, mappingName, (AggregateObjectMapping)mapping);
        } else if (mapping.isOneToOneMapping()) {
            addOneToOneMappingLines(method, mappingName, (OneToOneMapping)mapping);
        } else if (mapping.isOneToManyMapping()) {
            addOneToManyMappingLines(method, mappingName, (OneToManyMapping)mapping);
        } else if (mapping.isManyToManyMapping()) {
            addManyToManyMappingLines(method, mappingName, (ManyToManyMapping)mapping);
        } else if (mapping.isTransformationMapping()) {
            addTransformationMappingLines(method, mappingName, (TransformationMapping)mapping);
        } else if (mapping.isDirectCollectionMapping()) {
            addDirectCollectionMappingLines(method, mappingName, (DirectCollectionMapping)mapping);
        } else if (mapping.isAggregateCollectionMapping()) {
            addAggregateCollectionMappingLines(method, mappingName, (AggregateCollectionMapping)mapping);
        } else if (mapping.isVariableOneToOneMapping()) {
            addVariableOneToOneMappingLines(method, mappingName, (VariableOneToOneMapping)mapping);
        }

        if (mapping.isDirectMapMapping()) {
            addDirectMapMappingLines(method, mappingName, (DirectMapMapping)mapping);
        }
        if (mapping.isDirectToXMLTypeMapping()) {
            DirectToXMLTypeMappingHelper.getInstance().writeShouldreadWholeDocument(method, mappingName, mapping);
        }
        method.addLine("descriptor.addMapping(" + mapping.getAttributeName() + "Mapping);");
    }

    protected void addObjectTypeConverterLines(NonreflectiveMethodDefinition method, String converterName, ObjectTypeConverter converter) {
        if (converter.getDefaultAttributeValue() != null) {
            method.addLine(converterName + ".setDefaultAttributeValue(" + printString(converter.getDefaultAttributeValue()) + ");");
        }
        for (Iterator typesEnum = converter.getAttributeToFieldValues().keySet().iterator();
                 typesEnum.hasNext();) {
            Object attributeValue = typesEnum.next();
            Object fieldValue = converter.getAttributeToFieldValues().get(attributeValue);
            method.addLine(converterName + ".addConversionValue(" + printString(fieldValue) + ", " + printString(attributeValue) + ");");
        }

        // Read-only conversions.
        for (Iterator typesEnum = converter.getFieldToAttributeValues().keySet().iterator();
                 typesEnum.hasNext();) {
            Object fieldValue = typesEnum.next();
            Object attributeValue = converter.getFieldToAttributeValues().get(fieldValue);
            if (!converter.getAttributeToFieldValues().containsKey(attributeValue)) {
                method.addLine(converterName + ".addToAttributeOnlyConversionValue(" + printString(fieldValue) + ", " + printString(attributeValue) + ");");
            }
        }
    }

    protected void addOneToManyMappingLines(NonreflectiveMethodDefinition method, String mappingName, OneToManyMapping mapping) {
        Enumeration targetKeysEnum = mapping.getTargetForeignKeyFields().elements();
        Enumeration sourceKeysEnum = mapping.getSourceKeyFields().elements();
        while (sourceKeysEnum.hasMoreElements()) {
            DatabaseField sourceField = (DatabaseField)sourceKeysEnum.nextElement();
            DatabaseField targetField = (DatabaseField)targetKeysEnum.nextElement();
            method.addLine(mappingName + ".addTargetForeignKeyFieldName(\"" + targetField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
        }
    }

    protected void addOneToOneMappingLines(NonreflectiveMethodDefinition method, String mappingName, OneToOneMapping mapping) {
        for (Iterator foreignKeysEnum = mapping.getSourceToTargetKeyFields().keySet().iterator();
                 foreignKeysEnum.hasNext();) {
            DatabaseField sourceField = (DatabaseField)foreignKeysEnum.next();
            DatabaseField targetField = mapping.getSourceToTargetKeyFields().get(sourceField);
            if (mapping.getForeignKeyFields().contains(sourceField)) {
                method.addLine(mappingName + ".addForeignKeyFieldName(\"" + sourceField.getQualifiedName() + "\", \"" + targetField.getQualifiedName() + "\");");
            } else {
                method.addLine(mappingName + ".addTargetForeignKeyFieldName(\"" + targetField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
            }
        }
        if (!mapping.shouldVerifyDelete()) {
            method.addLine(mappingName + ".setShouldVerifyDelete(false);");
        }
    }

    protected void addOptimisticLockingLines(NonreflectiveMethodDefinition method, OptimisticLockingPolicy policy) {
        String policyClassName = policy.getClass().getName();
        String packageName = policyClassName.substring(0, policyClassName.lastIndexOf('.'));
        if (packageName.equals("org.eclipse.persistence.descriptors")) {
            policyClassName = Helper.getShortClassName(policy);
        }
        method.addLine(policyClassName + " lockingPolicy = new " + policyClassName + "();");

        if (policy instanceof SelectedFieldsLockingPolicy) {
            SelectedFieldsLockingPolicy fieldPolicy = (SelectedFieldsLockingPolicy)policy;
            for ( DatabaseField field : fieldPolicy.getLockFields()) {
                method.addLine("lockingPolicy.addLockFieldName(\"" + field.getQualifiedName() + "\");");
            }
        } else if (policy instanceof VersionLockingPolicy) {
            VersionLockingPolicy versionPolicy = (VersionLockingPolicy)policy;
            method.addLine("lockingPolicy.setWriteLockFieldName(\"" + versionPolicy.getWriteLockField().getQualifiedName() + "\");");
            if (versionPolicy.isStoredInObject()) {
                method.addLine("lockingPolicy.storeInObject();");
            }
            if (policy instanceof TimestampLockingPolicy) {
                TimestampLockingPolicy timestampPolicy = (TimestampLockingPolicy)policy;
                if (timestampPolicy.usesLocalTime()) {
                    method.addLine("lockingPolicy.useLocalTime();");
                }
            }
        }

        method.addLine("descriptor.setOptimisticLockingPolicy(lockingPolicy);");
    }

    protected void addQueryKeyLines(NonreflectiveMethodDefinition method, QueryKey queryKey) {
        if (queryKey.isDirectQueryKey()) {
            method.addLine("descriptor.addDirectQueryKey(\"" + queryKey.getName() + "\", \"" + ((DirectQueryKey)queryKey).getField().getQualifiedName() + "\");");
        } else if (queryKey.isAbstractQueryKey()) {
            method.addLine("descriptor.addAbstractQueryKey(\"" + queryKey.getName() + "\");");
        }
    }

    protected void addQueryManagerPropertyLines(NonreflectiveMethodDefinition method, ClassDescriptor descriptor) {
        method.addLine("// Query Manager.");
        // Existence check.
        if (!descriptor.isDescriptorTypeAggregate() && (!descriptor.isDescriptorForInterface())) {
            if (descriptor.getQueryManager().getDoesExistQuery().shouldAssumeExistenceForDoesExist()) {
                method.addLine("descriptor.getQueryManager().assumeExistenceForDoesExist();");
            } else if (descriptor.getQueryManager().getDoesExistQuery().shouldAssumeNonExistenceForDoesExist()) {
                method.addLine("descriptor.getQueryManager().assumeNonExistenceForDoesExist();");
            } else if (descriptor.getQueryManager().getDoesExistQuery().shouldCheckCacheForDoesExist()) {
                method.addLine("descriptor.getQueryManager().checkCacheForDoesExist();");
            } else if (descriptor.getQueryManager().getDoesExistQuery().shouldCheckDatabaseForDoesExist()) {
                method.addLine("descriptor.getQueryManager().checkDatabaseForDoesExist();");
            }
        }

        // Query timeout.
        if (descriptor.getQueryManager().getQueryTimeout() != DescriptorQueryManager.DefaultTimeout) {
            method.addLine("descriptor.getQueryManager().setQueryTimeout(" + descriptor.getQueryManager().getQueryTimeout() + ");");
        }

        // Custom SQL.
        if (descriptor.getQueryManager().hasReadObjectQuery() && descriptor.getQueryManager().getReadObjectQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setReadObjectSQLString(\"" 
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getReadObjectQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasReadObjectQuery() && descriptor.getQueryManager().getReadObjectQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getReadObjectQuery().getDatasourceCall(), "readObjectCall");
            method.addLine("descriptor.getQueryManager().setReadObjectCall(readObjectCall);");
        }
        if (descriptor.getQueryManager().hasReadAllQuery() && descriptor.getQueryManager().getReadAllQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setReadAllSQLString(\""
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getReadAllQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasReadAllQuery() && descriptor.getQueryManager().getReadAllQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getReadAllQuery().getDatasourceCall(), "readAllCall");
            method.addLine("descriptor.getQueryManager().setReadAllCall(readAllCall);");
        }
        if (descriptor.getQueryManager().hasInsertQuery() && descriptor.getQueryManager().getInsertQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setInsertSQLString(\""
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getInsertQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasInsertQuery() && descriptor.getQueryManager().getInsertQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getInsertQuery().getDatasourceCall(), "insertCall");
            method.addLine("descriptor.getQueryManager().setInsertCall(insertCall);");
        }
        if (descriptor.getQueryManager().hasUpdateQuery() && descriptor.getQueryManager().getUpdateQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setUpdateSQLString(\""
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getUpdateQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasUpdateQuery() && descriptor.getQueryManager().getUpdateQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getUpdateQuery().getDatasourceCall(), "updateCall");
            method.addLine("descriptor.getQueryManager().setUpdateCall(updateCall);");
        }
        if (descriptor.getQueryManager().hasDeleteQuery() && descriptor.getQueryManager().getDeleteQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setDeleteSQLString(\""
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getDeleteQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasDeleteQuery() && descriptor.getQueryManager().getUpdateQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getDeleteQuery().getDatasourceCall(), "deleteCall");
            method.addLine("descriptor.getQueryManager().setDeleteCall(deleteCall);");
        }
        if (descriptor.getQueryManager().hasDoesExistQuery() && descriptor.getQueryManager().getDoesExistQuery().isSQLCallQuery()) {
            method.addLine("descriptor.getQueryManager().setDoesExistSQLString(\""
              + constructValidSQLorEJBQLLinesForJavaSource(descriptor.getQueryManager().getDoesExistQuery().getSQLString()) + "\");");
        } else if (descriptor.getQueryManager().hasDoesExistQuery() && descriptor.getQueryManager().getDoesExistQuery().getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)descriptor.getQueryManager().getDoesExistQuery().getDatasourceCall(), "doesExistCall");
            method.addLine("descriptor.getQueryManager().setDoesExistCall(doesExistCall);");
        }

        // Named queries.
        if (descriptor.getQueryManager().getAllQueries().size() > 0) {
            method.addLine("// Named Queries.");
            Enumeration namedQueries = descriptor.getQueryManager().getAllQueries().elements();
            int iteration = 0;
            while (namedQueries.hasMoreElements()) {
                addNamedQueryLines(method, (DatabaseQuery)namedQueries.nextElement(), descriptor.getQueryManager(), iteration);
                ++iteration;
            }
        }
    }

    private String constructValidSQLorEJBQLLinesForJavaSource(String qlString){
    	//Bug2612384 Deals with the possibility of multi-line SQL statements.
    	//Expects beginning and closing quotes to be in place
    	String insertString = "\" " + Helper.cr() + '\t' + '\t' + "+ " + "\"";
		
    	if (qlString != null) {
    		qlString.trim();
    		//remove trailing carraige returns
    		while (qlString.endsWith("\n"))
    			qlString = qlString.substring(0, qlString.length() - 1);
			
    		qlString = qlString.replaceAll("\n",insertString);
    	}
    	return qlString;
    }

    protected void addXMLInteractionLines(NonreflectiveMethodDefinition method, XMLInteraction interaction, String variableName) {
        method.addLine("org.eclipse.persistence.eis.XMLInteraction " + variableName + " = new org.eclipse.persistence.eis.XMLInteraction();");
        if ((interaction.getFunctionName() != null) && (interaction.getFunctionName().length() != 0)) {
            method.addLine(variableName + ".setFunctionName(\"" + interaction.getFunctionName() + "\");");
        }
        if ((interaction.getInputRecordName() != null) && (interaction.getInputRecordName().length() != 0)) {
            method.addLine(variableName + ".setInputRecordName(\"" + interaction.getInputRecordName() + "\");");
        }
        if ((interaction.getInputRootElementName() != null) && (interaction.getInputRootElementName().length() != 0)) {
            method.addLine(variableName + ".setInputRootElementName(\"" + interaction.getInputRootElementName() + "\");");
        }
        if ((interaction.getInputResultPath() != null) && (interaction.getInputResultPath().length() != 0)) {
            method.addLine(variableName + ".setInputResultPath(\"" + interaction.getInputResultPath() + "\");");
        }
        if ((interaction.getOutputResultPath() != null) && (interaction.getOutputResultPath().length() != 0)) {
            method.addLine(variableName + ".setOutputResultPath(\"" + interaction.getOutputResultPath() + "\");");
        }
        for (int index = interaction.getArgumentNames().size();
                 index < interaction.getArgumentNames().size(); index++) {
            String argumentName = (String)interaction.getArgumentNames().get(index);
            String argument = (String)interaction.getArguments().get(index);
            method.addLine(variableName + ".addArgument(\"" + argumentName + "\", \"" + argument + "\");");
        }
    }

    protected void addNamedQueryLines(NonreflectiveMethodDefinition method, DatabaseQuery query, DescriptorQueryManager queryManager, int iteration) {
        String queryIdentifier = "namedQuery" + String.valueOf(iteration);
        method.addLine("// Named Query -- " + query.getName());
        String className = "";
        if (query.getDescriptor() != null) {
            className = query.getDescriptor().getJavaClassName();
        } else {
            //in default mapping, the query has not been associated with a descriptor before generation.
            className = query.getReferenceClassName();
        }
        if (query.isReportQuery()) {
            method.addLine("ReportQuery " + queryIdentifier + " = new ReportQuery(" + className + ".class, new ExpressionBuilder());");
        } else if (query.isReadAllQuery()) {
            method.addLine("ReadAllQuery " + queryIdentifier + " = new ReadAllQuery(" + className + ".class);");
        } else if (query.isReadObjectQuery()) {
            method.addLine("ReadObjectQuery " + queryIdentifier + " = new ReadObjectQuery(" + className + ".class);");
        } else if (query.isValueReadQuery()) {
            method.addLine("ValueReadQuery " + queryIdentifier + " = new ValueReadQuery();");
        } else if (query.isDataReadQuery()) {
            method.addLine("DataReadQuery " + queryIdentifier + " = new DataReadQuery();");
        } else if (query.isDirectReadQuery()) {
            method.addLine("DirectReadQuery " + queryIdentifier + " = new DirectReadQuery();");
        }
        if (query.getSQLString() != null) {
            method.addLine(queryIdentifier + ".setSQLString(\"" + constructValidSQLorEJBQLLinesForJavaSource(query.getSQLString()) + "\");");
        } else if (query.getEJBQLString() != null) {
            method.addLine(queryIdentifier + ".setEJBQLString(\"" + constructValidSQLorEJBQLLinesForJavaSource(query.getEJBQLString()) + "\");");
        } else if (query.getDatasourceCall() instanceof XMLInteraction) {
            addXMLInteractionLines(method, (XMLInteraction)query.getDatasourceCall(), queryIdentifier + "Call");
            method.addLine(queryIdentifier + ".setCall(" + queryIdentifier + "Call" + ");");
        }

        if ((query.getRedirector() != null) && (query.getRedirector().getClass().equals(MethodBaseQueryRedirector.class))) {
            method.addLine(queryIdentifier + ".setRedirector(new MethodBaseQueryRedirector(" + ((MethodBaseQueryRedirector)query.getRedirector()).getMethodClassName() + ".class, \"" + ((MethodBaseQueryRedirector)query.getRedirector()).getMethodName() + "\"));");
        }
        method.addLine(queryIdentifier + ".setName(\"" + query.getName() + "\");");
        if ((!query.isReadQuery()) || (query.getCascadePolicy() != DatabaseQuery.NoCascading)) {
            String cascadePolicy = "DatabaseQuery.NoCascading";
            if (query.getCascadePolicy() == DatabaseQuery.CascadePrivateParts) {
                cascadePolicy = "DatabaseQuery.CascadePrivateParts";
            } else if (query.getCascadePolicy() == DatabaseQuery.CascadeDependentParts) {
                cascadePolicy = "DatabaseQuery.CascadeDependentParts";
            } else if (query.getCascadePolicy() == DatabaseQuery.CascadeAllParts) {
                cascadePolicy = "DatabaseQuery.CascadeAllParts";
            }
            method.addLine(queryIdentifier + ".setCascadePolicy(" + cascadePolicy + ");");
        }
        if (query.getQueryTimeout() != DescriptorQueryManager.DefaultTimeout) {
            method.addLine(queryIdentifier + ".setQueryTimeout(" + String.valueOf(query.getQueryTimeout()) + ");");
        }
        if (!query.shouldUseWrapperPolicy()) {
            method.addLine(queryIdentifier + ".setShouldUseWrapperPolicy(" + String.valueOf(query.shouldUseWrapperPolicy()) + ");");
        }
        if (!query.shouldIgnoreBindAllParameters()) {
            method.addLine(queryIdentifier + ".setShouldBindAllParameters(" + String.valueOf(query.shouldBindAllParameters()) + ");");
        }
        if (!query.shouldIgnoreCacheStatement()) {
            method.addLine(queryIdentifier + ".setShouldCacheStatement(" + String.valueOf(query.shouldCacheStatement()) + ");");
        }
        if (query.getSessionName() != null) {
            method.addLine(queryIdentifier + ".setSessionName(\"" + query.getSessionName() + "\");");
        }
        if (!query.shouldMaintainCache()) {
            method.addLine(queryIdentifier + ".setShouldMaintainCache(" + String.valueOf(query.shouldMaintainCache()) + ");");
        }
        if (!query.shouldPrepare()) {
            method.addLine(queryIdentifier + ".setShouldPrepare(" + String.valueOf(query.shouldPrepare()) + ");");
        }

        if (query.isReadQuery()) {
            ReadQuery readQuery = (ReadQuery)query;
            if (readQuery.shouldCacheQueryResults()) {
                method.addLine(queryIdentifier + ".setQueryResultsCachePolicy(new QueryResultsCachePolicy());");
            }
            if (readQuery.getMaxRows() != 0) {
                method.addLine(queryIdentifier + ".setMaxRows(" + String.valueOf(readQuery.getMaxRows()) + ");");
            }
            if (readQuery.getFirstResult() != 0) {
                method.addLine(queryIdentifier + ".setFirstResult(" + String.valueOf(readQuery.getFirstResult()) + ");");
            }
        }
        
        //ExpressionBuilder string
        String builderString = null;

        if (query.isObjectLevelReadQuery()) {
            ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)query;

            // Refresh.
            if (readQuery.shouldRefreshIdentityMapResult()) {
                method.addLine(queryIdentifier + ".setShouldRefreshIdentityMapResult(" + String.valueOf(readQuery.shouldRefreshIdentityMapResult()) + ");");
            }

            // Cache usage.
            if (readQuery.getCacheUsage() != ObjectLevelReadQuery.UseDescriptorSetting) {
                String cacheUsage = "ObjectLevelReadQuery.UseDescriptorSetting";
                if (readQuery.getCacheUsage() == ObjectLevelReadQuery.DoNotCheckCache) {
                    cacheUsage = "ObjectLevelReadQuery.DoNotCheckCache";
                } else if (readQuery.getCacheUsage() == ObjectLevelReadQuery.CheckCacheByExactPrimaryKey) {
                    cacheUsage = "ObjectLevelReadQuery.CheckCacheByExactPrimaryKey";
                } else if (readQuery.getCacheUsage() == ObjectLevelReadQuery.CheckCacheByPrimaryKey) {
                    cacheUsage = "ObjectLevelReadQuery.CheckCacheByPrimaryKey";
                } else if (readQuery.getCacheUsage() == ObjectLevelReadQuery.CheckCacheThenDatabase) {
                    cacheUsage = "ObjectLevelReadQuery.CheckCacheThenDatabase";
                } else if (readQuery.getCacheUsage() == ObjectLevelReadQuery.CheckCacheOnly) {
                    cacheUsage = "ObjectLevelReadQuery.CheckCacheOnly";
                } else if (readQuery.getCacheUsage() == ObjectLevelReadQuery.ConformResultsInUnitOfWork) {
                    cacheUsage = "ObjectLevelReadQuery.ConformResultsInUnitOfWork";
                }
                method.addLine(queryIdentifier + ".setCacheUsage(" + cacheUsage + ");");
            }

            // Lock mode.
            if (readQuery.getLockMode() != ObjectLevelReadQuery.DEFAULT_LOCK_MODE) {
                String lockMode = null;
                if (readQuery.getLockMode() == ObjectLevelReadQuery.NO_LOCK && !readQuery.isReportQuery()) {
                    lockMode = "ObjectLevelReadQuery.NO_LOCK";
                } else if (readQuery.getLockMode() == ObjectLevelReadQuery.LOCK) {
                    lockMode = "ObjectLevelReadQuery.LOCK";
                } else if (readQuery.getLockMode() == ObjectLevelReadQuery.LOCK_NOWAIT) {
                    lockMode = "ObjectLevelReadQuery.LOCK_NOWAIT";
                }
                if (lockMode != null) {
                    method.addLine(queryIdentifier + ".setLockMode(" + lockMode + ");");
                }
            }

            // Remote refresh.
            if (readQuery.shouldRefreshRemoteIdentityMapResult()) {
                method.addLine(queryIdentifier + ".setShouldRefreshRemoteIdentityMapResult(" + String.valueOf(readQuery.shouldRefreshRemoteIdentityMapResult()) + ");");
            }

            // Distinct state.
            if (readQuery.getDistinctState() != ObjectLevelReadQuery.UNCOMPUTED_DISTINCT) {
                String distinctState = "ObjectLevelReadQuery.UNCOMPUTED_DISTINCT";
                if (readQuery.getDistinctState() == ObjectLevelReadQuery.USE_DISTINCT) {
                    distinctState = "ObjectLevelReadQuery.USE_DISTINCT";
                } else if (readQuery.getDistinctState() == ObjectLevelReadQuery.DONT_USE_DISTINCT) {
                    distinctState = "ObjectLevelReadQuery.DONT_USE_DISTINCT";
                }
                method.addLine(queryIdentifier + ".setDistinctState((short)" + distinctState + ");");
            }

            // In-memory policy.
            if (readQuery.getInMemoryQueryIndirectionPolicy().getPolicy() != InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION) {
                String inMemoryQueryIndirectionPolicy = "InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION";
                if (readQuery.getInMemoryQueryIndirectionPolicy().getPolicy() == InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION) {
                    inMemoryQueryIndirectionPolicy = "InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION";
                } else if (readQuery.getInMemoryQueryIndirectionPolicy().getPolicy() == InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED) {
                    inMemoryQueryIndirectionPolicy = "InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED";
                } else if (readQuery.getInMemoryQueryIndirectionPolicy().getPolicy() == InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED) {
                    inMemoryQueryIndirectionPolicy = "InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED";
                }
                method.addLine(queryIdentifier + ".setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(" + inMemoryQueryIndirectionPolicy + "));");
            }

            // Fetch groups.
            if (!readQuery.shouldUseDefaultFetchGroup()) {
                method.addLine(queryIdentifier + ".setShouldUseDefaultFetchGroup(false);");
            }
            if (readQuery.getFetchGroupName() != null) {
                method.addLine(queryIdentifier + ".setFetchGroupName(\"" + readQuery.getFetchGroupName() + "\");");
            } else if (readQuery.getFetchGroup() != null) {
                String fetchGroupIdentifier = readQuery.getFetchGroup().getName() + "FetchGroup";
                addFetchGroupLines(method, readQuery.getFetchGroup(), fetchGroupIdentifier);
                method.addLine(queryIdentifier + ".setFetchGroup(" + fetchGroupIdentifier + ");");
            }

            // Exclusive Connection (VPD).
            if (readQuery.shouldUseExclusiveConnection()) {
                method.addLine(queryIdentifier + ".setShouldUseExclusiveConnection(true);");
            }

            // Read-only
            if (readQuery.isReadOnly()) {
                method.addLine(queryIdentifier + ".setIsReadOnly(true);");
            }

            // Join-subclasses
            if (readQuery.shouldOuterJoinSubclasses()) {
                method.addLine(queryIdentifier + ".setShouldOuterJoinSubclasses(true);");
            }

            // Selection criteria.
            if (readQuery.getSelectionCriteria() != null) {
                builderString = buildBuilderString(builderString, method, iteration, queryIdentifier);
                buildExpressionString(builderString, method, queryIdentifier, readQuery.getSelectionCriteria(), ".setSelectionCriteria(");
            }
            
            //joinedAttribute
            for (Iterator joinedEnum = readQuery.getJoinedAttributeManager().getJoinedAttributeExpressions().iterator(); joinedEnum.hasNext();) {
                Expression joinedExp = (Expression)joinedEnum.next();
                builderString = buildBuilderString(builderString, method, iteration, queryIdentifier);
                buildExpressionString(builderString, method, queryIdentifier, joinedExp, ".addJoinedAttribute(");
            }
        }

        //ReadAllQuery
        if (query.isReadAllQuery()) {
            ReadAllQuery readAllQuery = (ReadAllQuery)query;
            //orderBy
            for (Expression orderbyExpression : readAllQuery.getOrderByExpressions()) {
                builderString = buildBuilderString(builderString, method, iteration, queryIdentifier);
                buildExpressionString(builderString, method, queryIdentifier, orderbyExpression, ".addOrdering(");
            }
            //batchReadAttribute
            for (Expression batchReadExp : readAllQuery.getBatchReadAttributeExpressions()) {
                builderString = buildBuilderString(builderString, method, iteration, queryIdentifier);
                buildExpressionString(builderString, method, queryIdentifier, batchReadExp, ".addBatchReadAttribute(");
            }
            //resultCollection
            if (readAllQuery.getContainerPolicy().isCursoredStreamPolicy()) {
                method.addLine(queryIdentifier + ".useCursoredStream();");                
            }
            if (readAllQuery.getContainerPolicy().isScrollableCursorPolicy()) {
                method.addLine(queryIdentifier + ".useScrollableCursor();");                
            }
            if (readAllQuery.getContainerPolicy().isCollectionPolicy()) {
                String collectionClass = readAllQuery.getContainerPolicy().getContainerClassName();
                if (!collectionClass.equals("java.util.Vector")) {
                    method.addLine(queryIdentifier + ".useCollectionClass(" + collectionClass + ".class);");                
                }
            }
        }

        //ReportQuery
        if (query.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)query;
            //ExpressionBuilder string
            builderString = buildBuilderString(builderString, method, iteration, queryIdentifier);
            //ReportItems
            for (ReportItem item : reportQuery.getItems()) {
                Expression expression = item.getAttributeExpression();
                String itemName = item.getName();
                StringWriter writer = new StringWriter();
                ExpressionJavaPrinter javaPrinter = new ExpressionJavaPrinter(builderString, writer, project.getDatasourceLogin().getPlatform());
                if (expression != null) {
                    String functionString;
                    Expression baseExpression;  //used in ReportQuery API, e.g. addCount(itemName, baseExpression)
                    String databaseString = null;  //used in addFunctionItem()
                    if (expression.isQueryKeyExpression()) {
                        functionString = ".addAttribute(\"";
                        baseExpression = expression;
                    } else if (expression.isFunctionExpression()) {
                        int selector = expression.getOperator().getSelector();
                        baseExpression = ((FunctionExpression)expression).getBaseExpression();
                        if (selector == ExpressionOperator.Average) {
                            functionString = ".addAverage(\"";
                        } else if (selector == ExpressionOperator.Count) {
                            functionString = ".addCount(\"";
                        } else if (selector == ExpressionOperator.Maximum) {
                            functionString = ".addMaximum(\"";
                        } else if (selector == ExpressionOperator.Minimum) {
                            functionString = ".addMinimum(\"";
                        } else if (selector == ExpressionOperator.StandardDeviation) {
                            functionString = ".addStandardDeviation(\"";
                        } else if (selector == ExpressionOperator.Sum) {
                            functionString = ".addSum(\"";
                        } else if (selector == ExpressionOperator.Variance) {
                            functionString = ".addVariance(\"";
                        } else { //custom function
                            functionString = ".addFunctionItem(\"";
                            databaseString = expression.getOperator().getDatabaseStrings()[0];
                            databaseString = databaseString.substring(0, databaseString.length()-1);
                        }
                    } else {
                        functionString = ".addItem(\"";
                        baseExpression = expression;
                    }
                    baseExpression.printJava(javaPrinter);                                    
                    if (databaseString == null) { //e.g. addCount(itemName, attributeExpression)
                        method.addLine(queryIdentifier + functionString + itemName + "\", " + writer.toString() + ");");                                                                    
                    } else { //i.e. addFunctionItem(itemName, attributeExpression, functionName) only
                        method.addLine(queryIdentifier + functionString + itemName + "\", " + writer.toString() + ", \"" + databaseString + "\");");                                                                    
                    }
                }
            }
            //groupBy
            for (Expression groupByExp : reportQuery.getGroupByExpressions()) {
                buildExpressionString(builderString, method, queryIdentifier, groupByExp, ".addGrouping(");
            }
            //shouldRetrievePrimaryKeys
            if (reportQuery.shouldRetrieveFirstPrimaryKey()) {
                method.addLine(queryIdentifier + ".setShouldRetrieveFirstPrimaryKey(true);");                
            } else if (reportQuery.shouldRetrievePrimaryKeys()) {
                method.addLine(queryIdentifier + ".setShouldRetrievePrimaryKeys(true);");                
            }
            //returnChoice
            if (reportQuery.shouldReturnSingleAttribute()) {
                method.addLine(queryIdentifier + ".setShouldReturnSingleAttribute(true);");                
            } else if (reportQuery.shouldReturnSingleValue()) {
                method.addLine(queryIdentifier + ".setShouldReturnSingleValue(true);");                
            } else {
                method.addLine(queryIdentifier + ".setShouldReturnSingleResult(true);");                
            }
        }
        
        // Query arguments.
        Iterator<String> argumentTypes = query.getArgumentTypeNames().iterator();
        for (Iterator arguments = query.getArguments().iterator(); arguments.hasNext();) {
            String argument = (String)arguments.next();
            String argumentTypeName = argumentTypes.next();
            method.addLine(queryIdentifier + ".addArgument(\"" + argument + "\", " + argumentTypeName + ".class);");
        }

        method.addLine("descriptor.getQueryManager().addQuery(\"" + query.getName() + "\", " + queryIdentifier + ");");
        method.addLine("");
    }

    //Build ExpressionBuilder string
    protected String buildBuilderString(String builderString, NonreflectiveMethodDefinition method, int iteration, String queryIdentifier) {
        if (builderString == null) {
            builderString = "expBuilder" + String.valueOf(iteration);
            method.addLine("ExpressionBuilder " + builderString + " = " + queryIdentifier + ".getExpressionBuilder();");
        }        
        return builderString;
    }

    //Build expression string for orderBy, batchRead, joined, selectionCriteria and groupBy. e.g. reportQuery1.addGrouping(exp)
    protected void buildExpressionString(String builderString, NonreflectiveMethodDefinition method, String queryIdentifier, Expression exp, String attrString) {
        StringWriter writer = new StringWriter();
        ExpressionJavaPrinter javaPrinter = new ExpressionJavaPrinter(builderString, writer, project.getDatasourceLogin().getPlatform());
        exp.printJava(javaPrinter);
        method.addLine(queryIdentifier + attrString + writer.toString() + ");");             
    }
    
    protected void addReturningPolicyLines(NonreflectiveMethodDefinition method, ReturningPolicy policy) {
        if (policy.getFieldInfos().isEmpty()) {
            return;
        }
        addReturningPolicyLines(method, policy, "returningPolicy");
        method.addLine("descriptor.setReturningPolicy(returningPolicy);");
    }

    protected void addReturningPolicyLines(NonreflectiveMethodDefinition method, ReturningPolicy policy, String policyName) {
        method.addLine("");
        method.addLine("// Returning Policy");

        method.addLine("ReturningPolicy " + policyName + " = new ReturningPolicy();");
        String prefix = policyName + ".addFieldFor";
        String[] str = { null, null };
        for (ReturningPolicy.Info info : policy.getFieldInfos()) {
            String qualifiedFieldName = info.getField().getQualifiedName();
            String type = null;
            if (info.getField().getType() != null) {
                type = info.getField().getType().getName() + ".class";
            }
            if (info.isInsert()) {
                String strInsert = prefix + "Insert";
                if (info.isInsertModeReturnOnly()) {
                    strInsert = strInsert + "ReturnOnly";
                }
                str[0] = strInsert;
            }
            if (info.isUpdate()) {
                String strUpdate = prefix + "Update";
                str[1] = strUpdate;
            }
            for (int i = 0; i < 2; i++) {
                if (str[i] != null) {
                    str[i] = str[i] + "(\"" + qualifiedFieldName + "\"";
                    if (type != null) {
                        str[i] = str[i] + ", " + type;
                    }
                    str[i] = str[i] + ");";
                    method.addLine(str[i]);

                    str[i] = null;
                }
            }
        }
    }

    protected void addTransformationMappingLines(NonreflectiveMethodDefinition method, String mappingName, TransformationMapping mapping) {
        if (!mapping.isWriteOnly()) {
            if (mapping.getAttributeMethodName() != null) {
                method.addLine(mappingName + ".setAttributeTransformation(\"" + mapping.getAttributeMethodName() + "\");");
            } else {
                method.addLine(mappingName + ".setAttributeTransformer(new " + mapping.getAttributeTransformerClassName() + "());");
            }
        }

        Iterator fieldTransformations = mapping.getFieldTransformations().iterator();
        while (fieldTransformations.hasNext()) {
            FieldTransformation trans = (FieldTransformation)fieldTransformations.next();
            String fieldName = trans.getFieldName();
            if (trans instanceof MethodBasedFieldTransformation) {
                String methodName = ((MethodBasedFieldTransformation)trans).getMethodName();
                method.addLine(mappingName + ".addFieldTransformation(\"" + fieldName + "\", \"" + methodName + "\");");
            } else {
                String transformerClass = ((TransformerBasedFieldTransformation)trans).getTransformerClassName();
                method.addLine(mappingName + ".addFieldTransformer(\"" + fieldName + "\", new " + transformerClass + "());");
            }
        }

        IndirectionPolicy policy = mapping.getIndirectionPolicy();
        if (policy instanceof ContainerIndirectionPolicy) {
            String containerClassName = ((ContainerIndirectionPolicy)policy).getContainerClassName();
            method.addLine(mappingName + ".useContainerIndirection(" + containerClassName + ".class);");
        } else if (policy instanceof BasicIndirectionPolicy) {
            method.addLine(mappingName + ".useBasicIndirection();");
        }
        method.addLine(mappingName + ".setIsMutable(" + mapping.isMutable() + ");");
    }

    protected void addConverterLines(NonreflectiveMethodDefinition method, String converterName, Converter converter) {
        if (converter instanceof ObjectTypeConverter) {
            method.addLine("ObjectTypeConverter " + converterName + " = new ObjectTypeConverter();");
            addObjectTypeConverterLines(method, converterName, (ObjectTypeConverter)converter);
        } else if (converter instanceof TypeConversionConverter) {
            method.addLine("TypeConversionConverter " + converterName + " = new TypeConversionConverter();");
            addTypeConversionConverterLines(method, converterName, (TypeConversionConverter)converter);
        } else if (converter instanceof SerializedObjectConverter) {
            method.addLine("SerializedObjectConverter " + converterName + " = new SerializedObjectConverter();");
        } else {
            method.addLine(converter.getClass().getName() + " " + converterName + " = new " + converter.getClass().getName() + "();");
        }
    }

    protected void addTypeConversionConverterLines(NonreflectiveMethodDefinition method, String converterName, TypeConversionConverter converter) {
        if (converter.getObjectClassName() != null) {
            // Bug 5170735 - if a conversion object class is an array type, we need to use different notation
			// retrieve the component type from the converter's ocj
			String arrayComponentType = Helper.getComponentTypeNameFromArrayString(converter.getObjectClassName());
			if (arrayComponentType != null) {
				method.addLine(converterName + ".setObjectClass(" + arrayComponentType + "[].class);");
			} else {
				method.addLine(converterName + ".setObjectClass(" + converter.getObjectClassName() + ".class);");
            }
        }
        if (converter.getDataClassName() != null) {
            // Bug 5170735 - if a conversion data class is an array type, we need to use different notation
			String arrayComponentType = Helper.getComponentTypeNameFromArrayString(converter.getDataClassName());
        	if (arrayComponentType != null) {
        		method.addLine(converterName + ".setDataClass(" + arrayComponentType + "[].class);");
            } else {
                method.addLine(converterName + ".setDataClass(" + converter.getDataClassName() + ".class);");
            }
        }
    }

    protected void addVariableOneToOneMappingLines(NonreflectiveMethodDefinition method, String mappingName, VariableOneToOneMapping mapping) {
        for (Iterator foreignKeysEnum = mapping.getSourceToTargetQueryKeyNames().keySet().iterator();
                 foreignKeysEnum.hasNext();) {
            DatabaseField sourceField = (DatabaseField)foreignKeysEnum.next();
            String targetQueryKey = (String)mapping.getSourceToTargetQueryKeyNames().get(sourceField);
            if (mapping.getForeignKeyFields().contains(sourceField)) {
                method.addLine(mappingName + ".addForeignQueryKeyName(\"" + sourceField.getQualifiedName() + "\", \"" + targetQueryKey + "\");");
            } else {
                method.addLine(mappingName + ".addTargetForeignQueryKeyName(\"" + targetQueryKey + "\", \"" + sourceField.getQualifiedName() + "\");");
            }
        }

        if (mapping.getTypeField() != null) {
            method.addLine(mappingName + ".setTypeFieldName(\"" + mapping.getTypeFieldName() + "\");");

            for (Iterator typeIndicatorsEnum = mapping.getTypeIndicatorNameTranslation().keySet().iterator();
                     typeIndicatorsEnum.hasNext();) {
                String className = (String)typeIndicatorsEnum.next();
                Object value = mapping.getTypeIndicatorNameTranslation().get(className);
                method.addLine(mappingName + ".addClassIndicator(" + className + ".class, " + printString(value) + ");");
            }
        }
    }

    protected NonreflectiveMethodDefinition buildConstructor() {
        NonreflectiveMethodDefinition methodDefinition = new NonreflectiveMethodDefinition();

        methodDefinition.setName(getClassName());
        methodDefinition.setIsConstructor(true);

        methodDefinition.addLine("setName(\"" + getProject().getName() + "\");");

        methodDefinition.addLine("applyLogin();");

        if (!getProject().getDefaultReadOnlyClasses().isEmpty()) {
            methodDefinition.addLine("setDefaultReadOnlyClasses(buildDefaultReadOnlyClasses());");
        }

        methodDefinition.addLine("");

        // Sort by name.
        List<ClassDescriptor> descriptors = buildSortedListOfDescriptors(getProject().getOrderedDescriptors());

        for (ClassDescriptor descriptor : descriptors) {
            // Singleton interface descriptors should not exist.
            if (!(descriptor.isDescriptorForInterface() && (descriptor.getInterfacePolicy().getImplementorDescriptor() != null))) {
                methodDefinition.addLine("addDescriptor(build" + getDescriptorMethodNames().get(descriptor) + "ClassDescriptor());");
            }
        }

        return methodDefinition;
    }

    protected NonreflectiveMethodDefinition buildDescriptorMethod(ClassDescriptor descriptor) {
        if (descriptor.isFullyInitialized()) {
            throw ValidationException.descriptorMustBeNotInitialized(descriptor);
        }

        NonreflectiveMethodDefinition method = new NonreflectiveMethodDefinition();

        method.setName("build" + getDescriptorMethodNames().get(descriptor) + "ClassDescriptor");
        method.setReturnType("ClassDescriptor");

        // ClassDescriptor
        String descriptorClass = "RelationalDescriptor";
        if ((descriptor.getClass() != ClassDescriptor.class) && (descriptor.getClass() != RelationalDescriptor.class)) {
            descriptorClass = descriptor.getClass().getName();
        }
        method.addLine(descriptorClass + " descriptor = new " + descriptorClass + "();");

        // ClassDescriptor type
        if (descriptor.isDescriptorForInterface()) {
            method.addLine("descriptor.descriptorIsForInterface();");
        } else if (descriptor.isAggregateDescriptor()) {
            method.addLine("descriptor.descriptorIsAggregate();");
        } else if (descriptor.isAggregateCollectionDescriptor()) {
            method.addLine("descriptor.descriptorIsAggregateCollection();");
        }

        // Java class
        method.addLine("descriptor.setJavaClass(" + descriptor.getJavaClassName() + ".class);");

        if ((!descriptor.isAggregateDescriptor()) && (!descriptor.isDescriptorForInterface())) {
            // Tables
            for (Enumeration tablesEnum = descriptor.getTables().elements();
                     tablesEnum.hasMoreElements();) {
                String tableName = ((DatabaseTable)tablesEnum.nextElement()).getQualifiedName();
                method.addLine("descriptor.addTableName(\"" + tableName + "\");");
            }

            // Primary key
            if (!descriptor.isChildDescriptor()) {
                for (Enumeration keysEnum = descriptor.getPrimaryKeyFieldNames().elements();
                         keysEnum.hasMoreElements();) {
                    method.addLine("descriptor.addPrimaryKeyFieldName(\"" + keysEnum.nextElement() + "\");");
                }
            }
            for (Iterator multipleTablePrimaryKeysEnum = descriptor.getAdditionalTablePrimaryKeyFields().values().iterator();
                     multipleTablePrimaryKeysEnum.hasNext();) {
                Map keyMapping = (Map)multipleTablePrimaryKeysEnum.next();
                Iterator keyMappingSourceFieldsEnum = keyMapping.keySet().iterator();
                Iterator keyMappingTargetFieldsEnum = keyMapping.values().iterator();
                while (keyMappingSourceFieldsEnum.hasNext()) {
                    DatabaseField sourceField = (DatabaseField)keyMappingSourceFieldsEnum.next();
                    DatabaseField targetField = (DatabaseField)keyMappingTargetFieldsEnum.next();
                    if (descriptor.getMultipleTableForeignKeys().containsKey(sourceField.getTable())) {
                        method.addLine("descriptor.addForeignKeyFieldNameForMultipleTable(\"" + targetField.getQualifiedName() + "\", \"" + sourceField.getQualifiedName() + "\");");
                    }
                }
            }
        }

        // Inheritance
        if (descriptor.hasInheritance()) {
            method.addLine("");
            addInheritanceLines(method, descriptor.getInheritancePolicy());
        }

        // Interface
        if (descriptor.hasInterfacePolicy()) {
            method.addLine("");
            addInterfaceLines(method, descriptor.getInterfacePolicy());
        }

        // ClassDescriptor properties
        method.addLine("");
        addDescriptorPropertyLines(method, descriptor);

        // Cache Invalidation policy
        method.addLine("");
        addCacheInvalidationPolicyLines(method, descriptor);

        // HistoryPolicy
        addHistoryPolicyLines(method, descriptor);

        // Query manager
        method.addLine("");
        addQueryManagerPropertyLines(method, descriptor);

        //fetch group
        method.addLine("");
        addFetchGroupManagerLine(method, descriptor);

        // Event manager
        method.addLine("");
        addEventManagerPropertyLines(method, descriptor);

        // Query keys
        if (!descriptor.getQueryKeys().isEmpty()) {
            method.addLine("");
            method.addLine("// Query keys.");
            for (Iterator queryKeysEnum = descriptor.getQueryKeys().values().iterator(); queryKeysEnum.hasNext();) {
                addQueryKeyLines(method, (QueryKey)queryKeysEnum.next());
            }
        }

        // Mappings
        if (!descriptor.getMappings().isEmpty()) {
            method.addLine("");
            method.addLine("// Mappings.");
            for (Enumeration mappingsEnum = sortMappings(descriptor.getMappings()).elements();
                     mappingsEnum.hasMoreElements();) {
                addMappingLines(method, (DatabaseMapping)mappingsEnum.nextElement());
                method.addLine("");
            }
        } else {
            method.addLine("");
        }

        // Amendment
        if ((descriptor.getAmendmentClassName() != null) && (descriptor.getAmendmentMethodName() != null)) {
            method.addLine("descriptor.applyAmendmentMethod();");
        }

        method.addLine("return descriptor;");

        return method;
    }

    /**
     *  Take an unsorted list of descriptors and sort it so that the order is maintained.
     */
    private List<ClassDescriptor> buildSortedListOfDescriptors(List<ClassDescriptor> descriptors) {
        List returnDescriptors = Helper.addAllUniqueToList(new ArrayList(descriptors.size()), descriptors);
        Object[] descriptorsArray = new Object[returnDescriptors.size()];
        for (int index = 0; index < returnDescriptors.size(); index++) {
            descriptorsArray[index] = returnDescriptors.get(index);
        }
        Arrays.sort(descriptorsArray, new DescriptorCompare());
        returnDescriptors = new ArrayList(descriptorsArray.length);
        for (Object descriptor : descriptorsArray) {
            returnDescriptors.add(descriptor);
        }
        return returnDescriptors;
    }

    protected NonreflectiveMethodDefinition buildLoginMethod(Login datasourceLogin) {
        NonreflectiveMethodDefinition method = new NonreflectiveMethodDefinition();

        method.setName("applyLogin");

        String loginClassName = datasourceLogin.getClass().getName();
        if (datasourceLogin.getClass().equals(DatabaseLogin.class)) {
            loginClassName = Helper.getShortClassName(datasourceLogin);
        }
        method.addLine(loginClassName + " login = new " + loginClassName + "();");

        if (datasourceLogin instanceof DatabaseLogin) {
            DatabaseLogin login = (DatabaseLogin)datasourceLogin;
            method.addLine("login.usePlatform(new " + login.getPlatformClassName() + "());");
            // CR#3349 Change from setDriverClass() to setDriverClassName()
            method.addLine("login.setDriverClassName(\"" + login.getDriverClassName() + "\");");
            method.addLine("login.setConnectionString(\"" + login.getConnectionString() + "\");");

            if (login.getUserName() != null) {
                method.addLine("login.setUserName(\"" + login.getUserName() + "\");");
            }

            if (login.getPassword() != null) {
                method.addLine("login.setEncryptedPassword(\"" + login.getPassword() + "\");");
            }

            method.addLine("");
            method.addLine("// Configuration Properties.");

            if (!login.shouldBindAllParameters()) {
                method.addLine("login.setShouldBindAllParameters(" + login.shouldBindAllParameters() + ");");
            }
            if (login.shouldCacheAllStatements()) {
                method.addLine("login.setShouldCacheAllStatements(" + login.shouldCacheAllStatements() + ");");
            }
            if (!login.shouldUseByteArrayBinding()) {
                method.addLine("login.setUsesByteArrayBinding(" + login.shouldUseByteArrayBinding() + ");");
            }
            if (login.shouldUseStringBinding()) {
                method.addLine("login.setUsesStringBinding(" + login.shouldUseStringBinding() + ");");
            }
            if (login.shouldUseStreamsForBinding()) {
                method.addLine("\tlogin.setUsesStreamsForBinding(" + login.shouldUseStreamsForBinding() + ");");
            }
            if (login.shouldForceFieldNamesToUpperCase()) {
                method.addLine("login.setShouldForceFieldNamesToUpperCase(" + login.shouldForceFieldNamesToUpperCase() + ");");
            }
            if (!login.shouldOptimizeDataConversion()) {
                method.addLine("login.setShouldOptimizeDataConversion(" + login.shouldOptimizeDataConversion() + ");");
            }
            if (!login.shouldTrimStrings()) {
                method.addLine("login.setShouldTrimStrings(" + login.shouldTrimStrings() + ");");
            }
            if (login.shouldUseBatchWriting()) {
                method.addLine("login.setUsesBatchWriting(" + login.shouldUseBatchWriting() + ");");
            }
            if (!login.shouldUseJDBCBatchWriting()) {
                method.addLine("\tlogin.setUsesJDBCBatchWriting(" + login.shouldUseJDBCBatchWriting() + ");");
            }
            if (login.shouldUseExternalConnectionPooling()) {
                method.addLine("login.setUsesExternalConnectionPooling(" + login.shouldUseExternalConnectionPooling() + ");");
            }
            if (login.shouldUseExternalTransactionController()) {
                method.addLine("login.setUsesExternalTransactionController(" + login.shouldUseExternalTransactionController() + ");");
            }
        } else if (datasourceLogin instanceof EISLogin) {
            EISLogin eisLogin = (EISLogin)datasourceLogin;
            method.addLine("login.setConnectionSpec(new " + eisLogin.getConnectionSpec().getClass().getName() + "());");
            if (eisLogin.getConnectionFactoryURL() != null) {
                method.addLine("login.setConnectionFactoryURL(\"" + eisLogin.getConnectionFactoryURL() + "\");");
            }
        }

        boolean addedSequencingHeader = false;
        Sequence defaultSequence = ((DatasourceLogin)datasourceLogin).getDefaultSequenceToWrite();
        if (defaultSequence != null) {
            method.addLine("");
            method.addLine("// Sequencing.");
            addedSequencingHeader = true;
            method.addLine(setDefaultOrAddSequenceString(defaultSequence, true));
        }
        Map sequences = ((DatasourceLogin)datasourceLogin).getSequencesToWrite();
        if ((sequences != null) && !sequences.isEmpty()) {
            if (!addedSequencingHeader) {
                method.addLine("");
                method.addLine("// Sequencing.");
                addedSequencingHeader = true;
            }
            Iterator it = sequences.values().iterator();
            while (it.hasNext()) {
                Sequence sequence = (Sequence)it.next();
                method.addLine(setDefaultOrAddSequenceString(sequence, false));
            }
        }

        method.addLine("");
        method.addLine("setDatasourceLogin(login);");

        return method;
    }

    protected String setDefaultOrAddSequenceString(Sequence sequence, boolean isSetDefault) {
        String prefix;
        if (isSetDefault) {
            prefix = "login.setDefaultSequence(new ";
        } else {
            prefix = "login.addSequence(new ";
        }
        String str;
        if (sequence instanceof TableSequence) {
            TableSequence ts = (TableSequence)sequence;
            str = "TableSequence(\"" + ts.getName() + "\", " + ts.getPreallocationSize() + ", \"" + ts.getTableName() + "\", \"" + ts.getNameFieldName() + "\", \"" + ts.getCounterFieldName() + "\"));";
        } else if (sequence instanceof UnaryTableSequence) {
            UnaryTableSequence uts = (UnaryTableSequence)sequence;
            str = "UnaryTableSequence(\"" + uts.getName() + "\", " + uts.getPreallocationSize() + ", \"" + uts.getCounterFieldName() + "\"));";
        } else {
            String typeName = Helper.getShortClassName(sequence);
            str = typeName + "(\"" + sequence.getName() + "\", " + sequence.getPreallocationSize() + "));";
        }
        return prefix + str;
    }

    /**
     * This figures out the best name for each descriptor,
     * first using the local class name then the qualified one for duplicates.
     */
    protected void computeDescriptorMethodNames() {
        Hashtable shortNames = new Hashtable();
        Iterator descriptors = project.getOrderedDescriptors().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();

            // Singleton interface descriptors should not exist.
            if (!(descriptor.isDescriptorForInterface() && (descriptor.getInterfacePolicy().getImplementorDescriptor() != null))) {
                String shortName = Helper.getShortClassName(descriptor.getJavaClassName());
                if (shortNames.containsKey(shortName)) {
                    // Use the full package name.
                    ClassDescriptor firstDescriptor = (ClassDescriptor)shortNames.get(shortName);
                    getDescriptorMethodNames().put(firstDescriptor, removeDots(firstDescriptor.getJavaClassName()));
                    getDescriptorMethodNames().put(descriptor, removeDots(descriptor.getJavaClassName()));
                } else {
                    shortNames.put(shortName, descriptor);
                    getDescriptorMethodNames().put(descriptor, shortName);
                }
            }
        }
    }

    /**
     * PUBLIC:
     * Generate the project class, output the java source code to the stream or file.
     * useUnicode determines if unicode escaped characters for non_ASCII charaters will be used.
     */
    public void generate(boolean useUnicode) throws ValidationException {
        if (getOutputWriter() == null) {
            try {
                setOutputWriter(new OutputStreamWriter(new FileOutputStream(getOutputPath() + getOutputFileName())));
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }

        CodeGenerator generator = new CodeGenerator(useUnicode);
        generator.setOutput(getOutputWriter());
        generateProjectClass().write(generator);
        try {
            getOutputWriter().flush();
            getOutputWriter().close();
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * PUBLIC:
     * Generate the project class, output the java source code to the stream or file.
     * Unicode escaped characters for non_ASCII charaters will be used.
     */
    public void generate() throws ValidationException {
        generate(true);
    }

    /**
     * Return a class definition object representing the code to be generated for the project and its descriptors.
     * This class will have one method per descriptor and its toString can be used to convert it to code.
     */
    protected ClassDefinition generateProjectClass() {
        computeDescriptorMethodNames();

        ClassDefinition classDefinition = new ClassDefinition();

        classDefinition.setName(getClassName());
        classDefinition.setSuperClass("org.eclipse.persistence.sessions.Project");
        classDefinition.setPackageName(getPackageName());

        classDefinition.addImport("org.eclipse.persistence.sessions.*");
        classDefinition.addImport("org.eclipse.persistence.descriptors.*");
        classDefinition.addImport("org.eclipse.persistence.descriptors.invalidation.*");
        classDefinition.addImport("org.eclipse.persistence.mappings.*");
        classDefinition.addImport("org.eclipse.persistence.mappings.converters.*");
        classDefinition.addImport("org.eclipse.persistence.queries.*");
        classDefinition.addImport("org.eclipse.persistence.expressions.ExpressionBuilder");
        classDefinition.addImport("org.eclipse.persistence.history.HistoryPolicy");
        classDefinition.addImport("org.eclipse.persistence.sequencing.*");

        classDefinition.setComment("This class was generated by the TopLink project class generator." + Helper.cr() + "It stores the meta-data (descriptors) that define the TopLink mappings." + Helper.cr() + "## " + DatabaseLogin.getVersion() + " ##" + Helper.cr() + "@see org.eclipse.persistence.sessions.factories.ProjectClassGenerator");

        classDefinition.addMethod(buildConstructor());

        if (getProject().getDatasourceLogin() != null) {
            classDefinition.addMethod(buildLoginMethod(getProject().getDatasourceLogin()));
        }

        for (ClassDescriptor descriptor : buildSortedListOfDescriptors(getProject().getOrderedDescriptors())) {
            // Singleton interface descriptors should not exist.
            if (!(descriptor.isDescriptorForInterface() && (descriptor.getInterfacePolicy().getImplementorDescriptor() != null))) {
                classDefinition.addMethod(buildDescriptorMethod(descriptor));
            }
        }

        return classDefinition;
    }

    /**
     * PUBLIC:
     * Return the name of class to be generated.
     * This is the unqualified name.
     */
    public String getClassName() {
        return className;
    }

    protected Hashtable getDescriptorMethodNames() {
        return descriptorMethodNames;
    }

    /**
     * PUBLIC:
     * Return the file name that the generate .java file will be output to.
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * PUBLIC:
     * Return the path that the generate .java file will be output to.
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * PUBLIC:
     * Return the writer the output to.
     */
    public Writer getOutputWriter() {
        return outputWriter;
    }

    /**
     * PUBLIC:
     * Return the package name of class to be generated.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * PUBLIC:
     * Return the project to generate from.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Return the printed version of the primitive value object.
     * This must determine the class and use the correct constructor arguments.
     */
    protected String printString(Object value) {
        if ((value == null) || (value == Helper.NULL_VALUE)) {
            return "null";
        }

        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        if (value instanceof Character) {
            return "new Character('" + value + "')";
        }

        //Bug2662265
        if (value instanceof java.util.Date) {
            java.util.Date date = (java.util.Date)value;
            return "new " + value.getClass().getName() + "(" + date.getTime() + "L)";
        }

        // This handles most cases.
        //CR#2648.  Get fully qualified name for object.
        // Bug 298443 - account for Byte, Double, Short, Long, etc. constructors.
        return "new " + value.getClass().getName() + "(\"" + String.valueOf(value) + "\")";

    }

    protected String removeDots(String packageName) {
        StringWriter writer = new StringWriter();
        int startIndex = 0;
        int dotIndex = packageName.indexOf('.', startIndex);
        while (dotIndex >= 0) {
            writer.write(packageName.substring(startIndex, dotIndex));
            startIndex = dotIndex + 1;
            dotIndex = packageName.indexOf('.', startIndex);
        }
        writer.write(packageName.substring(startIndex, packageName.length()));

        return writer.toString();
    }

    /**
     * PUBLIC:
     * Set the name of class to be generated.
     * This can be qualified or unqualified name and will set the file name to match.
     */
    public void setClassName(String newClassName) {
        int lastDotIndex = newClassName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            className = newClassName.substring(lastDotIndex + 1, newClassName.length());
            setPackageName(newClassName.substring(0, lastDotIndex));
        } else {
            className = newClassName;
        }
        setOutputFileName(newClassName);
    }

    protected void setDescriptorMethodNames(Hashtable descriptorMethodNames) {
        this.descriptorMethodNames = descriptorMethodNames;
    }

    /**
     * PUBLIC:
     * Set the file name that the generate .java file will be output to.
     * If the file does not include .java it will be appended.
     */
    public void setOutputFileName(String newOutputFileName) {
        if (newOutputFileName.indexOf(".java") < 0) {
            outputFileName = newOutputFileName + ".java";
        } else {
            outputFileName = newOutputFileName;
        }
    }

    /**
     * PUBLIC:
     * Set the path that the generate .java file will be output to.
     */
    public void setOutputPath(String newOutputPath) {
        outputPath = newOutputPath;
    }

    /**
     * PUBLIC:
     * Set the writer the output to.
     */
    public void setOutputWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    /**
     * PUBLIC:
     * Set the package name of class to be generated.
     */
    public void setPackageName(String newPackageName) {
        packageName = newPackageName;
    }

    /**
     * PUBLIC:
     * Set the project to generate from.
     * All of the projects descriptors will be stored into the file.
     */
    public void setProject(Project newProject) {
        project = newProject;
    }

    /**
     * Short the mappings by type.
     */
    protected Vector sortMappings(Vector mappings) {
        Vector sortedMappings = new Vector(mappings.size());

        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.getClass().equals(DirectToFieldMapping.class)) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isTransformationMapping()) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isAggregateMapping()) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isDirectCollectionMapping()) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isObjectReferenceMapping()) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isOneToManyMapping()) {
                sortedMappings.addElement(mapping);
            }
        }
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (mapping.isManyToManyMapping()) {
                sortedMappings.addElement(mapping);
            }
        }

        // Others
        for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappingsEnum.nextElement();
            if (!sortedMappings.contains(mapping)) {
                sortedMappings.addElement(mapping);
            }
        }

        return sortedMappings;
    }

    /**
     * PUBLIC:
     * Generate the source code to a project class to the project's descriptor into the writer.
     */
    public static void write(Project project, String projectClassName, Writer writer) {
        ProjectClassGenerator generator = new ProjectClassGenerator(project, projectClassName, writer);
        generator.generate();
    }

    /**
     * PUBLIC:
     * Generate the source code to a project class to the project's descriptor into the file.
     */
    public static void write(Project project, String projectClassName, String fileName) {
        ProjectClassGenerator generator = new ProjectClassGenerator(project, projectClassName, fileName);
        generator.generate();
    }
}
