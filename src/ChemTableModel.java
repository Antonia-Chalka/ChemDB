import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ChemTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	//Define index of each column
	private static final int COLUMN_ID = 0;
    private static final int COLUMN_FORMULA = 1;
    private static final int COLUMN_RANKSCORE = 2; 
    private static final int COLUMN_MW = 3;
    private static final int COLUMN_HBDONOR = 4;
    private static final int COLUMN_HBACCEPTOR = 5;
    private static final int COLUMN_LOGP = 6;
    private static final int COLUMN_STRUCTURE = 7;
    
    private String[] columnNames = {"ID", "Formula", "Rank Score", "MW (Da)", "HB Donor #", "HB Acceptor #", "logP" , "Smiles Structure"};
    private ArrayList<MoleculeData> molecules;


	public ChemTableModel(ArrayList<MoleculeData> molecules){
		this.molecules = molecules;
	}

	/* -----Methods needed because of implemented interface----- */
	@Override
	public int getColumnCount() { //Get number of columns
		return columnNames.length;
	}

	@Override
	public int getRowCount() { //Get number of rows
		return molecules.size();
	}

	@Override
    public String getColumnName(int columnIndex) { //Get name of columns
        return columnNames[columnIndex];
    }
     
    @Override
    public Class<?> getColumnClass(int columnIndex) {  //Get class type of a column
        if (molecules.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) { //Get value for a defined cell
        MoleculeData molecule  = molecules.get(rowIndex); //determine which molecule is to be examined by getting row index
		Object returnValue;
         
        switch (columnIndex) {
        case COLUMN_ID:
            returnValue = molecule.getID();
            break;
        case COLUMN_FORMULA:
            returnValue = molecule.getFormula();
            break;
        case COLUMN_LOGP:
            returnValue = molecule.getLogP();
            break;
        case COLUMN_MW:
            returnValue = molecule.getMw();
            break;
        case COLUMN_HBDONOR:
            returnValue = molecule.getHbDonorCount();
            break;
        case COLUMN_HBACCEPTOR:
            returnValue = molecule.getHbAcceptorCount();
            break;
        case COLUMN_RANKSCORE:
        	returnValue = molecule.getRankScore();
        	break;
        case COLUMN_STRUCTURE:
            returnValue = molecule.getSmilesStructure();
            break;
        default:
            throw new IllegalArgumentException("Invalid column index.");
        }
        return returnValue;
	}
}
