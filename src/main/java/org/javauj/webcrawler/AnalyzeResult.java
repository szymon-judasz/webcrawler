package org.javauj.webcrawler;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AnalyzeResult{
	public Set<URL> links;
	public Integer totalSizeOfImages;
	public Integer numberOfImages;
	public AnalyzeResult(){
		links = new HashSet<>();
		totalSizeOfImages = new Integer(0);
		numberOfImages = new Integer(0);
	}
}