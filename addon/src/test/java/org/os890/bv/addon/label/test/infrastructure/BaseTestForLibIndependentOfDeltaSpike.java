package org.os890.bv.addon.label.test.infrastructure;

import org.junit.Before;
import org.os890.bv.addon.label.impl.BeanManagerProvider;

//just needed because the lib itself isn't based on deltaspike
public abstract class BaseTestForLibIndependentOfDeltaSpike {
    @Before
    public void initWithoutDeltaSpike() { //wouldn't be needed with deltaspike
        BeanManagerProvider.getInstance().setBeanManager(
            org.apache.deltaspike.core.api.provider.BeanManagerProvider.getInstance().getBeanManager());
    }
}
