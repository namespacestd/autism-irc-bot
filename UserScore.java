
public class UserScore{
	private int points;
	private String name;
	
	public UserScore(String name){
		this.name = name;
		points = 0;
	}
	public UserScore(String name, int points){
		this.name = name;
		this.points = points;
	}
	
	public void increment(int num){
		points+=num;
	}
	public int getPoints(){
		return points;
	}
	public String getName(){
		return name;
	}
}