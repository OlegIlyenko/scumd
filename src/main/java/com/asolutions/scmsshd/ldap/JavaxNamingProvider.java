package com.asolutions.scmsshd.ldap;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public interface JavaxNamingProvider {

	InitialDirContext getBinding(String userDN, String lookupUserPassword) throws NamingException;

}
