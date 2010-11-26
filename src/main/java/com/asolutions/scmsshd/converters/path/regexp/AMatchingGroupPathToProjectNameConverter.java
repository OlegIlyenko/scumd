package com.asolutions.scmsshd.converters.path.regexp;

import com.asolutions.scmsshd.converters.path.PathToProjectNameConverter;
import com.asolutions.scmsshd.sshd.UnparsableProjectException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AMatchingGroupPathToProjectNameConverter implements PathToProjectNameConverter {

	public AMatchingGroupPathToProjectNameConverter() {
		super();
	}

	public String convert(String toParse) throws UnparsableProjectException {
		Matcher match = getPattern().matcher(toParse);
		if (match.find()){
			return match.group(1);
		}
		else{
			throw new UnparsableProjectException("Could Not Parse: [" + toParse + "] With [" + getPattern().toString() + "]");
		}
	}
	
	public abstract Pattern getPattern();

}