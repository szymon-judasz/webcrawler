package org.javauj.webcrawler;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class LinksAndImages{
	public Set<URL> links;
	public Set<URL> images;
	public LinksAndImages()
	{
		links = new HashSet<>();
		images = new HashSet<>();
	}
}