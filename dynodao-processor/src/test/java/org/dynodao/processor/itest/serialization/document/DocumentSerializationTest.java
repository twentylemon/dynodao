package org.dynodao.processor.itest.serialization.document;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeDocument_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeDocument_allFieldsPresent_returnsMapAttributeValueWithOverrideAttributeNames() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document("a1", "a2", "a3"));
        assertThat(value).isEqualTo(attributeValue("a1", "a2", "a3"));
    }

    @Test
    void serializeDocument_someFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document(null, null, "a3"));
        assertThat(value).isEqualTo(attributeValue(null, null, "a3"));
    }

    @Test
    void serializeDocument_allFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document(null, null, null));
        assertThat(value).isEqualTo(attributeValue(null, null, null));
    }

    @Test
    void serializeDocumentAsItem_null_returnsEmptyMap() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(null);
        assertThat(item).isEmpty();
    }

    @Test
    void serializeDocumentAsItem_allFieldsPresent_returnsMapWithOverrideAttributeNames() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(document("a1", "a2", "a3"));
        assertThat(item).isEqualTo(item("a1", "a2", "a3"));
    }

    @Test
    void serializeDocumentAsItem_someFieldsNull_returnsMapWithNulls() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(document(null, null, "a3"));
        assertThat(item).isEqualTo(item(null, null, "a3"));
    }

    @Test
    void serializeDocumentAsItem_allFieldsNull_returnsMapWithNulls() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(document(null, null, null));
        assertThat(item).isEqualTo(item(null, null, null));
    }

    @Test
    void deserializeDocument_null_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_nullAttributeValue_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_mapValueNull_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withS("not map"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_mapAttributeValue_returnsDocument() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(attributeValue("a1", "a2", "a3"));
        assertThat(value).isEqualTo(document("a1", "a2", "a3"));
    }

    @Test
    void deserializeDocument_emptyMap_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocument_valueHaveWrongKeys_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("dynamoNameIsAttribute3", new AttributeValue("a3"))));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocument_someKeysCorrect_returnsDocumentWithNullForMissingFieldsOnly() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("attribute3", new AttributeValue("a3"))));
        assertThat(value).isEqualTo(document(null, null, "a3"));
    }

    @Test
    void deserializeDocument_keysHaveWrongTypes_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("attribute3", new AttributeValue().withM(emptyMap()))));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocumentFromItem_null_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocumentFromItem_allFieldsPresent_returnsDocument() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(item("a1", "a2", "a3"));
        assertThat(value).isEqualTo(document("a1", "a2", "a3"));
    }

    @Test
    void deserializeDocumentFromItem_emptyMap_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(emptyMap());
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocumentFromItem_valueHaveWrongKeys_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(singletonMap("dynamoNameIsAttribute3", new AttributeValue("a3")));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocumentFromItem_someKeysCorrect_returnsDocumentWithNullForMissingFieldsOnly() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(singletonMap("attribute3", new AttributeValue("a3")));
        assertThat(value).isEqualTo(document(null, null, "a3"));
    }

    @Test
    void deserializeDocumentFromItem_keysHaveWrongTypes_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(singletonMap("attribute3", new AttributeValue().withM(emptyMap())));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    private Document document(String attribute1, String attribute2, String attribute3) {
        Document document = new Document();
        document.setAttribute1(attribute1);
        document.setAttribute2(attribute2);
        document.setDynamoNameIsAttribute3(attribute3);
        return document;
    }

    private Map<String, AttributeValue> item(String attribute1, String attribute2, String attribute3) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("attribute1", attribute1 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute1));
        item.put("attribute2", attribute2 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute2));
        item.put("attribute3", attribute3 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute3));
        return item;
    }

    private AttributeValue attributeValue(String attribute1, String attribute2, String attribute3) {
        return new AttributeValue().withM(item(attribute1, attribute2, attribute3));
    }

}