package com.github.dynodao.processor.stage.operation.table;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.stage.InterfaceType;
import com.github.dynodao.processor.stage.operation.ExecutionStage;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class CreateTableStage implements ExecutionStage {

    private final DynamoSchema schema;

    @Override
    public InterfaceType getInterfaceType() {
        return InterfaceType.CREATE_TABLE;
    }

}
