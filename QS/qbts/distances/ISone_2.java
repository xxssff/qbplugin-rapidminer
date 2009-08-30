package qbts.distances;


public class ISone_2 extends AbstractIS {

	public double IS(double c1, double r1, double c2, double r2){
		double d, R;
		
		R=Math.min(r1,r2)/Math.max(r1,r2);
		if (Double.isNaN(R)){
			return 0;
		}
		
		d= Math.abs(c1-c2)/(r1+r2);
		
		double v1= 0.5 * ( Math.cos(Math.PI * (1-R))+1);
					// =0,5*(COS(PI()*(1-L2))+1)
		
		if (d==0){
			return v1;
		}
		else if (d < 1) {
			return v1*.75*(Math.sin(Math.PI/2*(1+d))+1);
		}else{
			return v1*.75/d;
		}
	}
	
}
