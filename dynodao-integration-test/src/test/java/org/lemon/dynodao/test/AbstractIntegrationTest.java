package org.lemon.dynodao.test;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Base class for tests. Good docs are good.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class AbstractIntegrationTest {

    /**
     * Initials any mockito mock annotations in this class.
     * <p>
     * Though this class uses the mockito junit runner, it may be overridden by subclasses.
     */
    @Before
    public void initMocksIfRequired() {
        RunWith runWith = getClass().getAnnotation(RunWith.class);
        if (runWith == null || !runWith.value().equals(MockitoJUnitRunner.class)) {
            initMocks(this);
        }
    }

}
