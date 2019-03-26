package org.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import org.dynodao.processor.schema.DynamoSchema;

import javax.inject.Inject;

import static org.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

/**
 * Adds the {@link javax.annotation.Generated} annotation to the class being generated.
 */
class GeneratedAnnotationSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject GeneratedAnnotationSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        typeSpec.addAnnotation(generatedAnnotation());
    }

}