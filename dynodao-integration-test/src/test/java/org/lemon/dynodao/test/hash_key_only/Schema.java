package org.lemon.dynodao.test.hash_key_only;

import lombok.Data;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

}
