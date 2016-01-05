package org.javauj.webcrawler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller{
	public Controller controller;
	public WebAnalyzer webAnalyzer;
	public SQLiteLogger logger;
	public Stack<URL> history;
	URL currentURL;
	
	public void run()
	{
		controller = new Controller();
		webAnalyzer = new WebAnalyzer();
		logger = new SQLiteLogger("history.db", true);
		webAnalyzer.addObserver(logger);
		history = new Stack<>();
		
		
		currentURL = null;
		while(currentURL == null)
		{
	        String str = JOptionPane.showInputDialog(null, "Podaj adres url", null);
	        if(str == null){
	            return;
	        }
	        try
	        {
	        	currentURL = new URL(str);
	        } catch (MalformedURLException e)
	        {
	        	JOptionPane.showMessageDialog(null, "Błąd:" + e.toString());
	        }
		}
		
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					View.createAndShowGUI();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// RETURN BUTTON CLICKED
		View.returnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (history.isEmpty())
					return;
				URL latestUrl = history.pop();
				System.out.println(latestUrl);
				analyzeAndBind(latestUrl);
			}
		});
		// LINK CLICKED
		View.list.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				View.returnButton.setVisible(false);
				history.push(currentURL);
				analyzeAndBind(View.list.getSelectedValue());
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		
		analyzeAndBind(currentURL);
	}
	



	private void analyzeAndBind(URL url) // dispatch new thread return
	{
		
		AnalyzeResult result = webAnalyzer.analyzePage(url);

		javax.swing.SwingUtilities.invokeLater(new Runnable() { // 
			@Override
			public void run() {
				View.returnButton.setEnabled(false);
				View.list.setVisible(false);
				System.out.println(Thread.currentThread().getName());
				((DefaultListModel<URL>)View.list.getModel()).clear();
				for(URL u : result.links)
				{
					((DefaultListModel<URL>)View.list.getModel()).addElement(u);
				}
				View.sumLabel.setText("Img: " + result.numberOfImages.toString());
				View.totalSizeLabel.setText("Size: " + result.totalSizeOfImages + " B");
				View.returnButton.setEnabled(true);
				View.returnButton.setVisible(true);
				View.list.setVisible(true);
			}
		});
	}
	
	public static void main(String[] args)
	{
		new Controller().run();
	}
}
