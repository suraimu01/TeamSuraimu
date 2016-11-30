package TeamSuraimu;
import robocode.*;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class KingSuraimu extends TeamRobot{	/**
	 * run: KingSuraimu's default behavior
	 */

	private List<TankData> list = new ArrayList<TankData>();
	private TankData data ;
	
	private List<BulletInfo> bulletList = new ArrayList<BulletInfo>();
	private BulletInfo bulletInfo ;
	
	private String targetName = null ;
	private int targetIndex = 0 ;
	
	private String trackerName = null ;
	private int bulletCount = 0 ;
	
	private int trackfireCount = 0 ;
	private boolean removeComp = true;
	
	private int direction = 1 ;
//	private int trunIndex = 1 ;

	private int lonelyCount = 2;//getTeammates().length;
	private boolean lonelyFrag = false;

	public void run() {
	
		setAdjustGunForRobotTurn(true) ;
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);

		//setColor
		RobotColors c = new RobotColors();
		
		c.bodyColor = Color.cyan;
		c.gunColor = Color.cyan;
		c.radarColor = Color.red;
		
		setColors(Color.cyan,Color.cyan,Color.red);
		
		try {
		
			broadcastMessage(c);
		}catch (IOException e){}

		//
		setEventPriority("HitRobotEvent" , 80);
		setEventPriority("CustomEvent" , 99);		

		addCustomEvent(
			new Condition("removeBulletList") { 				

				public boolean test() {
					return (!bulletList.isEmpty() &&removeComp&& (getTime() >= (bulletList.get(0).getExpectTime() + 5)));
					
				};
			}
		);

		while(true) {
			// Replace the next 4 lines with any behavior you would like
			setTurnRadarRight(360);
			waitFor(new RadarTurnCompleteCondition(this));
			setTurnRadarLeft(360);
			waitFor(new RadarTurnCompleteCondition(this));
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		
		boolean NameOverlap = false ;
		
		int listIndex = 0;

		if (isTeammate(e.getName())) {
			
			return;
			
		}

		if(list.isEmpty()){
			
			data = new TankData() ;
			
			data.setName(e.getName());
			data.setData(e.getVelocity(),e.getHeading(),e.getBearing(),e.getDistance(),e.getEnergy() ,getTime());
			data.setXY(getX(),getY(),getHeading());
			list.add(data);	
			
		}
		else{
			
			int c = 0 ;

			for(TankData d : list){
				
				if(d.getName().equals(e.getName())){
				
						NameOverlap = true;
						
						listIndex = c ;

						d.setPrvData(d.getVelocity(),d.getHeading(),d.getBearing(),d.getDistance(),d.getEnergy(),d.getScanTime());
	
						d.setData(e.getVelocity(),e.getHeading(),e.getBearing(),e.getDistance(),e.getEnergy(),getTime());
						d.setXY(getX(),getY(),getHeading());
						
				}
				
				c ++ ;
			}
			
			if(!NameOverlap){
			
				data = new TankData() ;
				
				data.setName(e.getName());
				data.setData(e.getVelocity(),e.getHeading(),e.getBearing(),e.getDistance(),e.getEnergy(),getTime());	
				data.setXY(getX(),getY(),getHeading());
				
				list.add(data);	
				
			}
			
		}
	
		if(NameOverlap){
			
			data = list.get(listIndex);			
			
			antiGravity();
			
			if(!lonelyFrag ){
				try {
					
					broadcastMessage(list);
				}catch (IOException ex){}
			
			}
			//targetIndex = listIndex;
			
		}
		

		
		targetIndex = listIndex;

		
		trackFire(e , data);

		
		if(getOthers() <= 1 ){
					
			setTurnRadarRight((normalRelativeAngle((getHeading() + e.getBearing()) - 
									getRadarHeading()  ) ));
			waitFor(new RadarTurnCompleteCondition(this));
			scan();
		}
	}
	
		public void trackFire(ScannedRobotEvent e , TankData d){
	
		double firePower ;
		double bulletVelocity ;

		int bDirection = 1;

		if(targetName == null ){
			
			targetName = e.getName();
			
			try {
			
				broadcastMessage(targetName);
			}catch (IOException ex){}
			

		}
			//out.println(targetName);
		if(targetName.equals(e.getName())){
		
			firePower = 400 / e.getDistance() ;
			bulletVelocity = 20 - 3 * firePower ;
			
		
			if(!lonelyFrag ){			
out.println(d.getX() +","+ d.getY());
				try {
			
				broadcastMessage(d);
				}catch (IOException ex){}
				

				//return ;
			}

			if(e.getHeading() == d.getPrvHeading() && e.getVelocity() == d.getPrvVelocity() && d.trackable){

				double enemyXdis 
							= Math.sin(Math.toRadians(normalRelativeAngle(getHeading() + e.getBearing())))* e.getDistance() ;
				double enemyYdis
							= Math.cos(Math.toRadians(normalRelativeAngle(getHeading() + e.getBearing())))* e.getDistance();			
				
				//out.println(enemyXdis +","+enemyYdis);
				
				double A = bulletVelocity * bulletVelocity - e.getVelocity() * e.getVelocity() ;

				double B = e.getVelocity()*((Math.sin(e.getHeadingRadians()) * enemyXdis) +
								(Math.cos(e.getHeadingRadians()) * enemyYdis));
									 
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
	
				if(((time * e.getVelocity() * Math.sin(e.getHeadingRadians())) + getX() + enemyXdis) < 0 ){
				//out.println((getX() + enemyXdis));
					time = (getX() + enemyXdis) / e.getVelocity() ;
				}
			
				if(((time * e.getVelocity() * Math.sin(e.getHeadingRadians())) + getX() + enemyXdis) > getBattleFieldWidth() ){
				//out.println((getBattleFieldWidth() - getX() + enemyXdis));
					time = (getBattleFieldWidth() - (getX() + enemyXdis)) / e.getVelocity() ;
				}
				
				if(((time * e.getVelocity() * Math.cos(e.getHeadingRadians())) + getY() + enemyYdis) < 0 ){
				//out.println("-Y");
					time = (getY() + enemyYdis)/ e.getVelocity() ;
				}
			
				if(((time * e.getVelocity() * Math.cos(e.getHeadingRadians())) + getY() + enemyYdis) > getBattleFieldHeight() ){
				//out.println("Y");
					time = (getBattleFieldHeight() - (getY() + enemyYdis)) / e.getVelocity() ;
				}

			//	double predictionAngle = Math.toDegrees(Math.asin((enemyXdis + e.getVelocity() * time * Math.sin(e.getHeadingRadians())) / (bulletVelocity * time)));
		
				double ptan = Math.toDegrees(Math.atan((enemyXdis + e.getVelocity() * time * Math.sin(e.getHeadingRadians())) / (enemyYdis + e.getVelocity() * time * Math.cos(e.getHeadingRadians()))));

				if(!Double.isNaN(ptan)){				
	
					if(((e.getVelocity() * time * Math.cos(e.getHeadingRadians())) + enemyYdis + getY()) >= getY())	{
						turnGunRight(normalRelativeAngle(ptan - getGunHeading()));

					}
					else{
						turnGunRight(normalRelativeAngle(180 - getGunHeading() + ptan));
						
					}
				}
				

				else	setTurnGunRight((normalRelativeAngle((getHeading() + e.getBearing()) - 
									getGunHeading()  ) ));
				//out.println("time:"+ time);

	
				if(getGunHeat() == 0) {
				
					Bullet bullet = fireBullet(firePower);
					trackBullet(time , bullet) ;
				/*	out.println("--------------------"+bulletList.size());
					int i = 0;
					while(i != bulletList.size()){
						
						out.println(bulletList.get(i).getExpectTime() +","+ i);
						i++;
					}*/
				}
			}
			
			else if(e.getHeading() != d.getPrvHeading() && d.trackable){
					
					double omega = (d.getHeading() - d.getPrvHeading()) / (d.getScanTime() - d.getPrvScanTime());
					double radius = Math.abs(d.getVelocity() / omega) ;
					
					double estTime = d.getDistance() / 14 ;
					
					double centerX = d.getX() + (radius * Math.cos(Math.toRadians(d.getBearing())));
					double centerY = d.getY() + (radius * Math.sin(Math.toRadians(d.getBearing())));
					
					double estX = radius * Math.cos(Math.toRadians(d.getBearing() + omega*estTime));
					double estY = radius * Math.sin(Math.toRadians(d.getBearing() + omega*estTime));
					double estTheta = Math.toDegrees(Math.atan2(centerX + estX - getX(),centerY + estY - getY()));
out.println(estTheta);

					if(!Double.isNaN(estTheta)){		
						
						turnGunRight(normalRelativeAngle((estTheta) - getGunHeading()  ) );
					}					

					if(getGunHeat() == 0) {
				
					Bullet bullet = fireBullet(2);
					trackBullet(estTime + 5 , bullet) ;
					}

			}


			else{
			
				setTurnGunRight((normalRelativeAngle((getHeading() + e.getBearing()) - 
									getGunHeading()  ) ) + 5 * bDirection);

				
				if(getGunHeat() == 0){

					 fire(2);//fire(firePower);
					 
					bDirection *= -1 ;
				}

			}
		}
	}
	
	public void trackBullet(double ht , Bullet b){
		
		bulletInfo = new BulletInfo();
		
		bulletInfo.setData(ht,getTime(),b);
		
		bulletList.add(bulletInfo);
		
		bulletList.sort(new BulletListComparator());

	}

	public void antiGravity(){
	
		double antiX = 0;
		double antiY = 0;
		
		double forceX = 0 ;
		double forceY = 0;
		
		for(TankData d : list){
			
			antiX = -(Math.sin(Math.toRadians(normalRelativeAngle(getHeading() + d.getBearing())))* d.getDistance()) ;
			
			antiY = -(Math.cos(Math.toRadians(normalRelativeAngle(getHeading() + d.getBearing())))* d.getDistance());
			
			forceX += antiX * (100 / (d.getDistance() * d.getDistance()));
			forceY += antiY * (100 / (d.getDistance() * d.getDistance()));

		}
		
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
		/*
			if(forceY >= 0){
				direction = 1;
				setTurnRight(normalRelativeAngle(antiAngle - getHeading()));
			}
			else{
				//direction = -1;
				setTurnRight(normalRelativeAngle( 180 - getHeading() + antiAngle));
			}*/
		}
		setAhead(300 * direction) ;
	}
	
	public void onCustomEvent(CustomEvent e){
			
		if (e.getCondition().getName().equals("removeBulletList")){
			
			removeComp = false;
			
			if(!bulletList.isEmpty()){

				BulletInfo bi = bulletList.get(0);
				bulletList.remove(0);
				if(bi.getBullet().isActive()){
					
					trackfireCount ++;
					out.println(trackfireCount);
				}
				else{
					
					trackfireCount = 0 ;
				}
				
				
				if(trackfireCount >= 5){
					
					list.get(targetIndex).trackable = false ;
				}
			}
			removeComp = true;
		}
	}
	
	public void onRobotDeath(RobotDeathEvent e){
		out.println("death");	
		if(e.getName().equals(targetName)) targetName = null ;
		
		if(isTeammate(e.getName())) lonelyCount-- ;
		
		if(lonelyCount == 0)	lonelyFrag = true ;
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
