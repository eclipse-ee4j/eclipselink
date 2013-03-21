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
 *     17/10/2008-1.1  Michael O'Brien 
 *       - 251005: The default JNDI InitialContextFactory is modified from
 *       OC4J: oracle.j2ee.rmi.RMIInitialContextFactory to
 *       WebLogic: weblogic.jndi.WLInitialContextFactory
 *     cdelahun - Bug 214534: changes for JMSPublishingTransportManager configuration
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories;

// javase imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.JavaLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.LoggingOptionsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.log.ServerLogConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.EISLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.StructConverterConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.CustomServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.GlassfishPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.JBossPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.NetWeaver_7_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.Oc4jPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.ServerPlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_6_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_7_0_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebLogic_8_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_4_0_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_5_0_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_5_1_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.platform.WebSphere_6_0_PlatformConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ConnectionPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ConnectionPoolConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.PoolsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.ReadConnectionPoolConfig;
import org.eclipse.persistence.internal.sessions.factories.model.pool.WriteConnectionPoolConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectClassConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.RemoteCommandManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.rcm.command.CommandsConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.DefaultSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.NativeSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.TableSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.UnaryTableSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.XMLFileSequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.ServerSessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionBrokerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSPublishingTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.JMSTopicTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.RMIIIOPTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.RMITransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.SunCORBATransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.TransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.UserDefinedTransportManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.discovery.DiscoveryConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.JNDINamingServiceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.RMIRegistryNamingServiceConfig;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader.ECLIPSELINK_SESSIONS_SCHEMA;

/**
 * INTERNAL:
 * This class was generated by the TopLink project class generator.
 * It stores the meta-data (descriptors) that define the session.xml file
 *
 * Modifications were needed to the generated code, therefore, this project
 * should not be regenerated under any circumstances. Any changes must be
 * done manually.
 */
public class XMLSessionConfigProject extends org.eclipse.persistence.sessions.Project {
    // Default null values
    public static final boolean IS_ASYNCHRONOUS_DEFAULT = true;
    public static final boolean REMOVE_CONNECTION_ON_ERROR_DEFAULT = true;
    public static final boolean CACHE_SYNC_DEFAULT = false;
    public static final boolean NATIVE_SEQUENCING_DEFAULT = false;
    public static final boolean BIND_ALL_PARAMETERS_DEFAULT = false;
    public static final boolean CACHE_ALL_STATEMENTS_DEFAULT = false;
    public static final boolean BYTE_ARRAY_BINDING_DEFAULT = true;
    public static final boolean STRING_BINDING_DEFAULT = false;
    public static final boolean STREAMS_FOR_BINDING_DEFAULT = false;
    public static final boolean FORCE_FIELD_NAMES_TO_UPPERCASE_DEFAULT = false;
    public static final boolean OPTIMIZE_DATA_CONVERSION_DEFAULT = true;
    public static final boolean TRIM_STRINGS_DEFAULT = true;
    public static final boolean BATCH_WRITING_DEFAULT = false;
    public static final boolean JDBC20_BATCH_WRITING_DEFAULT = true;
    public static final boolean NATIVE_SQL_DEFAULT = false;
    public static final boolean ENABLE_RUNTIME_SERVICES_DEFAULT = true;
    public static final boolean ENABLE_JTA_DEFAULT = true;
    public static final boolean EXTERNAL_CONNECTION_POOL_DEFAULT = false;
    public static final boolean EXTERNAL_TRANSACTION_CONTROLLER_DEFAULT = false;
    public static final boolean EXCLUSIVE_DEFAULT = true;
    public static final int SEQUENCE_PREALLOCATION_SIZE_DEFAULT = 50;
    public static final int MAX_BATCH_WRITING_SIZE_DEFAULT = 32000;
    public static final int MULTICAST_PORT_DEFAULT = 3121;
    public static final int ANNOUNCEMENT_DELAY_DEFAULT = 1000;
    public static final int MULTICAST_PORT_RMI_CLUSTERING_DEFAULT = 6018;
    public static final int PACKET_TIME_TO_LIVE_DEFAULT = 2;
    public static final int CONNECTION_POOL_MAX_DEFAULT = 10;
    public static final int CONNECTION_POOL_MIN_DEFAULT = 5;
    public static final int READ_CONNECTION_POOL_MAX_DEFAULT = 2;
    public static final int READ_CONNECTION_POOL_MIN_DEFAULT = 2;

    //the default is composite name for consistency with previous TopLink versions
    public static final int DATASOURCE_LOOKUP_TYPE_DEFAULT = JNDIConnector.COMPOSITE_NAME_LOOKUP;
    public static final String SEQUENCE_TABLE_DEFAULT = "SEQUENCE";
    public static final String SEQUENCE_NAME_FIELD_DEFAULT = "SEQ_NAME";
    public static final String SEQUENCE_COUNTER_FIELD_DEFAULT = "SEQ_COUNT";
    public static final String LOG_LEVEL_DEFAULT = "info";
    public static final String MULTICAST_GROUP_ADDRESS_DEFAULT = "226.10.12.64";
    public static final String MULTICAST_GROUP_ADDRESS_RMI_CLUSTERING = "226.18.6.18";
    public static final String TOPIC_CONNECTION_FACTORY_NAME_DEFAULT = "jms/TopLinkTopicConnectionFactory";
    public static final String TOPIC_NAME_DEFAULT = "jms/TopLinkTopic";
    public static final String USERNAME_DEFAULT = "admin";
    public static final String PASSWORD_DEFAULT = "password";
    public static final String ENCRYPTION_CLASS_DEFAULT = "org.eclipse.persistence.internal.security.JCEEncryptor";
    public static final String INITIAL_CONTEXT_FACTORY_NAME_DEFAULT = "weblogic.jndi.WLInitialContextFactory";
    public static final String SEND_MODE_DEFAULT = "Asynchronous";
    public static final String CHANNEL_DEFAULT = "TopLinkCommandChannel";
    public static final String ON_CONNECTION_ERROR_DEFAULT = "DiscardConnection";
    public static final String CUSTOM_SERVER_PLATFORM_CLASS_DEFAULT = "org.eclipse.persistence.platform.server.CustomServerPlatform";
    public static final boolean EXCLUSIVE_CONNECTION_DEFAULT = false;
    public static final boolean LAZY_DEFAULT = true;

    public XMLSessionConfigProject() {
        setName("XMLSessionConfigProject");
        addDescriptor(buildSessionConfigsDescriptor());
        addDescriptor(buildSunCORBATransportManagerConfigDescriptor());
        addDescriptor(buildSessionEventManagerConfigDescriptor());
        addDescriptor(buildDefaultSessionLogConfigDescriptor());
        addDescriptor(buildLoggingOptionsConfigDescriptor());
        addDescriptor(buildJavaLogConfigDescriptor());
        addDescriptor(buildServerLogConfigDescriptor());
        addDescriptor(buildLogConfigDescriptor());
        addDescriptor(buildDatabaseLoginConfigDescriptor());
        addDescriptor(buildEISLoginConfigDescriptor());
        addDescriptor(buildXMLLoginConfigDescriptor());
        addDescriptor(buildLoginConfigDescriptor());
        addDescriptor(buildConnectionPolicyConfigDescriptor());
        addDescriptor(buildConnectionPoolConfigDescriptor());
        addDescriptor(buildPoolsConfigDescriptor());
        addDescriptor(buildReadConnectionPoolConfigDescriptor());
        addDescriptor(buildWriteConnectionPoolConfigDescriptor());
        addDescriptor(buildRemoteCommandManagerConfigDescriptor());
        addDescriptor(buildCommandsConfigDescriptor());
        addDescriptor(buildDatabaseSessionConfigDescriptor());
        addDescriptor(buildServerSessionConfigDescriptor());
        addDescriptor(buildSessionBrokerConfigDescriptor());
        addDescriptor(buildSessionConfigDescriptor());
        addDescriptor(buildJMSTopicTransportManagerConfigDescriptor());
        addDescriptor(buildJMSPublishingTransportManagerConfigDescriptor());
        addDescriptor(buildRMIIIOPTransportManagerConfigDescriptor());
        addDescriptor(buildRMITransportManagerConfigDescriptor());
        addDescriptor(buildTransportManagerConfigDescriptor());
        addDescriptor(buildUserDefinedTransportManagerConfigDescriptor());
        addDescriptor(buildDiscoveryConfigDescriptor());
        addDescriptor(buildJNDINamingServiceConfigDescriptor());
        addDescriptor(buildPropertyConfigDescriptor());
        addDescriptor(buildRMIRegistryNamingServiceConfigDescriptor());
        addDescriptor(buildProjectConfigDescriptor());
        addDescriptor(buildProjectClassConfigDescriptor());
        addDescriptor(buildProjectXMLConfigDescriptor());
        addDescriptor(buildStructConverterConfigDescriptor());

        // sequences
        addDescriptor(buildSequencingConfigDescriptor());
        addDescriptor(buildSequenceConfigDescriptor());
        addDescriptor(buildDefaultSequenceConfigDescriptor());
        addDescriptor(buildNativeSequenceConfigDescriptor());
        addDescriptor(buildTableSequenceConfigDescriptor());
        addDescriptor(buildUnaryTableSequenceConfigDescriptor());
        addDescriptor(buildXMLFileSequenceConfigDescriptor());

        // platforms
        addDescriptor(buildServerPlatformConfigDescriptor());
        addDescriptor(buildCustomServerPlatformConfigDescriptor());
        addDescriptor(buildServerPlatformConfigDescriptorFor(Oc4jPlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(GlassfishPlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebLogic_6_1_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebLogic_7_0_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebLogic_8_1_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_4_0_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_5_0_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_5_1_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(WebSphere_6_0_PlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(JBossPlatformConfig.class));
        addDescriptor(buildServerPlatformConfigDescriptorFor(NetWeaver_7_1_PlatformConfig.class));

        // Set the namespaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        namespaceResolver.put("xsd", W3C_XML_SCHEMA_NS_URI);

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }
        
        XMLLogin xmlLogin = new XMLLogin();
        DOMPlatform platform = new DOMPlatform();
        xmlLogin.setDatasourcePlatform(platform);
        this.setDatasourceLogin(xmlLogin);
    }


    public ClassDescriptor buildCommandsConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CommandsConfig.class);

        XMLDirectMapping cacheSyncMapping = new XMLDirectMapping();
        cacheSyncMapping.setAttributeName("m_cacheSync");
        cacheSyncMapping.setGetMethodName("getCacheSync");
        cacheSyncMapping.setSetMethodName("setCacheSync");
        cacheSyncMapping.setXPath("cache-sync/text()");
        cacheSyncMapping.setNullValue(Boolean.valueOf(CACHE_SYNC_DEFAULT));
        descriptor.addMapping(cacheSyncMapping);

        return descriptor;
    }

