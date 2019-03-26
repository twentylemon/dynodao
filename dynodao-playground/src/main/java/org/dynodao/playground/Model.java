package org.dynodao.playground;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.dynodao.annotation.DynoDaoAttribute;
import org.dynodao.annotation.DynoDaoDocument;
import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoIndexHashKey;
import org.dynodao.annotation.DynoDaoIndexRangeKey;
import org.dynodao.annotation.DynoDaoRangeKey;
import org.dynodao.annotation.DynoDaoSchema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Data
@NoArgsConstructor
@DynoDaoSchema(tableName = "models")
public class Model {

    @DynoDaoHashKey
    private String hashKey;
    @DynoDaoRangeKey
    private String rangeKey;

    @DynoDaoIndexRangeKey(lsiNames = "local-index-name")
    private String lsiRangeKey;

    @DynoDaoIndexHashKey(gsiNames = { "global-index-name", "global-index-2" })
    private String gsiHashKey;

    @DynoDaoIndexRangeKey(gsiNames = "global-index-name")
    private long gsiRangeKey;

    @DynoDaoIndexRangeKey(gsiNames = "global-index-2")
    private String gsiRangeKey2;

    @DynoDaoIndexHashKey(gsiNames = "global-solo-index")
    private String soloGsiHashKey;

    @DynoDaoAttribute("ATTRIBUTE")
    private int attribute;
    private long attribute2;
    private BigDecimal attribute3;
    private Integer attribute4;

    private Map<String, BigInteger> mapOfBigInt;
    private SortedMap<String, Integer> sortedMapOfInt;
    private HashMap<String, Map<String, String>> hashMapOfMaps;

    private NestedModel nestedModel;
//    private Map<String, NestedModel> nestedModelMap;
//    private List<NestedModel> nestedModelList;

}

@Data
@NoArgsConstructor
@DynoDaoDocument
class NestedModel {

    private String string;

}