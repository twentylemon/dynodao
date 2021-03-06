package com.github.dynodao.processor.itest;

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Disabled;

import javax.tools.JavaFileObject;

/**
 * Base class for compiling a class from test resources.
 */
@Disabled
public abstract class AbstractResourceCompilingTest extends AbstractCompilingTest {

    /**
     * Returns the resource file with the simple class name, in the same package as the test class.
     * @param simpleClassName the simple class name of the java class to obtain
     * @return the schema class
     */
    protected final JavaFileObject getSchema(String simpleClassName) {
        String packageName = getClass().getPackage().getName();
        String javaPath = String.format("%s.%s", packageName, simpleClassName);
        return JavaFileObjects.forResource(javaPath.replace('.', '/') + ".java");
    }

}
