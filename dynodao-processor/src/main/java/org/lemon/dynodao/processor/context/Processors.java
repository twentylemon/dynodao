package org.lemon.dynodao.processor.context;

import lombok.experimental.Delegate;

import javax.inject.Inject;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * Provides access to {@link Elements} and {@link Types} utility classes, as well as providing some additional
 * utility methods related to them.
 */
public class Processors implements Elements, Types {

    private final ProcessorContext processorContext;

    @Delegate(types = Elements.class)
    private final Elements elementUtils;

    @Delegate(types = Types.class)
    private final Types typeUtils;

    @Inject Processors(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        this.elementUtils = processorContext.getProcessingEnvironment().getElementUtils();
        this.typeUtils = processorContext.getProcessingEnvironment().getTypeUtils();
    }

    /**
     * Returns the {@link TypeElement} corresponding to the {@link Class} provided.
     * @param clazz the class to get the type of
     * @return the {@link TypeElement}
     * @see Elements#getTypeElement(CharSequence)
     */
    public TypeElement getTypeElement(Class<?> clazz) {
        return getTypeElement(clazz.getCanonicalName());
    }

    /**
     * Returns the {@link DeclaredType} of the <tt>type</tt> with the given <tt>typeArguments</tt>.
     * @param type the class type
     * @param typeArguments the template arguments, if any
     * @return the {@link DeclaredType}
     * @see Types#getDeclaredType(TypeElement, TypeMirror...)
     */
    public DeclaredType getDeclaredType(Class<?> type, Class<?>... typeArguments) {
        TypeElement typeElement = getTypeElement(type.getCanonicalName());
        TypeMirror[] args = Arrays.stream(typeArguments)
                .map(clazz -> getTypeElement(clazz.getCanonicalName()).asType())
                .toArray(TypeMirror[]::new);
        return getDeclaredType(typeElement, args);
    }

    /**
     * Returns <tt>true</tt> if the <tt>typeMirror</tt> is the same type as the declared type
     * constructed by the Class arguments.
     * @param typeMirror the type to test
     * @param type the declared class type
     * @param typeArguments the template arguments of the declared class type, if any
     * @return <tt>true</tt> if <tt>typeMirror</tt> is the same declared type as the classes provided
     * @see Types#isSameType(TypeMirror, TypeMirror)
     * @see #getDeclaredType(Class, Class[])
     */
    public boolean isSameType(TypeMirror typeMirror, Class<?> type, Class<?>... typeArguments) {
        return isSameType(typeMirror, getDeclaredType(type, typeArguments));
    }

    /**
     * Returns the annotation mirror of the specified class.
     * @param element the element which has the annotation
     * @param annotation the annotation class to get the mirror of
     * @return the annotation mirror, or null if none exists
     */
    public AnnotationMirror getAnnotationMirrorOfType(Element element, Class<? extends Annotation> annotation) {
        return element.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().asElement().toString().equals(annotation.getCanonicalName()))
                .findAny().orElse(null);
    }

}
