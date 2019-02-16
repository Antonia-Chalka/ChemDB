import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.marvin.calculations.logPPlugin;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;

public class MoleculeData {
	private Molecule mol;
	private String id;
	private String formula;
	private double logP;
	private double mw;
	private int hbDonorCount;
	private int hbAcceptorCount;
	private double rankScore;
	private String smilesStructure;
	
	//TODO maybe remove default values  and call setCuttoff method at the constructor?
	private double logPCutoff = 5 ;
	private double mwCutoff = 500.0;
	private double hbAccCutoff = 10.0; 
	private double hbDonorCutoff = 5.0;
	
	public MoleculeData(Molecule mol){
		this.mol = mol;
		formula = mol.getFormula();
		logP = calculatelogp(mol);
		mw = calculateMW(mol);
		hbDonorCount = calculateHBDonor(mol);
		hbAcceptorCount = calculateHBAcceptor(mol);
		rankScore = calculateRankScore();
		
		//System.out.println(toString());
	}
	
	//Use chemaxon methods to calculate exact logP
	private double calculatelogp(Molecule mol){
		logPPlugin plugin = new logPPlugin();	
		try {
			plugin.setMolecule(mol);
			plugin.run();
		} catch (PluginException e) {
			e.printStackTrace();
		}
		return plugin.getlogPTrue();	
	}
	
	private double calculateMW(Molecule mol){
		return mol.getExactMass();
	}	
	
	//Use chemaxon methods to calculate exact HB donor counts *without* multiplicity
	private int calculateHBDonor(Molecule mol){
		HBDAPlugin plugin = new HBDAPlugin();
		 try {
			plugin.setMolecule(mol);
			plugin.run();
		} catch (PluginException e) {
			e.printStackTrace();
		}
		int molecularDonorCount = plugin.getDonorAtomCount();  //Witout multiplicity
		//int molecularDonorCount = plugin.getDonorSiteCount(); //With multiplicity
		return molecularDonorCount;		
	}
	
	//Use chemaxon methods to calculate exact logP
	private int calculateHBAcceptor(Molecule mol){
		HBDAPlugin plugin = new HBDAPlugin();
		 try {
				plugin.setMolecule(mol);
				plugin.run();
			} catch (PluginException e) {
				e.printStackTrace();
			}
		int molecularAcceptorCount = plugin.getAcceptorAtomCount(); //Without multiplicity
		//int molecularAcceptorCount = plugin.getAcceptorSiteCount(); //With multiplicity
		return molecularAcceptorCount;
	}
	
	private double calculateRankScore() {
		double rankScore = 0;
		
		if (logP <= logPCutoff){
		rankScore++;
		}
		if (mw <= mwCutoff){
		rankScore++;
		}
		if (hbDonorCount <= hbDonorCutoff){
		rankScore++;
		}
		if (hbAcceptorCount <= hbAccCutoff){
		rankScore++;
		}	
		return rankScore/4;
	}

	//This method is called when applying filter to table data in GUI
	public void setCutoffs(double logPCutoff, double mwCutoff, double hbAccCutoff, double hbDonorCutoff) {
		//Update cut offs
		this.logPCutoff = logPCutoff;
		this.mwCutoff = mwCutoff;
		this.hbAccCutoff = hbAccCutoff;
		this.hbDonorCutoff = hbDonorCutoff;
		
		//Recalculate rank score
		rankScore = calculateRankScore();
	}
	
	public String toString(){ //Useful for debugging
		return "Molecule: " + id + 
				", Formula: " + formula + 
				", LogP: " + logP + 
				", MW: "+ mw + 
				", HB Donors: " + hbDonorCount + 
				", HB Acceptors: " + hbAcceptorCount + 
				", Rank Score: " + rankScore + 
				", Smiles STructure: " + smilesStructure;
	}
	
	/* -----Getter Section----- */
	
	public Molecule getMol() {
		return mol;
	}
	public String getID() {
		return id;
	}
	public String getFormula() {
		return formula;
	}
	public double getLogP() {
		return logP;
	}
	public double getMw() {
		return mw;
	}
	public int getHbDonorCount() {
		return hbDonorCount;
	}
	public int getHbAcceptorCount() {
		return hbAcceptorCount;
	}
	public double getRankScore() {
		return rankScore;
	}
	public String getSmilesStructure() {
		return smilesStructure;
	}
	
	/* -----Setter Section----- */
	
	public void setID(String ID) {
		this.id = ID;
	}
	public void setSmilesStructure(String smileformat){
		this.smilesStructure = smileformat;
	}	
}
