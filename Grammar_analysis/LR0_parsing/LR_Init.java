package demo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.DelayQueue;

public class LR_Init {
	private HashMap<Integer,String> grammar=new HashMap<Integer,String>();
	private HashMap<String,Integer> find_core=new HashMap<String,Integer>();
	private HashSet<String> non_terminal_set=new HashSet<String>();
	private HashSet<String> terminal_set=new HashSet<String>();
	private Integer num_of_production=0;
	public int getState_num() {
		return state_num;
	}


	private int state_num=0;
	private String grammar_begin="S'";
	private DFA_Struct DFA_LR=new DFA_Struct();
	
	public HashSet<String> getNon_terminal_set() {
		return non_terminal_set;
	}

	public HashSet<String> getTerminal_set() {
		return terminal_set;
	}

	public DFA_Struct getDFA_LR() {
		return DFA_LR;
	}

	public HashMap<Integer, String> getGrammar() {
		return grammar;
	}

	public LR_Init(String[] str,String gra_begin) {
		init(str,gra_begin);
		createDFA();
		System.out.println(DFA_LR.index_of_node.toString());
	}
	
	private ArrayList<String> find_begin_grammar(String begin) {
		ArrayList<String> tmp_str=new ArrayList<String>();
		for(Integer i:grammar.keySet()) {
			if(grammar.get(i).split("->")[0].equals(begin)) {
				tmp_str.add(grammar.get(i).split("->")[1]);
			}
		}
		return tmp_str;
	}
	
	private ArrayList<String> find_closure(String core_content,HashSet<String> is_done){
		ArrayList<String> arr=new ArrayList<String>();
		String point_after=core_content.split("->")[1].split("\\.")[1].trim().split(" ")[0];
		
		if(!non_terminal_set.contains(point_after)) {
			arr.add(core_content);
			terminal_set.add(point_after);
		}
		else {
			arr.add(core_content);
			ArrayList<String> product=find_begin_grammar(point_after);
			for(String p:product) {
				String tmp_str=point_after+"->."+p;
				if(!is_done.contains(tmp_str)) {
					is_done.add(tmp_str);
					arr.addAll(find_closure(tmp_str,is_done));
				}
			}
		}
		return arr;
	}
	
	private String change_point_location(String str) {
		String[] str_tmp_set=str.split("\\.");
		String[] right_point=str_tmp_set[1].trim().split(" ");
		String add_str=str_tmp_set[0];
		add_str+=" "+right_point[0]+".";
		if(right_point.length>1) {
			for(int i=1;i<right_point.length;i++) {
				add_str+=" ";
				add_str+=right_point[i];
			}
		}
		String s=add_str.split("->")[0];
		s+="->";
		s+=add_str.split("->")[1].trim();
		return s;
	}
	
	private void createDFA() {
		ArrayList<String> str_arr=new ArrayList<String>();
		Queue<Integer> dfs_queue=new ArrayDeque<Integer>();
		HashSet<Integer> visited=new HashSet<Integer>();
		//init
		
		
		dfs_queue.add(0);
		while(!dfs_queue.isEmpty()) {
			Integer state=dfs_queue.poll();
			if(visited.contains(state))continue;
			else {
				visited.add(state);
			}
			//Ìí¼Ó¾ä×Ó
			DFA_content tmp_dfa_content=DFA_LR.index_of_node.get(state);
			String core_content=tmp_dfa_content.getCore();
			if(core_content.trim().charAt(core_content.length()-1)=='.')continue;
			ArrayList<String> ret_str_arr=find_closure(core_content,new HashSet<String>());
			for(String str:ret_str_arr) {
				if(!str.equals(core_content)) {
					tmp_dfa_content.addContent(str);
				}
			}
			
			for(String str:tmp_dfa_content.getContent()) {
				String tmp_str=change_point_location(str);
				if(find_core.containsKey(tmp_str)) {
					DFA_LR.put_map_node(state, find_core.get(tmp_str), str.split("->")[1].split("\\.")[1].trim().split(" ")[0]);
				}
				else {
					DFA_LR.put_map_node(state, ++state_num, str.split("->")[1].split("\\.")[1].trim().split(" ")[0]);
					DFA_LR.put_map_content(state_num,change_point_location(str));
					dfs_queue.add(state_num);
					find_core.put(tmp_str, state_num);
				}
			}
		}
	}
	
	private void init(String[] str,String gra_tmp_begin) {
		int len=str.length;
		for(int i=0;i<len;i++) {
			String begin=str[i].split("->")[0];
			String[] production=str[i].split("->")[1].split("\\|");
			addGrammar(begin,production);
			non_terminal_set.add(begin);
		}
		non_terminal_set.add(grammar_begin);
		grammar.put(0, grammar_begin+"->"+gra_tmp_begin);
		DFA_LR.put_map_content(0, grammar_begin+"->."+gra_tmp_begin);
		find_core.put(grammar_begin+"->."+gra_tmp_begin, 0);
		//System.out.println(DFA_LR);
	}
	
	private void addGrammar(String begin,String[] production) {
		for(int i=0;i<production.length;i++) {
			grammar.put(++num_of_production, begin+"->"+production[i]);
		}
	}
	
	
	public static void main(String[] args) {
		String str[]=new String[3];
		str[0]="E->E + T|T";
		str[1]="T->T * F|F";
		str[2]="F->( E )|id";
		new LR_Init(str,"E");
	}
}
