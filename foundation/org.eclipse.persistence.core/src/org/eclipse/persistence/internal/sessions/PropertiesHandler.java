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
 *      Gordon Yorke - VM managed entity detachment
 *     Eduard Bartsch, SAP - Fix for Bug 351186 - ConcurrentModificationException Exception in PropertiesHandler 
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.logging.Level;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.persistence.FlushModeType;

import org.eclipse.persistence.annotations.IdValidation;
import org.eclipse.persistence.config.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.logging.SessionLog;

/**
 * 
 * The class processes some of EclipseLink properties.
 * The class may be used for any properties, but it only makes sense 
 * to use it in the following two cases:
 *      1. There is a set of legal values defined
 *          either requiring translation (like CacheTypeProp);
 *          or not (like LoggingLevelProp).
 *      2. Property is really a prefix from which the property obtained by 
 *      appending entity or class name (like DescriptorCustomizerProp -
 *      it corresponds to "eclipselink.descriptor.customizer." prefix that allows to
 *      define properties like "eclipselink.descriptor.customizer.myPackage.MyClass"). 
 * 
 * EclipseLink properties and their values defined in org.eclipse.persistence.config package.
 * 
 * To add a new property:
 *   Define a new property in PersistenceUnitProperties;
 *   Add a class containing property's values if required to config package (like CacheType);
 *      Alternatively values may be already defined elsewhere (like in LoggingLevelProp).
 *   Add an inner class to this class extending Prop corresponding to the new property (like CacheTypeProp);
 *      The first constructor parameter is property name; the second is default value;
 *      In constructor 
 *          provide 2-dimensional value array in case the values should be translated (like CacheTypeProp);
 *              in case translation is not required provide a single-dimension array (like LoggingLevelProp).
 *   In inner class Prop static initializer addProp an instance of the new prop class (like addProp(new CacheTypeProp())).
 * 
 * @see PersistenceUnitProperties
 * @see CacheType
 * @see TargetDatabase
 * @see TargetServer
 * 
 *  05/28/2008-1.0M8 Andrei Ilitchev. 
 *     - 224964: Provide support for Proxy Authentication through JPA.
 *     Added support for CONNECTION_EXCLUSIVE. Also added BooleanProp to allow simpler way of creating boolean-valued properties:
 *     instead of defining a new class for each new Boolean property just add a new instance of BooleanProp with property name and default:
 *           addProp(new BooleanProp(PersistenceUnitProperties.CONNECTION_EXCLUSIVE, "false"));
 *     Changed the existing boolean-valued properties to use this approach, also
 *     applied the same approach to LOGGING_LEVEL and CATEGORY_LOGGING_LEVEL_
 *     Also introduced a new version of getSessionPropertyValue that takes properties:
 *         public static String getSessionPropertyValue(String name, Map m, AbstractSession session) {
 *     it's convenient for use in EntityManagerImpl: first searches the passed properties then (recursively) properties of the session, then System properties.
 */
public class PropertiesHandler {
    
    /**
     * INTERNAL:
     * Gets property value from the map, if none found looks in System properties.
     * Use this to get a value for a non-prefixed property.
     * Could be used on prefixes (like "org.eclipse.persistence.cache-type.") too,
     * but will always return null
     * Throws IllegalArgumentException in case the property value is illegal.
     */
    public static String getPropertyValue(String name, Map m) {
        return getPropertyValue(name, m, true);
    }

    public static String getPropertyValueLogDebug(String name, Map m, AbstractSession session) {
        return getPropertyValueLogDebug(name, m, session, true);
    }
    
    public static String getPropertyValue(String name, Map m, boolean useSystemAsDefault) {
        return Prop.getPropertyValueToApply(name, m, null, useSystemAsDefault);
    }

    public static String getPropertyValueLogDebug(String name, Map m, AbstractSession session, boolean useSystemAsDefault) {
        return Prop.getPropertyValueToApply(name, m, session, useSystemAsDefault);
    }
    
    /**
     * INTERNAL:
     * Given property name and value verifies and translates the value.
     * Throws IllegalArgumentException in case the property value is illegal.
     */
    public static String getPropertyValue(String name, String value) {
        return Prop.getPropertyValueToApply(name, value, null);
    }

