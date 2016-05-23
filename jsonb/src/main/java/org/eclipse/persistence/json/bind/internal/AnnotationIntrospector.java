/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.adapter.AdapterBinding;
import org.eclipse.persistence.json.bind.internal.adapter.DeserializerBinding;
import org.eclipse.persistence.json.bind.internal.adapter.SerializerBinding;
import org.eclipse.persistence.json.bind.internal.conversion.JsonbDateFormatter;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.Property;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.annotation.JsonbVisibility;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Introspects configuration on classes and their properties by reading annotations.
 *
 * @author Roman Grigoriadi
 */
public class AnnotationIntrospector {

    private static final AnnotationIntrospector instance = new AnnotationIntrospector();

    private final ComponentMatcher componentMatcher;

    /**
     * Gets a singleton instance to use
     * @return instance
     */
    public static AnnotationIntrospector getInstance() {
        return instance;
    }

    private AnnotationIntrospector() {
        componentMatcher = ProcessingContext.getJsonbContext().getComponentMatcher();
    }

    /**
     * Gets a name of property for JSON marshalling.
     * Can be different writeName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return read name
     */
    public String getJsonbPropertyJsonWriteName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getGetter());
    }

    /**
     * Gets a name of property for JSON unmarshalling.
     * Can be different from readName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return write name
     */
    public String getJsonbPropertyJsonReadName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getSetter());
    }

    private String getJsonbPropertyCustomizedName(Property property, Method method) {
        JsonbProperty methodAnnotation = getMethodAnnotation(JsonbProperty.class, method);
        if (methodAnnotation != null && !methodAnnotation.value().isEmpty()) {
            return methodAnnotation.value();
        }
        //in case of property name getter/setter override field value
        JsonbProperty fieldAnnotation = getFieldAnnotation(JsonbProperty.class, property.getField());
        if (fieldAnnotation != null && !fieldAnnotation.value().isEmpty()) {
            return fieldAnnotation.value();
        }

        return property.getName();
    }

    /**
     * Checks for {@link JsonbAdapter} on a property.
     * @param property property not null
     * @return adapter info
     */
    public AdapterBinding getAdapterBinding(Property property) {
        Objects.requireNonNull(property);
        Optional<JsonbTypeAdapter> adapterAnnotation = getAnnotationFromPropertyIncludingType(JsonbTypeAdapter.class, property);
        final Optional<AdapterBinding> adapterBindingOptional = getAdapterBindingFromAnnotation(adapterAnnotation, ReflectionUtils.getOptionalRawType(property.getPropertyType()));
        if (adapterBindingOptional.isPresent()) {
            return adapterBindingOptional.get();
        }
        return adapterBindingOptional.orElse(null);
    }

    private Optional<AdapterBinding> getAdapterBindingFromAnnotation(Optional<JsonbTypeAdapter> adapterAnnotation, Optional<Class<?>> expectedClass) {
        final Optional<AdapterBinding> adapterBindingOptional = adapterAnnotation.map((adaAnn) -> {
            final Class<? extends JsonbAdapter> adapterClass = adaAnn.value();
            return ProcessingContext.getJsonbContext().getComponentMatcher().introspectAdapterBinding(adapterClass,
                    () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(adapterClass));
        });
        if (adapterBindingOptional.isPresent() && expectedClass.isPresent()
                && !(ReflectionUtils.getRawType(adapterBindingOptional.get().getBindingType()).equals(expectedClass.get()))) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_INCOMPATIBLE, adapterBindingOptional.get().getBindingType(), expectedClass.get()));
        }
        return adapterBindingOptional;
    }

    /**
     * Checks for {@link JsonbDeserializer} on a property.
     * @param property property not null
     * @return adapter info
     */
    public DeserializerBinding getDeserializerBinding(Property property) {
        Objects.requireNonNull(property);
        Optional<JsonbTypeDeserializer> deserializerAnnotation = getAnnotationFromPropertyIncludingType(JsonbTypeDeserializer.class, property);
        final Optional<DeserializerBinding> bindingOptional = deserializerAnnotation.map((deserAnn) -> {
            final Class<? extends JsonbDeserializer> deserializerClass = deserAnn.value();
            return ProcessingContext.getJsonbContext().getComponentMatcher().introspectDeserializerBinding(deserializerClass,
                    () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(deserializerClass));
        });
        if (bindingOptional.isPresent()) {
            return bindingOptional.get();
        }
        return bindingOptional.orElse(null);
    }

    /**
     * Checks for {@link JsonbSerializer} on a property.
     * @param property property not null
     * @return adapter info
     */
    public SerializerBinding getSerializerBinding(Property property) {
        Objects.requireNonNull(property);
        Optional<JsonbTypeSerializer> serializerAnnotation = getAnnotationFromPropertyIncludingType(JsonbTypeSerializer.class, property);
        final Optional<SerializerBinding> bindingOptional = serializerAnnotation.map((serAnn) -> {
            final Class<? extends JsonbSerializer> serializerClass = serAnn.value();
            return ProcessingContext.getJsonbContext().getComponentMatcher().introspectSerialzierBinding(serializerClass,
                    () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(serializerClass));
        });
        if (bindingOptional.isPresent()) {
            return bindingOptional.get();
        }
        return bindingOptional.orElse(null);
    }

    /**
     * Checks if property is nillable.
     * Looks for {@link JsonbProperty} nillable attribute only.
     * JsonbNillable is checked only for ClassModels.
     *
     * @param property property to search in, not null
     * @return True if property should be serialized when null.
     */
    public boolean isPropertyNillable(Property property) {
        Objects.requireNonNull(property);

        final Optional<JsonbProperty> jsonbProperty = getAnnotationFromProperty(JsonbProperty.class, property);
        return jsonbProperty.isPresent() && jsonbProperty.get().nillable();

    }

    /**
     * Checks for JsonbNillable annotation on a class, its superclasses and interfaces.
     *
     * @param clazz class to search JsonbNillable in.
     * @return true if found
     */
    public boolean isClassNillable(Class<?> clazz) {
        final Optional<JsonbNillable> jsonbNillable = searchAnnotationInClassHierarchy(JsonbNillable.class, clazz);
        final Optional<Boolean> result = jsonbNillable.map(JsonbNillable::value);
        return result.orElse(false);
    }

    /**
     * Checks if property is annotated transient.
     * If JsonbTransient annotation is present on field getter or setter, and other annotation is present on either
     * of it, JsonbException is thrown with message describing collision.
     *
     * @param property not null
     * @return JsonbTransient annotation is found on field, getter of setter of a property
     */
    public boolean isTransient(Property property) {
        Objects.requireNonNull(property);
        final Optional<JsonbTransient> jsonbTransient = getAnnotationFromProperty(JsonbTransient.class, property);
        if (jsonbTransient.isPresent()) {
            final Class[] FORBIDDEN_ANNOTATIONS = new Class[]{JsonbProperty.class, JsonbNillable.class, JsonbCreator.class, JsonbDateFormat.class, JsonbNumberFormat.class, JsonbPropertyOrder.class, JsonbVisibility.class};
            for (Class annotClass : FORBIDDEN_ANNOTATIONS) {
                if (getAnnotationFromProperty(annotClass, property).isPresent()) {
                    throw new JsonbException(String.format("JsonbTransient annotation collides with %s for property %s", annotClass, property.getName()));
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Search for {@link JsonbDateFormat} annotation on java property and construct {@link JsonbDateFormatter}.
     * @param property property to search not null
     * @return formatter to use
     */
    public JsonbDateFormatter getJsonbDateFormat(Property property) {
        //Filter non-date properties first
        final Optional<Class<?>> propertyRawTypeOptional = ReflectionUtils.getOptionalRawType(property.getPropertyType());
        if (propertyRawTypeOptional.isPresent()) {
            Class<?> rawType = propertyRawTypeOptional.get();
            if (!(Date.class.isAssignableFrom(rawType) || Calendar.class.isAssignableFrom(rawType)
                    || TemporalAccessor.class.isAssignableFrom(rawType))) {
                return null;
            }
        }
        //TODO what about non date generic properties that cannot be resolved statically for a class model,
        //TODO they will inherit JsonbDateFormat from class which will consume memory footprint.
        Objects.requireNonNull(property);
        final Optional<JsonbDateFormatter> propertyFormatter = getAnnotationFromProperty(JsonbDateFormat.class, property)
                .map(formatAnnot -> createJsonbDateFormatter(formatAnnot.value(), getDateFormatLocale(formatAnnot), property));

        return propertyFormatter.orElse(((Supplier<JsonbDateFormatter>) () -> {
            final JsonbDateFormatter classModelFormatter = property.getDeclaringClassModel().getClassCustomization().getDateTimeFormatter();
            return createJsonbDateFormatter(classModelFormatter.getFormat(), classModelFormatter.getLocale(), property);
        }).get());

    }

    /**
     * Search for {@link JsonbDateFormat} annotation on java class and construct {@link JsonbDateFormatter}.
     * @param clazz class to search not null
     * @return formatter to use
     */
    public JsonbDateFormatter getJsonbDateFormat(Class clazz) {
        Objects.requireNonNull(clazz);
        final Optional<JsonbDateFormatter> formatter = searchAnnotationInClassHierarchy(JsonbDateFormat.class, clazz)
                .map(jsonbDateFormat -> new JsonbDateFormatter(jsonbDateFormat.value(), getDateFormatLocale(jsonbDateFormat)));
        return formatter.orElse(getGlobalConfigJsonbDateFormatter());
    }

    private Locale getDateFormatLocale(JsonbDateFormat jsonbDateFormat) {
        return JsonbDateFormat.DEFAULT_LOCALE.equals(jsonbDateFormat.locale()) ? getConfigLocale() : Locale.forLanguageTag(jsonbDateFormat.locale());
    }

    private JsonbDateFormatter getGlobalConfigJsonbDateFormatter() {
        final Optional<Object> formatProperty = ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.DATE_FORMAT);
        String format = formatProperty.map(f -> {
            if (!(f instanceof String)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.DATE_FORMAT, String.class.getSimpleName()));
            }
            return (String) f;
        }).orElse(JsonbDateFormat.DEFAULT_FORMAT);
        return new JsonbDateFormatter(format, getConfigLocale());
    }

    /**
     * Creates {@link JsonbDateFormatter} caches formatter instance if possible.
     * For DEFAULT_FORMAT appropriate singleton instances from java.time.format.DateTimeFormatter
     * are used in date converters.
     */
    private JsonbDateFormatter createJsonbDateFormatter(String format, Locale locale, Property property) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(format) || JsonbDateFormat.DEFAULT_FORMAT.equals(format)) {
            //for epochMillis formatter is not used, for default format singleton instances of DateTimeFormatter
            //are used in the converters
            return new JsonbDateFormatter(format, locale);
        }

        final Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(property.getPropertyType());

        //Can't resolve date type if it is declared as generic type var
        if (!optionalRawType.isPresent()) {
            return new JsonbDateFormatter(DateTimeFormatter.ofPattern(format, locale), format, locale);
        }

        final Class<?> propertyRawType = optionalRawType.get();

        //Calendar and dates
        if (Date.class.isAssignableFrom(propertyRawType) || Calendar.class.isAssignableFrom(propertyRawType)) {
            return new JsonbDateFormatter(format, locale);
        }

        if (!TemporalAccessor.class.isAssignableFrom(propertyRawType)) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.UNSUPPORTED_DATE_TYPE, propertyRawType));
        }
        return new JsonbDateFormatter(DateTimeFormatter.ofPattern(format, locale), format, locale);
    }

    /**
     * Get a @JsonbVisibility annotation from a class or its package.
     * @param clazz Class to lookup annotation
     * @return Instantiated PropertyVisibilityStrategy if annotation is present
     */
    public Optional<PropertyVisibilityStrategy> getPropertyVisibilityStrategy(Class<?> clazz) {
        JsonbVisibility visibilityAnnotation = clazz.getAnnotation(JsonbVisibility.class);
        if (visibilityAnnotation == null) {
            visibilityAnnotation = clazz.getPackage().getAnnotation(JsonbVisibility.class);
        }
        final Optional<JsonbVisibility> visibilityOptional = Optional.ofNullable(visibilityAnnotation);
        return visibilityOptional.map(jsonbVisibility -> ReflectionUtils.createNoArgConstructorInstance(jsonbVisibility.value()));
    }

    /**
     * Gets an annotation from first resolved annotation in a property in this order:
     * <p>1. Field, 2. Getter, 3 Setter.</p>
     * First found overrides other.
     *
     * @param annotationClass Annotation class to search for
     * @param property property to search in
     * @param <T> Annotation type
     * @return Annotation if found, null otherwise
     */
    private <T extends Annotation> Optional<T> getAnnotationFromProperty(Class<T> annotationClass, Property property) {
        T fieldAnnotation = getFieldAnnotation(annotationClass, property.getField());
        if (fieldAnnotation != null) {
            return Optional.of(fieldAnnotation);
        }

        T getterAnnotation = getMethodAnnotation(annotationClass, property.getGetter());
        if (getterAnnotation != null) {
            return Optional.of(getterAnnotation);
        }

        T setterAnnotation = getMethodAnnotation(annotationClass, property.getSetter());
        if (setterAnnotation != null) {
            return Optional.of(setterAnnotation);
        }

        return Optional.empty();
    }

    /**
     * Same as getAnnotationFromProperty, but also searches a type of property for class level annotation.
     */
    private <T extends Annotation> Optional<T> getAnnotationFromPropertyIncludingType(Class<T> annotationClass, Property property) {
        final Optional<T> annotationFromProperty = getAnnotationFromProperty(annotationClass, property);
        if (annotationFromProperty.isPresent()) {
            return annotationFromProperty;
        }
        return getAnnotationFromPropertyRawTypeClass(property, annotationClass);
    }

    /**
     * Return class model of a property if resolvable (No runtime type here).
     */
    private <T extends Annotation> Optional<T> getAnnotationFromPropertyRawTypeClass(Property property, Class<T> annotationClazz) {
        final Optional<Class<?>> propertyRawTypeOptional = ReflectionUtils.getOptionalRawType(property.getPropertyType());
        return propertyRawTypeOptional.isPresent() ? searchAnnotationInClassHierarchy(annotationClazz, propertyRawTypeOptional.get())
                : Optional.empty();
    }



    private <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass, Field field ) {
        if (field == null) {
            return null;
        }
        return field.getAnnotation(annotationClass);
    }

    private <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass, Method method) {
        if (method == null) {
            return null;
        }
        return method.getAnnotation(annotationClass);
    }

    private <T extends Annotation> Optional<T> searchAnnotationInClassHierarchy(Class<T> annotationClass, Class<?> declaringClass) {
        if (declaringClass == null || declaringClass == Object.class || ProcessingContext.getMappingContext().supported(declaringClass)) {
            return Optional.empty();
        }
        Optional<T> candidate = Optional.ofNullable(declaringClass.getAnnotation(annotationClass));
        if (candidate.isPresent()) {
            return candidate;
        }
        candidate = searchPackage(annotationClass, declaringClass);
        if (candidate.isPresent()) {
            return candidate;
        }
        candidate = searchInterfaces(annotationClass, declaringClass);
        if (candidate.isPresent()) {
            return candidate;
        }

        return searchAnnotationInClassHierarchy(annotationClass, declaringClass.getSuperclass());
    }

    private <T extends Annotation> Optional<T> searchInterfaces(Class<T> annotationClass, Class<?> clazz) {
        if (clazz == Object.class) {
            return Optional.empty();
        }
        final Map<Class<?>, T> annotationMapping = new HashMap<>();
        collectFromInterfaces(annotationClass, clazz, annotationMapping);
        if (annotationMapping.size() == 1) {
            return Optional.of(annotationMapping.values().iterator().next());
        } else if (annotationMapping.size() > 1) {
            StringBuilder message = new StringBuilder("Duplicate mapping found for ").append(annotationClass).append(" on interfaces:");
            for (Map.Entry<Class<?>, T> entry : annotationMapping.entrySet()) {
                message.append("[").append(entry.getKey()).append("],");
            }
            throw new JsonbException(message.toString());
        } else {
            return Optional.empty();
        }
    }

    private <T extends Annotation> void collectFromInterfaces(Class<T> annotationClass, Class clazz, Map<Class<?>, T> collectedAnnotations) {

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            T annotation = interfaceClass.getAnnotation(annotationClass);
            if (annotation != null) {
                collectedAnnotations.put(interfaceClass, annotation);
            }
            collectFromInterfaces(annotationClass, interfaceClass, collectedAnnotations);
        }
    }

    private <T extends Annotation> Optional<T> searchPackage(Class<T> annotationClass, Class clazz) {
        return Optional.ofNullable(clazz.getPackage().getAnnotation(annotationClass));
    }

    /**
     * Returns JsonbPropertyOrder annotation if class has one
     * @param clazz Class to be checked
     * @return
     */
    public Optional<JsonbPropertyOrder> getJsonbPropertyOrderAnnotation(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(JsonbPropertyOrder.class));
    }

    private Locale getConfigLocale() {
        final Optional<Object> localeProperty = ProcessingContext.getJsonbContext().getConfig().getProperty(JsonbConfig.LOCALE);
        return  localeProperty.map(loc -> {
            if (!(loc instanceof Locale)) {
                throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_PROPERTY_INVALID_TYPE, JsonbConfig.LOCALE, Locale.class.getSimpleName()));
            }
            return (Locale) loc;
        }).orElse(Locale.getDefault());
    }
}
