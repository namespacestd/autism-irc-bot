public class FileBotMain2 {
    public static void main(String[] args) throws Exception {

        FileBot fBot = new FileBot("main", "#BULLIES");
        fBot.setVerbose(true);
        fBot.connect("irc.rizon.net");
		fBot.identify("Tazningo!1");
        fBot.joinChannel("#BULLIES");
		
		

    }



}