    public static String getPropertyValueLogDebug(String name, String value, AbstractSession session) {
        return Prop.getPropertyValueToApply(name, value, session);
    }
    
    /**
     * INTERNAL:
     * Gets property value from the map, if none found looks in System properties.
     * Use this to get a value for a prefixed property:
     * for "org.eclipse.persistence.cache-type.Employee"
     * pass "org.eclipse.persistence.cache-type.", "Employee".
     * Throws IllegalArgumentException in case the property value is illegal.
     */
    public static String getPrefixedPropertyValue(String prefix, String suffix, Map m) {
        return (String)getPrefixValues(prefix, m).get(suffix);
    }

    /**
     * INTERNAL:
     * Gets properties' values from the map, if none found looks in System properties.
     * In the returned map values keyed by suffixes.
     * Use it with prefixes (like "org.eclipse.persistence.cache-type.").
     * Could be used on simple properties (not prefixes, too),
     * but will always return either an empty map or a map containing a single 
     * value keyed by an empty String.
     * Throws IllegalArgumentException in case the property value is illegal.
     */
    public static Map getPrefixValues(String prefix, Map m) {
        return Prop.getPrefixValuesToApply(prefix, m, null, true);
    }
    
    public static Map getPrefixValuesLogDebug(String prefix, Map m, AbstractSession session) {
        return Prop.getPrefixValuesToApply(prefix, m, session, true);
    }
    
    /**
     * INTERNAL:
     * Returns the default property value that should be applied.
     * Throws IllegalArgumentException in case the name doesn't correspond
     * to any property.
     */
    public static String getDefaultPropertyValue(String name) {
        return Prop.getDefaultPropertyValueToApply(name, null);
    }
    
    public static String getDefaultPropertyValueLogDebug(String name, AbstractSession session) {
        return Prop.getDefaultPropertyValueToApply(name, session);
    }
    
    /**
     * INTERNAL:
     * Empty String value indicates that the default property value should be used.
     */
    protected static boolean shouldUseDefault(String value) {
        return value != null &&  value.length() == 0;
    }
    
    protected static abstract class Prop {
        static HashMap mainMap = new HashMap();
        Object[] valueArray;
        HashMap valueMap;
        String name;
        String defaultValue;
        String defaultValueToApply;
        boolean valueToApplyMayBeNull;
        boolean shouldReturnOriginalValueIfValueToApplyNotFound;
        
        static {
            addProp(new LoggerTypeProp());
            addProp(new LoggingLevelProp(PersistenceUnitProperties.LOGGING_LEVEL, Level.INFO.getName()));
            addProp(new LoggingLevelProp(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_, Level.INFO.getName()));
            addProp(new TargetDatabaseProp());
            addProp(new TargetServerProp());
            addProp(new CacheSizeProp());
            addProp(new CacheTypeProp());
            addProp(new BooleanProp(PersistenceUnitProperties.CACHE_SHARED_, "false"));
            addProp(new DescriptorCustomizerProp());
            addProp(new BatchWritingProp());
            addProp(new FlushClearCacheProp());
            addProp(new ReferenceModeProp());
            addProp(new FlushModeProp());
            addProp(new BooleanProp(PersistenceUnitProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.PERSISTENCE_CONTEXT_PERSIST_ON_COMMIT, "true"));
            addProp(new BooleanProp(PersistenceUnitProperties.PERSISTENCE_CONTEXT_COMMIT_WITHOUT_PERSIST_RULES, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.VALIDATE_EXISTENCE, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.ORDER_UPDATES, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.JOIN_EXISTING_TRANSACTION, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.COMPOSITE_UNIT, "false"));
            addProp(new BooleanProp(PersistenceUnitProperties.COMPOSITE_UNIT_MEMBER, "false"));
            addProp(new ExclusiveConnectionModeProp());
            addProp(new BooleanProp(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_IS_LAZY, "true"));
            addProp(new IdValidationProp());
            addProp(new ConnectionPoolProp());
        }
        
        Prop(String name) {
            this.name = name;
        }
        
