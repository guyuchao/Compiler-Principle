package demo;

import java.util.HashMap;
import java.util.HashSet;

public class LR_table {
	private HashMap<String,Integer> grammar=new HashMap<String,Integer>();
	private HashSet<String> non_terminal_set;
	private HashSet<String> terminal_set;
	private DFA_Struct DFA_LR;
	private HashMap<Integer,String> grammar_raw;
	
	private HashMap<Integer,HashMap<String,String>> action=new HashMap<Integer,HashMap<String,String>>();
	private HashMap<Integer,HashMap<String,Integer>> go_to=new HashMap<Integer,HashMap<String,Integer>>();
	
	public HashMap<Integer, HashMap<String, String>> getAction() {
		return action;
	}
	public HashMap<Integer, HashMap<String, Integer>> getGo_to() {
		return go_to;
	}
	private void create_action_table_r() {
		HashMap<Integer,DFA_content> dfa_c_map=DFA_LR.index_of_node;
		for(Integer i:dfa_c_map.keySet()) {
			String tmp=dfa_c_map.get(i).getCore();
			if(tmp.charAt(tmp.length()-1)=='.') {
				HashMap<String,String> tmp_map=action.get(i);
				int index=grammar.get(tmp.substring(0, tmp.length()-1));
				if(index==0)tmp_map.put("$", "accept");
				else {
					for(String str:terminal_set) {
						tmp_map.put(str, "r"+index);
					}
				}
			}
		}
	}
	private void create_action_table_s_goto() {
		HashMap<Integer,HashSet<DFA_NODE>> dfa_c_map=DFA_LR.dfa_map;
		for(Integer i:dfa_c_map.keySet()) {
			HashSet<DFA_NODE> set_node=dfa_c_map.get(i);
			for(DFA_NODE item:set_node) {
				if(non_terminal_set.contains(item.dis)) {
					//·ÇÖÕ½á·û
					go_to.get(i).put(item.dis, item.end);
				}
				else {
					action.get(i).put(item.dis, "s"+item.end);
				}
			}
		}
	}
	private void create_table(){
		create_action_table_r();
		create_action_table_s_goto();
	}
	public LR_table(LR_Init lrp) {
		this.grammar_raw=lrp.getGrammar();
		for(Integer i:lrp.getGrammar().keySet()) {
			grammar.put(lrp.getGrammar().get(i), i);
		}
		this.non_terminal_set=lrp.getNon_terminal_set();
		this.terminal_set=lrp.getTerminal_set();
		this.terminal_set.add("$");
		this.DFA_LR=lrp.getDFA_LR();
		for(int i=0;i<=lrp.getState_num();i++) {
			action.put(i, new HashMap<String,String>());
			go_to.put(i,new HashMap<String,Integer>());
		}
		//System.out.println(grammar.toString());
		this.create_table();
		//System.out.println(action.toString());
		//System.out.println(go_to.toString());
	}
	public HashMap<Integer, String> getGrammar_raw() {
		return grammar_raw;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str[]=new String[3];
		str[0]="S->a A|b B";
		str[1]="A->c A|d";
		str[2]="B->c B|d";
		new LR_table(new LR_Init(str,"S"));
	}

}
