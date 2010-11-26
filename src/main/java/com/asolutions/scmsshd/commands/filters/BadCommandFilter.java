package com.asolutions.scmsshd.commands.filters;

import com.asolutions.scmsshd.commands.FilteredCommand;

public interface BadCommandFilter {

	FilteredCommand filterOrThrow(String command) throws BadCommandException;

}
