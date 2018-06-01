package demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Left_factoring_extract {
	private Map<String,TreeSet<String>> grammar;
	private Map<Integer,String> index_map; 
	private int cnt_derivation;
	public Left_factoring_extract(Left_recursion_removal lrr) {
		grammar=lrr.getGrammar();
		index_map=lrr.getIndex_map();
		cnt_derivation=lrr.getCnt_derivation();
		this.general_common_factoring_extract();
		System.out.println(grammar.toString());
		//
		
	}
	private String common_prefix(String str1,String str2) {
		String[] str_1=str1.split(" ");
		String[] str_2=str2.split(" ");
		int cnt=0;
		String ret_str="";
		while(cnt<str_1.length&&cnt<str_2.length&&str_1[cnt].equals(str_2[cnt])) {
			ret_str+=str_1[cnt]+" ";
			cnt++;
		}
		return ret_str.trim();
	}
	
	private ArrayList<String> Set_to_Array(TreeSet<String> str_set){
		ArrayList<String> ret_array=new ArrayList<String>();
		for(String item:str_set) {
			ret_array.add(item);
		}
		return ret_array;
	}
	
	private String find_max_direct_common_factoring(ArrayList<String> deri_array) {
		HashSet<String> common_prefix_set=new HashSet<String>();
		Map<String,Integer> count_map=new HashMap<String,Integer>();
		int len=deri_array.size();
		for(int i=0;i<len-1;i++) {
			for(int j=i+1;j<len;j++) {
				String tmp=common_prefix(deri_array.get(i).trim(),deri_array.get(j).trim());
				if(!tmp.equals("")) {
					common_prefix_set.add(tmp);
				}
			}
		}
		if(common_prefix_set.isEmpty())return null;//没有common prefix
		
		for(String item:common_prefix_set) {
			for(String str:deri_array) {
				if(str.startsWith(item)==true) {
					if(!count_map.containsKey(item)) {
						count_map.put(item, 1);
					}
					else {
						Integer tmp=count_map.get(item);
						count_map.remove(item);
						count_map.put(item, tmp+1);
					}
				}
			}
		}//统计前缀出现次数
		int max=-1;
		String max_item="";
		for(String item:count_map.keySet()) {
			if(count_map.get(item)>max) {
				max=count_map.get(item);
				max_item=item;
			}
		}
		return max_item;
	}
	private TreeSet<String> replace(String str) {
		String need_replace=str.split(" ")[0];
		String remain=str.substring(need_replace.length());
		TreeSet<String> str_set=grammar.get(need_replace);
		TreeSet<String> ret_set=new TreeSet<String>();
		for(String item:str_set) {
			ret_set.add(item+remain);
		}
		return ret_set;
	}
	private boolean replace_non_terminal(String begin) {//替换左部非终结符
		boolean change=false;
		boolean have_replace_grammar=false;
		do {
			change=false;
			TreeSet<String> deri_set=grammar.get(begin);
			TreeSet<String> new_deri_set=(TreeSet<String>) deri_set.clone();
			for(String str:deri_set) {
				String new_str="";
				String tmp_str=str.split(" ")[0];
				if(grammar.containsKey(tmp_str)) {
					new_deri_set.remove(str);
					new_deri_set.addAll(replace(str));
					change=true;
					break;
				}
			}
			if(change==true) {
				grammar.remove(begin);
				grammar.put(begin, new_deri_set);
				have_replace_grammar=true;
			}
		}while(change);
		return have_replace_grammar;
	}
	/*
	private boolean isPrefix(String cmp,String need_to_cmp) {
		String[] cmp_s=cmp.split(" ");
		String[] need_to_cmp_s=need_to_cmp.split(" ");
		int cnt=0;
		while(cnt<cmp_s.length&&(cmp_s[cnt]==need_to_cmp_s[cnt])) {
			cnt++;
		}	
		if(cnt==cmp_s.length)return true;
		else {
			return false;
		}
	}*/
	
	private boolean direct_common_factoring_extract(String begin) {//直接左因子提取
		String need_to_extarct=find_max_direct_common_factoring(Set_to_Array(grammar.get(begin)));
		if(need_to_extarct!=null) {
			TreeSet<String> set=grammar.get(begin);
			TreeSet<String> new_set=new TreeSet<String>();
			String tmp_new_begin=begin+"'";
			TreeSet<String> tmp_new_begin_set=new TreeSet<String>();
			for(String item:set) {
				if(item.startsWith(need_to_extarct)==true) {
					new_set.add(need_to_extarct+" "+tmp_new_begin);
					if(item.substring(need_to_extarct.length()).equals("")) {
						tmp_new_begin_set.add("#");
					}
					else {
						tmp_new_begin_set.add(item.substring(need_to_extarct.length()));
					}
				}
				else {
					new_set.add(item);
				}
			}
			grammar.remove(begin);
			grammar.put(begin, new_set);
			index_map.put(++cnt_derivation, tmp_new_begin);
			grammar.put(tmp_new_begin, tmp_new_begin_set);
			return true;
		}
		return false;
	}
	
	private void general_common_factoring_extract() {

		boolean is_change=false;
		do {
			is_change=false;
			Set<String> tmp_begin_set=grammar.keySet();
			for(String begin:tmp_begin_set) {
				if(replace_non_terminal(begin)==true) {is_change=true;break;}
				if(direct_common_factoring_extract(begin)==true) {is_change=true;break;}
			}
		}while(is_change);
	}
	
	public static void main(String[] args) {
		String[] str=new String[3];
		//str[0]="if_stmt->if ( exp ) statement | if ( exp ) statement else statement";
		//str[0]="exp->term + exp|term";
		/*str[0]="assign_stmt->identifier = exp";
		str[1]="call_stmt->identifier ( exp_list )";
		str[2]="statement->assign_stmt|call_stmt|other";*/
		//str[1]="call_stmt->identifier ( exp_list )";
		//str[2]="statement->assign_stmt|call_stmt|other";
		//new Left_recursion_removal(str);
		new Left_factoring_extract(new Left_recursion_removal(str));		
	}
}
