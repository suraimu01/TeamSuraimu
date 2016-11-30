package TeamSuraimu;

/**
 * MyClass - a class by (your name here)
 */
public class Angle implements java.io.Serializable{

	private static final long serialVersionUID = 1L;


	private double angle = 0;
	
	public Angle(double a){
		
		angle = a;
	}	

	public double getAngle(){
		
		return angle ;
	}
}
