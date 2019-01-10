package org.lemon.dynodao.processor;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.index.DynamoIndexParser;

@Component(modules = ContextModule.class)
public interface ObjectGraph {

    void inject(DynoDaoProcessor processor);

    DynamoIndexParser dynamoIndexParser();
}

@Module
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ContextModule {

    private final ProcessorContext processorContext;

    @Provides
    ProcessorContext providesProcessorContext() {
        return processorContext;
    }

}