package j.service.hello;

import java.io.Serializable;

/**
 * 
 * @author JFramework
 *
 */
public class JHelloWords implements Serializable{
	private static final long serialVersionUID = 1L;
	private String words;
	
	/**
	 * 
	 *
	 */
	public JHelloWords() {
		super();
	}

	/**
	 * 
	 * @param words
	 */
	public void setWords(String words){
		this.words=words;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getWords(){
		return this.words;
	}
}