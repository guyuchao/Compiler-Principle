package demo;

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
	private int index=0;
	public String getStart_symbol() {
		return start_symbol;
	}
	private Set<String> non_terminal_set;
	private String start_symbol;
	private Map<String,HashSet<String>> FIRST;
	private Map<String,HashSet<String>> FOLLOW;
	public First_Follow_Gen(Map<Integer,String> index_of_generation,Set<String> non_terminal_set) {
		this.start_symbol="S'";
		this.index_of_generation=index_of_generation;
		this.non_terminal_set=non_terminal_set;
		this.index=index_of_generation.size();
		First_set_generate();
		Follow_set_generation();
	}

	public Map<String, HashSet<String>> getFOLLOW() {
		return FOLLOW;
	}
	private boolean judge_change(String begin,String generation_left_word) {
		HashSet<String> left_first=FIRST.get(generation_left_word);
		if(left_first.isEmpty()) {
			return false;//还未做
		}
		else {//已做
			//Integer size=FIRST.get(begin).size();
			HashSet<String> begin_first=(HashSet<String>) FIRST.get(begin).clone();
			for(String fs:left_first) {
				if(!is_in_first(begin,fs)) {
					begin_first.add(fs);
					FIRST.remove(begin);
					FIRST.put(begin, begin_first);
					return true;
				}
			}
			return false;
		}
	}
	private void First_set_generate() {
		FIRST=new HashMap<String,HashSet<String>>();
		for(String str:non_terminal_set){
			HashSet<String> tmp=new HashSet<String>();
			FIRST.put(str, tmp);//初始化first集
		}
		boolean is_change=false;
		do {
			is_change=false;
			
			for(int i=0;i<index;i++) {
				String begin=index_of_generation.get(i).split("-")[0];//non-terminal
				String derivation=index_of_generation.get(i).substring(index_of_generation.get(i).indexOf("->")+2).trim();
				is_change=add_first(begin,derivation);
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
		Set<String> set=FIRST.get(begin);
		for(String fs:set) {
			if(fs.equals(generation_left_word)) {
				return true;
			}
		}
		return false;
	}
	private boolean add_first(String begin,String derivation) {
		String generation_leftest_word=derivation.split(" ")[0];//一个产生式按内容分割最左边的
		if(FIRST.containsKey(generation_leftest_word)) {//非终结符
			boolean change=judge_change(begin,generation_leftest_word);
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
				FIRST.get(begin).add(generation_leftest_word);
				return true;
			}
		}
	}
	//以上是first集
	//以下是follow集
	private boolean is_in_follow(String begin,String generation_left_word) {
		Set<String> set=FOLLOW.get(begin);
		
		for(String fs:set) {
			if(fs.equals(generation_left_word)) {
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
		FOLLOW=new HashMap<String,HashSet<String>>();
		for(String str:non_terminal_set){
			HashSet<String> tmp=new HashSet<String>();
			if(str.equals(start_symbol))tmp.add("$");			
			FOLLOW.put(str, tmp);
		}
		//初始化
		boolean is_change=false;
		do {
			is_change=false;
			for(int i=0;i<index;i++) {
				String begin=index_of_generation.get(i).split("-")[0];//non-terminal
				String derivation=index_of_generation.get(i).substring(index_of_generation.get(i).indexOf("->")+2).trim();//generation
				is_change=add_follow(begin,derivation);
				if(is_change==false) {
					continue;
				}
				else {
					break;
				}
			}
		}while(is_change);
		
	}
	private boolean judge_follow_change_add_first(String begin,String add) {
		boolean ret=false;
		if(non_terminal_set.contains(add)) {
			for(String ffs:FIRST.get(add)) {
				if(ffs.equals("#"))continue;
				if(!is_in_follow(begin,ffs)) {
					FOLLOW.get(begin).add(ffs);
					ret=true;
				}
			}
			return ret;
		}
		else {
			if(!is_in_follow(begin,add)) {
				FOLLOW.get(begin).add(add);
				return true;
			}
			else {
				return false;
			}
		}		
	}
	private boolean judge_follow_change_add_follow(String begin,String add) {
		boolean ret=false;
		if(non_terminal_set.contains(add)) {
			for(String ffs:FOLLOW.get(add)) {
				if(ffs.equals("#"))continue;
				if(!is_in_follow(begin,ffs)) {
					FOLLOW.get(begin).add(ffs);
					ret=true;
				}
			}
			return ret;
		}
		else {
			if(!is_in_follow(begin,add)) {
				FOLLOW.get(begin).add(add);
				return true;
			}
			else {
				return false;
			}
		}		
	}
	private boolean add_follow(String begin,String derivation) {
		String[] derivation_set=derivation.split(" ");
		for(int i=0;i<derivation_set.length-1;i++) {
			if(non_terminal_set.contains(derivation_set[i])) {
				if(judge_follow_change_add_first(derivation_set[i],derivation_set[i+1])) {
					return true;
				}
				else {
					if(has_empty_in_first(derivation_set[i+1])) {
						int j=i+1;
						while(j<derivation_set.length-1) {
							if(has_empty_in_first(derivation_set[j])) {
								if(judge_follow_change_add_first(derivation_set[i],derivation_set[j])) {
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
							if(judge_follow_change_add_follow(derivation_set[i],begin)) {
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
		
		if(non_terminal_set.contains(derivation_set[derivation_set.length-1])&&judge_follow_change_add_follow(derivation_set[derivation_set.length-1],begin)) {
			return true;
		}
		return false;
	}
}
