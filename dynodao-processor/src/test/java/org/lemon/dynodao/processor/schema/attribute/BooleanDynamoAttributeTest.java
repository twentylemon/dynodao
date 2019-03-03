package org.lemon.dynodao.processor.schema.attribute;

import com.jparams.verifier.tostring.ToStringVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class BooleanDynamoAttributeTest extends AbstractUnitTest {

    private static final Object VISITOR_RETURN = new Object();
    private static final Object VISITOR_ARG = new Object();

    @Mock
    private DynamoAttributeVisitor<Object, Object> dynamoAttributeVisitorMock;

    private BooleanDynamoAttribute createInstance() {
        return BooleanDynamoAttribute.builder().build();
    }

    @Test
    public void getAttributeType_onlyUseCase_returnsBoolean() {
        assertThat(createInstance().getAttributeType()).isEqualTo(DynamoAttributeType.BOOLEAN);
    }

    @Test
    public void accept_noArg_invokesVisitBooleanWithNull() {
        BooleanDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBoolean(classUnderTest, null)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBoolean(classUnderTest, null);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    public void accept_withArg_invokesVisitBoolean() {
        BooleanDynamoAttribute classUnderTest = createInstance();
        when(dynamoAttributeVisitorMock.visitBoolean(classUnderTest, VISITOR_ARG)).thenReturn(VISITOR_RETURN);
        Object accept = classUnderTest.accept(dynamoAttributeVisitorMock, VISITOR_ARG);
        assertThat(accept).isEqualTo(VISITOR_RETURN);
        verify(dynamoAttributeVisitorMock).visitBoolean(classUnderTest, VISITOR_ARG);
        verifyNoMoreInteractions(dynamoAttributeVisitorMock);
    }

    @Test
    public void toString_typicalUseCase_includesAllFields() {
        ToStringVerifier.forClass(BooleanDynamoAttribute.class).verify();
    }

    @Test
    public void equals_typicalUseCase_correct() {
        EqualsVerifier.forClass(BooleanDynamoAttribute.class).verify();
    }

}