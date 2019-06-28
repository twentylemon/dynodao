package com.github.dynodao.processor.stage.operation;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.serialize.SerializerTypeSpec;
import com.github.dynodao.processor.stage.InterfaceType;
import com.github.dynodao.processor.stage.KeyLengthType;
import com.squareup.javapoet.FieldSpec;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Value
public final class GetStage {

    private final DynamoSchema schema;
    private final SerializerTypeSpec serializer;
    private final List<DynamoAttribute> attributes = new ArrayList<>();
    private final DynamoIndex dynamoIndex;
    private final KeyLengthType keyLengthType;
    private final Set<InterfaceType> interfaceTypes;

    @Builder
    GetStage(DynamoSchema schema, SerializerTypeSpec serializer, DynamoIndex index, KeyLengthType keyLengthType) {
        this.schema = schema;
        this.serializer = serializer;
        this.dynamoIndex = index;
        this.keyLengthType = keyLengthType;
        this.attributes.addAll(keyLengthType.getKeyAttributes(index));
        this.interfaceTypes = EnumSet.of(InterfaceType.typeOf(index, keyLengthType));
    }

    /**
     * @return the attributes of this model as {@link FieldSpec} as per {@link DynamoAttribute#asFieldSpec()}
     */
    public List<FieldSpec> getAttributesAsFields() {
        return attributes.stream()
                .map(DynamoAttribute::asFieldSpec)
                .collect(toList());
    }

}
