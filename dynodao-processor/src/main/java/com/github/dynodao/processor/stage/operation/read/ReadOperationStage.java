package com.github.dynodao.processor.stage.operation.read;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.stage.operation.OperationStage;
import lombok.Builder;
import lombok.Value;

@Value
public final class ReadOperationStage implements OperationStage {

    private final DynamoSchema schema;

    @Builder
    private ReadOperationStage(DynamoSchema schema) {
        this.schema = schema;
    }

}
