package demo1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class First_Follow_Gen {
	private Map<Integer,String> index_of_generation=new HashMap<Integer,String>();
	public Map<Integer, String> getIndex_of_generation() {
		return index_of_generation;
	}
	public HashMap<String, HashMap<String, Integer>> getLL1() {
		return LL1;
	}
	private int index=0;
	public Set<String> getNon_terminal_set() {
		return non_terminal_set;
	}
	public String getStart_symbol() {
		return start_symbol;
	}
	private Set<String> non_terminal_set;
	private String start_symbol;
	private Map<String,HashSet<FIRST_FOLLOW_struct>> FIRST;
	private Map<String,HashSet<FIRST_FOLLOW_struct>> FOLLOW;
	private HashMap<String,HashMap<String,Integer>>LL1;
	public First_Follow_Gen(Left_recursion_removal lrr,String start_symbol) {
		this.start_symbol=start_symbol;
		Map<String,TreeSet<String>> grammar=lrr.getGrammar();
		non_terminal_set=grammar.keySet();
		initialize(grammar);
		First_set_generate();
		Follow_set_generation();
		LL1_generation();
		System.out.println(index_of_generation);
		System.out.println(LL1.toString());
	}
	private void LL1_add(HashSet<FIRST_FOLLOW_struct> set,HashMap<String,Integer> tmp_map) {
		for(FIRST_FOLLOW_struct item:set) {
			if(item.first_token.equals("#"))continue;
			if(!tmp_map.containsKey(item.first_token)) {
				tmp_map.put(item.first_token, item.which);
			}
		}
	}
	private void LL1_generation() {
		LL1=new HashMap<String,HashMap<String,Integer>>();
		for(String begin:FIRST.keySet()) {
			HashMap<String,Integer> tmp_map=new HashMap<String,Integer>();
			LL1_add(FIRST.get(begin),tmp_map);
			if(has_empty_in_first(begin)) {
				LL1_add(FOLLOW.get(begin),tmp_map);
			}
			LL1.put(begin, tmp_map);
		}
	}
	private boolean judge_change(String begin,String generation_left_word,int which) {
		HashSet<FIRST_FOLLOW_struct> left_first=FIRST.get(generation_left_word);
		if(left_first.isEmpty()) {
			return false;//还未做
		}
		else {//已做
			//Integer size=FIRST.get(begin).size();
			HashSet<FIRST_FOLLOW_struct> begin_first=(HashSet<FIRST_FOLLOW_struct>) FIRST.get(begin).clone();
			for(FIRST_FOLLOW_struct fs:left_first) {
				if(!is_in_first(begin,fs.first_token)) {
					begin_first.add(new FIRST_FOLLOW_struct(fs.first_token,which));
					FIRST.remove(begin);
					FIRST.put(begin, begin_first);
					return true;
				}
			}
			return false;
		}
	}
	private void First_set_generate() {
		FIRST=new HashMap();
		for(String str:non_terminal_set){
			HashSet<FIRST_FOLLOW_struct> tmp=new HashSet<FIRST_FOLLOW_struct>();
			FIRST.put(str, tmp);//初始化first集
		}
		boolean is_change=false;
		do {
			is_change=false;
			
			for(int i=1;i<=index;i++) {
				String begin=index_of_generation.get(i).split("-")[0];//non-terminal
				String derivation=index_of_generation.get(i).substring(index_of_generation.get(i).indexOf("->")+2).trim();
				is_change=add_first(begin,derivation,i);
				if(is_change==true) {
					//修改，重新循环
					break;
				}
				else {
					continue;
				}
			}
		}while(is_change);
	}
	private boolean is_in_first(String begin,String generation_left_word) {
		Set<FIRST_FOLLOW_struct> set=FIRST.get(begin);
		for(FIRST_FOLLOW_struct fs:set) {
			if(fs.first_token.equals(generation_left_word)) {
				return true;
			}
		}
		return false;
	}
	private boolean add_first(String begin,String derivation,int which) {
		String generation_leftest_word=derivation.split(" ")[0];//一个产生式按内容分割最左边的
		if(FIRST.containsKey(generation_leftest_word)) {//非终结符
			boolean change=judge_change(begin,generation_leftest_word,which);
			if(change==false) {
				//未改变
				return false;
			}
			else {
				return true;
			}
		}
		else {//终结符
			if(is_in_first(begin,generation_leftest_word)) {
				return false;//已存在first集里
			}
			else {
				FIRST.get(begin).add(new FIRST_FOLLOW_struct(generation_leftest_word,which));
				return true;
			}
		}
	}
	private void initialize(Map<String,TreeSet<String>> grammar) {
		for(String str:grammar.keySet()) {
			for(String str2:grammar.get(str)) {
				index_of_generation.put(++index, str+"->"+str2);
			}
		}
	}
	//以上是first集
	//以下是follow集
	private boolean is_in_follow(String begin,String generation_left_word) {
		Set<FIRST_FOLLOW_struct> set=FOLLOW.get(begin);
		
		for(FIRST_FOLLOW_struct fs:set) {
			if(fs.first_token.equals(generation_left_word)) {
				return true;
			}
		}
		return false;
	}
	private boolean has_empty_in_first(String str) {
		if(!non_terminal_set.contains(str))return false;
		return is_in_first(str,"#");
	}
	private void Follow_set_generation() {
		 //文法的开始符号不直接推出终结符
		FOLLOW=new HashMap<String,HashSet<FIRST_FOLLOW_struct>>();
		for(String str:non_terminal_set){
			HashSet<FIRST_FOLLOW_struct> tmp=new HashSet<FIRST_FOLLOW_struct>();
			if(str.equals(start_symbol))tmp.add(new FIRST_FOLLOW_struct("$",0));			
			FOLLOW.put(str, tmp);
		}
		//初始化
		boolean is_change=false;
		do {
			is_change=false;
			for(int i=1;i<=index;i++) {
				String begin=index_of_generation.get(i).split("-")[0];//non-terminal
				String derivation=index_of_generation.get(i).substring(index_of_generation.get(i).indexOf("->")+2).trim();//generation
				is_change=add_follow(begin,derivation,i);
				if(is_change==false) {
					continue;
				}
				else {
					break;
				}
			}
		}while(is_change);
		
	}
	private boolean judge_follow_change_add_first(String begin,String add,int which) {
		boolean ret=false;
		if(non_terminal_set.contains(add)) {
			for(FIRST_FOLLOW_struct ffs:FIRST.get(add)) {
				if(ffs.first_token.equals("#"))continue;
				if(!is_in_follow(begin,ffs.first_token)) {
					FOLLOW.get(begin).add(new FIRST_FOLLOW_struct(ffs.first_token,which));
					ret=true;
				}
			}
			return ret;
		}
		else {
			if(!is_in_follow(begin,add)) {
				FOLLOW.get(begin).add(new FIRST_FOLLOW_struct(add,which));
				return true;
			}
			else {
				return false;
			}
		}		
	}
	private boolean judge_follow_change_add_follow(String begin,String add,int which) {
		boolean ret=false;
		if(non_terminal_set.contains(add)) {
			for(FIRST_FOLLOW_struct ffs:FOLLOW.get(add)) {
				if(ffs.first_token.equals("#"))continue;
				if(!is_in_follow(begin,ffs.first_token)) {
					FOLLOW.get(begin).add(new FIRST_FOLLOW_struct(ffs.first_token,which));
					ret=true;
				}
			}
			return ret;
		}
		else {
			if(!is_in_follow(begin,add)) {
				FOLLOW.get(begin).add(new FIRST_FOLLOW_struct(add,which));
				return true;
			}
			else {
				return false;
			}
		}		
	}
	private int find_e_derivation(String derivation) {
		for(int i=1;i<=index;i++) {
			if((index_of_generation.get(i).split("->")[1].trim().equals(derivation.split("->")[1].trim()))&&(index_of_generation.get(i).split("->")[0].trim().equals(derivation.split("->")[0].trim()))) {
				return i;
			}
		}
		return -1;
	}
	private boolean add_follow(String begin,String derivation,int which) {
		String[] derivation_set=derivation.split(" ");
		for(int i=0;i<derivation_set.length-1;i++) {
			if(non_terminal_set.contains(derivation_set[i])) {
				if(judge_follow_change_add_first(derivation_set[i],derivation_set[i+1],which)) {
					return true;
				}
				else {
					if(has_empty_in_first(derivation_set[i+1])) {
						int j=i+1;
						while(j<derivation_set.length-1) {
							if(has_empty_in_first(derivation_set[j])) {
								if(judge_follow_change_add_first(derivation_set[i],derivation_set[j],which)) {
									return true;
								}
								else {
									j++;
								}
							}
							else {
								break;
							}
						}
						if(j==derivation_set.length-1) {
							if(judge_follow_change_add_follow(derivation_set[i],begin,find_e_derivation(derivation_set[derivation_set.length-1]+"->#"))) {
								return true;
							}
						}
					}
				}
			}
			else {
				continue;
			}
		}
		
		if(non_terminal_set.contains(derivation_set[derivation_set.length-1])&&judge_follow_change_add_follow(derivation_set[derivation_set.length-1],begin,find_e_derivation(derivation_set[derivation_set.length-1]+"->#"))) {
			return true;
		}
		return false;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] str=new String[4];
		/*str[0]="exp->exp addop term | term";
		str[1]="addop->+ | -";
		str[2]="term->term mulop factor|factor";
		str[3]="mulop->*";
		str[4]="factor->( exp ) | number";*/
		
		str[0]="statement->if_stmt|other";
		str[1]="if_stmt->if ( exp ) statement else_part";
		str[2]="else_part->else statement|#";
		str[3]="exp->0|1";
		
		/*str[0]="stmt_sequence->stmt stmt_seq";
		str[1]="stmt_seq->; stmt_sequence|#";
		str[2]="stmt->s";*/
		
		new First_Follow_Gen(new Left_recursion_removal(str),"statement");
	}

}

class FIRST_FOLLOW_struct{
	String first_token;
	int which;
	public FIRST_FOLLOW_struct(String first_token,int which) {
		this.first_token=first_token;
		this.which=which;
	}
	public String toString() {
		return this.first_token+","+this.which+" ";
	}
}