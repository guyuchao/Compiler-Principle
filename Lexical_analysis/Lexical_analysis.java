package demo2;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Lexical_analysis {
	private Map<Integer,Set<DFA_EdgeNode>> dfa_final=null;
	private Integer dfa_final_begin=null;
	private Map<Integer,String> Acceptable =null;
	private Map<String,String> InputMap=null;
	private String code_text=null;	
	private ArrayList<Token> token_array=null;
	
	public Lexical_analysis(Read_regular read_re) {//初始化
		dfa_final=read_re.getDfa_final();
		dfa_final_begin=read_re.getDfa_final_begin();
		Acceptable=read_re.getAcceptable();
		token_array=new ArrayList<Token>();
		InputMap=read_re.getInputMap();
		FileIOstream fis=new FileIOstream();
		String inputpathname="E:\\Code.txt";
		code_text=fis.InputFile(inputpathname);
		this.Code_to_Token(code_text.split("\n"));//内容存放在token_array里面
		String outputpathname="E:\\Code_token.out";
		String out=this.array_to_string();
		fis.OuputFile(out, outputpathname);
	}
	
	private String array_to_string() {
		String ret_str="";
		for(Token token:token_array) {
			System.out.println(token.content+"  "+token.type);
			
			switch(token.type) {
			case "NUMBER":
				ret_str+="(NUMBER)";
				ret_str+=token.content;
				ret_str+="   ";
				break;
			case "IDENTIFER":
				ret_str+="(IDENTIFER)";
				ret_str+=token.content;
				ret_str+="   ";
				break;
			case "NOTATION":
				ret_str+="(NOTATION)";
				ret_str+=token.content;
				ret_str+="   ";
				break;
			case "RESERVE":
				ret_str+="(RESERVE)";
				ret_str+=token.content;
				ret_str+="   ";
				break;
			case "LINE":
				ret_str+="\r\n";
				break;
			case "TAB":
				ret_str+="(TAB)";
				ret_str+="   ";
				break;
			case "SPACE":
				ret_str+="(SPACE)";
				ret_str+="   ";
				break;
			}
		}
		return ret_str;
	}
	
	private Integer dfa_via_to(Integer begin,String via) {
		if(dfa_final.containsKey(begin)) {
			Set<DFA_EdgeNode> tmp_set=dfa_final.get(begin);
			for(DFA_EdgeNode node:tmp_set) {
				if(node.dis.equals(via)) {
					return Integer.parseInt(node.end);
				}
			}
		}	
		return -1;
	}

	private void Code_to_Token(String[] text) {
		
		for(int i=0;i<text.length;i++) {
			//this.transvers();
			//text[i]=text[i].substring(0,text[i].length()-1);
			int ptr_text=0;
			int end_ptr_text=text[i].length()-1;
			
			while(ptr_text<=end_ptr_text) {
				Character ch=text[i].charAt(ptr_text);
				if(text[i].charAt(ptr_text)=='\t') {
					token_array.add(new Token("TAB",null));
					ptr_text++;
					continue;
				}
				if(text[i].charAt(ptr_text)=='\n'||text[i].charAt(ptr_text)=='\r') {
					ptr_text++;
					continue;
				}
				switch(InputMap.get(ch.toString())) {
				case "SPACE":
					token_array.add(new Token("SPACE",null));
					//System.out.println(text[i].length());
					Character ch_tmp;
					if(++ptr_text<end_ptr_text) {
						ch_tmp=text[i].charAt(ptr_text);
					}
					else {
						break;
					}
					while(ch_tmp==' ') {ch_tmp=text[i].charAt(++ptr_text);}
					break;
				case "NOTATION":
					token_array.add(new Token("NOTATION",ch.toString()));
					++ptr_text;
					break;
				default:
					String tmp_str="";
					String type_tmp=InputMap.get(ch.toString());
					Integer now_state=dfa_final_begin;
					Integer final_state=null;
					while((final_state=dfa_via_to(now_state,type_tmp))!=-1) {
						tmp_str+=ch;
						ch=text[i].charAt(++ptr_text);
						now_state=final_state;
						type_tmp=InputMap.get(ch.toString());
					}
					
					if(InputMap.containsKey(tmp_str)&&InputMap.get(tmp_str).equals("RESERVE")) {
						token_array.add(new Token("RESERVE",tmp_str));
					}
					else {
						//System.out.println(tmp_str+"   "+now_state);
						token_array.add(new Token(Acceptable.get(now_state),tmp_str));
					}
					//this.transvers();
				}
				
			}
			token_array.add(new Token("LINE",null));
		}
	}
	
}
class Token{
	String type=null;
	String content=null;
	public Token(String type) {
		this.type=type;
	}
	public Token(String type,String content) {
		this.type=type;
		this.content=content;
	}
}