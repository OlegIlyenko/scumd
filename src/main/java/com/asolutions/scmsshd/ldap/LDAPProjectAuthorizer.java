package com.asolutions.scmsshd.ldap;

import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

/**
 * @deprecated Should be implemented as dao
 */
@Deprecated
public class LDAPProjectAuthorizer implements ProjectAuthorizer {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private String lookupUserDN;
	private String lookupUserPassword;
	private String groupBaseDN;
	private String userBaseDN;
	private String groupSuffix;
	private AuthorizationLevel authorizationLevel;
	private JavaxNamingProvider provider;

	public LDAPProjectAuthorizer(String lookupUserDN,
								 String lookupUserPassword, 
								 String groupBaseDN, 
								 String userBaseDN,
								 String groupSuffix,
								 String url, 
								 boolean promiscuous, 
								 AuthorizationLevel authorizationLevel)
			throws NamingException {
		this(lookupUserDN, lookupUserPassword, groupBaseDN,userBaseDN, groupSuffix,
				new JavaxNamingProviderImpl(url, promiscuous), authorizationLevel);
	}

	public LDAPProjectAuthorizer(String lookupUserDN,
								 String lookupUserPassword, 
								 String groupBaseDN,
								 String userBaseDN,
								 String groupSuffix,
								 JavaxNamingProvider provider,
								 AuthorizationLevel authorizationLevel) throws NamingException {
		this.lookupUserDN = lookupUserDN;
		this.lookupUserPassword = lookupUserPassword;
		this.groupBaseDN = groupBaseDN;
		this.userBaseDN = userBaseDN;
		this.groupSuffix = groupSuffix;
		this.provider = provider;
		this.authorizationLevel = authorizationLevel;
		getLdapBinding(provider);
	}

	protected InitialDirContext getLdapBinding(JavaxNamingProvider provider)
			throws NamingException {
		return provider.getBinding(this.lookupUserDN,
										   this.lookupUserPassword);
	}

	public AuthorizationLevel userIsAuthorizedForProject(String username, String group, ServerSession session)
			throws UnparsableProjectException {
		username = getUserDN(username);
		group = getGroupDN(group);
		try {
			Attributes attrs = getLdapBinding(provider).getAttributes(group);
			NamingEnumeration<?> e = attrs.get("member").getAll();
			while (e.hasMoreElements())
			{
				String value = e.nextElement().toString().toLowerCase();
				if (username.toLowerCase().equals(value)){
					return authorizationLevel;
				}
			}
			return null;
		} catch (NamingException e) {
			log.error("Error running impl" , e);
			return null;
		}
	}

	private String getGroupDN(String group) {
		if (groupSuffix == null)
		{
			return "cn=" + group + "," + groupBaseDN;
		}
		else{
			return "cn=" + group + "-" + groupSuffix + "," + groupBaseDN;
		}
	}

	private String getUserDN(String username) {
		return "cn=" + username + "," + userBaseDN;
	}

}
