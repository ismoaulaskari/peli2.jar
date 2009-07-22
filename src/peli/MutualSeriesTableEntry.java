package peli;
/**
 * v1.1 Extension to provide overall seriestable comparison after mutual
 * comparison ends in a tie.
 *  * @author aulaskar
 *
 */
public class MutualSeriesTableEntry extends SeriesTableEntry {
	
        private SeriesTableEntry overallresults = null;
        
	public MutualSeriesTableEntry(int rank, String name) {
		super(rank, name);
	}

	public MutualSeriesTableEntry(int rank, String name, SeriesTableEntry overallresults1) {
		super(rank, name);
		this.overallresults = overallresults1;
	}
	
	public SeriesTableEntry getOverallEntry() {
		return this.overallresults;
	}
	

}
