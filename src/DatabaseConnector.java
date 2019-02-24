import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.io.IOException;

import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;

public class DatabaseConnector {	
	private final String USERNAME = "test";
	private final String PASSWORD = null;
	private final String HOST = "jdbc:mysql://localhost:3306/StructureDB";
    private final String QUERY = "Select cd_smiles,cd_id from structuredb.test";
    
    private Connection conn;	
    private Statement stmt;
    private ArrayList<MoleculeData> molecules = new ArrayList<MoleculeData>(); //data obtained from DB and passed to other methods
	
    
	public DatabaseConnector() {
		initialiseConnection();
		runQuery();
    	closeConnection();
	}

	private void initialiseConnection() {
		try {
			try { 
				Class.forName("com.mysql.jdbc.Driver"); //makes compiled jar file work
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD); 
			conn.setSchema("stucturedb");
		}
		catch(SQLException e) {
			System.out.println("SQL Error while connecting: " + e);
		}
	}	
		
	private void runQuery(){
	    try {
	        stmt = conn.createStatement();
	        ResultSet rs = stmt.executeQuery(QUERY);
	        while (rs.next()) {
	        	String smileformat = rs.getString("cd_smiles"); //get smiles format
	        	String id = rs.getString("cd_id"); //get compound id
	        	
	        	//convert smiles format to chemaxon molecule class
	            Molecule mol = null;
	            try {
					mol = MolImporter.importMol(smileformat);
				} catch (IOException e) {
										e.printStackTrace();
				}
	            
	            //Calculate molecule properties & add smile structure and id
	            MoleculeData molecule = new MoleculeData(mol);
	            molecule.setSmilesStructure(smileformat);
	            molecule.setID(id);
	            
	            molecules.add(molecule); //add to arraylist that will be exported           
	        }        
	    } catch (SQLException e ) {
	            e.printStackTrace();	       
	    } finally {	//no matter what happens, close any existing statement
	    	if (stmt != null) { 
	    		try {
	    			stmt.close();
	    		} catch (SQLException e) {
	    			e.printStackTrace();
	    		} 
	    	}
	    }
	}
	
	private void closeConnection() {
		if(conn != null) {
			try { 
				conn.close();
			} catch(Exception e){
				System.out.println("Can't close.");
			}
		}		
	}
	
	/* -----Getters section below----- */
	
	public Connection getConnection() {
		return conn;	
	}
	
	public ArrayList<MoleculeData> getMolecules() {
		return molecules;
	}	
}