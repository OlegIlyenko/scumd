package com.asolutions.scmsshd.commands.git;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GitSCMCommandTest extends MockTestCase{
	
	@Test
	public void testGitCTORCreatesObjectCorrectly() throws Exception {
		FilteredCommand command = new FilteredCommand("git-upload-pack", "/proj-2/git.git");
		ProjectAuthorizer mockProjectAuthorizer = context.mock(ProjectAuthorizer.class);
		PathToProjectNameConverter mockProjectNameConverter = context.mock(PathToProjectNameConverter.class);
		final Properties mockConfig = context.mock(Properties.class);
		
		GitSCMCommand cmd = new GitSCMCommand(command, mockProjectAuthorizer, mockProjectNameConverter, mockConfig);
		assertEquals(GitSCMCommandHandler.class, cmd.getSCMCommandHandler().getClass());
		assertEquals(command, cmd.getFilteredCommand());
		assertEquals(mockProjectAuthorizer, cmd.getProjectAuthorizer());
		assertEquals(mockProjectNameConverter, cmd.getPathToProjectNameConverter());
	}
	
}
