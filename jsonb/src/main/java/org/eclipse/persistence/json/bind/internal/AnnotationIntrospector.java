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
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.serializer.JsonbDateFormatter;
import org.eclipse.persistence.json.bind.internal.serializer.JsonbNumberFormatter;
import org.eclipse.persistence.json.bind.model.ClassCustomization;
import org.eclipse.persistence.json.bind.model.CustomizationBuilder;
import org.eclipse.persistence.json.bind.model.JsonbAnnotatedElement;
import org.eclipse.persistence.json.bind.model.JsonbCreator;
import org.eclipse.persistence.json.bind.model.Property;

import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Introspects configuration on classes and their properties by reading annotations.
 *
 * @author Roman Grigoriadi
 */
public class AnnotationIntrospector {

    private static final AnnotationIntrospector instance = new AnnotationIntrospector();

    /**
     * Gets a singleton instance to use
     * @return instance
     */
    public static AnnotationIntrospector getInstance() {
        return instance;
    }

    /**
     * Gets a name of property for JSON marshalling.
     * Can be different writeName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return read name
     */
    public String getJsonbPropertyJsonWriteName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getGetterElement());
    }

    /**
     * Gets a name of property for JSON unmarshalling.
     * Can be different from writeName for same property.
     * @param property property representation - field, getter, setter (not null)
     * @return write name
     */
    public String getJsonbPropertyJsonReadName(Property property) {
        Objects.requireNonNull(property);
        return getJsonbPropertyCustomizedName(property, property.getSetterElement());
    }

    private String getJsonbPropertyCustomizedName(Property property, JsonbAnnotatedElement<Method> methodElement) {
        JsonbProperty methodAnnotation = getMethodAnnotation(JsonbProperty.class, methodElement);
        if (methodAnnotation != null && !methodAnnotation.value().isEmpty()) {
            return methodAnnotation.value();
        }
        //in case of property name getter/setter override field value
        JsonbProperty fieldAnnotation = getFieldAnnotation(JsonbProperty.class, property.getFieldElement());
        if (fieldAnnotation != null && !fieldAnnotation.value().isEmpty()) {
            return fieldAnnotation.value();
        }

        return null;
    }


    /**
     * Searches for JsonbCreator annotation on constructors and static methods.
     *
     * @param clazz class to search
     * @return JsonbCreator metadata object
     */
    public JsonbCreator getCreator(Class<?> clazz) {
        JsonbCreator jsonbCreator = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            final javax.json.bind.annotation.JsonbCreator annot = findAnnotation(constructor.getDeclaredAnnotations(), javax.json.bind.annotation.JsonbCreator.class);
            if (annot != null) {
                jsonbCreator = createJsonbCreator(constructor, jsonbCreator, clazz);
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            final javax.json.bind.annotation.JsonbCreator annot = findAnnotation(method.getDeclaredAnnotations(), javax.json.bind.annotation.JsonbCreator.class);
            if (annot != null && Modifier.isStatic(method.getModifiers())) {
                if (!clazz.equals(method.getReturnType())) {
                    throw new JsonbException(Messages.getMessage(MessageKeys.INCOMPATIBLE_FACTORY_CREATOR_RETURN_TYPE, method, clazz));
                }
                jsonbCreator = createJsonbCreator(method, jsonbCreator, clazz);
            }
        }
        return jsonbCreator;
    }

    private JsonbCreator createJsonbCreator(Executable executable, JsonbCreator existing, Class<?> clazz) {
        if (existing != null) {
            throw new JsonbException(Messages.getMessage(MessageKeys.MULTIPLE_JSONB_CREATORS, clazz));
        }
        List<String> paramNames = new ArrayList<>();
        for (Parameter param : executable.getParameters()) {
            paramNames.add(param.getName());
        }
        return new JsonbCreator(executable, paramNames.toArray(new String[paramNames.size()]));
    }

    /**
     * Checks for {@link JsonbAdapter} on a property.
     * @param property property not null
     * @return adapter info
     */
    public AdapterBinding getAdapterBinding(Property property) {
        Objects.requireNonNull(property);
        JsonbTypeAdapter adapterAnnotation = getAnnotationFromProperty(JsonbTypeAdapter.class, property)
                .orElseGet(()-> getAnnotationFromPropertyType(property, JsonbTypeAdapter.class));
        if (adapterAnnotation == null) {
            return null;
        }

        return getAdapterBindingFromAnnotation(adapterAnnotation, ReflectionUtils.getOptionalRawType(property.getPropertyType()));
    }

    private AdapterBinding getAdapterBindingFromAnnotation(JsonbTypeAdapter adapterAnnotation, Optional<Class<?>> expectedClass) {
        final Class<? extends JsonbAdapter> adapterClass = adapterAnnotation.value();
        final AdapterBinding adapterBinding = ProcessingContext.getJsonbContext().getComponentMatcher().introspectAdapterBinding(adapterClass,
                () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(adapterClass));

        if (expectedClass.isPresent() && !(ReflectionUtils.getRawType(adapterBinding.getBindingType()).equals(expectedClass.get()))) {
            throw new JsonbException(Messages.getMessage(MessageKeys.ADAPTER_INCOMPATIBLE, adapterBinding.getBindingType(), expectedClass.get()));
        }
        return adapterBinding;
    }

    /**
     * Checks for {@link JsonbDeserializer} on a property.
     * @param property property not null
     * @return adapter info
     */
    public DeserializerBinding getDeserializerBinding(Property property) {
        Objects.requireNonNull(property);
        JsonbTypeDeserializer deserializerAnnotation = getAnnotationFromProperty(JsonbTypeDeserializer.class, property)
                .orElseGet(()-> getAnnotationFromPropertyType(property, JsonbTypeDeserializer.class));
        if (deserializerAnnotation == null) {
            return null;
        }

        final Class<? extends JsonbDeserializer> deserializerClass = deserializerAnnotation.value();
        return ProcessingContext.getJsonbContext().getComponentMatcher().introspectDeserializerBinding(deserializerClass,
                () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(deserializerClass));
    }

    /**
     * Checks for {@link JsonbSerializer} on a property.
     * @param property property not null
     * @return adapter info
     */
    public SerializerBinding getSerializerBinding(Property property) {
        Objects.requireNonNull(property);
        JsonbTypeSerializer serializerAnnotation = getAnnotationFromProperty(JsonbTypeSerializer.class, property)
                .orElseGet(()-> getAnnotationFromPropertyType(property, JsonbTypeSerializer.class));
        if (serializerAnnotation == null) {
            return null;
        }

        final Class<? extends JsonbSerializer> serializerClass = serializerAnnotation.value();
        return ProcessingContext.getJsonbContext().getComponentMatcher().introspectSerialzierBinding(serializerClass,
                () -> ProcessingContext.getJsonbContext().getComponentInstanceCreator().getOrCreateComponent(serializerClass));

    }

    private <T extends Annotation> T getAnnotationFromPropertyType(Property property, Class<T> annotationClass) {
        final Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(property.getPropertyType());
        if (!optionalRawType.isPresent()) {
            //TODO will not work for type variable properties, which are bound to class that is annotated.
            return null;
        }
        //TODO performance hit if class scanning is done often (jsonb is not reused)
        return findAnnotation(collectAnnotations(optionalRawType.get()).getAnnotations(), annotationClass);
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
     * @param clazzElement class to search JsonbNillable in.
     * @return true if found
     */
    public boolean isClassNillable(JsonbAnnotatedElement<Class<?>> clazzElement) {
        final JsonbNillable jsonbNillable = findAnnotation(clazzElement.getAnnotations(), JsonbNillable.class);
        return jsonbNillable != null && jsonbNillable.value();
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
        Objects.requireNonNull(property);

        final JsonbDateFormat annotation = getAnnotationFromProperty(JsonbDateFormat.class, property)
                .orElseGet(()->{
                    //if property is not TypeVariable and its class is not date skip it
                    final Optional<Class<?>> propertyRawTypeOptional = ReflectionUtils.getOptionalRawType(property.getPropertyType());
                    if (propertyRawTypeOptional.isPresent()) {
                        Class<?> rawType = propertyRawTypeOptional.get();
                        if (!(Date.class.isAssignableFrom(rawType) || Calendar.class.isAssignableFrom(rawType)
                                || TemporalAccessor.class.isAssignableFrom(rawType))) {
                            return null;
                        }
                    }
                    return findAnnotation(property.getDeclaringClassElement().getAnnotations(), JsonbDateFormat.class);
                });
        if (annotation == null) {
            return null;
        }

        return createJsonbDateFormatter(annotation.value(), annotation.locale(), property);
    }

    /**
     * Search for {@link JsonbDateFormat} annotation on java class and construct {@link JsonbDateFormatter}.
     * If not found looks at annotations declared on property type class.
     * @param clazzElement class to search not null
     * @return formatter to use
     */
    public JsonbDateFormatter getJsonbDateFormat(JsonbAnnotatedElement<Class<?>> clazzElement) {
        Objects.requireNonNull(clazzElement);
        final JsonbDateFormat format = findAnnotation(clazzElement.getAnnotations(), JsonbDateFormat.class);
        if (format == null) {
            return null;
        }
        return new JsonbDateFormatter(format.value(), format.locale());
    }

    /**
     * Search for {@link JsonbNumberFormat} annotation on java class.
     *
     * @param clazzElement class to search not null
     * @return formatter to use
     */
    public JsonbNumberFormatter getJsonbNumberFormat(JsonbAnnotatedElement<Class<?>> clazzElement) {
        final JsonbNumberFormat formatAnnotation = findAnnotation(clazzElement.getAnnotations(), JsonbNumberFormat.class);
        if (formatAnnotation == null) {
            return null;
        }
        return new JsonbNumberFormatter(formatAnnotation.value(), formatAnnotation.locale());
    }

    /**
     * Search {@link JsonbNumberFormat} on property, if not found looks at annotations declared on property type class.
     * @param property
     * @return
     */
    public JsonbNumberFormatter getJsonbNumberFormat(Property property) {
        final JsonbNumberFormat annotation = getAnnotationFromProperty(JsonbNumberFormat.class, property)
                .orElseGet(()->{
                    //if property is not TypeVariable and its class is not number skip it
                    final Optional<Class<?>> propertyRawTypeOptional = ReflectionUtils.getOptionalRawType(property.getPropertyType());
                    if (propertyRawTypeOptional.isPresent()) {
                        Class<?> rawType = propertyRawTypeOptional.get();
                        if (!Number.class.isAssignableFrom(rawType)) {
                            return null;
                        }
                    }
                    return findAnnotation(property.getDeclaringClassElement().getAnnotations(), JsonbNumberFormat.class);
                });

        if (annotation == null) {
            return null;
        }
        return new JsonbNumberFormatter(annotation.value(), annotation.locale());
    }

    /**
     * Creates {@link JsonbDateFormatter} caches formatter instance if possible.
     * For DEFAULT_FORMAT appropriate singleton instances from java.time.format.DateTimeFormatter
     * are used in date converters.
     */
    private JsonbDateFormatter createJsonbDateFormatter(String format, String locale, Property property) {
        if (JsonbDateFormat.TIME_IN_MILLIS.equals(format) || JsonbDateFormat.DEFAULT_FORMAT.equals(format)) {
            //for epochMillis formatter is not used, for default format singleton instances of DateTimeFormatter
            //are used in the converters
            return new JsonbDateFormatter(format, locale);
        }

        final Optional<Class<?>> optionalRawType = ReflectionUtils.getOptionalRawType(property.getPropertyType());

        //Can't resolve date type if it is declared as generic type var
        if (!optionalRawType.isPresent()) {
            return new JsonbDateFormatter(DateTimeFormatter.ofPattern(format, Locale.forLanguageTag(locale)), format, locale);
        }

        final Class<?> propertyRawType = optionalRawType.get();

        //Calendar and dates
        if (Date.class.isAssignableFrom(propertyRawType) || Calendar.class.isAssignableFrom(propertyRawType)) {
            return new JsonbDateFormatter(format, locale);
        }

        if (!TemporalAccessor.class.isAssignableFrom(propertyRawType)) {
            throw new IllegalStateException(Messages.getMessage(MessageKeys.UNSUPPORTED_DATE_TYPE, propertyRawType));
        }
        return new JsonbDateFormatter(DateTimeFormatter.ofPattern(format, Locale.forLanguageTag(locale)), format, locale);
    }

    /**
     * Get a @JsonbVisibility annotation from a class or its package.
     * @param clazz Class to lookup annotation
     * @return Instantiated PropertyVisibilityStrategy if annotation is present
     */
    public Optional<PropertyVisibilityStrategy> getPropertyVisibilityStrategy(Class<?> clazz) {
        JsonbVisibility visibilityAnnotation = findAnnotation(clazz.getDeclaredAnnotations(), JsonbVisibility.class);
        if (visibilityAnnotation == null) {
            visibilityAnnotation = findAnnotation(clazz.getPackage().getDeclaredAnnotations(), JsonbVisibility.class);
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
        T fieldAnnotation = getFieldAnnotation(annotationClass, property.getFieldElement());
        if (fieldAnnotation != null) {
            return Optional.of(fieldAnnotation);
        }

        T getterAnnotation = getMethodAnnotation(annotationClass, property.getGetterElement());
        if (getterAnnotation != null) {
            return Optional.of(getterAnnotation);
        }

        T setterAnnotation = getMethodAnnotation(annotationClass, property.getSetterElement());
        if (setterAnnotation != null) {
            return Optional.of(setterAnnotation);
        }

        return Optional.empty();
    }


    private <T extends Annotation> T getFieldAnnotation(Class<T> annotationClass, JsonbAnnotatedElement<Field> fieldElement) {
        if (fieldElement == null) {
            return null;
        }
        return findAnnotation(fieldElement.getAnnotations(), annotationClass);
    }

    private <T extends Annotation> T findAnnotation(Annotation[] declaredAnnotations, Class<T> annotationClass) {
        return findAnnotation(declaredAnnotations, annotationClass, new HashSet<>());
    }

    /**
     * Searches for annotation, collects processed, to avoid StackOverflow.
     */
    private <T extends Annotation> T findAnnotation(Annotation[] declaredAnnotations, Class<T> annotationClass, Set<Annotation> processed) {
        for (Annotation candidate : declaredAnnotations) {
            final Class<? extends Annotation> annType = candidate.annotationType();
            if (annType.equals(annotationClass)) {
                return (T) candidate;
            }
            processed.add(candidate);
            final List<Annotation> inheritedAnnotations = new ArrayList<>(Arrays.asList(annType.getDeclaredAnnotations()));
            inheritedAnnotations.removeAll(processed);
            if (inheritedAnnotations.size() > 0) {
                final T inherited = findAnnotation(inheritedAnnotations.toArray(new Annotation[inheritedAnnotations.size()]), annotationClass, processed);
                if (inherited != null) {
                    return inherited;
                }
            }
        }
        return null;
    }

    private <T extends Annotation> T getMethodAnnotation(Class<T> annotationClass, JsonbAnnotatedElement<Method> methodElement) {
        if (methodElement == null) {
            return null;
        }
        return findAnnotation(methodElement.getAnnotations(), annotationClass);
    }

    private <T extends Annotation> void collectFromInterfaces(Class<T> annotationClass, Class clazz, Map<Class<?>, T> collectedAnnotations) {

        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            T annotation = findAnnotation(interfaceClass.getDeclaredAnnotations(), annotationClass);
            if (annotation != null) {
                collectedAnnotations.put(interfaceClass, annotation);
            }
            collectFromInterfaces(annotationClass, interfaceClass, collectedAnnotations);
        }
    }

    /**
     * Returns JsonbPropertyOrder annotation if class has one
     * @param clazz Class to be checked
     * @return
     */
    public Optional<JsonbPropertyOrder> getJsonbPropertyOrderAnnotation(Class<?> clazz) {
        return Optional.ofNullable(findAnnotation(clazz.getDeclaredAnnotations(), JsonbPropertyOrder.class));
    }

    /**
     * Get class interfaces recursively.
     */
    public Set<Class<?>> collectInterfaces(Class<?> cls) {
        Set<Class<?>> collected = new LinkedHashSet<>();
        Queue<Class<?>> toScan = new LinkedList<>();
        toScan.addAll(Arrays.asList(cls.getInterfaces()));
        Class<?> nextIfc;
        while((nextIfc = toScan.poll()) != null) {
            collected.add(nextIfc);
            toScan.addAll(Arrays.asList(nextIfc.getInterfaces()));
        }
        return collected;
    }

    public ClassCustomization introspectCustomization(JsonbAnnotatedElement<Class<?>> clsElement) {
        final CustomizationBuilder builder = new CustomizationBuilder();
        builder.setNillable(isClassNillable(clsElement));
        builder.setDateFormatter(getJsonbDateFormat(clsElement));
        builder.setNumberFormat(getJsonbNumberFormat(clsElement));
        builder.setCreator(getCreator(clsElement.getElement()));
        return builder.buildClassCustomization();
    }

    /**
     * Collect annotations of a class and its interfaces.
     * @return element with class and annotations
     */
    public JsonbAnnotatedElement<Class<?>> collectAnnotations(Class<?> clazz) {
        JsonbAnnotatedElement<Class<?>> classElement = new JsonbAnnotatedElement<>(clazz);

        for (Class<?> ifc : AnnotationIntrospector.getInstance().collectInterfaces(clazz)) {
            addIfNotPresent(classElement, ifc.getDeclaredAnnotations());
        }

        if (!clazz.isPrimitive() && !clazz.isArray()) {
            addIfNotPresent(classElement, clazz.getPackage().getAnnotations());
        }
        return classElement;
    }

    private void addIfNotPresent(JsonbAnnotatedElement<?> element, Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (element.getAnnotation(annotation.annotationType()) == null) {
                element.putAnnotation(annotation);
            }
        }
    }
}
