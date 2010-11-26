package com.asolutions.scmsshd.commands.factories;

import com.asolutions.MockTestCase;
import com.asolutions.scmsshd.commands.FilteredCommand;
import com.asolutions.scmsshd.commands.NoOpCommand;
import com.asolutions.scmsshd.commands.filters.BadCommandException;
import com.asolutions.scmsshd.commands.filters.BadCommandFilter;
import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.ProjectAuthorizer;
import org.jmock.Expectations;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CommandFactoryBaseTest  extends MockTestCase {
	
	private static final String ARGUMENT = "argument";
	private static final String COMMAND = "command";
	
	@Test
	public void testBadCommandReturnsNoOp() throws Exception {
		final BadCommandFilter mockBadCommandFilter = context.mock(BadCommandFilter.class);
		checking(new Expectations(){{
			one(mockBadCommandFilter).filterOrThrow(COMMAND);
			will(throwException(new BadCommandException()));
		}});
		
		CommandFactoryBase factory = new CommandFactoryBase();
		factory.setBadCommandFilter(mockBadCommandFilter);
		assertEquals(NoOpCommand.class, factory.createCommand(COMMAND).getClass());
	}
	
	@Test
	public void testChecksBadCommandFirst() throws Exception {
		final BadCommandFilter mockBadCommandFilter = context.mock(BadCommandFilter.class);
		final FilteredCommand filteredCommand = new FilteredCommand(COMMAND, ARGUMENT);
		final SCMCommandFactory mockScmCommandFactory = context.mock(SCMCommandFactory.class);
		final ProjectAuthorizer mockProjAuth = context.mock(ProjectAuthorizer.class);
		final PathToProjectNameConverter mockPathConverter = context.mock(PathToProjectNameConverter.class);
		final Properties mockConfig = context.mock(Properties.class);
		
		checking(new Expectations(){{
			one(mockBadCommandFilter).filterOrThrow(COMMAND);
			will(returnValue(filteredCommand));
			one(mockScmCommandFactory).create(filteredCommand, mockProjAuth, mockPathConverter, mockConfig);
		}});
		
		CommandFactoryBase factory = new CommandFactoryBase();
		factory.setBadCommandFilter(mockBadCommandFilter);
		factory.setScmCommandFactory(mockScmCommandFactory);
		factory.setProjectAuthorizor(mockProjAuth);
		factory.setPathToProjectNameConverter(mockPathConverter);
		factory.setConfiguration(mockConfig);
		factory.createCommand(COMMAND);
	}

}
