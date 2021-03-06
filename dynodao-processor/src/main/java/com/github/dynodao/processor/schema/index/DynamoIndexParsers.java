package com.github.dynodao.processor.schema.index;

import com.github.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link DynamoIndexParser} in the appropriate order.
 */
public class DynamoIndexParsers implements Streamable<DynamoIndexParser> {

    @Inject TableIndexParser tableIndexParser;
    @Inject LocalSecondaryIndexParser localSecondaryIndexParser;
    @Inject GlobalSecondaryIndexParser globalSecondaryIndexParser;

    private final List<DynamoIndexParser> dynamoIndexParsers = new ArrayList<>();

    @Inject DynamoIndexParsers() { }

    @Inject void initDynamoIndexParsers() {
        dynamoIndexParsers.add(tableIndexParser);
        dynamoIndexParsers.add(localSecondaryIndexParser);
        dynamoIndexParsers.add(globalSecondaryIndexParser);
    }

    @Override
    public Iterator<DynamoIndexParser> iterator() {
        return dynamoIndexParsers.iterator();
    }

}

