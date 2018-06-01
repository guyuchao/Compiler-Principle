package demo2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NFA_Adjtable {
	HashMap<Integer,Set<NFA_EdgeNode>> mymap=new HashMap<Integer,Set<NFA_EdgeNode>>();//邻接表
	void insert(Integer begin,Integer end,String dis){
		if(!mymap.containsKey(begin)){
			Set<NFA_EdgeNode> set=new HashSet<NFA_EdgeNode>();
			set.add(new NFA_EdgeNode(end,dis));
			mymap.put(begin, set);
		}
		else{
			Set<NFA_EdgeNode> set=mymap.get(begin);
			set.add(new NFA_EdgeNode(end,dis));
		}
	}
	void transvers(){
		Iterator<Integer> it=mymap.keySet().iterator();
		while(it.hasNext()){
			Integer node=it.next();
			System.out.print(node+"   :   ");
			Set<NFA_EdgeNode> set=mymap.get(node);
			Iterator<NFA_EdgeNode> it2=set.iterator();
			while(it2.hasNext()){
				NFA_EdgeNode tmp3=it2.next();
				System.out.print(tmp3.end+"("+tmp3.dis+")  ");
			}
			System.out.print("\n");
		}
	}
}
class NFA_EdgeNode{ //NFA节点类
	int end;
	String dis;
	public NFA_EdgeNode(int end,String dis){
		this.end=end;
		this.dis=dis;
	}
}