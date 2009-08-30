package qbts.distances;


public class IS2 extends AbstractIS {

	public double IS(double c1, double r1, double c2, double r2){
		double d, R;
		
		R=Math.min(r1,r2)/Math.max(r1,r2);
		if (Double.isNaN(R)){
			return 0;
		}
		
		d= Math.abs(c1-c2)/(r1+r2);
		
		double v1= Math.cos(Math.PI/2*(1-R));
		//  Math.cos(Math.PI/2*(1-R))
		// COS(PI/2-PI/2*R)
		if (d==0){
			return v1;
		}
		else if (d < 1) {
			return v1/2*(Math.sin(Math.PI/2*(1+d))+1);
		}else{
			return v1/(2*d);
		}
	}
	
}
