package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SLR_table {
	private HashMap<String,Integer> grammar=new HashMap<String,Integer>();
	private HashSet<String> non_terminal_set;
	private HashSet<String> terminal_set;
	private DFA_Struct DFA_LR;
	private HashMap<Integer,String> grammar_raw;
	private Map<String,HashSet<String>> FOLLOW;
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
			ArrayList<String> tmp_list=dfa_c_map.get(i).getCore();
			for(String str:tmp_list) {
				HashMap<String,String> tmp_map=action.get(i);
				if(str.charAt(str.length()-1)=='.') {
					
					int index=grammar.get(str.substring(0, str.length()-1));
					if(index==0)tmp_map.put("$", "accept");
					else {
						for(String item:FOLLOW.get(str.split("->")[0])) {
							tmp_map.put(item,"r"+index);
						}	
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
	public SLR_table(SLR_Init lrp) {
		this.grammar_raw=lrp.getGrammar();
		for(Integer i:lrp.getGrammar().keySet()) {
			grammar.put(lrp.getGrammar().get(i), i);
		}
		this.FOLLOW=lrp.getFOLLOW();
		this.non_terminal_set=lrp.getNon_terminal_set();
		this.terminal_set=lrp.getTerminal_set();
		this.terminal_set.add("$");
		this.DFA_LR=lrp.getDFA_LR();
		for(int i=0;i<=lrp.getState_num();i++) {
			action.put(i, new HashMap<String,String>());
			go_to.put(i,new HashMap<String,Integer>());
		}
		this.create_table();
		System.out.println(action);
	}
	public HashMap<Integer, String> getGrammar_raw() {
		return grammar_raw;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str[]=new String[3];
		str[0]="E->E + T|T";
		str[1]="T->T * F|F";
		str[2]="F->( E )|id";
		
		new SLR_table(new SLR_Init(str,"E"));
	}

}
