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

package org.eclipse.persistence.json.bind.model;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.stream.Stream;

/**
 * Predefined type adapter which is supposed to handle polymorphism.
 *
 * <p>PolymorphismAdapter is supposed to adapt polymorphic type instances to {@link TypeWrapper}, so that resulting json document
 * will contain not only a polymorphic object, but also a class name, which is required for deserialization in case
 * of polymorphic types.
 * Consider a class model:</p>
 * <pre>
 *     class Pojo {
 *         Animal animal;
 *         List&lt;Animal&gt; listOfAnimals
 *     }
 * </pre>
 *
 * <p>With a Dog and Cat types which extend Animal, adapter can be configured like this:</p>
 *
 * <pre>
 * class AnimalAdapter extends PolymorphismAdapter&lt;Animal&gt; {}
 * JsonbConfig config ...
 * config.withAdapters(new AnimalAdapter(Dog.class, Cat.class));
 * </pre>
 *
 * <p>Polymorphic types will be than transparently handled for all instances of Animal in a class model.</p>
 *
 * <p>Enumerating allowed classes: If array of allowed classes is passed into parent constructor of
 * {@link PolymorphismAdapter}, only those classes would be allowed for loading by name. If incoming JSON
 * is not trusted, this option should always be used.</p>
 *
 * @author Roman Grigoriadi
 */
public class PolymorphismAdapter<T> implements JsonbAdapter<T, TypeWrapper<T>> {

    private final String[] allowedClasses;

    public PolymorphismAdapter(final Class... allowedClasses) {
        this.allowedClasses = Stream.of(allowedClasses).map(Class::getName).toArray(value -> new String[allowedClasses.length]);
    }

    public String[] getAllowedClasses() {
        return allowedClasses;
    }

    @Override
    public TypeWrapper<T> adaptToJson(T obj) throws Exception {
        TypeWrapper<T> wrapper = new TypeWrapper<>();
        wrapper.setClassName(obj.getClass().getName());
        wrapper.setInstance(obj);
        return wrapper;
    }

    @Override
    public T adaptFromJson(TypeWrapper<T> obj) throws Exception {
        return obj.getInstance();
    }
}
