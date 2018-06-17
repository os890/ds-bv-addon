package org.os890.bv.addon.label.impl;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

class BeanManagerProvider {
    private static final BeanManagerProvider INSTANCE = new BeanManagerProvider();

    static BeanManagerProvider getInstance() {
        return INSTANCE;
    }

    BeanManager getBeanManager() {
        try
        {
            // this location is specified in JSR-299 and must be
            // supported in all certified EE environments
            return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        }
        catch (NamingException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
