
public class FileLocation{
	private String folder;
	private String fileName;
	private String code;
	
	public FileLocation(String folder, String fileName, String code){
		this.folder = folder;
		this.fileName = fileName;
		this.code = code;
	}
	
	public String getFolder(){
		return folder;
	}
	public String getFilename(){
		return fileName;
	}
	public String getCode(){
		return code;
	}
	public String toString(){
		return "folder:"+folder+" filename:"+fileName+" code"+code;
	}
}
