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
	public void put_map_content(Integer index,String con) {
		if(!index_of_node.containsKey(index)) {
			index_of_node.put(index, new DFA_content());
		}
		//往index里面添加内容
		if(index_of_node.get(index).getCore()==null) {
			index_of_node.get(index).setCore(con);
		}
		else {
			index_of_node.get(index).addContent(con);
		}
	}
	public String toString() {
		return index_of_node.toString()+"\n"+dfa_map.toString();
		
	}
}
class DFA_content{
	private String core=null;
	public String getCore() {
		return core;
	}
	public void setCore(String core) {
		this.core = core;
	}
	private ArrayList<String> content=new ArrayList<String>();
	public ArrayList<String> getContent() {
		ArrayList<String> ret=(ArrayList<String>) content.clone();
		ret.add(core);
		return ret;
	}
	public void addContent(String str) {
		content.add(str);
	}
	public String toString() {
		return "core:"+core+"   content:"+content.toString();
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