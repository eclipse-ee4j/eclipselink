package org.eclipse.persistence.json.bind.defaultmapping.basic.model;

/**
 * Encapsulates different types of boolean values as a field so that the boolean value's serialization and
 * deserialization could be tested
 *
 * Created by Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com) on 9/15/16.
 */
public class BooleanModel {
    public Boolean field1;
    public boolean field2;

    public BooleanModel() {
    }

    public BooleanModel(boolean field1, Boolean field2) {
        this.field2 = field2;
        this.field1 = field1;
    }
}
