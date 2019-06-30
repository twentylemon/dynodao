package com.github.dynodao.processor.stage.operation;

import com.github.dynodao.processor.stage.InterfaceType;

/**
 * A stage in the builder that can perform an action in dynamo, such as reading or writing.
 */
public interface ExecutionStage {

    /**
     * Returns the interface this stage implements, ie the action this stage can perform.
     * @return the interface this stage implements
     */
    InterfaceType getInterfaceType();

}
