package org.lemon.dynodao.processor.generate.type;

import static java.util.stream.Collectors.joining;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbMapper;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbQueryExpression;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.paginatedList;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.index.IndexType;
import org.lemon.dynodao.processor.model.InterfaceType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.util.DynamoDbUtil;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

class DocumentQueryTypeSpecMutator implements TypeSpecMutator {

    @Inject ProcessorContext processorContext;

    private MethodSpec queryWithNoReturnOrBody;
    private ParameterSpec dynamoDbMapperParam;

    @Inject DocumentQueryTypeSpecMutator() { }

    @Inject void init() {
        dynamoDbMapperParam = ParameterSpec.builder(dynamoDbMapper(), "dynamoDbMapper").build();

        TypeElement interfaceType = processorContext.getElementUtils().getTypeElement(InterfaceType.DOCUMENT_QUERY.getInterfaceClass().get().getCanonicalName());
        ExecutableElement method = (ExecutableElement) interfaceType.getEnclosedElements().iterator().next();
        MethodSpec.Builder load = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(dynamoDbMapperParam);

        queryWithNoReturnOrBody = load.build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        if (isDocumentQuery(pojo)) {
            MethodSpec query = buildQuery(pojo);
            typeSpec.addMethod(query);
        }
    }

    private boolean isDocumentQuery(PojoClassBuilder pojo) {
        return pojo.getInterfaceType().equals(InterfaceType.DOCUMENT_QUERY);
    }

    private MethodSpec buildQuery(PojoClassBuilder pojo) {
        MethodSpec.Builder query = queryWithNoReturnOrBody.toBuilder()
                .returns(paginatedList(pojo.getDocument()));

        TypeName queryExpression = dynamoDbQueryExpression(pojo.getDocument());

        query.addStatement("$T query = new $T()", queryExpression, queryExpression);
        appendIndexName(query, pojo);
        appendKeyConditionExpression(query, pojo);
        appendExpressionAttributeNames(query, pojo);
        appendExpressionAttributeValues(query, pojo);
        query.addStatement("return $N.query($T.class, query)", dynamoDbMapperParam, pojo.getDocument().asType());
        return query.build();
    }

    private void appendIndexName(MethodSpec.Builder query, PojoClassBuilder pojo) {
        if (!pojo.getDynamoIndex().get().getIndexType().equals(IndexType.TABLE)) {
            query.addStatement("query.setIndexName($S)", pojo.getDynamoIndex().get().getName());
        }
    }

    private void appendKeyConditionExpression(MethodSpec.Builder query, PojoClassBuilder pojo) {
        String expression = pojo.getFields().stream()
                .map(field -> String.format("#%s = :%s", field.name, field.name))
                .collect(joining(" AND "));
        query.addStatement("query.setKeyConditionExpression($S)", expression);
    }

    private void appendExpressionAttributeNames(MethodSpec.Builder query, PojoClassBuilder pojo) {
        pojo.getFields().forEach(field -> query.addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + field.name, field.name));
    }

    private void appendExpressionAttributeValues(MethodSpec.Builder query, PojoClassBuilder pojo) {
        // TODO need to select the proper withX method here
        // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html#HowItWorks.CoreComponents.TablesItemsAttributes
        // The only data types allowed for primary key attributes are string, number, or binary.
        pojo.getFields().forEach(field -> query.addStatement("query.addExpressionAttributeValuesEntry($S, new $T().withS($N))", ":" + field.name, attributeValue(), field));
    }
}
