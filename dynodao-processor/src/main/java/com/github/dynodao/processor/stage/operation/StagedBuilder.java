package com.github.dynodao.processor.stage.operation;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.stage.operation.read.ReadOperationStage;
import com.github.dynodao.processor.stage.operation.table.TableOperationStage;
import lombok.Builder;
import lombok.Value;

/**
 * The entry point to creating instances for use in the {@link com.github.dynodao.DynoDao DynoDao} methods.
 * Each stage consists of stateful data which describes the operation (read, write), filters, etc.
 * <pre>
 *  - TableOperationStage: no op, requires an operation type
 *      - CreateTableStage: can create the table
 *  - ReadOperationStage: no op, requires an index to operation upon (usingTable or usingIndex)
 *      - IndexStage: scan, withers allow for query/load operations instead
 *          - ReadExecutionStage: query or load, depending on attributes and keyLength of the index
 * </pre>
 */
@Value
public final class StagedBuilder {

    private final DynamoSchema schema;

    private final TableOperationStage tableOperationStage;
    private final ReadOperationStage readOperationStage;

    @Builder
    private StagedBuilder(DynamoSchema schema) {
        this.schema = schema;
        this.tableOperationStage = TableOperationStage.builder()
                .schema(schema)
                .build();
        this.readOperationStage = ReadOperationStage.builder()
                .schema(schema)
                .build();
    }

}
