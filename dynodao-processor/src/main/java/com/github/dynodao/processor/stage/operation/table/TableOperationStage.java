package com.github.dynodao.processor.stage.operation.table;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.stage.operation.OperationStage;
import lombok.Builder;
import lombok.Value;

@Value
public final class TableOperationStage implements OperationStage {

    private final DynamoSchema schema;

    private final CreateTableStage createTableStage;

    @Builder
    private TableOperationStage(DynamoSchema schema) {
        this.schema = schema;
        this.createTableStage = CreateTableStage.builder()
                .schema(schema)
                .build();
    }

}
