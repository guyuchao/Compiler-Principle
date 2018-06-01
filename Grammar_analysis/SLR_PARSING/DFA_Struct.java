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
	public void put_map_core(Integer index,String con) {
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
	private ArrayList<String> core=new ArrayList<String>();
	public ArrayList<String> getCore() {
		return core;
	}
	public void addCore(String core) {
		this.core.add(core);
	}
	private ArrayList<String> content=new ArrayList<String>();
	public ArrayList<String> getContent() {
		ArrayList<String> ret=new ArrayList<String>();
		ret.addAll(core);
		ret.addAll(content);
		return ret;
	}
	public void addContent(String str) {
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