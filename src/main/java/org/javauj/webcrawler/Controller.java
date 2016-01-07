package org.javauj.webcrawler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class Controller{
	public Controller controller;
	public WebAnalyzer webAnalyzer;
	public SQLiteLogger logger;
	public Stack<URL> history;
	URL currentURL;
	URL analyzingURL;
	
	public void run()
	{
		controller = new Controller();
		webAnalyzer = new WebAnalyzer();
		logger = new SQLiteLogger("history.db", false);
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
				URL latestUrl = history.peek();
				analyzeAndBind(latestUrl, false); // swing dispatcher
			}
		});
		// LINK CLICKED
		View.list.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				analyzeAndBind(View.list.getSelectedValue(), true); // swing dispatcher
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
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				analyzeAndBind(currentURL, true); // from EDT
			}
		});
		final SQLiteLogger working_logger = logger;
		View.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt)
			{
				working_logger.close();
			}
		});

	}
	

	public void setEnableComponents(boolean enable)
	{
		View.returnButton.setEnabled(enable);
		View.list.setEnabled(enable);
	}

	private void analyzeAndBind(final URL url, final boolean newPage) // should always be called from edt
	{
		if(!javax.swing.SwingUtilities.isEventDispatchThread())
			throw new RuntimeException("Code execution not from EDT");
		setEnableComponents(false);
		new SwingWorker<AnalyzeResult, Object>() {
			AnalyzeResult result;
			@Override
			protected AnalyzeResult doInBackground(){
				try {
					result = webAnalyzer.analyzePage(url);
				} catch (IOException e) {
					return null;
				}
				return result;
			}
			
			@Override 
			protected void done()
			{
				if(!javax.swing.SwingUtilities.isEventDispatchThread())
				{
					throw new RuntimeException("not EDT");
				}
				if(result != null)
				{
					((DefaultListModel<URL>)View.list.getModel()).clear();
					for(URL u : result.links)
					{
						((DefaultListModel<URL>)View.list.getModel()).addElement(u);
					}
					View.sumLabel.setText("Img: " + result.numberOfImages.toString());
					View.totalSizeLabel.setText("Size: " + result.totalSizeOfImages + " B");
					
					if (newPage)
					{
						history.push(currentURL);
					} else
					{
						history.pop();
					}
					currentURL = url;
				}
				setEnableComponents(true);
			}
		}.execute();

	}
	
	public static void main(String[] args)
	{
		new Controller().run();
	}
}
