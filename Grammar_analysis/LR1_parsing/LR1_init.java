package demo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.TreeMap;

public class LR1_init {
	private TreeMap<Integer,String> grammar=new TreeMap<Integer,String>();
	private TreeMap<content_struct,Integer> find_core=new TreeMap<content_struct,Integer>();
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

	public TreeMap<Integer, String> getGrammar() {
		return grammar;
	}

	public LR1_init(String[] str,String gra_begin) {
		init(str,gra_begin);
		createDFA();
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
	
	private ArrayList<content_struct> find_closure(content_struct core_content,HashSet<content_struct> is_done){
		ArrayList<content_struct> arr=new ArrayList<content_struct>();
		content_struct point_after=new content_struct(core_content.content.split("\\.")[1].trim().split(" ")[0],core_content.search_str);
		if(non_terminal_set.contains(point_after.content)) {
			ArrayList<String> product=find_begin_grammar(point_after.content);
			String search_notation=point_after.search_str;
			for(String p:product) {
				String p_plus_search=(core_content.content+" "+search_notation).split("\\.")[1].trim().split(" ")[1];
			
				String tmp_str=point_after.content+"->."+p;
				String new_nonterminal=p.split(" ")[0];
				if(non_terminal_set.contains(new_nonterminal)) {
					if(!is_done.contains(new content_struct(tmp_str,p_plus_search))) {
						is_done.add(new content_struct(tmp_str,p_plus_search));
						arr.add(new content_struct(tmp_str,p_plus_search));
						arr.addAll(find_closure(new content_struct(tmp_str,p_plus_search),is_done));
					}
				}
				else {
					if(!is_done.contains(new content_struct(tmp_str,p_plus_search))) {
						is_done.add(new content_struct(tmp_str,p_plus_search));
						arr.add(new content_struct(tmp_str,p_plus_search));
					}
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
			//添加句子
			DFA_content tmp_dfa_content=DFA_LR.index_of_node.get(state);
			ArrayList<content_struct> core_content=tmp_dfa_content.getCore();
			ArrayList<content_struct> ret_str_arr=new ArrayList<content_struct>();
			for(content_struct each:core_content) {
				if(each.content.trim().charAt(each.content.length()-1)=='.')continue;
				ret_str_arr.add(each);
				ret_str_arr.addAll(find_closure(each,new HashSet<content_struct>()));
			}
			for(content_struct str:ret_str_arr) {//加入到dfa_content
				if(!core_content.contains(str)) {
					tmp_dfa_content.addContent(str);
				}
			}
			TreeMap<String,Integer> via_str_to_state=new TreeMap<String,Integer>();
			for(content_struct str:tmp_dfa_content.getContent()) {
				if(str.content.trim().charAt(str.content.length()-1)=='.')continue;
				String tmp_str=change_point_location(str.content);
				String via=str.content.split("->")[1].split("\\.")[1].trim().split(" ")[0];
				if(find_core.containsKey(new content_struct(tmp_str,str.search_str))) {
					DFA_LR.put_map_node(state, find_core.get(new content_struct(tmp_str,str.search_str)), via);
				}
				else {
					if(!via_str_to_state.containsKey(via)) {
						DFA_LR.put_map_node(state, ++state_num, via);
						via_str_to_state.put(via, state_num);
						DFA_LR.put_map_core(state_num,new content_struct(tmp_str,str.search_str));
						dfs_queue.add(state_num);
						find_core.put(new content_struct(tmp_str,str.search_str), state_num);
					}
					else {
						DFA_LR.put_map_core(via_str_to_state.get(via),new content_struct(tmp_str,str.search_str));
					}
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
		for(Integer key:grammar.keySet()) {
			String[] product_ele=grammar.get(key).split("->")[1].split(" ");
			for(int j=0;j<product_ele.length;j++) {
				if(!non_terminal_set.contains(product_ele[j])) {
					terminal_set.add(product_ele[j]);
				}
			}
		}
		non_terminal_set.add(grammar_begin);
		grammar.put(0, grammar_begin+"->"+gra_tmp_begin);
		DFA_LR.put_map_core(0, new content_struct(grammar_begin+"->."+gra_tmp_begin,"$"));
		find_core.put(new content_struct(grammar_begin+"->."+gra_tmp_begin,"$"), 0);
	}
	
	private void addGrammar(String begin,String[] production) {
		for(int i=0;i<production.length;i++) {
			grammar.put(++num_of_production, begin+"->"+production[i]);
		}
	}
	
	
	public static void main(String[] args) {
		String str[]=new String[1];
		/*str[0]="S->id|V := E";
		str[1]="V->id";
		str[2]="E->V|n";*/
		str[0]="A->( A )|a";
		new LR1_init(str,"A");
	}
}
