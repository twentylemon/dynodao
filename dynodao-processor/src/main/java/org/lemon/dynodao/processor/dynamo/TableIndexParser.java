package org.lemon.dynodao.processor.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import org.lemon.dynodao.DynoDao;
import org.lemon.dynodao.processor.context.ProcessorMessager;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Extracts the overall table "index" from the schema document.
 */
class TableIndexParser implements DynamoIndexParser {

    @Inject ProcessorMessager processorMessager;
    @Inject SchemaFieldsProvider schemaFieldsProvider;

    @Inject TableIndexParser() { }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynamoDBHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getField().getAnnotation(DynamoDBRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(document, hashKeys, rangeKeys);

        return singleton(toIndex(hashKeys, rangeKeys, attributes));
    }

    private void validate(TypeElement document, Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys) {
        if (hashKeys.size() != 1) {
            if (hashKeys.isEmpty()) {
                processorMessager.submitError("@%s must exist on exactly one scalar attribute, but none found.", DynamoDBHashKey.class.getSimpleName())
                        .atElement(document)
                        .atAnnotation(DynoDao.class);
            }
            hashKeys.forEach(hashKey -> processorMessager.submitError("@%s must exist on exactly one attribute.", DynamoDBHashKey.class.getSimpleName())
                    .atElement(hashKey.getField())
                    .atAnnotation(DynamoDBHashKey.class));
        }
        if (rangeKeys.size() > 1) {
            processorMessager.submitError("@%s must exist on at most one attribute, but found %s", DynamoDBRangeKey.class.getSimpleName(), rangeKeys);
        }
    }

    private DynamoIndex toIndex(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        return DynamoIndex.builder()
                .indexType(IndexType.TABLE)
                .name("<table>")
                .hashKeyAttribute(hashKeys.iterator().next())
                .rangeKeyAttribute(rangeKeys.stream().findAny())
                .projectedAttributes(attributes)
                .build();
    }

}
