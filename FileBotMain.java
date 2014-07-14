public class FileBotMain {
    public static void main(String[] args) throws Exception {

        FileBot fBot = new FileBot("secondary", "");
        fBot.setVerbose(true);
        fBot.connect("irc.rizon.net");
	fBot.joinChannel("#knightsoftheeasterncalculus", "vividred10outof10");
    }



}
