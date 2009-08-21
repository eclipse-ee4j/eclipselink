package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.Map;

public abstract class CanonicalModelProperties {
    // TODO: all the naming needs to be finalized ...
    
    public enum LOCATION { CP, SP, APP, SO, PCP, CO };
    public enum QUALIFIER_POSITION { PRE, POST }

    public static String PERSISTENCE_XML_PACKAGE = "package";
    public static String PERSISTENCE_XML_PACKAGE_DEFAULT = "";
    public static String PERSISTENCE_XML_LOCATION = "std-location";
    public static String PERSISTENCE_XML_LOCATION_DEFAULT = LOCATION.CO.name();
    public static String PERSISTENCE_XML_FILE = "filename";
    public static String PERSISTENCE_XML_FILE_DEFAULT = "META-INF/persistence.xml";

    public static String CANONICAL_MODEL_QUALIFIER = "eclipselink.canonical-model.qualifier";
    public static String CANONICAL_MODEL_QUALIFIER_DEFAULT = "_";
    public static String CANONICAL_MODEL_QUALIFIER_POSITION = "eclipselink.canonical-model.qualifier-position";
    public static String CANONICAL_MODEL_QUALIFIER_POSITION_DEFAULT = QUALIFIER_POSITION.POST.name();
    
    /**
     * INTERNAL:
     */
    public static String getOption(String option, String optionDefault, Map<String, String> options) {
        String value = options.get(option);
        return (value == null) ? optionDefault : value;
    }
}
