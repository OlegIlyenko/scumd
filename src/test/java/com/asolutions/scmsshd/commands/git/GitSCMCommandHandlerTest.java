package com.asolutions.scmsshd.commands.git;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.authorizors.AuthorizationLevel;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.handlers.CommandContext;
import com.asolutions.scmsshd.commands.handlers.SCMCommandHandler;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.session.ServerSession;
import org.jmock.Expectations;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class GitSCMCommandHandlerTest extends MockTestCase {
	
	@Test
	public void testExecuteWithUploadPack() throws Exception {
		final FilteredCommand filteredCommand = new FilteredCommand("git-upload-pack", "proj-2/git.git");
		final InputStream mockInputStream = context.mock(InputStream.class);
		final OutputStream mockOutputStream = context.mock(OutputStream.class, "mockOutputStream");
		final OutputStream mockErrorStream = context.mock(OutputStream.class, "mockErrorStream");
		final ExitCallback mockExitCallback = context.mock(ExitCallback.class);
		final SCMCommandHandler mockUploadPackHandler = context.mock(SCMCommandHandler.class, "mockUploadPackHandler");
		final SCMCommandHandler mockFetchPackHandler = context.mock(SCMCommandHandler.class, "mockFetchPackHandler");
        final ServerSession mockSession = context.mock(ServerSession.class);
        final Properties mockProperties = context.mock(Properties.class);
        final CommandContext commandContext = new CommandContext(
                filteredCommand, mockInputStream, mockOutputStream,
                mockErrorStream, mockExitCallback, mockProperties,
                mockSession, AuthorizationLevel.AUTH_LEVEL_READ_WRITE);


		checking(new Expectations(){{
			one(mockUploadPackHandler).execute(commandContext);
		}});
		
		GitSCMCommandHandler handler = new GitSCMCommandHandler(mockUploadPackHandler, mockFetchPackHandler);
		handler.execute(commandContext);
	}
	
	@Test
	public void testWithUploadPackReadOnlyAccessLevelWorks() throws Exception {
		final FilteredCommand filteredCommand = new FilteredCommand("git-upload-pack", "proj-2/git.git");
		final InputStream mockInputStream = context.mock(InputStream.class);
		final OutputStream mockOutputStream = context.mock(OutputStream.class, "mockOutputStream");
		final OutputStream mockErrorStream = context.mock(OutputStream.class, "mockErrorStream");
		final ExitCallback mockExitCallback = context.mock(ExitCallback.class);
		final SCMCommandHandler mockUploadPackHandler = context.mock(SCMCommandHandler.class, "mockUploadPackHandler");
		final SCMCommandHandler mockFetchPackHandler = context.mock(SCMCommandHandler.class, "mockFetchPackHandler");
        final ServerSession mockSession = context.mock(ServerSession.class);
		final Properties mockProperties = context.mock(Properties.class);
        final CommandContext commandContext = new CommandContext(
                filteredCommand, mockInputStream, mockOutputStream,
                mockErrorStream, mockExitCallback, mockProperties,
                mockSession, AuthorizationLevel.AUTH_LEVEL_READ_ONLY);

		checking(new Expectations(){{
			one(mockUploadPackHandler).execute(commandContext);
		}});
		
		GitSCMCommandHandler handler = new GitSCMCommandHandler(mockUploadPackHandler, mockFetchPackHandler);
		handler.execute(commandContext);
	}
	
	@Test
	public void testExecuteWithReceivePack() throws Exception {
		final FilteredCommand filteredCommand = new FilteredCommand("git-receive-pack", "proj-2/git.git");
		final InputStream mockInputStream = context.mock(InputStream.class);
		final OutputStream mockOutputStream = context.mock(OutputStream.class, "mockOutputStream");
		final OutputStream mockErrorStream = context.mock(OutputStream.class, "mockErrorStream");
		final ExitCallback mockExitCallback = context.mock(ExitCallback.class);
		final SCMCommandHandler mockUploadPackHandler = context.mock(SCMCommandHandler.class, "mockUploadPackHandler");
		final SCMCommandHandler mockReceivePackHandler = context.mock(SCMCommandHandler.class, "mockReceivePackHandler");
		final Properties mockProperties = context.mock(Properties.class);
        final ServerSession mockSession = context.mock(ServerSession.class);
		final CommandContext commandContext = new CommandContext(
                filteredCommand, mockInputStream, mockOutputStream,
                mockErrorStream, mockExitCallback, mockProperties,
                mockSession, AuthorizationLevel.AUTH_LEVEL_READ_WRITE);

		checking(new Expectations(){{
			one(mockReceivePackHandler).execute(commandContext);
		}});
		
		GitSCMCommandHandler handler = new GitSCMCommandHandler(mockUploadPackHandler, mockReceivePackHandler);
		handler.execute(commandContext);
	}
	
}
