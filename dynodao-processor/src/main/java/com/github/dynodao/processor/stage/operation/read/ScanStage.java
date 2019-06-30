package com.github.dynodao.processor.stage.operation.read;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import lombok.Value;

@Value
public final class ScanStage {

    private final DynamoSchema schema;
    private final DynamoIndex index;

}
