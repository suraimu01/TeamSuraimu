package TeamSuraimu;


public class EnemyData  implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private  TankData d;

	public EnemyData(TankData d){
	
		this.d = d;
	}
	
	public TankData getData(){
		
		return d;
	}
	
}
