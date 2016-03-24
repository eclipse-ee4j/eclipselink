/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David Král - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal.properties;

/**
 * Contains all message keys present in language property files
 *
 * @author David Král
 */
public enum MessageKeys {

    PROCESS_FROM_JSON("processFromJson"),
    PROCESS_TO_JSON("processToJson"),
    CANNOT_MARSHAL_OBJECT("cannotMarshallObject"),
    TYPE_RESOLUTION_ERROR("typeResolutionError"),
    GENERIC_BOUND_NOT_FOUND("genericBoundNotFound"),
    CANT_CONVERT_JSON_VALUE("cantConvertJsonValue"),
    CANT_CREATE_ROOT_INSTANCE("cantCreateRootInstance"),
    NO_DEFAULT_CONSTRUCTOR("noDefaultConstructor"),
    INVOKING_GETTER("invokingGetter"),
    GETTING_VALUE("gettingValue"),
    SETTING_VALUE("settingValue"),
    NO_LOGGER_NAME("noLoggerName"),
    RESOLVE_PARAMETRIZED_TYPE("resolveParametrizedType"),
    CANT_CREATE_INSTANCE("cantCreateInstance"),
    INFER_TYPE_FOR_UNMARSHALL("inferTypeForUnmarshall"),
    NOT_VALUE_TYPE("notValueType"),
    UNEXPECTED_PARSE_EVENT("unexpectedParseEvent"),
    CREATING_HANDLES("creatingHandles"),
    GETTING_VALUE_WITH("gettingValueWith"),
    SETTING_VALUE_WITH("settingValueWith"),
    UNPAIRED_SURROGATE("unpairedSurrogate"),
    ADAPTER_EXCEPTION("adapterException"),
    ADAPTER_FOUND("adapterFound"),
    ADAPTER_INCOMPATIBLE("adapterIncompatible"),
    PROPERTY_ORDER("propertyOrder"),
    UNSUPPORTED_JSONP_SERIALIZER_VALUE("unsupportedJsonpSerializerValue"),
    JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE("Only Boolean type values are supported for JsonbConfig.FORMATTING property."),
    BEAN_MANAGER_NOT_FOUND_JNDI("beanManagerNotFoundJndi"),
    BEAN_MANAGER_NOT_FOUND_NO_PROVIDER("beanManagerNotFoundNoProvider"),
    BEAN_MANAGER_NOT_FOUND_USING_DEFAULT("usingDefaultConstructorInstantiator"),
    IJSON_ENABLED_SINGLE_VALUE("iJsonEnabledSingleValue"),
    PROPERTY_NOT_FOUND_DESERIALIZER("propertyNotFoundDeserializer"),
    SETTING_PROPERTY_DESERIALIZER("settingPropertyDeserializer"),
    CLASS_LOAD_NOT_ALLOWED("classLoadNotAllowed"),
    ;

    /** Message bundle key. */
    final String key;

    /**
     * Creates an instance of message bundle key.
     * @param key Message key from bundle.
     */
    MessageKeys(final String key) {
        this.key = key;
    }

}
