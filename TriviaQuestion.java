import java.util.ArrayList;

public class TriviaQuestion{
	private String question;
	private ArrayList<String> validAnswers;
	
	public TriviaQuestion(String question, ArrayList<String> validAnswers){
		this.question=question;
		this.validAnswers=validAnswers;
	}
	
	public ArrayList<String> getAnswers(){
		return validAnswers;
	}
	public String getQuestion(){
		return question;
	}
}