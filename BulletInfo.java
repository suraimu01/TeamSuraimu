package TeamSuraimu;
import robocode.*;

class BulletInfo {

	private double hitTime ;
	private long nowTime ;
	private Bullet bullet ;
	
	public void setData(double ht ,long nt , Bullet b){
		
		hitTime = ht ;
		nowTime = nt ;
		bullet = b ;
	}

	public double getHitTime(){
		
		return hitTime ;
	}
	
	public double getExpectTime(){
		
		return (hitTime + nowTime) ;
	}
	
	public Bullet getBullet(){
		
		return bullet;
	}
	
}
