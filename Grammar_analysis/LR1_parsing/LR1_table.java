package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class LR1_table {
	private HashMap<String,Integer> grammar=new HashMap<String,Integer>();
	private HashSet<String> non_terminal_set;
	private HashSet<String> terminal_set;
	private DFA_Struct DFA_LR;
	private TreeMap<Integer,String> grammar_raw;
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
			ArrayList<content_struct> tmp_list=dfa_c_map.get(i).getCore();
			for(content_struct str:tmp_list) {
				HashMap<String,String> tmp_map=action.get(i);
				if(str.content.charAt(str.content.length()-1)=='.') {
					int index=grammar.get(str.content.substring(0, str.content.length()-1));
					if(index==0)tmp_map.put(str.search_str, "accept");
					else {
						tmp_map.put(str.search_str,"r"+index);
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
	public LR1_table(LR1_init lrp) {
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
		this.create_table();
		System.out.println(action);
	}
	public TreeMap<Integer, String> getGrammar_raw() {
		return grammar_raw;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str[]=new String[1];
		/*str[0]="S->id|V := E";
		str[1]="V->id";
		str[2]="E->V|n";*/
		str[0]="A->( A )|a";	
		new LR1_table(new LR1_init(str,"A"));
	}

}
