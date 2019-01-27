package org.lemon.dynodao.processor.serialize.generate;

import org.lemon.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link SerializerTypeSpecMutator} in the appropriate order.
 */
public class SerializerTypeSpecMutators implements Streamable<SerializerTypeSpecMutator> {

    @Inject GeneratedAnnotationSerializerTypeSpecMutator generatedAnnotationSerializerTypeSpecMutator;
    @Inject JavaDocSerializerTypeSpecMutator javaDocSerializerTypeSpecMutator;
    @Inject ModifiersSerializerTypeSpecMutator modifiersSerializerTypeSpecMutator;
    @Inject CtorSerializerTypeSpecMutator ctorSerializerTypeSpecMutator;
    @Inject SerializeMethodsSerializerTypeSpecMutator serializeMethodsSerializerTypeSpecMutator;

    private final List<SerializerTypeSpecMutator> serializerTypeSpecMutators = new ArrayList<>();

    @Inject SerializerTypeSpecMutators() { }

    /**
     * Populates the serializerTypeSpecMutators field. The list is ordered, the first elements are added to the
     * generated class first.
     */
    @Inject void initSerializerTypeSpecMutators() {
        serializerTypeSpecMutators.add(generatedAnnotationSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(javaDocSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(modifiersSerializerTypeSpecMutator);
        serializerTypeSpecMutators.add(ctorSerializerTypeSpecMutator);

        serializerTypeSpecMutators.add(serializeMethodsSerializerTypeSpecMutator);
    }

    @Override
    public Iterator<SerializerTypeSpecMutator> iterator() {
        return serializerTypeSpecMutators.iterator();
    }

}