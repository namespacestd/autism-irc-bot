import java.io.*;
import java.net.*;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.User;
import org.jibble.pircbot.Colors;

import java.util.ArrayList;

public class FileBot extends PircBot {
    private static final String COMMAND_FILES = "!files";
    //private static final String COMMAND_GET = "!get";    
    private static final String COMMAND_WHAT = "!commands";
	private static final String COMMAND_KICK = "kick ";
    private static final String COMMAND_ROLL = "!roll";
	private static final String COMMAND_TSUNDERE = "!baka";
	private static final String COMMAND_TRIVIA = "!trivia";
	private static final String COMMAND_SCORES = "!score";
	private static final String COMMAND_HANGMAN = "!hangman";
	private static final String COMMAND_TEXTTWIST = "!texttwist";
	
	private static final int DCC_TIMEOUT = 120000;
	private static final String MAIN_CHANNEL = "#BULLIES";
	
	private static final String MAIN_BOT = "KAMI_SAMA";
	private static final String SECONDARY_BOT = "DAAARK_FLAMEEE_MASTR";
	private static final String DOWNLOAD_BOT = "MedakaBot";
	
	private static final String[] IMPORTANT_OPS = {"fannypackqt420", "Laudandus", "pinecone", "nobodyinparticular", "NamespaceSTD", MAIN_BOT, DOWNLOAD_BOT, SECONDARY_BOT};
	
	private static final String[] commands = {"!files = lists the files avaliable on the xdcc bot",
											  "!baka <target> = performs a tsundere attack on target; if no target specified, random person in room is chosen",
											  "!roll <sides/numDice> = if invalid or no number/string provided, rolls 2d6. Valid Strings: 100, 30d50",
											  "!trivia <subject> = currently two subjects: pokemon and anime. If nothing provided, defaults to anime",
											  "!scores = gets the current score standings",
											  "!commands = lists the bot commands"};
	
	private static final String[] TSUNDERE_ATTACKS =
		{"hits !sender with a whip. I-it's not like I like you or anything. BAKA.",
		 "bashes !sender over the head with a bat. G-gomen...I-it's not like I'm apologizing or anything. BAKA.",
		 "looks away from !sender. I-it's not like I needed your help or anything. I could have done it myself",
		 "gives !sender a bento. I-I just had some extra ingredients today. BAAAKA.",
		 "blushes at !sender. I-It's not like I died for YOUR sins or anything. I just WANTED to, alright? BAKA.",
		 "kicks !sender. BAKA BAKA BAKA BAKA BAKA BAKA BAKA BAKA",
		 "gives !sender some chocolate. I-I made these chocolates all by myself, so be greatful!",
		 "starts yelling at !sender. Urusai! Urusai! Urusai! Urusai! Urusai! Urusai! Urusai!.",
		 "turns extremely red at !sender's love confession and uppercuts !sender. W-whaaaa...",
		 "pokes !sender in the eyes. W-wha stop looking at me! I-It's not like I dressed up for you or anything.",
		 "looks away from !sender. D-don't look at me with that kind of face. It's embarrassing.",
		 "punches !sender in the abdomen. Shut up! Shut up already! \"I like you\" or \"I love you,\" I can't deal with any of it!",
		 "headbutts !sender in the face. I-Is there a-anyhting... I like about you...? I don't know! BAKA BAKA BAKA"};
	
	private ArrayList<String> neverOP = new ArrayList<String>();
	
	private boolean triviaLive = false;
	private ArrayList<TriviaQuestion> triviaQuestions = new ArrayList<TriviaQuestion>();
	private ArrayList<TriviaQuestion> discard = new ArrayList<TriviaQuestion>();
	private ArrayList<String> currentAnswers = new ArrayList<String>();
	private String currentQuestion = "";
	private ArrayList<UserScore> scores = new ArrayList<UserScore>();
	
	private int currentPokemon = 0;
	
