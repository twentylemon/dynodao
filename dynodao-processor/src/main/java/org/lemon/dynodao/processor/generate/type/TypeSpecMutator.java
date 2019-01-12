package org.lemon.dynodao.processor.generate.type;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

public interface TypeSpecMutator {

    /**
     * Mutates the <tt>typeSpec</tt> to add necessary methods, fields, etc.
     *
     * @param typeSpec
     * @param pojo the pojo being built
     * @return the methods to add
     */
    void build(TypeSpec.Builder typeSpec, PojoClassBuilder pojo);
}