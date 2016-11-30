package TeamSuraimu;
import robocode.*;

import java.util.List;
import java.util.ArrayList;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Suraimu - a robot by (your name here)
 */
public class Suraimubes extends TeamRobot implements Droid{
	/**
	 * run: Suraimu's default behavior
	 */
	private List<BulletInfo> bulletList = new ArrayList<BulletInfo>();
	private BulletInfo bulletInfo ;	
	private String targetName;

	public void run() {
	
		setAdjustGunForRobotTurn(true) ;
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

	}

	public void onMessageReceived(MessageEvent e) {
	
		if (e.getMessage() instanceof RobotColors) {
			
			RobotColors c = (RobotColors) e.getMessage();

			setBodyColor(c.bodyColor);
			setGunColor(c.gunColor);
			setRadarColor(c.radarColor);

		}
		
		else if(e.getMessage() instanceof String){
		
			targetName = (String) e.getMessage();
		}
		
		else if(e.getMessage() instanceof TankData ) {

			TankData d = (TankData) e.getMessage();
			
		out.println(targetName +","+ d.getName());
		
			if(targetName.equals(d.getName())){	

				

				double bulletVelocity = 11;
	
				double theta = Math.toDegrees(Math.atan2((d.getX() - getX()),(d.getY() - getY()) ));
					out.println(theta);
				double dis = Math.sqrt(Math.pow((d.getX() - getX()),2) + Math.pow((d.getY() - getY()),2));			
	
			//	setAhead(dis);
				if(d.getHeading() == d.getPrvHeading() && d.getVelocity() == d.getPrvVelocity() && d.trackable){

					double enemyXdis 
								= Math.sin(Math.toRadians(normalRelativeAngle(theta)))* dis;
					double	enemyYdis
								= Math.cos(Math.toRadians(normalRelativeAngle(theta)))* dis;			
					
					//out.println(enemyXdis +","+enemyYdis);
					
					double A = bulletVelocity * bulletVelocity - d.getVelocity() * d.getVelocity() ;
	
					double B = d.getVelocity()*((Math.sin(Math.toRadians(d.getHeading())) * enemyXdis) +
									(Math.cos(Math.toRadians(d.getHeading())) * enemyYdis));
										 
					double C = enemyXdis * enemyXdis + enemyYdis * enemyYdis ;
					
					double t1 = (B + Math.sqrt(B * B + A * C)) / A ;
					double t2 = (B - Math.sqrt(B * B + A * C)) / A ;
					double time = 0;
					
					if(t1 < 0){	
					
						if(t2 >= 0){
							
							time = t2;
						}
					}
					else{
					
						if(t2 < 0 || t1 < t2){
						
							time = t1 ;
						}
						else{
						
							time = t2 ;
						}
					}
		
					if(((time * d.getVelocity() * Math.sin(Math.toRadians(d.getHeading()))) + getX() + enemyXdis) < 0 ){
					//out.println((getX() + enemyXdis));
						time = (getX() + enemyXdis) / d.getVelocity() ;
						return;
					}
				
					if(((time * d.getVelocity() * Math.sin(Math.toRadians(d.getHeading()))) + getX() + enemyXdis) > getBattleFieldWidth() ){
					//out.println((getBattleFieldWidth() - getX() + enemyXdis));
						time = (getBattleFieldWidth() - (getX() + enemyXdis)) / d.getVelocity() ;
						return;
					}
					
					if(((time * d.getVelocity() * Math.cos(Math.toRadians(d.getHeading()))) + getY() + enemyYdis) < 0 ){
					//out.println("-Y");
						time = (getY() + enemyYdis)/ d.getVelocity() ;
						return;
					}
				
					if(((time * d.getVelocity() * Math.cos(Math.toRadians(d.getHeading()))) + getY() + enemyYdis) > getBattleFieldHeight() ){
					//out.println("Y");
						time = (getBattleFieldHeight() - (getY() + enemyYdis)) / d.getVelocity() ;
						return;
					}
	
				//	double predictionAngle = Math.toDegrees(Math.asin((enemyXdis + e.getVelocity() * time * Math.sin(e.getHeadingRadians())) / (bulletVelocity * time)));
			
					double ptan = Math.toDegrees(Math.atan((enemyXdis + d.getVelocity() * time * Math.sin(Math.toRadians(d.getHeading()))) / (enemyYdis + d.getVelocity() * time * Math.cos(Math.toRadians(d.getHeading())))));
	
					if(!Double.isNaN(ptan)){				
		
						if(((d.getVelocity() * time * Math.cos(Math.toRadians(d.getHeading()))) + enemyYdis + getY()) >= getY())	{
							turnGunRight(normalRelativeAngle(ptan - getGunHeading()));
							//setTurnRight(ptan - getHeading());
						}
						else{
							turnGunRight(normalRelativeAngle(180 - getGunHeading() + ptan));
							//setTurnRight(180 + ptan - getHeading());
						}
					}
					else	{
	
						setTurnGunRight((normalRelativeAngle((theta) - 
										getGunHeading()  ) ));
										
						//setTurnRight((normalRelativeAngle((theta) - 
										//getHeading()  ) ));
	
					}
					//out.println("time:"+ time);
	
				//	setTurnRight((normalRelativeAngle((theta) - getHeading()  ) ) + 10);
		
					if(getGunHeat() == 0) {
					
						Bullet bullet = fireBullet(3);
						trackBullet(time , bullet) ;
	
					}
				}
			
				else if(d.getHeading() != d.getPrvHeading() && d.trackable){
					
					double omega = (d.getHeading() - d.getPrvHeading()) / (d.getScanTime() - d.getPrvScanTime());
					double radius = Math.abs(d.getVelocity() / omega) ;
					
					double estTime = d.getDistance() / 11 ;
					
					double centerX = d.getX() + (radius * Math.cos(Math.toRadians(d.getBearing())));
					double centerY = d.getY() + (radius * Math.sin(Math.toRadians(d.getBearing())));
					
					double estX = radius * Math.cos(Math.toRadians(d.getBearing() + omega*estTime));
					double estY = radius * Math.sin(Math.toRadians(d.getBearing() + omega*estTime));
					double estTheta = Math.toDegrees(Math.atan2(centerX + estX - getX(),centerY + estY - getY()));
out.println(estTheta);

				if(!Double.isNaN(estTheta))turnGunRight(normalRelativeAngle((estTheta) - getGunHeading()  ) );
				//	setTurnRight((normalRelativeAngle((estTheta) - getHeading()  ) ) + 10);
					
					if(getGunHeat() == 0) {
				
					Bullet bullet = fireBullet(3);
					trackBullet(estTime + 5 , bullet) ;
					}			
				}

				else{
				
					setTurnGunRight((normalRelativeAngle((theta) - 
										getGunHeading()  ) ));
				
	
					
					if(getGunHeat() == 0) fire(3);//fire(firePower);
				}

				if(normalRelativeAngle((theta) - getHeading()) > 0)
					setTurnRight((normalRelativeAngle((theta) - getHeading()  ) ) + 10);
					
				else
					setTurnRight((normalRelativeAngle((theta) - getHeading()  ) ) - 10);

				//setAhead(1000);
			}
			
		double antiX = 0;
		double antiY = 0;
		
		double forceX = 0 ;
		double forceY = 0;
		
		int direction = 1;
		
	//	for(TankData d : list){
			
			antiX = -(Math.sin(Math.toRadians(normalRelativeAngle(getHeading() + d.getBearing())))* d.getDistance()) ;
			
			antiY = -(Math.cos(Math.toRadians(normalRelativeAngle(getHeading() + d.getBearing())))* d.getDistance());
			
			forceX += antiX * (100 / (d.getDistance() * d.getDistance()));
			forceY += antiY * (100 / (d.getDistance() * d.getDistance()));

		//}
		
		//wall L -> R -> D -> U
		
		forceX += getX() * ( 10000 / (getX() * getX() * getX()));
	
		forceX += -(getBattleFieldWidth() - getX()) * (10000 / Math.pow((getBattleFieldWidth() - getX()),3));
		
		forceY += getY() * ( 10000 / (getY() * getY() * getY()));
		
		forceY += -(getBattleFieldHeight() - getY()) * (10000 / Math.pow((getBattleFieldHeight() - getY()),3));
		
		//center
		
		//forceX += (getBattleFieldWidth() / 2) * (100 / Math.pow(Math.abs(getBattleFieldWidth()/2 - getX()),2));
		//forceY += (getBattleFieldHeight() / 2) * (100 / Math.pow(Math.abs(getBattleFieldHeight()/2 - getY()),2));
		
		double antiAngle = Math.toDegrees(Math.atan2(forceX , forceY));
	//		out.println(forceX+","+forceY +","+antiAngle);
		if(!Double.isNaN(antiAngle)){
			
			if(Math.abs(antiAngle - getHeading()) <= 90){
				
				setTurnRight(normalRelativeAngle(antiAngle - getHeading()));
				direction = 1;
			}
			else{
				setTurnRight(normalRelativeAngle( -180 - getHeading() + antiAngle));
				direction = -1;
			}
		}	
		setAhead(300 * direction) ;
		
		}
		
	}
	
	public void trackBullet(double ht , Bullet b){
		
		bulletInfo = new BulletInfo();
		
		bulletInfo.setData(ht,getTime(),b);
		
		bulletList.add(bulletInfo);
		
		bulletList.sort(new BulletListComparator());

	}
	
/*	public void onHitRobot(HitRobotEvent e){
		
		if(isTeammate(e.getName())){
			
			if(0 <= e.getBearing() && e.getBearing() <= 180)	turnRight(-90);
			else turnRight(90);
			ahead(100);
		}
	}
*/
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
	
	public double normalAbsoluteAngle(double angle) {
		if (angle >= 0 && angle < 360)
			return angle;
		double fixedAngle = angle;
		while (fixedAngle < 0)
			fixedAngle += 360;
		while (fixedAngle >= 360)
			fixedAngle -= 360;
		return fixedAngle;
	}
	
	public void onWin(WinEvent e) {
		clearAllEvents() ;
		setTurnGunRight(40000);
		for (int i = 0; i < 50; i++)
		{
			turnRight(40);
			turnLeft(40);
		}
	}
}
