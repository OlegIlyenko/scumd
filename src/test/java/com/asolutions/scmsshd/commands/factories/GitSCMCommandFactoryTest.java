package com.asolutions.scmsshd.commands.factories;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.git.SCMCommand;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class GitSCMCommandFactoryTest extends MockTestCase{
	
	@Test
	public void testCreatesAGitCommand() throws Exception {
		FilteredCommand filteredCommand = new FilteredCommand();
		ProjectAuthorizer mockProjectAuthorizer = context.mock(ProjectAuthorizer.class);
		final PathToProjectNameConverter mockPathConverter = context.mock(PathToProjectNameConverter.class);
		
		final Properties mockConfig = context.mock(Properties.class);
		
		GitSCMCommandFactory factory = new GitSCMCommandFactory();
		SCMCommand command = (SCMCommand) factory.create(filteredCommand, mockProjectAuthorizer, mockPathConverter, mockConfig);
		assertEquals(filteredCommand, command.getFilteredCommand());
		assertEquals(mockProjectAuthorizer, command.getProjectAuthorizer());
	}

}
