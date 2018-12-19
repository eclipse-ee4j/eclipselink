package org.eclipse.persistence.jpa.test.canonical;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(DomainPersistable.class)
public class DomainInterface_ {

    public static volatile SingularAttribute<DomainPersistable, Long> id;
    public static volatile SingularAttribute<DomainPersistable, String> name;
}
