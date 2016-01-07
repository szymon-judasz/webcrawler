package org.javauj.webcrawler;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class View{
	// some config values
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	public static JButton returnButton;
	public static JList<URL> list;
	public static JLabel totalSizeLabel;
	public static JLabel sumLabel;
	public static JFrame frame;
	
	public static void addComponentsToPane(Container pane)
	{
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		returnButton = new JButton("Return");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.ipady = 30;
		pane.add(returnButton, c);
		
		totalSizeLabel = new JLabel("Size:");
		sumLabel = new JLabel("Images:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		pane.add(sumLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		pane.add(totalSizeLabel, c);
		
		DefaultListModel<URL> listModel = new DefaultListModel<>();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.gridwidth = 2;
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setMinimumSize(new Dimension(100, 20));
		JScrollPane listScrollPane = new JScrollPane(list);
		listScrollPane.setMinimumSize(new Dimension(100, 20));
		pane.add(listScrollPane, c);
		
	}
	
	public static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("Webcrawler");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		addComponentsToPane(frame.getContentPane());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
