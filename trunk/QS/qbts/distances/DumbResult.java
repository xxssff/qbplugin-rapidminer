package qbts.distances;

import com.rapidminer.operator.ResultObjectAdapter;

public class DumbResult extends ResultObjectAdapter {

	private static final long serialVersionUID = 1L;

	private double p1,p2, p3;
	
	
    public DumbResult(double p1, double p2, double p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
    
    

	public double getP1() {
		return p1;
	}



	public void setP1(double p1) {
		this.p1 = p1;
	}



	public double getP2() {
		return p2;
	}



	public void setP2(double p2) {
		this.p2 = p2;
	}



	public double getP3() {
		return p3;
	}



	public void setP3(double p3) {
		this.p3 = p3;
	}



	public String getExtension() { return "srs"; }
    
    public String getFileDescription() { return "dumb result"; }

}
