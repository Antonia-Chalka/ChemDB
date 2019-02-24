import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JButton;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class GUI {
	DatabaseConnector conn = new DatabaseConnector(); //Connect to database
	private JFrame frame;
	private JPanel upperPanel, centrePanel, bottomPanel, displayPanel, filterPanel, resultsPanel ;
	private JTable resultsTable;
	private JTextField logpTField, mwTField, hdonorTField, hacceptorTField, hitsTField;
	private JCheckBox logpCBox, mwCBox, hdonorCBox, hacceptorCBox, smileStrCBox;
	private TableRowSorter<TableModel> sorter;
	private ArrayList<MoleculeData> molecules = conn.getMolecules();
	private JLabel filterLabel;
	private ChemTableModel tableModel;

	public static void main (String[] args){ 
		EventQueue.invokeLater(new Runnable() {//prepare everything in GUI and then run it
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}

 	public GUI() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(147, 112, 219));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setPreferredSize(new Dimension(1000,600));
		
		upperPanel = new JPanel();
		frame.getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BorderLayout(0, 0));
		setUpperPanel();
		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		setLowerPanel();
		
		centrePanel = new JPanel();
		frame.getContentPane().add(centrePanel, BorderLayout.CENTER);
		centrePanel.setLayout(new BorderLayout(0, 0));
		setCentrePanel();
	
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
 	private void setUpperPanel() {	
		JLabel queryHeader = new JLabel("Connected as " + conn.getConnection() + ". Hover mouse over component for tips." );
		queryHeader.setFont(new Font("Tahoma", Font.BOLD, 15));
		queryHeader.setHorizontalAlignment(SwingConstants.CENTER);
		queryHeader.setForeground(Color.BLACK);
		upperPanel.add(queryHeader, BorderLayout.NORTH);		
	}
 	
 	//Lower panel contains filtering panel + results display/export panel
	private void setLowerPanel() {
		
		filterLabel = new JLabel("Filter retrieved compounds by using Lipinski's rules below. Rule of 5 is default.");
		filterLabel.setHorizontalAlignment(SwingConstants.CENTER);
		filterLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		bottomPanel.add(filterLabel, BorderLayout.NORTH);
		
		filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(1, 0, 0, 0));
		setFilterPanel();
		bottomPanel.add(filterPanel, BorderLayout.CENTER);
		
		
		resultsPanel = new JPanel();
		bottomPanel.add(resultsPanel, BorderLayout.SOUTH);
		setResultsPanel();	
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.X_AXIS));
			
	}

	private void setFilterPanel() {	
		JLabel logPLabel = new JLabel("logP <=");
		logPLabel.setToolTipText("Default is less than 5");
		filterPanel.add(logPLabel);
		
		logpTField = new JTextField();
		logpTField.setText("5");
		filterPanel.add(logpTField);	
		
		JLabel mwLabel = new JLabel("MW (Da) <=");
		mwLabel.setToolTipText("Default is less than 500 Daltons.");
		filterPanel.add(mwLabel);
		
		mwTField = new JTextField();
		mwTField.setText("500");
		filterPanel.add(mwTField);		
		
		JLabel donorcLabel = new JLabel("HB Donor # <=");
		donorcLabel.setToolTipText("Default is less than 5 (without multiplicity).");
		filterPanel.add(donorcLabel);
		
		hdonorTField = new JTextField();
		hdonorTField.setText("5");
		filterPanel.add(hdonorTField);		
		
		JLabel hacceptorLabel = new JLabel("HB Acceptor # <=");
		hacceptorLabel.setToolTipText("Default is less than 10 (without multiplicity)");
		filterPanel.add(hacceptorLabel);
		
		hacceptorTField = new JTextField();
		hacceptorTField.setText("10");
		filterPanel.add(hacceptorTField);
		
		JButton filterButton = new JButton("Filter");
		filterButton.setToolTipText("Filter rows according to specified criteria.");
		filterPanel.add(filterButton);
		filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	filterResults();
            } 
         } );	
	}

	private void setResultsPanel() {
		JLabel resultsLabel = new JLabel("Results:");
		resultsLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		resultsPanel.add(resultsLabel);
		
		hitsTField = new JTextField();
		hitsTField.setEditable(false);
		hitsTField.setText("Showing no compounds.");
		hitsTField.setToolTipText("Shows number of compounds (i.e. rows) displayed on table.");
		resultsPanel.add(hitsTField);
		
		JButton showDataButton = new JButton("Show All Data");
		showDataButton.setToolTipText("Remove filter and show all retrieved compounds/rows.");
		resultsPanel.add(showDataButton);
		showDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//Remove table filter, repaint table, & update hits field
            	 sorter.setRowFilter(null);
            	 resultsTable.repaint();
            	 hitsTField.setText("Showing " + resultsTable.getRowCount()  + " compounds.");
            } 
        } 
		);
		
		JButton exportButton = new JButton("Export table to csv");
		exportButton.setToolTipText("Export displayed rows to CSV file in user's directory. \n"
				+ "Please note that while only displayed rows will be exported, all column data will be included in export.");
		resultsPanel.add(exportButton);
		exportButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	exportToCSV(resultsTable, System.getProperty("user.dir") + "/Exported_Data.csv");
            } 
         } 
		));
	}

	private void setCentrePanel() {
		displayPanel = new JPanel();
		setupDisplayPanel(); //set up panel with the checkboxes
		centrePanel.add(displayPanel, BorderLayout.NORTH);
		
		setupTablePanel(); //set up panel that contains the compound table		
		centrePanel.add(resultsTable, BorderLayout.CENTER);
		centrePanel.add(new JScrollPane(resultsTable), BorderLayout.CENTER); //add a scroll pane to table to make navigation easier	
	}
	
	private void setupDisplayPanel() {
		JLabel displayLabel = new JLabel("Display:");
		displayPanel.add(displayLabel);
		displayLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		displayLabel.setToolTipText("Use the checkboxes to indicate which columns "
				+ "are to be displayed. To update the table, press the 'Update Display' button.");
		
		logpCBox = new JCheckBox("logP");
		logpCBox.setSelected(true); //set selected by default
		logpCBox.setToolTipText("Check to display logP column, uncheck to hide. Is checked by default.");
		displayPanel.add(logpCBox);
		
		mwCBox = new JCheckBox("MW");
		mwCBox.setSelected(true);
		mwCBox.setToolTipText("Check to display molecular weight column, uncheck to hide. Is checked by default.");
		displayPanel.add(mwCBox);
		
		hdonorCBox = new JCheckBox("HB Donor #");
		hdonorCBox.setSelected(true);
		hdonorCBox.setToolTipText("Check to display hydogen bond donor number (without multiplicity) column, uncheck to hide. Is checked by default.");
		displayPanel.add(hdonorCBox);
		
		hacceptorCBox = new JCheckBox("H Acceptor #");
		hacceptorCBox.setSelected(true);
		hacceptorCBox.setToolTipText("Check to display hydogen bond acceptor number (without multiplicity) column, uncheck to hide. Is checked by default.");
		displayPanel.add(hacceptorCBox);
		
		smileStrCBox = new JCheckBox("Smile Structure");
		smileStrCBox.setToolTipText("Check to display the compound in smiles format, uncheck to hide. Is unchecked by default.");
		displayPanel.add(smileStrCBox);
		
		JButton displayButton = new JButton("Update Display");
		displayButton.setToolTipText("Update columns that are to be displayed based on what boxes are checked.");
		displayPanel.add(displayButton);
		displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	displayResults();
            } 
         } );		
	}

	private void setupTablePanel() {
		tableModel = new ChemTableModel(molecules);
		resultsTable = new JTable(tableModel);
		resultsTable.setRowSelectionAllowed(true); //allow user to highlight rows
		resultsTable.getTableHeader().setReorderingAllowed(false); //Stop user from reordering columns
		
		resultsTable.setAutoCreateRowSorter(true); //create sorter for all table columns
		
		//Set up sorter so it automatically sorts the table based on RankScore
		sorter = new TableRowSorter<TableModel>(resultsTable.getModel());
		resultsTable.setRowSorter(sorter);		
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2; //index of rank score column
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		//Tool tip for all columns
		resultsTable.getTableHeader().setToolTipText("Please note that for calculating the Rank Score, "
				+ "each of the filtering criteria (logP, MW, HB Donour/Acceptor counts) contribute 1/4 to the score.");
		
		displayResults(); //set up table's column widths
	}


	//This method filters the table rows according to user input and is fired when 'Filter' button is pressed
	private void filterResults() {
		//Get filters (logP, mw, HB Donor/Acceptor Count)
		double hAcceptorfilter = Double.parseDouble(hacceptorTField.getText()); 
		double hDonorfilter = Double.parseDouble(hdonorTField.getText()); 
		double mwfilter = Double.parseDouble(mwTField.getText());
		double logPfilter = Double.parseDouble(logpTField.getText());	
		
		//recalculate rankScore
		for (MoleculeData molecule : molecules){
			molecule.setCutoffs(logPfilter, mwfilter, hAcceptorfilter, hDonorfilter);
		}
		
		resultsTable.repaint(); //update table data because of recalculated rank score
		
		//Apply table filter to RankScore column (rows which pass all filters have a score of 1)
		RowFilter<TableModel, Object> filter = RowFilter.regexFilter("1", 2);
		sorter.setRowFilter(filter);
		
		//Update the field which shows number of results
		hitsTField.setText("Showing " + resultsTable.getRowCount()  + " compounds.");
	}
	
	/*This method sets up column width and is fired when first setting up table + when 'Display' button is pressed
	 *Columns that are to be hidden are set to have a width of 0 (preserves order + allows to export all column data)
	 */
	private void displayResults() {
		//Set up column 0 (ID)
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(45);
		
		//Set up Column 1 (Formula)
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		
		//Set up Column 2 (Rank Score)
		resultsTable.getColumnModel().getColumn(2).setPreferredWidth(45);
		
		//Check if Column 3 (Molecular Weight) is to be displayed and set it up
		if (mwCBox.isSelected() == true) {
			resultsTable.getColumnModel().getColumn(3).setMaxWidth(100);
			resultsTable.getColumnModel().getColumn(3).setPreferredWidth(55);
		} else {
			resultsTable.getColumnModel().getColumn(3).setMinWidth(0);
			resultsTable.getColumnModel().getColumn(3).setMaxWidth(0);
		}
		
		//Check if Column 4 (HB Donor Count) is to be displayed and set it up
		if (hdonorCBox.isSelected() == true) {
			resultsTable.getColumnModel().getColumn(4).setMaxWidth(100);
			resultsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		} else {
			resultsTable.getColumnModel().getColumn(4).setMinWidth(0);
			resultsTable.getColumnModel().getColumn(4).setMaxWidth(0);
		}
		
		//Check if Column 5 (HB Acceptor Count) is to be displayed and set it up
		if (hacceptorCBox.isSelected() == true) {
			resultsTable.getColumnModel().getColumn(5).setMaxWidth(100);
			resultsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		} else {
			resultsTable.getColumnModel().getColumn(5).setMinWidth(0);
			resultsTable.getColumnModel().getColumn(5).setMaxWidth(0);
		}
		
		//Check if Column 6 (logP) is to be displayed and set it up
		if (logpCBox.isSelected() == true) {
			resultsTable.getColumnModel().getColumn(6).setMaxWidth(100);
			resultsTable.getColumnModel().getColumn(6).setPreferredWidth(45);
		} else {
			resultsTable.getColumnModel().getColumn(6).setMinWidth(0);
			resultsTable.getColumnModel().getColumn(6).setMaxWidth(0);
		}
		
		//Check if Column 7 (Smiles Structure) is to be displayed and set it up
		if (smileStrCBox.isSelected() == true) {
			resultsTable.getColumnModel().getColumn(7).setMinWidth(350);
			resultsTable.getColumnModel().getColumn(7).setMaxWidth(900);
			resultsTable.getColumnModel().getColumn(7).setPreferredWidth(500);
		} else {
			resultsTable.getColumnModel().getColumn(7).setMinWidth(0);
			resultsTable.getColumnModel().getColumn(7).setMaxWidth(0);
		}
		
		//Update the field which shows number of results
		hitsTField.setText("Showing " + resultsTable.getRowCount()  + " compounds.");
	}
	
	//Create a comma delineated csv file of the table data (include ALL columns but ONLY displayed rows), fired when export button is pressed
	private void exportToCSV(String pathToExportTo) {
	    try {
	        FileWriter csv = new FileWriter(new File(pathToExportTo));
	        for (int i = 0; i < resultsTable.getColumnCount(); i++) {//get Column names to use as headers
	            csv.write(resultsTable.getColumnName(i) + ",");
	        }
	        csv.write("\n");
	        for (int i = 0; i < resultsTable.getRowCount(); i++) { //loop for all displayed rows and get value at each column
	            for (int j = 0; j < resultsTable.getColumnCount(); j++) {
	                csv.write(resultsTable.getValueAt(i, j).toString() + ",");
	            }
	            csv.write("\n");
	        }
	        csv.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }    
	}
}
