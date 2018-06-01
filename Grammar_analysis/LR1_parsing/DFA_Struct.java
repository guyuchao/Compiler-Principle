package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DFA_Struct {
	HashMap<Integer,DFA_content> index_of_node=new HashMap<Integer,DFA_content>();
	HashMap<Integer,HashSet<DFA_NODE>> dfa_map=new HashMap<Integer,HashSet<DFA_NODE>>();
	public void put_map_node(Integer begin,Integer end,String dis) {
		if(!dfa_map.containsKey(begin)) {
			dfa_map.put(begin, new HashSet<DFA_NODE>());
		}
		dfa_map.get(begin).add(new DFA_NODE(end,dis));
	}
	public void put_map_core(Integer index,content_struct con) {
		if(!index_of_node.containsKey(index)) {
			index_of_node.put(index, new DFA_content());
		}
		//Õ˘index¿Ô√ÊÃÌº”core

		index_of_node.get(index).addCore(con);
		
	}
	public String toString() {
		return index_of_node.toString()+"\n"+dfa_map.toString();
		
	}
}
class DFA_content{
	private ArrayList<content_struct> core=new ArrayList<content_struct>();
	public ArrayList<content_struct> getCore() {
		return core;
	}
	public void addCore(content_struct core) {
		this.core.add(core);
	}
	private ArrayList<content_struct> content=new ArrayList<content_struct>();
	public ArrayList<content_struct> getContent() {
		ArrayList<content_struct> ret=new ArrayList<content_struct>();
		ret.addAll(core);
		ret.addAll(content);
		return ret;
	}
	public void addContent(content_struct str) {
		content.add(str);
	}
	public String toString() {
		return "core:"+core.toString()+"   content:"+content.toString();
	}
	public Integer coreSize() {
		return core.size();
	}
}

class DFA_NODE{
	Integer end;
	String dis;
	public DFA_NODE(Integer end,String dis) {
		this.end=end;
		this.dis=dis;
	}
	public String toString() {
		return "end: "+end+"  dis: "+dis;
	}
}
class content_struct implements Comparable<content_struct>{
	String content;
	String search_str;
	public content_struct(String content,String search_str) {
		this.content=content;
		this.search_str=search_str;
	}
	public String toString() {
		return content+"   ,   "+search_str;
	}
	
	@Override
	public int compareTo(content_struct o) {
		// TODO Auto-generated method stub
		if(!this.content.equals(o.content))return this.content.compareTo(o.content);
		else {return this.search_str.compareTo(o.search_str);}
	}
}