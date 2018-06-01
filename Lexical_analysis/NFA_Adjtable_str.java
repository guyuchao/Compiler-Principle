package demo2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NFA_Adjtable_str {
	HashMap<Character,Set<NFA_EdgeNode_str>> mymap=new HashMap<Character,Set<NFA_EdgeNode_str>>();//邻接表
	void insert(Character begin,Character end,String dis){
		if(!mymap.containsKey(begin)){
			Set<NFA_EdgeNode_str> set=new HashSet<NFA_EdgeNode_str>();
			set.add(new NFA_EdgeNode_str(end,dis));
			mymap.put(begin, set);
		}
		else{
			Set<NFA_EdgeNode_str> set=mymap.get(begin);
			set.add(new NFA_EdgeNode_str(end,dis));
		}
	}
	void transvers(){
		Iterator<Character> it=mymap.keySet().iterator();
		while(it.hasNext()){
			Character node=it.next();
			System.out.print(node+"   :   ");
			Set<NFA_EdgeNode_str> set=mymap.get(node);
			Iterator<NFA_EdgeNode_str> it2=set.iterator();
			while(it2.hasNext()){
				NFA_EdgeNode_str tmp3=it2.next();
				System.out.print((char)tmp3.end+"("+tmp3.dis+")  ");
			}
			System.out.print("\n");
		}
	}
}
class NFA_EdgeNode_str{ //NFA节点类
	int end;
	String dis;
	public NFA_EdgeNode_str(Character end,String dis){
		this.end=end;
		this.dis=dis;
	}
}