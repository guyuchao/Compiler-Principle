package demo1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Left_recursion_removal {
	
	private Map<String,TreeSet<String>> grammar;
	private Map<Integer,String> index_map; 
	private int cnt_derivation;
	
	public Map<String, TreeSet<String>> getGrammar() {
		return grammar;
	}

	public Map<Integer, String> getIndex_map() {
		return index_map;
	}

	public int getCnt_derivation() {
		return cnt_derivation;
	}

	private void general_left_recursion_removal() {
		int tmp_cnt=cnt_derivation;
		for(int i=1;i<=tmp_cnt;i++) {
			direct_left_recursion_removal(index_map.get(i));
			for(int j=i+1;j<=tmp_cnt;j++) {
				replace(index_map.get(j),index_map.get(i));
			}
		}
	}
	
	private void replace(String wait_for_replace_begin,String replace_non_t) {
		//需要替换的为replace_non_t
		//
		TreeSet<String> wait_for_replace_set=grammar.get(wait_for_replace_begin);
		TreeSet<String> replace_set=grammar.get(replace_non_t);
		TreeSet<String> tmp_set=new TreeSet<String>();
		for(String s:wait_for_replace_set) {
			int index=s.indexOf(replace_non_t);
			if(index==0) {
				for(String i:replace_set) {
					tmp_set.add((s.substring(0,index)+i+s.substring(index+replace_non_t.length())));
				}
			}
			else {
				tmp_set.add(s);
			}
		}
		grammar.remove(wait_for_replace_begin);
		grammar.put(wait_for_replace_begin, tmp_set);
	}
	
	private void setGrammar(String[] str) {
		cnt_derivation=0;
		grammar=new TreeMap<String,TreeSet<String>>();
		index_map=new TreeMap<Integer,String>();
		for(String s:str) {
			int len=s.length();
			String tmp_begin="";
			String tmp_else="";
			for(int i=0;i<len;i++) {
				if(s.charAt(i)!='-') {
					tmp_begin+=s.charAt(i);
				}
				else {
					tmp_else=s.substring(i+2);
					break;
				}
			}
			String[] tmp=tmp_else.split("\\|");
			TreeSet<String> tmp_set=new TreeSet<String>();
			for(String tmp_s:tmp) {
				tmp_set.add(tmp_s.trim());
			}
			index_map.put(++cnt_derivation, tmp_begin);
			grammar.put(tmp_begin, tmp_set);
		}
	}
	
	private void direct_left_recursion_removal(String begin) {
		//修改原grammar map
		
		TreeSet<String> str_set=grammar.get(begin);
		TreeSet<String> revise_set_tmpbegin=new TreeSet<String>();
		TreeSet<String> revise_set_begin=new TreeSet<String>();
		//新修改的set
		String tmp_begin=begin+"'";
		for(String s:str_set) {
			if(s.startsWith(begin)==true) {//前缀是begin
				int index=s.indexOf(begin)+begin.length();
				revise_set_tmpbegin.add((s.substring(index)+" "+tmp_begin));
			}
			else {
				revise_set_begin.add((s+" "+tmp_begin));
			}
		}
		if(!revise_set_tmpbegin.isEmpty()) {
			revise_set_tmpbegin.add("#");
			grammar.remove(begin);
			grammar.put(begin, revise_set_begin);
			index_map.put(++cnt_derivation, tmp_begin);
			grammar.put(tmp_begin, revise_set_tmpbegin);
		}
	}
	private ArrayList<String> getKeyArray(){
		ArrayList<String> arr=new ArrayList<String>();
		Set<String> key_set=grammar.keySet();
		for(String key:key_set) {
			arr.add(key);
		}
		return arr;
	}
	private void transvers() {
		for(String key:grammar.keySet()) {
			System.out.println(key+grammar.get(key).toString());
		}
		System.out.println("");
	}
	public Left_recursion_removal(String[] str) {
		//传入文法String数组
		this.setGrammar(str);
		//transvers();
		//System.out.println(getKeyArray().toString());
		this.general_left_recursion_removal();
		//this.transvers();
	}
	
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		
		String[] str=new String[5];
		str[0]="exp->exp addop term | term";
		str[1]="addop->+ | -";
		str[2]="term->term mulop factor|factor";
		str[3]="mulop->*";
		str[4]="factor->( exp ) | number";
		//str[2]="C->Ac|c";
		new Left_recursion_removal(str);
	}

}
