package org.javauj.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Observable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebAnalyzer extends Observable {
	
	
	synchronized public AnalyzeResult analyzePage(URL url) throws IOException
	{
		String pageHtml = downloadPage(url);
		LinksAndImages linksAndImages = scanForLinksAndImages(pageHtml, url.toString());
		Integer totalSizeOfImages = getSizeOfManyLinks(linksAndImages.images);
		Integer numberOfImages = linksAndImages.images.size();

		AnalyzeResult result = new AnalyzeResult();
		result.links = linksAndImages.links;
		result.totalSizeOfImages = totalSizeOfImages;
		result.numberOfImages = numberOfImages;
		setChanged();
		notifyObservers(url);
		return result;
	}
	
	
	String downloadPage(URL url) throws IOException {
		URLConnection uc;
		BufferedReader in = null;
		try{
			uc = url.openConnection();
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			
			StringBuilder pageBuilder = new StringBuilder();
			String line;
			while((line = in.readLine()) != null) {
				pageBuilder.append(line);
			}
			in.close();
			return pageBuilder.toString();
		} finally
		{
			if (in != null)
			{
				in.close();
			}
		}
	}
	
	public LinksAndImages scanForLinksAndImages(String html, String baseURI) {
		LinksAndImages result = new LinksAndImages();
		Document doc = Jsoup.parse(html, baseURI);
		Elements links = doc.select("a[href]");
		Elements images = doc.select("img");
		for (Element e : links) {
			try {
				result.links.add(new URL(e.attr("abs:href")));
			} catch (MalformedURLException e1) {
				System.err.println("Error during parsing link: " + e.outerHtml());
				//e1.printStackTrace();
			}
		}
		for (Element e : images)
		{
			try {
				result.images.add(new URL(e.attr("abs:src")));
			} catch (MalformedURLException e1) {
				System.err.println("Error during parsing image link: " + e.outerHtml());
				//e1.printStackTrace();
			}
		}
		return result;
	}
	
	public Integer getPageSize(URL url) {
		try {
			URLConnection uc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			int pagesize = 0;
			while(in.read() != -1) {
				pagesize++;
			}
			in.close();
			return new Integer(pagesize);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Integer(0);
	}
	
	public Integer getSizeOfManyLinks(Collection<URL> col) {
		Integer result = new Integer(0);
		for (URL url : col)
		{
			result = result + getPageSize(url);
		}
		return result;
	}
}
