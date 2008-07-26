package peli;
/**
 * v1.1 Extension to provide overall seriestable comparison after mutual
 * comparison ends in a tie.
 *  * @author aulaskar
 *
 */
public class MutualSeriesTableEntry extends SeriesTableEntry {

	public MutualSeriesTableEntry(String s) {
		super(s);		
	}

	public MutualSeriesTableEntry(String s, SeriesTableEntry overallresults1) {
		super(s);		
		this.overallresults = overallresults1;
	}
	
	public SeriesTableEntry getOverallEntry() {
		return this.overallresults;
	}
	
	private SeriesTableEntry overallresults = null;
}
