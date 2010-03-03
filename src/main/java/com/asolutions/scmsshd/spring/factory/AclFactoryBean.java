package com.asolutions.scmsshd.spring.factory;

import com.asolutions.scmsshd.model.security.RawRepositoryAcl;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.util.List;

/**
 * @author Oleg Ilyenko
 */
public class AclFactoryBean extends AbstractFactoryBean<List> {

    private List<RawRepositoryAcl> rawRepositoryAcls;

    public List<RawRepositoryAcl> getRawRepositoryAcl() {
        return rawRepositoryAcls;
    }

    public void setRawRepositoryAcl(List<RawRepositoryAcl> rawRepositoryAcls) {
        this.rawRepositoryAcls = rawRepositoryAcls;
    }

    @Override
    public Class<? extends List> getObjectType() {
        return List.class;
    }

    @Override
    protected List<RawRepositoryAcl> createInstance() throws Exception {
        return rawRepositoryAcls;
    }

}
