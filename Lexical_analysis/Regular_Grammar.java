package demo2;

import java.util.HashSet;
import java.util.Set;

public class Regular_Grammar {
	private NFA_Adjtable_str nfa_adj_str=new NFA_Adjtable_str();
	private Character nfa_begin;
	private Character nfa_end='Z';//Z为终态
	private Set<Character> non_terminal=new HashSet<Character>();
	private Set<Character> terminal=new HashSet<Character>();
	public Regular_Grammar(String pathname) {
		this.Read_Regular_Grammar(pathname);
	}
	private boolean isCharacter(Character c) {
		if(c<='z'&&c>='a')return true;
		if(c>='A'&&c<='Z')return true;
		return false;
	}
	private void Read_Regular_Grammar(String pathname) {
		FileIOstream fio=new FileIOstream();
		String[] text=fio.InputFile(pathname).split("\r\n");
		//起始状态
		for(int i=0;i<text[0].length();i++) {
			if(text[0].charAt(i)=='[') {
				nfa_begin=text[0].charAt(i+1);
			}
		}
		//获取所有状态
		for(int i=1;i<text.length;i++) {
			String tmp_str=text[i];
			for(int j=0;j<tmp_str.length();j++) {
				if(isCharacter(tmp_str.charAt(j))) {
					non_terminal.add(tmp_str.charAt(j));
					break;
				}
			}
		}
		for(int i=1;i<text.length;i++) {
			String tmp_str=text[i];
			String derivation="";
			Character tmp_begin=null;
			for(int j=0;j<tmp_str.length();j++) {
				if(tmp_begin==null&&isCharacter(tmp_str.charAt(j))) {
					tmp_begin=tmp_str.charAt(j);
				}
				if(tmp_str.charAt(j)=='>') {
					derivation=tmp_str.substring(j+1);
					break;
				}
			}
			create_graph(tmp_begin,derivation.split("\\|"));//非终结符，产生式
			//System.out.println(derivation);
		}
	}
	private void create_graph(Character begin,String[] derivation) {
		//右线性文法
		for(int i=0;i<derivation.length;i++) {
			//System.out.println(derivation[i]);
			Character tmp_terminal=derivation[i].charAt(0);
			if(tmp_terminal=='#') {
				nfa_adj_str.insert(begin, nfa_end, tmp_terminal.toString());
			}
			else {
				Character tmp_end=derivation[i].charAt(1);
				nfa_adj_str.insert(begin, tmp_end, tmp_terminal.toString());
			}
		}
	}
	public static void main(String[] args) {
		new Regular_Grammar("C:\\Users\\guyuchao\\eclipse-workspace\\Lexical\\TestCase\\regular_grammar.txt").nfa_adj_str.transvers();
	}
}