        Prop(String name, String defaultValue) {
            this(name);
            this.defaultValue = defaultValue;
        }

        static String getPropertyValueFromMap(String name, Map m, boolean useSystemAsDefault) {
            String value = (String)m.get(name);
            return value == null && useSystemAsDefault ? System.getProperty(name) : value;
        }
    
        // Collect all entries corresponding to the prefix name.
        // Note that entries from Map m override those from System properties.
        static Map getPrefixValuesFromMap(String name, Map m, boolean useSystemAsDefault) {
            Map mapOut = new HashMap();
            
            if(useSystemAsDefault) {
                Map.Entry[] entries = (Map.Entry[]) AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return System.getProperties().entrySet().toArray(new Map.Entry[] {});
                        }
                    }
                );

                for (Map.Entry entry:entries) {
                    String str = (String)entry.getKey();
                    if(str.startsWith(name)) {
                        String entityName = str.substring(name.length(), str.length());
                        mapOut.put(entityName, entry.getValue());
                    }
                }
            }
            
            Iterator it = m.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String str = (String)entry.getKey();
                if(str.startsWith(name)) {
                    String entityName = str.substring(name.length(), str.length());
                    mapOut.put(entityName, entry.getValue());
                }
            }
            
            return mapOut;
        }
    
        static String getPropertyValue(String name, boolean shouldUseDefault, Map m, AbstractSession session, boolean useSystemAsDefault) {
            Prop prop = (Prop)mainMap.get(name);
            if(prop == null) {
                // it's not our property
                return null; 
            }
            String value = getPropertyValueFromMap(name, m, useSystemAsDefault);
            if(value == null) {
                return null;
            }
            return prop.getValueToApply(value, shouldUseDefault, session);
        }
                
        static String getPropertyValueToApply(String name, Map m, AbstractSession session, boolean useSystemAsDefault) {
            Prop prop = (Prop)mainMap.get(name);
            if(prop == null) {
                throw new IllegalArgumentException(name); 
            }
            String value = getPropertyValueFromMap(name, m, useSystemAsDefault);
            if(value == null) {
                return null;
            }
            return prop.getValueToApply(value, shouldUseDefault(value), session);
        }
                
        static String getPropertyValueToApply(String name, String value, AbstractSession session) {
            Prop prop = (Prop)mainMap.get(name);
            if(prop == null) {
                throw new IllegalArgumentException(name); 
            }
            return prop.getValueToApply(value, shouldUseDefault(value), session);
        }
                
        static Map<String, Object> getPrefixValuesToApply(String prefix, Map m, AbstractSession session, boolean useSystemAsDefault) {
            Prop prop = (Prop)mainMap.get(prefix);            
            // maps suffixes to property values
            Map mapIn = getPrefixValuesFromMap(prefix, m, useSystemAsDefault);
            if(mapIn.isEmpty()) {
                return mapIn;
            }
            
            HashMap mapOut = new HashMap(mapIn.size());
            Iterator it = mapIn.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String suffix = (String)entry.getKey();
                Object value = entry.getValue();
                if ((prop != null) && (value instanceof String)) {
                    value = prop.getValueToApply((String)value, shouldUseDefault((String)value), suffix, session);
                }
                mapOut.put(suffix, value);
            }
            // maps suffixes to valuesToApply
            return mapOut;
        }
        
        static String getDefaultPropertyValueToApply(String name, AbstractSession session) {
            Prop prop = (Prop)mainMap.get(name);
            if(prop == null) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-default-for-unknown-property", new Object[]{name}));
            }
            prop.logDefault(session);
            return prop.defaultValueToApply;
        }
                
        String getValueToApply(String value, boolean shouldUseDefault, AbstractSession session) {
            return getValueToApply(value, shouldUseDefault, null, session);
        }
        
        // suffix is used only for properties-prefixes (like CacheType)
        String getValueToApply(String value, boolean shouldUseDefault, String suffix, AbstractSession session) {            
            if(shouldUseDefault) {
                logDefault(session, suffix);
                return defaultValueToApply;
            }
            String valueToApply = value;
            if(valueMap != null) {
                String key = getUpperCaseString(value);
                valueToApply = (String)valueMap.get(key);
                if(valueToApply == null) {
                    boolean notFound = true;
                    if(valueToApplyMayBeNull) {
                        notFound = !valueMap.containsKey(key);
                    }
                    if(notFound) {
                        if(shouldReturnOriginalValueIfValueToApplyNotFound) {
                            valueToApply = value;
                        } else {
                            String propertyName = name;
                            if(suffix != null) {
                                propertyName = propertyName + suffix;
                            }
                            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-illegal-property-value", new Object[]{propertyName, getPrintValue(value)}));
                        }
                    }
                }
            }
            String logValue = PersistenceUnitProperties.getOverriddenLogStringForProperty(name);
            if (logValue != null){
                log(session, logValue, logValue, suffix);
            } else {
                log(session, value, valueToApply, suffix);
            }
            return valueToApply;
        }

        static String getUpperCaseString(String value) {
            if(value != null) {
                return value.toUpperCase();
            } else {
                return null;
            }
        }
        static String getPrintValue(String value) {
            if(value != null) {
                return value;
            } else {
                return "null";
            }
        }
        void initialize() {
            if(valueArray != null) {
                valueMap = new HashMap(valueArray.length);
                if(valueArray instanceof Object[][]) {
                    Object[][] valueArray2 = (Object[][])valueArray;
                    for(int i=0; i<valueArray2.length; i++) {
                        valueMap.put(getUpperCaseString((String)valueArray2[i][0]), valueArray2[i][1]);
                        if(valueArray2[i][1] == null) {
                            valueToApplyMayBeNull = true;
                        }
                    }
                } else {
                    for(int i=0; i<valueArray.length; i++) {
                        valueMap.put(getUpperCaseString((String)valueArray[i]), valueArray[i]);
                        if(valueArray[i] == null) {
                            valueToApplyMayBeNull = true;
                        }
                    }
                }
                defaultValueToApply = (String)valueMap.get(getUpperCaseString(defaultValue));
            } else {
                defaultValueToApply = defaultValue;
            }
        }

        void logDefault(AbstractSession session) {
            logDefault(session, null);
        }
        
        void logDefault(AbstractSession session, String suffix) {
            if (session != null) {
                if (session.shouldLog(SessionLog.FINEST, SessionLog.PROPERTIES)) {
                    String propertyName = name;
                    if (suffix != null) {
                        propertyName = propertyName + suffix;
                    }
                    if (defaultValue != defaultValueToApply) {
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "handler_property_value_default", new Object[]{propertyName, defaultValue, defaultValueToApply});
                    } else {
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_default", new Object[]{propertyName, defaultValue});
                    }
                }
            }
        }
        
        void log(AbstractSession session, String value, String valueToApply, String suffix) {
            if (session != null) {
                if (session.shouldLog(SessionLog.FINEST, SessionLog.PROPERTIES)) {
                    String propertyName = name;
                    if(suffix != null) {
                        propertyName = propertyName + suffix;
                    }
                    if(value != valueToApply) {
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "handler_property_value_specified", new Object[]{propertyName, value, valueToApply});
                    } else {
                        session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyName, value});
                    }
                }
            }
        }
        
        static void addProp(Prop prop) {
            prop.initialize();
            mainMap.put(prop.name, prop);
        }
    }

    protected static class LoggerTypeProp extends Prop {
        LoggerTypeProp() {
            super(PersistenceUnitProperties.LOGGING_LOGGER, LoggerType.DEFAULT);
            this.shouldReturnOriginalValueIfValueToApplyNotFound = true;
            String pcg = "org.eclipse.persistence.logging.";
            valueArray = new Object[][] {
                {LoggerType.DefaultLogger, pcg + "DefaultSessionLog"},
                {LoggerType.JavaLogger, pcg + "JavaLog"}
            };
        }
    }

    protected static class ConnectionPoolProp extends Prop {
        ConnectionPoolProp() {
            super(PersistenceUnitProperties.CONNECTION_POOL);
        }
    }

    protected static class LoggingLevelProp extends Prop {
        LoggingLevelProp(String name, String defaultValue) {
            super(name, defaultValue);
            valueArray = new Object[] { 
                Level.OFF.getName(),
                Level.SEVERE.getName(),
                Level.OFF.getName(),
                Level.WARNING.getName(),
                Level.INFO.getName(),
                Level.CONFIG.getName(),
                Level.FINE.getName(),
                Level.FINER.getName(),
                Level.FINEST.getName(),
                Level.ALL.getName()
            };
        }
    }

    protected static class ReferenceModeProp extends Prop {
        ReferenceModeProp() {
            super(EntityManagerProperties.PERSISTENCE_CONTEXT_REFERENCE_MODE, ReferenceMode.HARD.toString());
            valueArray = new Object[] {
                ReferenceMode.HARD.toString(),
                ReferenceMode.WEAK.toString(),
                ReferenceMode.FORCE_WEAK.toString()
            };
        }
    }
    
    protected static class FlushModeProp extends Prop {
        FlushModeProp() {
            super(EntityManagerProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, FlushModeType.AUTO.toString());
            valueArray = new Object[] {
                FlushModeType.AUTO.toString(),
                FlushModeType.COMMIT.toString()
            };
        }
    }

    protected static class TargetDatabaseProp extends Prop {
        TargetDatabaseProp() {
            super(PersistenceUnitProperties.TARGET_DATABASE, TargetDatabase.DEFAULT);
            this.shouldReturnOriginalValueIfValueToApplyNotFound = true;
            String pcg = "org.eclipse.persistence.platform.database.";
            valueArray = new Object[][] { 
                {TargetDatabase.Auto, pcg + "DatabasePlatform"},
                {TargetDatabase.Oracle, pcg + "OraclePlatform"},
                {TargetDatabase.Oracle8, pcg + "oracle.Oracle8Platform"},
                {TargetDatabase.Oracle9, pcg + "oracle.Oracle9Platform"},
                {TargetDatabase.Oracle10, pcg + "oracle.Oracle10Platform"},
                {TargetDatabase.Oracle11, pcg + "oracle.Oracle11Platform"},
                {TargetDatabase.Attunity, pcg + "AttunityPlatform"},
                {TargetDatabase.Cloudscape, pcg + "CloudscapePlatform"},
                {TargetDatabase.Database, pcg + "DatabasePlatform"},
                {TargetDatabase.DB2Mainframe, pcg + "DB2MainframePlatform"},
                {TargetDatabase.DB2, pcg + "DB2Platform"},
                {TargetDatabase.DBase, pcg + "DBasePlatform"},
                {TargetDatabase.Derby, pcg + "DerbyPlatform"},
                {TargetDatabase.HANA,  pcg + "HANAPlatform"},
                {TargetDatabase.HSQL, pcg + "HSQLPlatform"},
                {TargetDatabase.Informix, pcg + "InformixPlatform"},
                {TargetDatabase.JavaDB, pcg + "JavaDBPlatform"},
                {TargetDatabase.MySQL, pcg + "MySQLPlatform"},
                {TargetDatabase.MaxDB, pcg + "MaxDBPlatform"},
                {TargetDatabase.MySQL4, pcg + "MySQLPlatform"}, // 211249: keep backwards compatibility
                {TargetDatabase.PointBase,  pcg + "PointBasePlatform"},
                {TargetDatabase.PostgreSQL,  pcg + "PostgreSQLPlatform"},
                {TargetDatabase.SQLAnywhere,  pcg + "SQLAnywherePlatform"},
                {TargetDatabase.SQLServer,  pcg + "SQLServerPlatform"},
                {TargetDatabase.Sybase,  pcg + "SybasePlatform"},
                {TargetDatabase.Symfoware,  pcg + "SymfowarePlatform"},
                {TargetDatabase.TimesTen,  pcg + "TimesTenPlatform"}
            };
        }
    }

    protected static class TargetServerProp extends Prop {
        TargetServerProp() {
            super(PersistenceUnitProperties.TARGET_SERVER, TargetServer.DEFAULT);
            this.shouldReturnOriginalValueIfValueToApplyNotFound = true;
            String pcg = "org.eclipse.persistence.platform.server.";
            valueArray = new Object[][] { 
                {TargetServer.None, pcg + "NoServerPlatform"},
                {TargetServer.OC4J, pcg + "oc4j.Oc4jPlatform"},
                {TargetServer.SunAS9, pcg + "sunas.SunAS9ServerPlatform"},
                {TargetServer.Glassfish, pcg + "glassfish.GlassfishPlatform"},
                {TargetServer.WebSphere, pcg + "was.WebSpherePlatform"},
                {TargetServer.WebSphere_6_1, pcg + "was.WebSphere_6_1_Platform"},
                {TargetServer.WebSphere_7, pcg + "was.WebSphere_7_Platform"},
                {TargetServer.WebLogic, pcg + "wls.WebLogicPlatform"},
                {TargetServer.WebLogic_9, pcg + "wls.WebLogic_9_Platform"},
                {TargetServer.WebLogic_10, pcg + "wls.WebLogic_10_Platform"},
                {TargetServer.JBoss, pcg + "jboss.JBossPlatform"},
                {TargetServer.SAPNetWeaver_7_1, pcg + "sap.SAPNetWeaver_7_1_Platform"},            };
        }  
    }

    protected static class CacheSizeProp extends Prop {
        CacheSizeProp() {
            super(PersistenceUnitProperties.CACHE_SIZE_, Integer.toString(1000));
        }  
    }

    protected static class CacheTypeProp extends Prop {
        CacheTypeProp() {
            super(PersistenceUnitProperties.CACHE_TYPE_, CacheType.DEFAULT);
            String pcg = "org.eclipse.persistence.internal.identitymaps.";
            valueArray = new Object[][] { 
                {CacheType.Weak, pcg + "WeakIdentityMap"},
                {CacheType.Soft, pcg + "SoftIdentityMap"},
                {CacheType.SoftWeak, pcg + "SoftCacheWeakIdentityMap"},
                {CacheType.HardWeak, pcg + "HardCacheWeakIdentityMap"},
                {CacheType.Full, pcg + "FullIdentityMap"},
                {CacheType.NONE, pcg + "NoIdentityMap"}
            };
        }
    }

    protected static class BooleanProp extends Prop {
        BooleanProp(String name, String defaultValue) {
            super(name, defaultValue);
            valueArray = new Object[] { 
                "true",
                "false"
            };
        }  
    }

    protected static class DescriptorCustomizerProp extends Prop {
        DescriptorCustomizerProp() {
            super(PersistenceUnitProperties.DESCRIPTOR_CUSTOMIZER_);
        }  
    }

    protected static class BatchWritingProp extends Prop {
        BatchWritingProp() {
            super(PersistenceUnitProperties.BATCH_WRITING, BatchWriting.DEFAULT);
            this.shouldReturnOriginalValueIfValueToApplyNotFound = true;
            valueArray = new Object[] { 
                BatchWriting.None,
                BatchWriting.JDBC,
                BatchWriting.Buffered,
                BatchWriting.OracleJDBC
            };
        }
    }

    protected static class FlushClearCacheProp extends Prop {
        FlushClearCacheProp() {
            super(PersistenceUnitProperties.FLUSH_CLEAR_CACHE, FlushClearCache.DEFAULT);
            valueArray = new Object[] { 
                FlushClearCache.Merge,
                FlushClearCache.Drop,
                FlushClearCache.DropInvalidate
            };
        }
    }

    protected static class ExclusiveConnectionModeProp extends Prop {
        ExclusiveConnectionModeProp() {
            super(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, ExclusiveConnectionMode.DEFAULT);
            valueArray = new Object[] { 
                ExclusiveConnectionMode.Transactional,
                ExclusiveConnectionMode.Isolated,
                ExclusiveConnectionMode.Always
            };
        }
    }

    protected static class IdValidationProp extends Prop {
        IdValidationProp() {
            super(PersistenceUnitProperties.ID_VALIDATION, IdValidation.ZERO.toString());
            valueArray = new Object[] { 
                IdValidation.NULL.toString(),
                IdValidation.ZERO.toString(),
                IdValidation.NEGATIVE.toString(),
                IdValidation.NONE.toString()
            };
        }
    }
}
