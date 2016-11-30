package TeamSuraimu;

/**
 * MyClass - a class by (your name here)
 */
public class TankData implements java.io.Serializable
{

	private static final long serialVersionUID = 1L;

	public TankData(){
	}
	
	private String name ;
	private double velocity ;
	private double heading ;
	private double bearing ;
	private double distance ;
	private double energy ;
	private double scanTime ;
	
	private double prvVelocity ;
	private double prvHeading ;
	private double prvBearing ;
	private double prvDistance ;
	private double prvEnergy ;
	private double prvScanTime ;
	
	private double X;
	private double Y;

	public boolean trackable = true ;
	
	public void setName(String s){
		
		name = s ;
	}

	public void setData(double v,double h,double b,double d,double e,double t){
		
		velocity = v ;
		heading = h ;
		bearing = b ;
		distance = d ;
		energy = e ;
		scanTime = t ;
	}
	
	public void setPrvData(double v,double h,double b,double d,double e,double t){
	
		prvVelocity = v;
		prvHeading = h;
		prvBearing = b ;
		prvDistance = d;
		prvEnergy = e ;
		prvScanTime = t;
	}

	public void setXY(double x,double y,double h){
	
		X = Math.sin(Math.toRadians(normalRelativeAngle(h + bearing)))* distance + x;
		Y = Math.cos(Math.toRadians(normalRelativeAngle(h + bearing)))* distance + y;
	}

	public String getName(){
		return name ;
	}
	
	public double getVelocity(){
		return velocity;
	}
	
	public double getHeading(){
		return heading;
	}
	
	public double getBearing(){
		return bearing ;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public double getScanTime(){
		return scanTime;
	}
	
	public double getPrvVelocity(){
		return prvVelocity;
	}
	
	public double getPrvHeading(){
		return prvHeading;
	}
	
	public double getPrvBearing(){
		return prvBearing ;
	}
	
	public double getPrvDistance(){
		return prvDistance;
	}
	
	public double getPrvEnergy() {
		return prvEnergy;
	}
	
	public double getPrvScanTime(){
		return prvScanTime;
	}
	
	public double getX(){
		return X ;
	}
	
	public double getY(){
		return Y ;
	}
	
	public double normalRelativeAngle(double angle) {
		if (angle > -180 && angle <= 180)
			return angle;
		double fixedAngle = angle;
		while (fixedAngle <= -180)
			fixedAngle += 360;
		while (fixedAngle > 180)
			fixedAngle -= 360;
		return fixedAngle;
	}
}