    public ClassDescriptor buildConnectionPolicyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConnectionPolicyConfig.class);

        XMLDirectMapping exclusiveConnectionMapping = new XMLDirectMapping();
        exclusiveConnectionMapping.setAttributeName("m_useExclusiveConnection");
        exclusiveConnectionMapping.setGetMethodName("getUseExclusiveConnection");
        exclusiveConnectionMapping.setSetMethodName("setUseExclusiveConnection");
        exclusiveConnectionMapping.setXPath("exclusive-connection/text()");
        exclusiveConnectionMapping.setNullValue(Boolean.valueOf(EXCLUSIVE_CONNECTION_DEFAULT));
        descriptor.addMapping(exclusiveConnectionMapping);

        XMLDirectMapping lazyMapping = new XMLDirectMapping();
        lazyMapping.setAttributeName("m_lazy");
        lazyMapping.setGetMethodName("getLazy");
        lazyMapping.setSetMethodName("setLazy");
        lazyMapping.setXPath("lazy/text()");
        lazyMapping.setNullValue(Boolean.valueOf(LAZY_DEFAULT));
        descriptor.addMapping(lazyMapping);

        return descriptor;
    }

    public ClassDescriptor buildConnectionPoolConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConnectionPoolConfig.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping maxConnectionsMapping = new XMLDirectMapping();
        maxConnectionsMapping.setAttributeName("m_maxConnections");
        maxConnectionsMapping.setGetMethodName("getMaxConnections");
        maxConnectionsMapping.setSetMethodName("setMaxConnections");
        maxConnectionsMapping.setXPath("max-connections/text()");
        maxConnectionsMapping.setNullValue(Integer.valueOf(CONNECTION_POOL_MAX_DEFAULT));
        descriptor.addMapping(maxConnectionsMapping);

        XMLDirectMapping minConnectionsMapping = new XMLDirectMapping();
        minConnectionsMapping.setAttributeName("m_minConnections");
        minConnectionsMapping.setGetMethodName("getMinConnections");
        minConnectionsMapping.setSetMethodName("setMinConnections");
        minConnectionsMapping.setXPath("min-connections/text()");
        minConnectionsMapping.setNullValue(Integer.valueOf(CONNECTION_POOL_MIN_DEFAULT));
        descriptor.addMapping(minConnectionsMapping);

        XMLCompositeObjectMapping loginConfigMapping = new XMLCompositeObjectMapping();
        loginConfigMapping.setReferenceClass(LoginConfig.class);
        loginConfigMapping.setAttributeName("m_loginConfig");
        loginConfigMapping.setGetMethodName("getLoginConfig");
        loginConfigMapping.setSetMethodName("setLoginConfig");
        loginConfigMapping.setXPath("login");
        descriptor.addMapping(loginConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildCustomServerPlatformConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CustomServerPlatformConfig.class);
        descriptor.getInheritancePolicy().setParentClass(ServerPlatformConfig.class);

        XMLDirectMapping serverClassMapping = new XMLDirectMapping();
        serverClassMapping.setAttributeName("m_serverClassName");
        serverClassMapping.setGetMethodName("getServerClassName");
        serverClassMapping.setSetMethodName("setServerClassName");
        serverClassMapping.setXPath("server-class/text()");
        serverClassMapping.setNullValue(CUSTOM_SERVER_PLATFORM_CLASS_DEFAULT);
        descriptor.addMapping(serverClassMapping);

        XMLDirectMapping externalTransactionControllerMapping = new XMLDirectMapping();
        externalTransactionControllerMapping.setAttributeName("m_externalTransactionControllerClass");
        externalTransactionControllerMapping.setGetMethodName("getExternalTransactionControllerClass");
        externalTransactionControllerMapping.setSetMethodName("setExternalTransactionControllerClass");
        externalTransactionControllerMapping.setXPath("external-transaction-controller-class/text()");
        descriptor.addMapping(externalTransactionControllerMapping);

        return descriptor;
    }

    public ClassDescriptor buildDatabaseLoginConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseLoginConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LoginConfig.class);

        XMLDirectMapping driverClassMapping = new XMLDirectMapping();
        driverClassMapping.setAttributeName("m_driverClass");
        driverClassMapping.setGetMethodName("getDriverClass");
        driverClassMapping.setSetMethodName("setDriverClass");
        driverClassMapping.setXPath("driver-class/text()");
        descriptor.addMapping(driverClassMapping);

        XMLDirectMapping connectionURLMapping = new XMLDirectMapping();
        connectionURLMapping.setAttributeName("m_connectionURL");
        connectionURLMapping.setGetMethodName("getConnectionURL");
        connectionURLMapping.setSetMethodName("setConnectionURL");
        connectionURLMapping.setXPath("connection-url/text()");
        descriptor.addMapping(connectionURLMapping);

        XMLDirectMapping datasourceMapping = new XMLDirectMapping();
        datasourceMapping.setAttributeName("m_datasource");
        datasourceMapping.setGetMethodName("getDatasource");
        datasourceMapping.setSetMethodName("setDatasource");
        datasourceMapping.setXPath("datasource/text()");
        descriptor.addMapping(datasourceMapping);

        XMLDirectMapping lookupTypeMapping = new XMLDirectMapping();
        lookupTypeMapping.setAttributeName("m_lookupType");
        lookupTypeMapping.setGetMethodName("getLookupType");
        lookupTypeMapping.setSetMethodName("setLookupType");
        lookupTypeMapping.setNullValue(Integer.valueOf(DATASOURCE_LOOKUP_TYPE_DEFAULT));
        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("string", Integer.valueOf(JNDIConnector.STRING_LOOKUP));
        converter.addConversionValue("composite-name", Integer.valueOf(JNDIConnector.COMPOSITE_NAME_LOOKUP));
        converter.addConversionValue("compound-name", Integer.valueOf(JNDIConnector.COMPOUND_NAME_LOOKUP));
        lookupTypeMapping.setConverter(converter);
        lookupTypeMapping.setXPath("datasource/@lookup");
        descriptor.addMapping(lookupTypeMapping);

        XMLDirectMapping bindAllParametersMapping = new XMLDirectMapping();
        bindAllParametersMapping.setAttributeName("m_bindAllParameters");
        bindAllParametersMapping.setGetMethodName("getBindAllParameters");
        bindAllParametersMapping.setSetMethodName("setBindAllParameters");
        bindAllParametersMapping.setXPath("bind-all-parameters/text()");
        bindAllParametersMapping.setNullValue(Boolean.valueOf(BIND_ALL_PARAMETERS_DEFAULT));
        descriptor.addMapping(bindAllParametersMapping);

        XMLDirectMapping cacheAllStatementsMapping = new XMLDirectMapping();
        cacheAllStatementsMapping.setAttributeName("m_cacheAllStatements");
        cacheAllStatementsMapping.setGetMethodName("getCacheAllStatements");
        cacheAllStatementsMapping.setSetMethodName("setCacheAllStatements");
        cacheAllStatementsMapping.setXPath("cache-all-statements/text()");
        cacheAllStatementsMapping.setNullValue(Boolean.valueOf(CACHE_ALL_STATEMENTS_DEFAULT));
        descriptor.addMapping(cacheAllStatementsMapping);

        XMLDirectMapping byteArrayBindingMapping = new XMLDirectMapping();
        byteArrayBindingMapping.setAttributeName("m_byteArrayBinding");
        byteArrayBindingMapping.setGetMethodName("getByteArrayBinding");
        byteArrayBindingMapping.setSetMethodName("setByteArrayBinding");
        byteArrayBindingMapping.setXPath("byte-array-binding/text()");
        byteArrayBindingMapping.setNullValue(Boolean.valueOf(BYTE_ARRAY_BINDING_DEFAULT));
        descriptor.addMapping(byteArrayBindingMapping);

        XMLDirectMapping stringBindingMapping = new XMLDirectMapping();
        stringBindingMapping.setAttributeName("m_stringBinding");
        stringBindingMapping.setGetMethodName("getStringBinding");
        stringBindingMapping.setSetMethodName("setStringBinding");
        stringBindingMapping.setXPath("string-binding/text()");
        stringBindingMapping.setNullValue(Boolean.valueOf(STRING_BINDING_DEFAULT));
        descriptor.addMapping(stringBindingMapping);

        XMLDirectMapping streamsForBindingMapping = new XMLDirectMapping();
        streamsForBindingMapping.setAttributeName("m_streamsForBinding");
        streamsForBindingMapping.setGetMethodName("getStreamsForBinding");
        streamsForBindingMapping.setSetMethodName("setStreamsForBinding");
        streamsForBindingMapping.setXPath("streams-for-binding/text()");
        streamsForBindingMapping.setNullValue(Boolean.valueOf(STREAMS_FOR_BINDING_DEFAULT));
        descriptor.addMapping(streamsForBindingMapping);

        XMLDirectMapping forceFieldNamesToUppercaseMapping = new XMLDirectMapping();
        forceFieldNamesToUppercaseMapping.setAttributeName("m_forceFieldNamesToUppercase");
        forceFieldNamesToUppercaseMapping.setGetMethodName("getForceFieldNamesToUppercase");
        forceFieldNamesToUppercaseMapping.setSetMethodName("setForceFieldNamesToUppercase");
        forceFieldNamesToUppercaseMapping.setXPath("force-field-names-to-upper-case/text()");
        forceFieldNamesToUppercaseMapping.setNullValue(Boolean.valueOf(FORCE_FIELD_NAMES_TO_UPPERCASE_DEFAULT));
        descriptor.addMapping(forceFieldNamesToUppercaseMapping);

        XMLDirectMapping optimizeDataConversionMapping = new XMLDirectMapping();
        optimizeDataConversionMapping.setAttributeName("m_optimizeDataConversion");
        optimizeDataConversionMapping.setGetMethodName("getOptimizeDataConversion");
        optimizeDataConversionMapping.setSetMethodName("setOptimizeDataConversion");
        optimizeDataConversionMapping.setXPath("optimize-data-conversion/text()");
        optimizeDataConversionMapping.setNullValue(Boolean.valueOf(OPTIMIZE_DATA_CONVERSION_DEFAULT));
        descriptor.addMapping(optimizeDataConversionMapping);

        XMLDirectMapping trimStringsMapping = new XMLDirectMapping();
        trimStringsMapping.setAttributeName("m_trimStrings");
        trimStringsMapping.setGetMethodName("getTrimStrings");
        trimStringsMapping.setSetMethodName("setTrimStrings");
        trimStringsMapping.setXPath("trim-strings/text()");
        trimStringsMapping.setNullValue(Boolean.valueOf(TRIM_STRINGS_DEFAULT));
        descriptor.addMapping(trimStringsMapping);

        XMLDirectMapping batchWritingMapping = new XMLDirectMapping();
        batchWritingMapping.setAttributeName("m_batchWriting");
        batchWritingMapping.setGetMethodName("getBatchWriting");
        batchWritingMapping.setSetMethodName("setBatchWriting");
        batchWritingMapping.setXPath("batch-writing/text()");
        batchWritingMapping.setNullValue(Boolean.valueOf(BATCH_WRITING_DEFAULT));
        descriptor.addMapping(batchWritingMapping);

        XMLDirectMapping jdbc20BatchWritingMapping = new XMLDirectMapping();
        jdbc20BatchWritingMapping.setAttributeName("m_jdbcBatchWriting");
        jdbc20BatchWritingMapping.setGetMethodName("getJdbcBatchWriting");
        jdbc20BatchWritingMapping.setSetMethodName("setJdbcBatchWriting");
        jdbc20BatchWritingMapping.setXPath("jdbc-batch-writing/text()");
        jdbc20BatchWritingMapping.setNullValue(Boolean.valueOf(JDBC20_BATCH_WRITING_DEFAULT));
        descriptor.addMapping(jdbc20BatchWritingMapping);

        XMLDirectMapping maxBatchWritingSizeMapping = new XMLDirectMapping();
        maxBatchWritingSizeMapping.setAttributeName("m_maxBatchWritingSize");
        maxBatchWritingSizeMapping.setGetMethodName("getMaxBatchWritingSize");
        maxBatchWritingSizeMapping.setSetMethodName("setMaxBatchWritingSize");
        maxBatchWritingSizeMapping.setXPath("max-batch-writing-size/text()");
        maxBatchWritingSizeMapping.setNullValue(Integer.valueOf(MAX_BATCH_WRITING_SIZE_DEFAULT));
        descriptor.addMapping(maxBatchWritingSizeMapping);

        XMLDirectMapping nativeSQLMapping = new XMLDirectMapping();
        nativeSQLMapping.setAttributeName("m_nativeSQL");
        nativeSQLMapping.setGetMethodName("getNativeSQL");
        nativeSQLMapping.setSetMethodName("setNativeSQL");
        nativeSQLMapping.setXPath("native-sql/text()");
        nativeSQLMapping.setNullValue(Boolean.valueOf(NATIVE_SQL_DEFAULT));
        descriptor.addMapping(nativeSQLMapping);

        XMLCompositeObjectMapping structConverterConfigMapping = new XMLCompositeObjectMapping();
        structConverterConfigMapping.setReferenceClass(StructConverterConfig.class);
        structConverterConfigMapping.setAttributeName("m_structConverterConfig");
        structConverterConfigMapping.setGetMethodName("getStructConverterConfig");
        structConverterConfigMapping.setSetMethodName("setStructConverterConfig");
        structConverterConfigMapping.setXPath("struct-converters");
        descriptor.addMapping(structConverterConfigMapping);
        
        return descriptor;
    }

    public ClassDescriptor buildDatabaseSessionConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseSessionConfig.class);
        descriptor.getInheritancePolicy().setParentClass(SessionConfig.class);

        XMLCompositeObjectMapping primaryProject = new XMLCompositeObjectMapping();
        primaryProject.setReferenceClass(ProjectConfig.class);
        primaryProject.setAttributeName("m_primaryProject");
        primaryProject.setGetMethodName("getPrimaryProject");
        primaryProject.setSetMethodName("setPrimaryProject");
        primaryProject.setXPath("primary-project");
        descriptor.addMapping(primaryProject);

        XMLCompositeCollectionMapping additionalProjects = new XMLCompositeCollectionMapping();
        additionalProjects.setReferenceClass(ProjectConfig.class);
        additionalProjects.setAttributeName("m_additionalProjects");
        additionalProjects.setGetMethodName("getAdditionalProjects");
        additionalProjects.setSetMethodName("setAdditionalProjects");
        additionalProjects.setXPath("additional-project");
        descriptor.addMapping(additionalProjects);

        XMLCompositeObjectMapping loginConfigMapping = new XMLCompositeObjectMapping();
        loginConfigMapping.setReferenceClass(LoginConfig.class);
        loginConfigMapping.setAttributeName("m_loginConfig");
        loginConfigMapping.setGetMethodName("getLoginConfig");
        loginConfigMapping.setSetMethodName("setLoginConfig");
        loginConfigMapping.setXPath("login");
        descriptor.addMapping(loginConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildStructConverterConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StructConverterConfig.class);

        XMLCompositeDirectCollectionMapping convertersMapping = new XMLCompositeDirectCollectionMapping();
        convertersMapping.setAttributeName("m_structConverterClasses");
        convertersMapping.setGetMethodName("getStructConverterClasses");
        convertersMapping.setSetMethodName("setStructConverterClasses");
        convertersMapping.setXPath("struct-converter/text()");
        descriptor.addMapping(convertersMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildDefaultSessionLogConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DefaultSessionLogConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LogConfig.class);

        XMLDirectMapping logLevelMapping = new XMLDirectMapping();
        logLevelMapping.setAttributeName("m_logLevel");
        logLevelMapping.setGetMethodName("getLogLevel");
        logLevelMapping.setSetMethodName("setLogLevel");
        logLevelMapping.setXPath("log-level/text()");
        logLevelMapping.setNullValue(LOG_LEVEL_DEFAULT);
        descriptor.addMapping(logLevelMapping);

        XMLDirectMapping filenameMapping = new XMLDirectMapping();
        filenameMapping.setAttributeName("m_filename");
        filenameMapping.setGetMethodName("getFilename");
        filenameMapping.setSetMethodName("setFilename");
        filenameMapping.setXPath("file-name/text()");
        descriptor.addMapping(filenameMapping);

        XMLCompositeObjectMapping loginConfigMapping = new XMLCompositeObjectMapping();
        loginConfigMapping.setReferenceClass(LoggingOptionsConfig.class);
        loginConfigMapping.setAttributeName("m_loggingOptions");
        loginConfigMapping.setGetMethodName("getLoggingOptions");
        loginConfigMapping.setSetMethodName("setLoggingOptions");
        loginConfigMapping.setXPath("logging-options");
        descriptor.addMapping(loginConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildLoggingOptionsConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(LoggingOptionsConfig.class);

        XMLDirectMapping shouldLogExceptionStackTraceMapping = new XMLDirectMapping();
        shouldLogExceptionStackTraceMapping.setAttributeName("m_logExceptionStacktrace");
        shouldLogExceptionStackTraceMapping.setGetMethodName("getShouldLogExceptionStackTrace");
        shouldLogExceptionStackTraceMapping.setSetMethodName("setShouldLogExceptionStackTrace");
        shouldLogExceptionStackTraceMapping.setXPath("log-exception-stacktrace/text()");
        descriptor.addMapping(shouldLogExceptionStackTraceMapping);

        XMLDirectMapping shouldPrintThreadMapping = new XMLDirectMapping();
        shouldPrintThreadMapping.setAttributeName("m_printThread");
        shouldPrintThreadMapping.setGetMethodName("getShouldPrintThread");
        shouldPrintThreadMapping.setSetMethodName("setShouldPrintThread");
        shouldPrintThreadMapping.setXPath("print-thread/text()");
        descriptor.addMapping(shouldPrintThreadMapping);

        XMLDirectMapping shouldPrintSessionMapping = new XMLDirectMapping();
        shouldPrintSessionMapping.setAttributeName("m_printSession");
        shouldPrintSessionMapping.setGetMethodName("getShouldPrintSession");
        shouldPrintSessionMapping.setSetMethodName("setShouldPrintSession");
        shouldPrintSessionMapping.setXPath("print-session/text()");
        descriptor.addMapping(shouldPrintSessionMapping);

        XMLDirectMapping shouldPrintConnectionMapping = new XMLDirectMapping();
        shouldPrintConnectionMapping.setAttributeName("m_printConnection");
        shouldPrintConnectionMapping.setGetMethodName("getShouldPrintConnection");
        shouldPrintConnectionMapping.setSetMethodName("setShouldPrintConnection");
        shouldPrintConnectionMapping.setXPath("print-connection/text()");
        descriptor.addMapping(shouldPrintConnectionMapping);

        XMLDirectMapping shouldPrintDateMapping = new XMLDirectMapping();
        shouldPrintDateMapping.setAttributeName("m_printDate");
        shouldPrintDateMapping.setGetMethodName("getShouldPrintDate");
        shouldPrintDateMapping.setSetMethodName("setShouldPrintDate");
        shouldPrintDateMapping.setXPath("print-date/text()");
        descriptor.addMapping(shouldPrintDateMapping);

        return descriptor;
    }

    public ClassDescriptor buildDiscoveryConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DiscoveryConfig.class);

        XMLDirectMapping multicastGroupAddressMapping = new XMLDirectMapping();
        multicastGroupAddressMapping.setAttributeName("m_multicastGroupAddress");
        multicastGroupAddressMapping.setGetMethodName("getMulticastGroupAddress");
        multicastGroupAddressMapping.setSetMethodName("setMulticastGroupAddress");
        multicastGroupAddressMapping.setXPath("multicast-group-address/text()");
        multicastGroupAddressMapping.setNullValue(MULTICAST_GROUP_ADDRESS_DEFAULT);
        descriptor.addMapping(multicastGroupAddressMapping);

        XMLDirectMapping multicastPortMapping = new XMLDirectMapping();
        multicastPortMapping.setAttributeName("m_multicastPort");
        multicastPortMapping.setGetMethodName("getMulticastPort");
        multicastPortMapping.setSetMethodName("setMulticastPort");
        multicastPortMapping.setXPath("multicast-port/text()");
        multicastPortMapping.setNullValue(Integer.valueOf(MULTICAST_PORT_DEFAULT));
        descriptor.addMapping(multicastPortMapping);

        XMLDirectMapping announcementDelayMapping = new XMLDirectMapping();
        announcementDelayMapping.setAttributeName("m_announcementDelay");
        announcementDelayMapping.setGetMethodName("getAnnouncementDelay");
        announcementDelayMapping.setSetMethodName("setAnnouncementDelay");
        announcementDelayMapping.setXPath("announcement-delay/text()");
        announcementDelayMapping.setNullValue(Integer.valueOf(ANNOUNCEMENT_DELAY_DEFAULT));
        descriptor.addMapping(announcementDelayMapping);

        XMLDirectMapping packetTimeToLiveMapping = new XMLDirectMapping();
        packetTimeToLiveMapping.setAttributeName("m_packetTimeToLive");
        packetTimeToLiveMapping.setGetMethodName("getPacketTimeToLive");
        packetTimeToLiveMapping.setSetMethodName("setPacketTimeToLive");
        packetTimeToLiveMapping.setXPath("packet-time-to-live/text()");
        packetTimeToLiveMapping.setNullValue(Integer.valueOf(PACKET_TIME_TO_LIVE_DEFAULT));
        descriptor.addMapping(packetTimeToLiveMapping);

        return descriptor;
    }

    public ClassDescriptor buildEISLoginConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISLoginConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LoginConfig.class);

        XMLDirectMapping connectionSpecClassMapping = new XMLDirectMapping();
        connectionSpecClassMapping.setAttributeName("m_connectionSpecClass");
        connectionSpecClassMapping.setGetMethodName("getConnectionSpecClass");
        connectionSpecClassMapping.setSetMethodName("setConnectionSpecClass");
        connectionSpecClassMapping.setXPath("connection-spec-class/text()");
        descriptor.addMapping(connectionSpecClassMapping);

        XMLDirectMapping connectionFactoryURLMapping = new XMLDirectMapping();
        connectionFactoryURLMapping.setAttributeName("m_connectionFactoryURL");
        connectionFactoryURLMapping.setGetMethodName("getConnectionFactoryURL");
        connectionFactoryURLMapping.setSetMethodName("setConnectionFactoryURL");
        connectionFactoryURLMapping.setXPath("connection-factory-url/text()");
        descriptor.addMapping(connectionFactoryURLMapping);

        return descriptor;
    }

    public ClassDescriptor buildXMLLoginConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLLoginConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LoginConfig.class);

        return descriptor;
    }
    
    public ClassDescriptor buildJMSPublishingTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JMSPublishingTransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(TransportManagerConfig.class);

        XMLDirectMapping topicHostURLMapping = new XMLDirectMapping();
        topicHostURLMapping.setAttributeName("m_topicHostURL");
        topicHostURLMapping.setGetMethodName("getTopicHostURL");
        topicHostURLMapping.setSetMethodName("setTopicHostURL");
        topicHostURLMapping.setXPath("topic-host-url/text()");
        descriptor.addMapping(topicHostURLMapping);

        XMLDirectMapping topicConnectionFactoryNameMapping = new XMLDirectMapping();
        topicConnectionFactoryNameMapping.setAttributeName("m_topicConnectionFactoryName");
        topicConnectionFactoryNameMapping.setGetMethodName("getTopicConnectionFactoryName");
        topicConnectionFactoryNameMapping.setSetMethodName("setTopicConnectionFactoryName");
        topicConnectionFactoryNameMapping.setXPath("topic-connection-factory-name/text()");
        topicConnectionFactoryNameMapping.setNullValue(TOPIC_CONNECTION_FACTORY_NAME_DEFAULT);
        descriptor.addMapping(topicConnectionFactoryNameMapping);

        XMLDirectMapping topicNameMapping = new XMLDirectMapping();
        topicNameMapping.setAttributeName("m_topicName");
        topicNameMapping.setGetMethodName("getTopicName");
        topicNameMapping.setSetMethodName("setTopicName");
        topicNameMapping.setXPath("topic-name/text()");
        topicNameMapping.setNullValue(TOPIC_NAME_DEFAULT);
        descriptor.addMapping(topicNameMapping);

        XMLCompositeObjectMapping jndiNamingServiceConfigMapping = new XMLCompositeObjectMapping();
        jndiNamingServiceConfigMapping.setReferenceClass(JNDINamingServiceConfig.class);
        jndiNamingServiceConfigMapping.setAttributeName("m_jndiNamingServiceConfig");
        jndiNamingServiceConfigMapping.setGetMethodName("getJNDINamingServiceConfig");
        jndiNamingServiceConfigMapping.setSetMethodName("setJNDINamingServiceConfig");
        jndiNamingServiceConfigMapping.setXPath("jndi-naming-service");
        descriptor.addMapping(jndiNamingServiceConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildJMSTopicTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JMSTopicTransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(JMSPublishingTransportManagerConfig.class);

        return descriptor;
    }

    public ClassDescriptor buildJNDINamingServiceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JNDINamingServiceConfig.class);

        XMLDirectMapping urlMapping = new XMLDirectMapping();
        urlMapping.setAttributeName("m_url");
        urlMapping.setGetMethodName("getURL");
        urlMapping.setSetMethodName("setURL");
        urlMapping.setXPath("url/text()");
        descriptor.addMapping(urlMapping);

        XMLDirectMapping usernameMapping = new XMLDirectMapping();
        usernameMapping.setAttributeName("m_username");
        usernameMapping.setGetMethodName("getUsername");
        usernameMapping.setSetMethodName("setUsername");
        usernameMapping.setXPath("user-name/text()");
        usernameMapping.setNullValue(USERNAME_DEFAULT);
        descriptor.addMapping(usernameMapping);

        XMLDirectMapping encryptionClassMapping = new XMLDirectMapping();
        encryptionClassMapping.setAttributeName("m_encryptionClass");
        encryptionClassMapping.setGetMethodName("getEncryptionClass");
        encryptionClassMapping.setSetMethodName("setEncryptionClass");
        encryptionClassMapping.setXPath("encryption-class/text()");
        encryptionClassMapping.setNullValue(ENCRYPTION_CLASS_DEFAULT);
        descriptor.addMapping(encryptionClassMapping);

        XMLDirectMapping passwordMapping = new XMLDirectMapping();
        passwordMapping.setAttributeName("m_encryptedPassword");
        passwordMapping.setGetMethodName("getEncryptedPassword");
        passwordMapping.setSetMethodName("setEncryptedPassword");
        passwordMapping.setXPath("password/text()");
        passwordMapping.setNullValue(PASSWORD_DEFAULT);
        descriptor.addMapping(passwordMapping);

        XMLDirectMapping initialContextFactoryNameMapping = new XMLDirectMapping();
        initialContextFactoryNameMapping.setAttributeName("m_initialContextFactoryName");
        initialContextFactoryNameMapping.setGetMethodName("getInitialContextFactoryName");
        initialContextFactoryNameMapping.setSetMethodName("setInitialContextFactoryName");
        initialContextFactoryNameMapping.setXPath("initial-context-factory-name/text()");
        initialContextFactoryNameMapping.setNullValue(INITIAL_CONTEXT_FACTORY_NAME_DEFAULT);
        descriptor.addMapping(initialContextFactoryNameMapping);

        XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
        propertiesMapping.setReferenceClass(PropertyConfig.class);
        propertiesMapping.setAttributeName("m_propertyConfigs");
        propertiesMapping.setGetMethodName("getPropertyConfigs");
        propertiesMapping.setSetMethodName("setPropertyConfigs");
        propertiesMapping.setXPath("property");
        descriptor.addMapping(propertiesMapping);

        return descriptor;
    }

    public ClassDescriptor buildJavaLogConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JavaLogConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LogConfig.class);

        XMLCompositeObjectMapping loginConfigMapping = new XMLCompositeObjectMapping();
        loginConfigMapping.setReferenceClass(LoggingOptionsConfig.class);
        loginConfigMapping.setAttributeName("m_loggingOptions");
        loginConfigMapping.setGetMethodName("getLoggingOptions");
        loginConfigMapping.setSetMethodName("setLoggingOptions");
        loginConfigMapping.setXPath("logging-options");
        descriptor.addMapping(loginConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildServerLogConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ServerLogConfig.class);
        descriptor.getInheritancePolicy().setParentClass(LogConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildLogConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(LogConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSessionLogConfig.class, "eclipselink-log");
        descriptor.getInheritancePolicy().addClassIndicator(JavaLogConfig.class, "java-log");
        descriptor.getInheritancePolicy().addClassIndicator(ServerLogConfig.class, "server-log");

        return descriptor;
    }

    public ClassDescriptor buildLoginConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(LoginConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(EISLoginConfig.class, "eis-login");
        descriptor.getInheritancePolicy().addClassIndicator(XMLLoginConfig.class, "xml-login");
        descriptor.getInheritancePolicy().addClassIndicator(DatabaseLoginConfig.class, "database-login");

        XMLDirectMapping platformClassMapping = new XMLDirectMapping();
        platformClassMapping.setAttributeName("m_platformClass");
        platformClassMapping.setGetMethodName("getPlatformClass");
        platformClassMapping.setSetMethodName("setPlatformClass");
        platformClassMapping.setXPath("platform-class/text()");
        platformClassMapping.setConverter(new Converter(){
            private Map platformList;
            private String oldPrefix = "oracle.toplink.";
            private String newPrefix = "org.eclipse.persistence.";
            private String oldOxmPrefix = oldPrefix + "ox.";
            private String newOxmPrefix = newPrefix + "oxm.";
            public Object convertObjectValueToDataValue(Object objectValue, Session session){
                //if this code is writin out, write out the converted value
                return objectValue;
            }

            public Object convertDataValueToObjectValue(Object dataValue, Session session){
                if(dataValue == null) {
                    return null;
                }
                // convert deprecated platforms to new platforms
                if(((String)dataValue).startsWith(oldPrefix)) {
                    if(((String)dataValue).startsWith(oldOxmPrefix)) {
                        dataValue = ((String)dataValue).replaceFirst(oldOxmPrefix, newOxmPrefix);
                    } else {
                        dataValue = ((String)dataValue).replaceFirst(oldPrefix, newPrefix);
                    }
                }
                Object result = platformList.get(dataValue);
                if (result == null){
                    return dataValue;
                }else{
                    return result;
                }
            }

            public boolean isMutable(){
                return false;
            }

            public void initialize(DatabaseMapping mapping, Session session){
                this.platformList = new HashMap();
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.AccessPlatform", "org.eclipse.persistence.platform.database.AccessPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.AttunityPlatform", "org.eclipse.persistence.platform.database.AttunityPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.CloudscapePlatform", "org.eclipse.persistence.platform.database.CloudscapePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DatabasePlatform", "org.eclipse.persistence.platform.database.DatabasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DB2MainframePlatform", "org.eclipse.persistence.platform.database.DB2MainframePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DB2Platform", "org.eclipse.persistence.platform.database.DB2Platform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DBasePlatform", "org.eclipse.persistence.platform.database.DBasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.HSQLPlatform", "org.eclipse.persistence.platform.database.HSQLPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.InformixPlatform", "org.eclipse.persistence.platform.database.InformixPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.OraclePlatform", "org.eclipse.persistence.platform.database.OraclePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.PointBasePlatform", "org.eclipse.persistence.platform.database.PointBasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLServerPlatform", "org.eclipse.persistence.platform.database.SQLServerPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SybasePlatform", "org.eclipse.persistence.platform.database.SybasePlatform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle8Platform", "org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle9Platform", "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
                this.platformList.put("org.eclipse.persistence.platform.database.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");
            }
            
        });
        descriptor.addMapping(platformClassMapping);

        XMLDirectMapping usernameMapping = new XMLDirectMapping();
        usernameMapping.setAttributeName("m_username");
        usernameMapping.setGetMethodName("getUsername");
        usernameMapping.setSetMethodName("setUsername");
        usernameMapping.setXPath("user-name/text()");
        descriptor.addMapping(usernameMapping);

        XMLDirectMapping encryptionClassMapping = new XMLDirectMapping();
        encryptionClassMapping.setAttributeName("m_encryptionClass");
        encryptionClassMapping.setGetMethodName("getEncryptionClass");
        encryptionClassMapping.setSetMethodName("setEncryptionClass");
        encryptionClassMapping.setXPath("encryption-class/text()");
        encryptionClassMapping.setNullValue(ENCRYPTION_CLASS_DEFAULT);
        descriptor.addMapping(encryptionClassMapping);

        XMLDirectMapping passwordMapping = new XMLDirectMapping();
        passwordMapping.setAttributeName("m_password");
        passwordMapping.setGetMethodName("getEncryptedPassword");
        passwordMapping.setSetMethodName("setEncryptedPassword");
        passwordMapping.setXPath("password/text()");
        descriptor.addMapping(passwordMapping);

        XMLDirectMapping tableQualifierMapping = new XMLDirectMapping();
        tableQualifierMapping.setAttributeName("m_tableQualifier");
        tableQualifierMapping.setGetMethodName("getTableQualifier");
        tableQualifierMapping.setSetMethodName("setTableQualifier");
        tableQualifierMapping.setXPath("table-qualifier/text()");
        descriptor.addMapping(tableQualifierMapping);

        XMLDirectMapping externalConnectionPoolMapping = new XMLDirectMapping();
        externalConnectionPoolMapping.setAttributeName("m_externalConnectionPooling");
        externalConnectionPoolMapping.setGetMethodName("getExternalConnectionPooling");
        externalConnectionPoolMapping.setSetMethodName("setExternalConnectionPooling");
        externalConnectionPoolMapping.setXPath("external-connection-pooling/text()");
        externalConnectionPoolMapping.setNullValue(Boolean.valueOf(EXTERNAL_CONNECTION_POOL_DEFAULT));
        descriptor.addMapping(externalConnectionPoolMapping);

        XMLDirectMapping externalTransactionControllerMapping = new XMLDirectMapping();
        externalTransactionControllerMapping.setAttributeName("m_externalTransactionController");
        externalTransactionControllerMapping.setGetMethodName("getExternalTransactionController");
        externalTransactionControllerMapping.setSetMethodName("setExternalTransactionController");
        externalTransactionControllerMapping.setXPath("external-transaction-controller/text()");
        externalTransactionControllerMapping.setNullValue(Boolean.valueOf(EXTERNAL_TRANSACTION_CONTROLLER_DEFAULT));
        descriptor.addMapping(externalTransactionControllerMapping);

        XMLCompositeObjectMapping sequencingMapping = new XMLCompositeObjectMapping();
        sequencingMapping.setAttributeName("m_sequencingConfig");
        sequencingMapping.setSetMethodName("setSequencingConfig");
        sequencingMapping.setGetMethodName("getSequencingConfig");
        sequencingMapping.setReferenceClass(SequencingConfig.class);
        sequencingMapping.setXPath("sequencing");
        descriptor.addMapping(sequencingMapping);

        XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
        propertiesMapping.setReferenceClass(PropertyConfig.class);
        propertiesMapping.setAttributeName("m_propertyConfigs");
        propertiesMapping.setGetMethodName("getPropertyConfigs");
        propertiesMapping.setSetMethodName("setPropertyConfigs");
        propertiesMapping.setXPath("property");
        descriptor.addMapping(propertiesMapping);

        return descriptor;
    }

    public ClassDescriptor buildPoolsConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PoolsConfig.class);

        XMLCompositeObjectMapping readConnectionPoolConfigMapping = new XMLCompositeObjectMapping();
        readConnectionPoolConfigMapping.setReferenceClass(ReadConnectionPoolConfig.class);
        readConnectionPoolConfigMapping.setAttributeName("m_readConnectionPoolConfig");
        readConnectionPoolConfigMapping.setGetMethodName("getReadConnectionPoolConfig");
        readConnectionPoolConfigMapping.setSetMethodName("setReadConnectionPoolConfig");
        readConnectionPoolConfigMapping.setXPath("read-connection-pool");
        descriptor.addMapping(readConnectionPoolConfigMapping);

        XMLCompositeObjectMapping writeConnectionPoolConfigMapping = new XMLCompositeObjectMapping();
        writeConnectionPoolConfigMapping.setReferenceClass(WriteConnectionPoolConfig.class);
        writeConnectionPoolConfigMapping.setAttributeName("m_writeConnectionPoolConfig");
        writeConnectionPoolConfigMapping.setGetMethodName("getWriteConnectionPoolConfig");
        writeConnectionPoolConfigMapping.setSetMethodName("setWriteConnectionPoolConfig");
        writeConnectionPoolConfigMapping.setXPath("write-connection-pool");
        descriptor.addMapping(writeConnectionPoolConfigMapping);

        XMLCompositeObjectMapping sequenceConnectionPoolConfigMapping = new XMLCompositeObjectMapping();
        sequenceConnectionPoolConfigMapping.setReferenceClass(ConnectionPoolConfig.class);
        sequenceConnectionPoolConfigMapping.setAttributeName("m_sequenceConnectionPoolConfig");
        sequenceConnectionPoolConfigMapping.setGetMethodName("getSequenceConnectionPoolConfig");
        sequenceConnectionPoolConfigMapping.setSetMethodName("setSequenceConnectionPoolConfig");
        sequenceConnectionPoolConfigMapping.setXPath("sequence-connection-pool");
        descriptor.addMapping(sequenceConnectionPoolConfigMapping);

        XMLCompositeCollectionMapping connectionPoolConfigsMapping = new XMLCompositeCollectionMapping();
        connectionPoolConfigsMapping.setReferenceClass(ConnectionPoolConfig.class);
        connectionPoolConfigsMapping.setAttributeName("m_connectionPoolConfigs");
        connectionPoolConfigsMapping.setGetMethodName("getConnectionPoolConfigs");
        connectionPoolConfigsMapping.setSetMethodName("setConnectionPoolConfigs");
        connectionPoolConfigsMapping.setXPath("connection-pool");
        descriptor.addMapping(connectionPoolConfigsMapping);

        return descriptor;
    }

    public ClassDescriptor buildProjectClassConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ProjectClassConfig.class);
        descriptor.getInheritancePolicy().setParentClass(ProjectConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildProjectConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ProjectConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(ProjectXMLConfig.class, "xml");
        descriptor.getInheritancePolicy().addClassIndicator(ProjectClassConfig.class, "class");

        XMLDirectMapping projectStringMapping = new XMLDirectMapping();
        projectStringMapping.setAttributeName("m_projectString");
        projectStringMapping.setGetMethodName("getProjectString");
        projectStringMapping.setSetMethodName("setProjectString");
        projectStringMapping.setXPath("text()");
        descriptor.addMapping(projectStringMapping);

        return descriptor;
    }

    public ClassDescriptor buildProjectXMLConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ProjectXMLConfig.class);
        descriptor.getInheritancePolicy().setParentClass(ProjectConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildPropertyConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PropertyConfig.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("m_value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath("@value");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }


    public ClassDescriptor buildRMIIIOPTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RMIIIOPTransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(RMITransportManagerConfig.class);
        return descriptor;
    }


    public ClassDescriptor buildRMIRegistryNamingServiceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RMIRegistryNamingServiceConfig.class);

        XMLDirectMapping urlMapping = new XMLDirectMapping();
        urlMapping.setAttributeName("m_url");
        urlMapping.setGetMethodName("getURL");
        urlMapping.setSetMethodName("setURL");
        urlMapping.setXPath("url/text()");
        descriptor.addMapping(urlMapping);

        return descriptor;
    }

    public ClassDescriptor buildRMITransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RMITransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(TransportManagerConfig.class);

        XMLDirectMapping sendModeMapping = new XMLDirectMapping();
        sendModeMapping.setAttributeName("m_sendMode");
        sendModeMapping.setGetMethodName("getSendMode");
        sendModeMapping.setSetMethodName("setSendMode");
        sendModeMapping.setXPath("send-mode/text()");
        sendModeMapping.setNullValue(SEND_MODE_DEFAULT);
        descriptor.addMapping(sendModeMapping);

        XMLCompositeObjectMapping discoveryConfigMapping = new XMLCompositeObjectMapping();
        discoveryConfigMapping.setReferenceClass(DiscoveryConfig.class);
        discoveryConfigMapping.setAttributeName("m_discoveryConfig");
        discoveryConfigMapping.setGetMethodName("getDiscoveryConfig");
        discoveryConfigMapping.setSetMethodName("setDiscoveryConfig");
        discoveryConfigMapping.setXPath("discovery");
        descriptor.addMapping(discoveryConfigMapping);

        XMLCompositeObjectMapping jndiNamingServiceConfigMapping = new XMLCompositeObjectMapping();
        jndiNamingServiceConfigMapping.setReferenceClass(JNDINamingServiceConfig.class);
        jndiNamingServiceConfigMapping.setAttributeName("m_jndiNamingServiceConfig");
        jndiNamingServiceConfigMapping.setGetMethodName("getJNDINamingServiceConfig");
        jndiNamingServiceConfigMapping.setSetMethodName("setJNDINamingServiceConfig");
        jndiNamingServiceConfigMapping.setXPath("jndi-naming-service");
        descriptor.addMapping(jndiNamingServiceConfigMapping);

        XMLCompositeObjectMapping rmiRegistryNamingServiceConfigMapping = new XMLCompositeObjectMapping();
        rmiRegistryNamingServiceConfigMapping.setReferenceClass(RMIRegistryNamingServiceConfig.class);
        rmiRegistryNamingServiceConfigMapping.setAttributeName("m_rmiRegistryNamingServiceConfig");
        rmiRegistryNamingServiceConfigMapping.setGetMethodName("getRMIRegistryNamingServiceConfig");
        rmiRegistryNamingServiceConfigMapping.setSetMethodName("setRMIRegistryNamingServiceConfig");
        rmiRegistryNamingServiceConfigMapping.setXPath("rmi-registry-naming-service");
        descriptor.addMapping(rmiRegistryNamingServiceConfigMapping);

        return descriptor;
    }

    public ClassDescriptor buildReadConnectionPoolConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReadConnectionPoolConfig.class);
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping maxConnectionsMapping = new XMLDirectMapping();
        maxConnectionsMapping.setAttributeName("m_maxConnections");
        maxConnectionsMapping.setGetMethodName("getMaxConnections");
        maxConnectionsMapping.setSetMethodName("setMaxConnections");
        maxConnectionsMapping.setXPath("max-connections/text()");
        maxConnectionsMapping.setNullValue(Integer.valueOf(READ_CONNECTION_POOL_MAX_DEFAULT));
        descriptor.addMapping(maxConnectionsMapping);

        XMLDirectMapping minConnectionsMapping = new XMLDirectMapping();
        minConnectionsMapping.setAttributeName("m_minConnections");
        minConnectionsMapping.setGetMethodName("getMinConnections");
        minConnectionsMapping.setSetMethodName("setMinConnections");
        minConnectionsMapping.setXPath("min-connections/text()");
        minConnectionsMapping.setNullValue(Integer.valueOf(READ_CONNECTION_POOL_MIN_DEFAULT));
        descriptor.addMapping(minConnectionsMapping);

        XMLCompositeObjectMapping loginConfigMapping = new XMLCompositeObjectMapping();
        loginConfigMapping.setReferenceClass(LoginConfig.class);
        loginConfigMapping.setAttributeName("m_loginConfig");
        loginConfigMapping.setGetMethodName("getLoginConfig");
        loginConfigMapping.setSetMethodName("setLoginConfig");
        loginConfigMapping.setXPath("login");
        descriptor.addMapping(loginConfigMapping);

        XMLDirectMapping exclusiveMapping = new XMLDirectMapping();
        exclusiveMapping.setAttributeName("m_exclusive");
        exclusiveMapping.setGetMethodName("getExclusive");
        exclusiveMapping.setSetMethodName("setExclusive");
        exclusiveMapping.setXPath("exclusive/text()");
        exclusiveMapping.setNullValue(Boolean.valueOf(EXCLUSIVE_DEFAULT));
        descriptor.addMapping(exclusiveMapping);

        return descriptor;
    }

    public ClassDescriptor buildRemoteCommandManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RemoteCommandManagerConfig.class);

        XMLDirectMapping channelMapping = new XMLDirectMapping();
        channelMapping.setAttributeName("m_channel");
        channelMapping.setGetMethodName("getChannel");
        channelMapping.setSetMethodName("setChannel");
        channelMapping.setXPath("channel/text()");
        channelMapping.setNullValue(CHANNEL_DEFAULT);
        descriptor.addMapping(channelMapping);

        XMLCompositeObjectMapping commandsConfigMapping = new XMLCompositeObjectMapping();
        commandsConfigMapping.setReferenceClass(CommandsConfig.class);
        commandsConfigMapping.setAttributeName("m_commandsConfig");
        commandsConfigMapping.setGetMethodName("getCommandsConfig");
        commandsConfigMapping.setSetMethodName("setCommandsConfig");
        commandsConfigMapping.setXPath("commands");
        descriptor.addMapping(commandsConfigMapping);

        XMLCompositeObjectMapping transportManagerMapping = new XMLCompositeObjectMapping();
        transportManagerMapping.setReferenceClass(TransportManagerConfig.class);
        transportManagerMapping.setAttributeName("m_transportManager");
        transportManagerMapping.setGetMethodName("getTransportManagerConfig");
        transportManagerMapping.setSetMethodName("setTransportManagerConfig");
        transportManagerMapping.setXPath("transport");
        descriptor.addMapping(transportManagerMapping);

        return descriptor;
    }

    public ClassDescriptor buildServerPlatformConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ServerPlatformConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(CustomServerPlatformConfig.class, "custom-platform");
        descriptor.getInheritancePolicy().addClassIndicator(Oc4jPlatformConfig.class, "oc4j-platform");
        descriptor.getInheritancePolicy().addClassIndicator(GlassfishPlatformConfig.class, "glassfish-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebLogic_6_1_PlatformConfig.class, "weblogic-61-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebLogic_7_0_PlatformConfig.class, "weblogic-70-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebLogic_8_1_PlatformConfig.class, "weblogic-81-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_4_0_PlatformConfig.class, "websphere-40-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_5_0_PlatformConfig.class, "websphere-50-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_5_1_PlatformConfig.class, "websphere-51-platform");
        descriptor.getInheritancePolicy().addClassIndicator(WebSphere_6_0_PlatformConfig.class, "websphere-60-platform");
        descriptor.getInheritancePolicy().addClassIndicator(JBossPlatformConfig.class, "jboss-platform");
        descriptor.getInheritancePolicy().addClassIndicator(NetWeaver_7_1_PlatformConfig.class, "netweaver-71-platform");
        

        XMLDirectMapping enableRuntimeServicesMapping = new XMLDirectMapping();
        enableRuntimeServicesMapping.setAttributeName("m_enableRuntimeServices");
        enableRuntimeServicesMapping.setGetMethodName("getEnableRuntimeServices");
        enableRuntimeServicesMapping.setSetMethodName("setEnableRuntimeServices");
        enableRuntimeServicesMapping.setXPath("enable-runtime-services/text()");
        enableRuntimeServicesMapping.setNullValue(Boolean.valueOf(ENABLE_RUNTIME_SERVICES_DEFAULT));
        descriptor.addMapping(enableRuntimeServicesMapping);

        XMLDirectMapping enableJTAMapping = new XMLDirectMapping();
        enableJTAMapping.setAttributeName("m_enableJTA");
        enableJTAMapping.setGetMethodName("getEnableJTA");
        enableJTAMapping.setSetMethodName("setEnableJTA");
        enableJTAMapping.setXPath("enable-jta/text()");
        enableJTAMapping.setNullValue(Boolean.valueOf(ENABLE_JTA_DEFAULT));
        descriptor.addMapping(enableJTAMapping);

        return descriptor;
    }

    public ClassDescriptor buildServerPlatformConfigDescriptorFor(Class serverPlatformClass) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(serverPlatformClass);
        descriptor.getInheritancePolicy().setParentClass(ServerPlatformConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildServerSessionConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ServerSessionConfig.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseSessionConfig.class);

        XMLCompositeObjectMapping poolsConfigMapping = new XMLCompositeObjectMapping();
        poolsConfigMapping.setReferenceClass(PoolsConfig.class);
        poolsConfigMapping.setAttributeName("m_poolsConfig");
        poolsConfigMapping.setGetMethodName("getPoolsConfig");
        poolsConfigMapping.setSetMethodName("setPoolsConfig");
        poolsConfigMapping.setXPath("connection-pools");
        descriptor.addMapping(poolsConfigMapping);

        XMLCompositeObjectMapping connectionPolicyMapping = new XMLCompositeObjectMapping();
        connectionPolicyMapping.setReferenceClass(ConnectionPolicyConfig.class);
        connectionPolicyMapping.setAttributeName("m_connectionPolicyConfig");
        connectionPolicyMapping.setGetMethodName("getConnectionPolicyConfig");
        connectionPolicyMapping.setSetMethodName("setConnectionPolicyConfig");
        connectionPolicyMapping.setXPath("connection-policy");
        descriptor.addMapping(connectionPolicyMapping);

        return descriptor;
    }

    public ClassDescriptor buildSessionBrokerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SessionBrokerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(SessionConfig.class);

        XMLCompositeDirectCollectionMapping sessionNamesMapping = new XMLCompositeDirectCollectionMapping();
        sessionNamesMapping.setAttributeName("m_sessionNames");
        sessionNamesMapping.setGetMethodName("getSessionNames");
        sessionNamesMapping.setSetMethodName("setSessionNames");
        sessionNamesMapping.setXPath("session-name/text()");
        descriptor.addMapping(sessionNamesMapping);

        return descriptor;
    }

    public ClassDescriptor buildSessionConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SessionConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DatabaseSessionConfig.class, "database-session");
        descriptor.getInheritancePolicy().addClassIndicator(ServerSessionConfig.class, "server-session");
        descriptor.getInheritancePolicy().addClassIndicator(SessionBrokerConfig.class, "session-broker");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping serverPlatformConfigMapping = new XMLCompositeObjectMapping();
        serverPlatformConfigMapping.setReferenceClass(ServerPlatformConfig.class);
        serverPlatformConfigMapping.setAttributeName("m_serverPlatformConfig");
        serverPlatformConfigMapping.setGetMethodName("getServerPlatformConfig");
        serverPlatformConfigMapping.setSetMethodName("setServerPlatformConfig");
        serverPlatformConfigMapping.setXPath("server-platform");
        descriptor.addMapping(serverPlatformConfigMapping);

        XMLCompositeObjectMapping remoteCommandManagerConfigMapping = new XMLCompositeObjectMapping();
        remoteCommandManagerConfigMapping.setReferenceClass(RemoteCommandManagerConfig.class);
        remoteCommandManagerConfigMapping.setAttributeName("m_remoteCommandManagerConfig");
        remoteCommandManagerConfigMapping.setGetMethodName("getRemoteCommandManagerConfig");
        remoteCommandManagerConfigMapping.setSetMethodName("setRemoteCommandManagerConfig");
        remoteCommandManagerConfigMapping.setXPath("remote-command");
        descriptor.addMapping(remoteCommandManagerConfigMapping);


        XMLCompositeObjectMapping sessionEventManagerConfigMapping = new XMLCompositeObjectMapping();
        sessionEventManagerConfigMapping.setReferenceClass(SessionEventManagerConfig.class);
        sessionEventManagerConfigMapping.setAttributeName("m_sessionEventManagerConfig");
        sessionEventManagerConfigMapping.setGetMethodName("getSessionEventManagerConfig");
        sessionEventManagerConfigMapping.setSetMethodName("setSessionEventManagerConfig");
        sessionEventManagerConfigMapping.setXPath("event-listener-classes");
        descriptor.addMapping(sessionEventManagerConfigMapping);

        XMLDirectMapping profilerMapping = new XMLDirectMapping();
        profilerMapping.setAttributeName("m_profiler");
        profilerMapping.setGetMethodName("getProfiler");
        profilerMapping.setSetMethodName("setProfiler");
        profilerMapping.setXPath("profiler/text()");
        descriptor.addMapping(profilerMapping);

        XMLDirectMapping exceptionHandlerClassMapping = new XMLDirectMapping();
        exceptionHandlerClassMapping.setAttributeName("m_exceptionHandlerClass");
        exceptionHandlerClassMapping.setGetMethodName("getExceptionHandlerClass");
        exceptionHandlerClassMapping.setSetMethodName("setExceptionHandlerClass");
        exceptionHandlerClassMapping.setXPath("exception-handler-class/text()");
        descriptor.addMapping(exceptionHandlerClassMapping);

        XMLCompositeObjectMapping logConfigMapping = new XMLCompositeObjectMapping();
        logConfigMapping.setReferenceClass(LogConfig.class);
        logConfigMapping.setAttributeName("m_logConfig");
        logConfigMapping.setGetMethodName("getLogConfig");
        logConfigMapping.setSetMethodName("setLogConfig");
        logConfigMapping.setXPath("logging");
        descriptor.addMapping(logConfigMapping);

        XMLDirectMapping sessionCustomizerClassMapping = new XMLDirectMapping();
        sessionCustomizerClassMapping.setAttributeName("m_sessionCustomizerClass");
        sessionCustomizerClassMapping.setGetMethodName("getSessionCustomizerClass");
        sessionCustomizerClassMapping.setSetMethodName("setSessionCustomizerClass");
        sessionCustomizerClassMapping.setXPath("session-customizer-class/text()");
        descriptor.addMapping(sessionCustomizerClassMapping);

        return descriptor;
    }

    public ClassDescriptor buildSessionEventManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SessionEventManagerConfig.class);

        XMLCompositeDirectCollectionMapping sessionEventListenersMapping = new XMLCompositeDirectCollectionMapping();
        sessionEventListenersMapping.setAttributeName("m_sessionEventListeners");
        sessionEventListenersMapping.setGetMethodName("getSessionEventListeners");
        sessionEventListenersMapping.setSetMethodName("setSessionEventListeners");
        sessionEventListenersMapping.setXPath("event-listener-class/text()");
        descriptor.addMapping(sessionEventListenersMapping);

        return descriptor;
    }


    public ClassDescriptor buildSunCORBATransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SunCORBATransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(TransportManagerConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildSessionConfigsDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setDefaultRootElement("sessions");
        descriptor.setJavaClass(SessionConfigs.class);
        descriptor.setSchemaReference(new XMLSchemaClassPathReference(ECLIPSELINK_SESSIONS_SCHEMA));

        XMLDirectMapping versionMapping = new XMLDirectMapping();
        versionMapping.setAttributeName("m_version");
        versionMapping.setGetMethodName("getVersion");
        versionMapping.setSetMethodName("setVersion");
        versionMapping.setXPath("@version");
        descriptor.addMapping(versionMapping);

        XMLCompositeCollectionMapping sessionConfigsMapping = new XMLCompositeCollectionMapping();
        sessionConfigsMapping.setReferenceClass(SessionConfig.class);
        sessionConfigsMapping.setAttributeName("m_sessionConfigs");
        sessionConfigsMapping.setGetMethodName("getSessionConfigs");
        sessionConfigsMapping.setSetMethodName("setSessionConfigs");
        sessionConfigsMapping.setXPath("session");
        descriptor.addMapping(sessionConfigsMapping);

        return descriptor;
    }

    public ClassDescriptor buildTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransportManagerConfig.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(RMITransportManagerConfig.class, "rmi-transport");
        descriptor.getInheritancePolicy().addClassIndicator(RMIIIOPTransportManagerConfig.class, "rmi-iiop-transport");
        descriptor.getInheritancePolicy().addClassIndicator(JMSPublishingTransportManagerConfig.class, "jms-publishing-transport");
        descriptor.getInheritancePolicy().addClassIndicator(JMSTopicTransportManagerConfig.class, "jms-topic-transport");
        descriptor.getInheritancePolicy().addClassIndicator(SunCORBATransportManagerConfig.class, "sun-corba-transport");
        descriptor.getInheritancePolicy().addClassIndicator(UserDefinedTransportManagerConfig.class, "user-defined-transport");

        XMLDirectMapping onConnectionErrorMapping = new XMLDirectMapping();
        onConnectionErrorMapping.setAttributeName("m_onConnectionError");
        onConnectionErrorMapping.setGetMethodName("getOnConnectionError");
        onConnectionErrorMapping.setSetMethodName("setOnConnectionError");
        onConnectionErrorMapping.setXPath("on-connection-error/text()");
        onConnectionErrorMapping.setNullValue(ON_CONNECTION_ERROR_DEFAULT);
        descriptor.addMapping(onConnectionErrorMapping);

        return descriptor;
    }

    public ClassDescriptor buildUserDefinedTransportManagerConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UserDefinedTransportManagerConfig.class);
        descriptor.getInheritancePolicy().setParentClass(TransportManagerConfig.class);

        XMLDirectMapping transportClassMapping = new XMLDirectMapping();
        transportClassMapping.setAttributeName("m_transportClass");
        transportClassMapping.setGetMethodName("getTransportClass");
        transportClassMapping.setSetMethodName("setTransportClass");
        transportClassMapping.setXPath("transport-class/text()");
        descriptor.addMapping(transportClassMapping);

        return descriptor;
    }

    public ClassDescriptor buildWriteConnectionPoolConfigDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)buildConnectionPoolConfigDescriptor();
        descriptor.setJavaClass(WriteConnectionPoolConfig.class);
        return descriptor;
    }

    public ClassDescriptor buildSequencingConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SequencingConfig.class);
        descriptor.setDefaultRootElement("sequencing");

        XMLCompositeObjectMapping defaultSequenceMapping = new XMLCompositeObjectMapping();
        defaultSequenceMapping.setAttributeName("m_defaultSequenceConfig");
        defaultSequenceMapping.setSetMethodName("setDefaultSequenceConfig");
        defaultSequenceMapping.setGetMethodName("getDefaultSequenceConfig");
        defaultSequenceMapping.setReferenceClass(SequenceConfig.class);
        defaultSequenceMapping.setXPath("default-sequence");
        descriptor.addMapping(defaultSequenceMapping);

        XMLCompositeCollectionMapping sequencesMapping = new XMLCompositeCollectionMapping();
        sequencesMapping.setAttributeName("sequenceConfigs");
        sequencesMapping.setSetMethodName("setSequenceConfigs");
        sequencesMapping.setGetMethodName("getSequenceConfigs");
        sequencesMapping.setReferenceClass(SequenceConfig.class);
        sequencesMapping.setXPath("sequences/sequence");
        descriptor.addMapping(sequencesMapping);

        return descriptor;
    }

    public ClassDescriptor buildSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SequenceConfig.class);
        descriptor.setDefaultRootElement("sequence");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSequenceConfig.class, "default-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(NativeSequenceConfig.class, "native-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(TableSequenceConfig.class, "table-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(UnaryTableSequenceConfig.class, "unary-table-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(XMLFileSequenceConfig.class, "xmlfile-sequence");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("name/text()");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping preallocationSizeMapping = new XMLDirectMapping();
        preallocationSizeMapping.setAttributeName("m_preallocationSize");
        preallocationSizeMapping.setGetMethodName("getPreallocationSize");
        preallocationSizeMapping.setSetMethodName("setPreallocationSize");
        preallocationSizeMapping.setXPath("preallocation-size/text()");
        preallocationSizeMapping.setNullValue(Integer.valueOf(50));
        descriptor.addMapping(preallocationSizeMapping);

        return descriptor;
    }

    public ClassDescriptor buildDefaultSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DefaultSequenceConfig.class);

        descriptor.getInheritancePolicy().setParentClass(SequenceConfig.class);

        return descriptor;
    }

    public ClassDescriptor buildNativeSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NativeSequenceConfig.class);

        descriptor.getInheritancePolicy().setParentClass(SequenceConfig.class);

        return descriptor;
    }

    public ClassDescriptor buildTableSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TableSequenceConfig.class);

        descriptor.getInheritancePolicy().setParentClass(SequenceConfig.class);

        XMLDirectMapping tableNameMapping = new XMLDirectMapping();
        tableNameMapping.setAttributeName("m_table");
        tableNameMapping.setGetMethodName("getTable");
        tableNameMapping.setSetMethodName("setTable");
        tableNameMapping.setXPath("table/text()");
        tableNameMapping.setNullValue("SEQUENCE");
        descriptor.addMapping(tableNameMapping);

        XMLDirectMapping nameFieldNameMapping = new XMLDirectMapping();
        nameFieldNameMapping.setAttributeName("m_nameField");
        nameFieldNameMapping.setGetMethodName("getNameField");
        nameFieldNameMapping.setSetMethodName("setNameField");
        nameFieldNameMapping.setXPath("name-field/text()");
        nameFieldNameMapping.setNullValue("SEQ_NAME");
        descriptor.addMapping(nameFieldNameMapping);

        XMLDirectMapping counterFieldNameMapping = new XMLDirectMapping();
        counterFieldNameMapping.setAttributeName("m_counterField");
        counterFieldNameMapping.setGetMethodName("getCounterField");
        counterFieldNameMapping.setSetMethodName("setCounterField");
        counterFieldNameMapping.setXPath("counter-field/text()");
        counterFieldNameMapping.setNullValue("SEQ_COUNT");
        descriptor.addMapping(counterFieldNameMapping);

        return descriptor;
    }

    public ClassDescriptor buildUnaryTableSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UnaryTableSequenceConfig.class);

        descriptor.getInheritancePolicy().setParentClass(SequenceConfig.class);

        XMLDirectMapping counterFieldNameMapping = new XMLDirectMapping();
        counterFieldNameMapping.setAttributeName("m_counterField");
        counterFieldNameMapping.setGetMethodName("getCounterField");
        counterFieldNameMapping.setSetMethodName("setCounterField");
        counterFieldNameMapping.setXPath("counter-field/text()");
        counterFieldNameMapping.setNullValue("SEQ_COUNT");
        descriptor.addMapping(counterFieldNameMapping);

        return descriptor;
    }

    public ClassDescriptor buildXMLFileSequenceConfigDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLFileSequenceConfig.class);

        descriptor.getInheritancePolicy().setParentClass(SequenceConfig.class);

        return descriptor;
    }
}
