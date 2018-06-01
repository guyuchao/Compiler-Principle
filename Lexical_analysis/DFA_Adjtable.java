package demo2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DFA_Adjtable {
	HashMap<String,Set<DFA_EdgeNode>> mymap=new HashMap<String,Set<DFA_EdgeNode>>();
	void insert(String begin,String end,String dis){
		if(!mymap.containsKey(begin)){
			Set<DFA_EdgeNode> set=new HashSet<DFA_EdgeNode>();
			set.add(new DFA_EdgeNode(end,dis));
			mymap.put(begin, set);
		}
		else{
			Set<DFA_EdgeNode> set=mymap.get(begin);
			set.add(new DFA_EdgeNode(end,dis));
		}
	}
	void insert(String begin,Set<DFA_EdgeNode> value){
		mymap.put(begin, value);
	}
	void transvers(){
		Iterator<String> it=mymap.keySet().iterator();
		while(it.hasNext()){
			String node=it.next();
			System.out.print(node+"   :   ");
			Set<DFA_EdgeNode> set=mymap.get(node);
			Iterator<DFA_EdgeNode> it2=set.iterator();
			while(it2.hasNext()){
				DFA_EdgeNode tmp3=it2.next();
				System.out.print(tmp3.end+"("+tmp3.dis+")  ");
			}
			System.out.print("\n");
		}
	}
}
class DFA_EdgeNode{
	String end;
	String dis;
	String acceptable=null;
	public DFA_EdgeNode(String end,String dis){
		this.end=end;
		this.dis=dis;
	}
}
