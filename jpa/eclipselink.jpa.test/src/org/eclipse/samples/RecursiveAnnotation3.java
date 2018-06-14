package org.eclipse.samples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.persistence.Access;
import javax.persistence.AccessType;

@Target(ElementType.ANNOTATION_TYPE)
@RecursiveAnnotation
@Access(AccessType.FIELD)
public @interface RecursiveAnnotation3 {
}
