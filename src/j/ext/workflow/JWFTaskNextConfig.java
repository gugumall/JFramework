package j.ext.workflow;


public class JWFTaskNextConfig {
	private String condition;
	private String[] uuids;

	
	public String getConditon(){
		return this.condition;
	}
	public void setCondition(String condition){
		this.condition=condition;
	}
	
	public String[] getUuids(){
		return this.uuids;
	}
	public void setUuids(String _uuids){
		this.uuids=_uuids==null?null:_uuids.split(",");
	}
}
