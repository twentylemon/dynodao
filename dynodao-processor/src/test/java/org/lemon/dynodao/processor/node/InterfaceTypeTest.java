package org.lemon.dynodao.processor.node;

import org.junit.jupiter.api.Test;
import org.lemon.dynodao.DynoDaoCreateTable;
import org.lemon.dynodao.DynoDaoLoad;
import org.lemon.dynodao.DynoDaoQuery;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.schema.index.IndexType;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class InterfaceTypeTest extends AbstractUnitTest {

    @Test
    void typeOf_none_returnsNone() {
        InterfaceType interfaceType = InterfaceType.typeOf(null, KeyLengthType.NONE);
        assertThat(interfaceType).isEqualTo(InterfaceType.NONE);
    }

    @Test
    void typeOf_tableKeyLengthMatchesHash_returnsLoad() {
        InterfaceType interfaceType = InterfaceType.typeOf(index().rangeKey(Optional.empty()).build(), KeyLengthType.HASH);
        assertThat(interfaceType).isEqualTo(InterfaceType.LOAD);
    }
    @Test
    void typeOf_tableKeyLengthMatchesRange_returnsLoad() {
        InterfaceType interfaceType = InterfaceType.typeOf(index().build(), KeyLengthType.RANGE);
        assertThat(interfaceType).isEqualTo(InterfaceType.LOAD);
    }

    @Test
    void typeOf_tableQuery_returnsQuery() {
        InterfaceType interfaceType = InterfaceType.typeOf(index().build(), KeyLengthType.HASH);
        assertThat(interfaceType).isEqualTo(InterfaceType.QUERY);
    }

    @Test
    void typeOf_lsi_returnsQuery() {
        InterfaceType interfaceType = InterfaceType.typeOf(index().indexType(IndexType.LOCAL_SECONDARY_INDEX).build(), KeyLengthType.RANGE);
        assertThat(interfaceType).isEqualTo(InterfaceType.QUERY);
    }

    @Test
    void typeOf_gsi_returnsQuery() {
        InterfaceType interfaceType = InterfaceType.typeOf(index().indexType(IndexType.GLOBAL_SECONDARY_INDEX).build(), KeyLengthType.RANGE);
        assertThat(interfaceType).isEqualTo(InterfaceType.QUERY);
    }

    @Test
    void getInterfaceClass_onlyUseCase_returnsCorrectClass() {
        Map<InterfaceType, Optional<Class<?>>> expectedInterfaceClasses = new EnumMap<>(InterfaceType.class);
        expectedInterfaceClasses.put(InterfaceType.NONE, Optional.empty());
        expectedInterfaceClasses.put(InterfaceType.CREATE, Optional.of(DynoDaoCreateTable.class));
        expectedInterfaceClasses.put(InterfaceType.LOAD, Optional.of(DynoDaoLoad.class));
        expectedInterfaceClasses.put(InterfaceType.QUERY, Optional.of(DynoDaoQuery.class));

        Map<InterfaceType, Optional<Class<?>>> interfaceClasses = Arrays.stream(InterfaceType.values())
                .collect(toMap(Function.identity(), InterfaceType::getInterfaceClass));

        assertThat(interfaceClasses).isEqualTo(expectedInterfaceClasses);
    }

    private DynamoIndex.DynamoIndexBuilder index() {
        return DynamoIndex.builder()
                .name("index-name")
                .indexType(IndexType.TABLE)
                .hashKey(DocumentDynamoAttribute.builder().build())
                .rangeKey(Optional.of(DocumentDynamoAttribute.builder().build()));
    }

}