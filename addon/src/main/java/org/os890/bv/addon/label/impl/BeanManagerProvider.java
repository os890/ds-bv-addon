package org.os890.bv.addon.label.impl;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BeanManagerProvider {
    private final static BeanManagerProvider INSTANCE = new BeanManagerProvider();

    private BeanManager customBeanManager; //workaround which wouldn't be needed with deltaspike

    public static BeanManagerProvider getInstance() {
        return INSTANCE;
    }

    BeanManager getBeanManager() {
        try
        {
            if (customBeanManager != null) {
                return customBeanManager;
            }

            // this location is specified in JSR-299 and must be
            // supported in all certified EE environments
            customBeanManager = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
            return customBeanManager;
        }
        catch (NamingException e)
        {
            throw new IllegalStateException(e);
        }
    }

    public void setBeanManager(BeanManager beanManager) {
        INSTANCE.customBeanManager = beanManager;
    }
}