	private boolean hangmanLive = false;
	private ArrayList<String> dictionary = null;
	private String currentWord = null;
	private char[] currentEliminated = {};
	private String currentProgress = null;
	private boolean manualInput = false;
	
	
	private boolean textTwistLive = false;
	private String currentTwist = null;
	private ArrayList<String> twistAnswers = null;
	private ArrayList<TriviaQuestion> textTwistQuestions = new ArrayList<TriviaQuestion>();
	private ArrayList<TriviaQuestion> textTwistDiscard = new ArrayList<TriviaQuestion>();
	private static final int[] WORDSCORING = {0,1,1,2,2,3,3,4,4,5,5};
	
	private String selfChannel;
	private boolean notImpressed = false;
	
	
    public FileBot(String name, String channel) { 	
		if(name.equals("main"))
			this.setName(MAIN_BOT);
		else
			this.setName(SECONDARY_BOT);
		
		this.selfChannel = channel;
    }
	
	//////////////////////////////////////////////////ON ACTION COMMANDS//////////////////////////////////////////////////
	
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(getName().equals(MAIN_BOT) || !containsUser(getUsers(MAIN_CHANNEL), MAIN_BOT)) {
			if(sender.equals("Trivia"))
				sendMessage(channel, "herculaneum wild bill hickock a helicopter 14th century (1348-50) the  invasion of britain new york their blood group the fifth republic henry VII pittsburgh sioux the battle of jarama dom perignon major esterhazy grover  cleveland the yalta agreement georges pompidou 1957 bolivia the star-spangled banner the korean war bonnie and clyde 1890s the family bock's car ras tafari  mexico and france ");
			
			if(message.equalsIgnoreCase(COMMAND_FILES)) {
				dccSendFile(new File("./xdcc/mybot.txt"), sender, DCC_TIMEOUT);
			} 
			//else if(message.toLowerCase().contains(".trivia"))
				//sendMessage(channel, ".strivia");
			else if(message.toLowerCase().startsWith(COMMAND_SCORES)){
				sendScores(channel);
			}
			else if(message.toLowerCase().startsWith(COMMAND_TEXTTWIST)){
				if(!textTwistLive){
					textTwistLive = true;
					loadTextTwist();
					sendMessage(channel, Colors.CYAN + "TEXT TWIST!");
					askTextTwistQuestion(channel);
				}
				else{
					textTwistLive = false;
					sendMessage(channel, Colors.CYAN + "TEXT TWIST OFF");
				}
					
			}
			else if(message.toLowerCase().startsWith(COMMAND_HANGMAN)){
				if(triviaLive)
					sendMessage(channel, Colors.CYAN + "Trivia is currently active");
				else{
					loadHangMan();
					startHangMan(channel);
				}
			}
			else if(message.toLowerCase().startsWith(COMMAND_TRIVIA)){
				if(hangmanLive)
					sendMessage(channel, Colors.CYAN + "Hangman is currently active");
				else
					triviaOptions(message, channel);
			}
			else if(sender.equals("NamespaceSTD") && message.toLowerCase().startsWith("!skip")){
				skipQuestion(channel);
			}
			else if(message.toLowerCase().startsWith(COMMAND_WHAT)){
				for(String c : commands)
					sendMessage(sender, c);
			}
			else if(message.toLowerCase().startsWith(COMMAND_TSUNDERE)){
				tsundereAction(channel, message);
			}
			else if(message.toLowerCase().startsWith(COMMAND_ROLL)){	
				diceRoll(channel, message, sender);
			}
			else if(triviaLive && currentAnswers.contains(message.toLowerCase().trim())){
				correctAnswer(message, sender, channel);
			}
			else if(hangmanLive){
				int answer = checkAnswer(message);
				if(answer==1){
					sendMessage(channel, Colors.CYAN + sender + " guessed a letter correctly! +1 point");
					sendMessage(channel, Colors.CYAN + currentProgress);
					hangmanScore(sender, 1);
					
					if(!currentProgress.contains("_")){
						sendMessage(channel, Colors.CYAN + "Next Word!");
						manualInput = false;
						hangmanQuestion(channel);
					}
				}
				else if(answer==5){
					sendMessage(channel, Colors.CYAN + sender + " has guessed the word correctly! +5 point");
					sendMessage(channel, Colors.CYAN + currentWord);
					hangmanScore(sender, 5);
					sendMessage(channel, Colors.CYAN + "Next Word!");
					hangmanQuestion(channel);
				}
			}
			else if(textTwistLive){
				int answer = checkTextTwistAnswer(message);
				
				if(answer!=0){
					UserScore score = userInScore(sender);
					
					if(score==null)
						scores.add(new UserScore(sender, answer));
					else
						score.increment(answer);
				}
				
				if(answer==8){
					sendMessage(channel, Colors.CYAN + sender +" has figured out the final word: "+message+". +8 points");
					askTextTwistQuestion(channel);
				}
				else if(answer!=0){
					sendMessage(channel, Colors.CYAN + sender +" has found the word: "+message+". +"+answer+" points");
					sendMessage(channel, Colors.CYAN + currentTwist);
				}
				

			}
		}
    }
	
	public void onJoin(String channel, String sender, String login, String hostname) {
		if(getName().equals(MAIN_BOT) || !containsUser(getUsers(MAIN_CHANNEL), MAIN_BOT)){
			String message = "Enter the command \"!files\" to recieve the xdcc pack list. Instructions on how to retrieve pack files are included in the txt file.";
			sendMessage(sender, message);
		}
		
		/*
		for(String ops : IMPORTANT_OPS)
			if(ops.equals(sender) && !neverOP.contains(sender))
				op(channel, sender);*/
	}
	
	public void onDisconnect(){
		try{
			connect("irc.rizon.net");
			identify("Tazningo!1");
			joinChannel(selfChannel);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public void onPrivateMessage(String sender, String login, String hostname, String message){
		if(sender.equals("NamespaceSTD")){
			if(message.toLowerCase().startsWith(COMMAND_KICK)){
				String target = message.substring(COMMAND_KICK.length()).trim();
				kick(MAIN_CHANNEL, target);
			}
			else if(message.toLowerCase().startsWith("reload trivia")){
				sendMessage(sender, "OKAY!");
				loadTrivia();
				loadHangMan();
				name150();
			}
			else if(message.toLowerCase().startsWith("reload text twist"))
				loadTextTwist();
				
			else if(message.toLowerCase().startsWith("hangman manual")){
				manualInput = true;
				currentWord = message.toLowerCase().replace("hangman manual", "");
				currentWord = currentWord.trim();
				startHangMan("#NamespaceSTD");
			}
			else if(message.toLowerCase().startsWith("flash"))
				notImpressed = !notImpressed;
		}
		if(message.equalsIgnoreCase(COMMAND_FILES)) {
			dccSendFile(new File("./xdcc/mybot.txt"), sender, DCC_TIMEOUT);
		} 
	}
	
	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason){
		if(channel.equals(MAIN_CHANNEL) && recipientNick.equals(this.getName()))
			joinChannel(MAIN_CHANNEL);
		/*else
			for(String ops : IMPORTANT_OPS){
				if(ops.equals(recipientNick) && !(ops.equals(MAIN_BOT)) && !(ops.equals(SECONDARY_BOT)) && !(ops.equals("NamespaceSTD")))
					kick(MAIN_CHANNEL, kickerNick);
			}*/
	}
	/*
	
	public void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient){
		for(String ops : IMPORTANT_OPS){
			if(ops.equals(recipient)){
				op(MAIN_CHANNEL, recipient);
				deOp(MAIN_CHANNEL, sourceNick);
			}
		}
	}
	public void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient){
		for(String ops : IMPORTANT_OPS){
			if(ops.equals(recipient)){
				op(MAIN_CHANNEL, recipient);
				deOp(MAIN_CHANNEL, sourceNick);
				neverOP.add(sourceNick);
			}
		}
	}*/
	//////////////////////////////////////////////////TEXT TWIST FUNCTIONS//////////////////////////////////////////////////
		public void loadTextTwist(){
			try{
				textTwistQuestions = new ArrayList<TriviaQuestion>();
				textTwistDiscard = new ArrayList<TriviaQuestion>();
				FileInputStream fstream = new FileInputStream("./texttwist.txt");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine = null;
				String folder = "";
				
				while ((strLine = br.readLine()) != null){
					strLine = strLine.replace("\t", "");
					String[] question = strLine.split("->");
					String[] answers = question[1].split("@");
					TriviaQuestion quest = new TriviaQuestion(question[0], fromArray(answers));
					textTwistQuestions.add(quest);
				}
				in.close();
			}
			catch(Exception e){
				System.err.println(e);
			}
		}	
		public void askTextTwistQuestion(String channel){
			if(textTwistQuestions.size()==0){
				triviaQuestions = textTwistDiscard;
				textTwistDiscard = new ArrayList<TriviaQuestion>();
			}
			TriviaQuestion rand = textTwistQuestions.remove((int)(Math.random()*textTwistQuestions.size()));
			textTwistDiscard.add(rand);
			currentTwist = rand.getQuestion();
			twistAnswers = rand.getAnswers();
			
			sendMessage(channel, Colors.CYAN + currentTwist);
		}
		public int checkTextTwistAnswer(String message){
			message = message.trim().toLowerCase();
			if(message.equals(twistAnswers.get(twistAnswers.size()-1))){
				System.out.println("8");
				return 8;
			}
			else if(twistAnswers.contains(message)){
				System.out.println("correct word");
				twistAnswers.remove(message);
				return WORDSCORING[message.length()];
			}
			return 0;
		}
	
	
	
	//////////////////////////////////////////////////TRIVIA FUNCTIONS//////////////////////////////////////////////////
	
	public void loadTrivia(){
		try{
			triviaQuestions = new ArrayList<TriviaQuestion>();
			discard = new ArrayList<TriviaQuestion>();
			FileInputStream fstream = new FileInputStream("./TriviaDatabase.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			String folder = "";
			
			while ((strLine = br.readLine()) != null){
				strLine = strLine.replace("\t", "");
				String[] question = strLine.split("->");
				String[] answers = question[1].split("@");
				TriviaQuestion quest = new TriviaQuestion(question[0], fromArray(answers));
				triviaQuestions.add(quest);
			}
			in.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}	
	public void name150(){
		try{
			triviaQuestions = new ArrayList<TriviaQuestion>();
			discard = new ArrayList<TriviaQuestion>();
			FileInputStream fstream = new FileInputStream("./Trivia150Pokemon.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			String folder = "";
			
			while ((strLine = br.readLine()) != null){
				String[] answers = {strLine};
				TriviaQuestion quest = new TriviaQuestion("", fromArray(answers));
				triviaQuestions.add(quest);
			}
			in.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}	
	public void askQuestion(String channel){
		if(triviaQuestions.size()==0){
			triviaQuestions = discard;
			discard = new ArrayList<TriviaQuestion>();
		}
		TriviaQuestion rand = triviaQuestions.remove((int)(Math.random()*triviaQuestions.size()));
		discard.add(rand);
		currentQuestion = rand.getQuestion();
		currentAnswers = rand.getAnswers();
					
		sendMessage(channel, Colors.CYAN + currentQuestion);
	}	
	public void nextPokemon(String channel){
		if(currentPokemon == 0)
			sendMessage(channel, Colors.CYAN + "Name all 150 Pokemon in order!");
		else if(currentPokemon == 150){
			sendMessage(channel, Colors.CYAN + "Gratz, you all together managed to name all 150. The current score standings are: ");
			sendScores(channel);
			triviaLive = false;
			currentPokemon = 0;
		}
		
		currentPokemon++;
		currentAnswers = triviaQuestions.get(currentPokemon-1).getAnswers();
	}
	
	public void triviaOptions(String message, String channel){
		if(!triviaLive){
			triviaLive = true;
					
			if(message.toLowerCase().replace("!trivia ", "").trim().equals("pokemon")){
				name150();
				nextPokemon(channel);
			}
			else{
				loadTrivia();
				askQuestion(channel);
			}
		}
		else{
			triviaLive = false;
			currentPokemon = 0;
			sendMessage(channel, Colors.CYAN + "Trivia OFF");
		}
	}
	public void skipQuestion(String channel){
		if(currentPokemon!=0){
			sendMessage(channel, Colors.CYAN + capitalize(currentAnswers.get(0)));
			nextPokemon(channel);
		}
		else
			askQuestion(channel);
	}
	public void sendScores(String channel){
		if(scores.size()==0)
			sendMessage(channel, Colors.CYAN + "No scores recorded.");
		else{
			sortScores();
			for(UserScore us : scores)
				sendMessage(channel, Colors.CYAN + us.getName() + " has "+us.getPoints()+" point(s).");
		}
	}	
	public void sortScores(){
		int highest = 0;
		ArrayList<UserScore> sorted = new ArrayList<UserScore>();
		while(scores.size()!=0){
			highest = 0;
			for(int j=0; j<scores.size(); j++)
				if(scores.get(highest).getPoints()<scores.get(j).getPoints())
					highest = j;
			sorted.add(scores.remove(highest));
		}
		scores = sorted;
	}
	public void correctAnswer(String message, String sender, String channel){
		UserScore score = userInScore(sender);
				
		if(score==null)
			scores.add(new UserScore(sender, 1));
		else
			score.increment(1);
				
		if(currentPokemon!=0)
			sendMessage(channel, Colors.CYAN + capitalize(currentAnswers.get(0)));
					
		sendMessage(channel, Colors.CYAN + sender + " got the right answer! He/she gets +1 point!");
				
		if(currentPokemon!=0)
			nextPokemon(channel);
		else{
			sendMessage(channel, Colors.CYAN + "Next Question:");
			askQuestion(channel);	
		}
	}
	
	//////////////////////////////////////////////////HANGMAN COMMANDS//////////////////////////////////////////////////
	
	public void loadHangMan(){
		if(dictionary == null){
			dictionary = new ArrayList<String>();
			try{
				FileInputStream fstream = new FileInputStream("./dictionary.txt");
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine = null;
				String folder = "";
				
				while ((strLine = br.readLine()) != null)
					dictionary.add(strLine);
					
				in.close();
			}
			catch(Exception e){
				System.err.println(e);
			}
		}
	}
	public void startHangMan(String channel){
		if(!hangmanLive){
			hangmanLive = true;
			sendMessage(channel, Colors.CYAN + "Hangman! Guess the word. Correct letters give +1 point. Naming the word gives +5 points.");
			hangmanQuestion(channel);
		}
		else{
			hangmanLive = false;
			sendMessage(channel, Colors.CYAN + "Hangman OFF");
		}
	}
	public void hangmanQuestion(String channel){
		if(!manualInput)
			currentWord = dictionary.get((int)(Math.random()*dictionary.size())).trim().toLowerCase();
		currentEliminated = currentWord.toCharArray();
		currentProgress = createBlank(currentWord);
		sendMessage(channel, Colors.CYAN + currentProgress);
	}
	public String createBlank(String word){
		String blank = "";
		for(int i=0; i<word.length(); i++)
			blank+="_ ";
		return blank;
	}
	public int checkAnswer(String guess){
		guess = guess.trim().toLowerCase();
		if(guess.length()>1){
			if(guess.equals(currentWord))
				return 5;
			else
				return 0;
		}
		else if(guess.length()==1){
			//System.out.println("Guess: "+guess);
			//System.out.println("Answer: "+currentWord);
			char[] temp = currentProgress.toCharArray();
			for(int i=0; i<currentEliminated.length; i++){
				if(guess.charAt(0)!='_' && guess.charAt(0)!=' ' && currentEliminated[i] == guess.charAt(0)){
					temp[i*2]=currentEliminated[i];
					currentEliminated[i]='_';
				}
				//System.out.println(currentEliminated[i]);
			}
			String result = String.valueOf(temp);
			//System.out.println("Result: " + result);
			if(!result.equals(currentProgress)){
				currentProgress = result;
				return 1;			
			}
		}
		
		return 0;
	}
	
	public void hangmanScore(String sender, int points){
		UserScore score = userInScore(sender);
				
		if(score==null)
			scores.add(new UserScore(sender, points));
		else
			score.increment(points);
	}
	
	//////////////////////////////////////////////////HELPER FUNCTIONS//////////////////////////////////////////////////
	
	public void tsundereAction(String channel, String message){
		if(!notImpressed){
			User[] users = getUsers(channel);
			String[] mess = message.split(" ");
					
			int random_index = (int)(Math.random() * TSUNDERE_ATTACKS.length);
					
			if(mess.length==2 && !mess[1].trim().equals(MAIN_BOT))			
				sendAction(channel, TSUNDERE_ATTACKS[random_index].replace("!sender", mess[1]));
			else{
				String target = randomUser(users);
				while(target.equals(this.getName()))
					target = randomUser(users);
				sendAction(channel, TSUNDERE_ATTACKS[random_index].replace("!sender", target));
			}
		}
		else
			sendAction(channel, " is not impressed.");
	}
	
	public void diceRoll(String channel, String message, String sender){
		String mess = message.trim();
		if(mess.equals(COMMAND_ROLL)){
			int dice1 = (int)(Math.random()*6) + 1;
			int dice2 = (int)(Math.random()*6) + 1;
					
			sendMessage(channel, Colors.LIGHT_GRAY + sender + " rolls a "+dice1+" and a "+dice2+".");
		}
				
		String[] parts = mess.split(" ");
		try{
			if(parts.length==2){
				if(parts[1].contains("d")){
					parts = parts[1].split("d");
					if(parts.length!=2)
						throw new Exception();
					int numDie = Integer.parseInt(parts[0]);
					int sides = Integer.parseInt(parts[1]);
					
					if (numDie<=0 || sides <=0)
						throw new Exception();
								
					String mes = Colors.LIGHT_GRAY + sender + " rolls "+numDie+" "+sides+"-sided die and gets:";
					for(int i=0; i<numDie; i++)
						mes+= " " + ((int)(Math.random()*sides)+1);
					sendMessage(channel, mes+".");
				}
				else{
					int sides = Integer.parseInt(parts[1]);
					if(sides <=0)
						throw new Exception();
								
					int dice = (int)(Math.random()*sides) +1;
					sendMessage(channel, Colors.LIGHT_GRAY + sender + " rolls a "+sides+" sided dice and gets a "+dice+".");
				}
			}
		}
		catch(Exception ex){
			sendMessage(channel, Colors.BOLD + Colors.RED +"BAKA "+sender );
		}
	}
	public boolean containsUser(User[] users, String target){
		for(User u: users)
			if(u.getNick().equals(target))
				return true;
		return false;
	}
	
	public ArrayList<String> fromArray(String[] array){
		ArrayList<String> temp = new ArrayList<String>();
		for(String a : array){
			temp.add(a.trim().toLowerCase());
			//System.out.println(a);
		}
		return temp;
	}
	public String capitalize(String target){
		if(target!=null)
			target = target.substring(0,1).toUpperCase() + target.substring(1, target.length());
		return target;
	}
	

	
	public UserScore userInScore(String name){
		for(UserScore us : scores)
			if(us.getName().equals(name))
				return us;
		return null;
	}
	public String randomUser(User[] users){
		return users[(int)(Math.random()*users.length)].getNick();
	}
}	


/*
            // Get all files (not directories) in the directory.

            String[] fileList = dir.getAbsoluteFile().getParentFile().getParentFile().list( );
	    System.out.println(dir.getAbsoluteFile().getParentFile().getParentFile().getName());

            if (fileList == null || fileList.length == 0) {

	    
                sendMessage(channel, "Sorry, no files available right now.");

            } else {

                // List the files.
		
                for (int i = 0; i < fileList.length; i++) {

                    //if(fileList[i].isFile( )) { 
			
                        sendMessage(channel, fileList[i]);

                   // }

                }

            }
			
					ht = new ArrayList<FileLocation>();
		
		
		
				else if(message.toLowerCase( ).startsWith(COMMAND_GET + " ")) {
            String fileToGet = message.substring(COMMAND_GET.length( )).trim( );
			File dir = new File(".");
			File fileToSend = null;
			for(FileLocation fl : ht)
				if(fl.getCode().equals(fileToGet)){
					String directory = "C:\\Users\\Adong\\Documents\\Anime and Other Media\\"+fl.getFolder()+"\\"+fl.getFilename();
					fileToSend = new File(directory);
					if(!fileToSend.exists()){
						directory = "H:\\Anime\\"+fl.getFolder()+"\\"+fl.getFilename();
						fileToSend = new File(directory);
					}
					System.out.println(directory);
					
					break;
				}
			if(fileToSend!=null){
				DccFileTransfer dft = dccSendFile(fileToSend, sender, DCC_TIMEOUT);
				dft.setPacketDelay(0);
				System.out.println(dft.getTransferRate());
			}
				
        }

		
*/
