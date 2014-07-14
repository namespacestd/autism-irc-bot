import java.util.*;
import java.io.*;

public class Test{
	public static void main(String[] args){
		ArrayList<FileLocation> ht = new ArrayList<FileLocation>();
		
		try{
			FileInputStream fstream = new FileInputStream("./FileInfo.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = br.readLine();
			String folder = "";
			
			while ((strLine = br.readLine()) != null){
				if(strLine.length() > 5){
					if(strLine.contains("--"))
						folder = strLine.replace("-", "");
					else{
						//System.out.println(strLine);
						String[] info = strLine.split("-> ");
						for(String t : info)
							System.out.println(t);
						ht.add(new FileLocation(folder, info[0], info[1]));
					}
				}
			}
			in.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
		
		for(FileLocation loc : ht)
			System.out.println(loc);
	}
}