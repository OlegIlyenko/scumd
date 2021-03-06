package com.asolutions.scmsshd.commands.git;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.session.ServerSession;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SCMCommandTest extends MockTestCase {

	private static final String USERNAME = "username";
	private static final String PROJECT = "proj-2";
	
	private FilteredCommand filteredCommand;
	private ProjectAuthorizer mockProjectAuthorizer;
	private ServerSession mockSession;
	private PathToProjectNameConverter mockPathToProjectConverter;
	private ExitCallback mockExitCallback;
	
	private SCMCommand command = new SCMCommand();
	private InputStream mockInputStream;
	private OutputStream mockOutputStream;
	private OutputStream mockErrorStream;
	private Properties mockConfig;

	@Before
	public void setup() {
		filteredCommand = new FilteredCommand("git-upload-pack", "/proj-2");
		mockProjectAuthorizer = context.mock(ProjectAuthorizer.class);
		mockSession = context.mock(ServerSession.class);
		mockPathToProjectConverter = context.mock(PathToProjectNameConverter.class);
		mockExitCallback = context.mock(ExitCallback.class);
		mockInputStream = context.mock(InputStream.class);
		mockOutputStream = context.mock(OutputStream.class);
		mockErrorStream = context.mock(OutputStream.class, "mockErrorStream");
		mockConfig = context.mock(Properties.class);
		
		command.setProjectAuthorizer(mockProjectAuthorizer);
		command.setSession(mockSession);
		command.setExitCallback(mockExitCallback);
		command.setPathToProjectNameConverter(mockPathToProjectConverter);
		command.setFilteredCommand(filteredCommand);
		command.setErrorStream(mockErrorStream);
		command.setInputStream(mockInputStream);
		command.setOutputStream(mockOutputStream);
		command.setConfiguration(mockConfig);
	}

	@Test
	public void testCanSetInputStream() throws Exception {
		assertEquals(mockInputStream, command.getInputStream());
	}

	@Test
	public void testCanSetOutputStream() throws Exception {
		assertEquals(mockOutputStream, command.getOutputStream());
	}

	@Test
	public void testCanSetErrorStream() throws Exception {
		assertEquals(mockErrorStream, command.getErrorStream());
	}

	@Test
	public void testExitCallback() throws Exception {
		assertEquals(mockExitCallback, command.getExitCallback());
	}

	@Test
	public void testSetSession() throws Exception {
		assertEquals(mockSession, command.getSession());
	}
	
	@Test
	public void testSetConfig() throws Exception {
		assertEquals(mockConfig, command.getConfiguration());
	}

	@Test
	public void testGetUsernameFromSession() throws Exception {
		checking(new Expectations() {
		{
			oneOf(mockSession).getUsername();
			will(returnValue(USERNAME));
		}
		});
		assertEquals(USERNAME, command.getUsername());
	}
	
	@Test
	public void testGetUsernameWhenNoSessionSet() throws Exception {
		command.setSession(null);
		assertNull(command.getUsername());
	}

	@Test
	public void testAuthorizerPassing() throws Exception {
		final SCMCommandHandler mockSCMCommandHandler = context.mock(SCMCommandHandler.class);
        final ServerSession mockSession = context.mock(ServerSession.class);

		checking(new Expectations() {{
			one(mockSession).getUsername();
			will(returnValue(USERNAME));
			one(mockPathToProjectConverter).convert("/proj-2");
			will(returnValue(PROJECT));
			one(mockProjectAuthorizer).userIsAuthorizedForProject(USERNAME, PROJECT, null);
			will(returnValue(AuthorizationLevel.AUTH_LEVEL_READ_ONLY));
            one(mockSCMCommandHandler).execute(new CommandContext(filteredCommand,
                    mockInputStream,
                    mockOutputStream,
                    mockErrorStream,
                    mockExitCallback,
                    mockConfig,
                    mockSession,
                    AuthorizationLevel.AUTH_LEVEL_READ_ONLY));
		}});
		command.setSCMCommandHandler(mockSCMCommandHandler);
		command.runImpl();
	}
	
	@Test
	public void testAuthorizerFailing() throws Exception {
		checking(new Expectations() {{
			one(mockSession).getUsername();
			will(returnValue(USERNAME));
			one(mockPathToProjectConverter).convert("/proj-2");
			will(returnValue(PROJECT));
			one(mockProjectAuthorizer).userIsAuthorizedForProject(USERNAME, PROJECT, null);
			will(returnValue(null));
			one(mockExitCallback).onExit(1);
		}});
		command.runImpl();
	}

}
