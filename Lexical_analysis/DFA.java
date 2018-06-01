package demo2;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class DFA {
	
	private DFA_Adjtable dfa_adj=new DFA_Adjtable();//dfa图的邻接表
	
	
	Integer dfa_begin;//dfa的初态
	Set<Integer> dfa_end=new HashSet<Integer>();//dfa的终态集合
	
	private Set<Integer> dfa_state=new HashSet<Integer>();//化简过的dfa状态集
	
	private NFA_Adjtable nfa_adj;//nfa图
	private Integer nfa_begin,nfa_end;//nfa起点与终点
	private Set<String> via_dis_set;//通过边的路径值
	
	public DFA(NFA nfa){//构造函数：传入nfa
		nfa_adj=nfa.getAdjtable();
		nfa_begin=nfa.getNfa_begin();
		nfa_end=nfa.getNfa_end();
		via_dis_set=get_nfa_dis();//获取边集
	}
	public Set<Integer> getDfa_state() {
		return dfa_state;
	}
	public Set<String> getVia_dis_set() {
		return via_dis_set;
	}
	private Set<String> get_nfa_dis(){
		Set<String> ret_set=new HashSet<String>();
		Queue<Integer> queue=new ArrayDeque<Integer>();//广搜队列
		Set<Integer> visited=new HashSet<Integer>();//广搜判重set
		queue.add(nfa_begin);//广搜初始化
		
		while(!queue.isEmpty()){//队列不空
			if(visited.contains(queue.peek())){//节点已经拓展过，丢弃
				queue.poll();
				continue;
			}
			else{//未拓展，加入集合
				visited.add(queue.peek());
			}
			
			int vertex=queue.poll();//取出节点
			
			if(nfa_adj.mymap.containsKey(vertex)){
				Set<NFA_EdgeNode> nfa_set=nfa_adj.mymap.get(vertex);//取出邻接表中该节点的边集
				Iterator<NFA_EdgeNode> it_nfa_set=nfa_set.iterator();
				while(it_nfa_set.hasNext()){
					NFA_EdgeNode tmp=it_nfa_set.next();
					queue.add(tmp.end);//节点入队
					if(!tmp.dis.equals("#")){
						ret_set.add(tmp.dis);//边的值不为空加入集合
					}
				}
			}
		}
		return ret_set;
	}
	
	public Set<Integer> e_closure(int begin){//e-闭包：重载对于单个点的闭包
		Set<Integer> tmp_set=new HashSet<Integer>();
		tmp_set.add(begin);
		return e_closure(tmp_set);//调用集合的e-闭包
	}
	public Set<Integer> e_closure(Set<Integer> I){//集合的e-闭包
		Iterator<Integer> it=I.iterator();
		Set<Integer> visited=new HashSet<Integer>();//作为广搜判重的集合，同时，集合中的元素也是e-闭包需要返回的
		
		while(it.hasNext()){//对于集合每个起点做闭包
			Queue<Integer> queue=new ArrayDeque<Integer>();//广搜
			queue.add(it.next());
			while(!queue.isEmpty()){
				if(visited.contains(queue.peek())){
					queue.poll();
					continue;
				}
				else{
					visited.add(queue.peek());
				}
				int vertex=queue.poll();
				
				if(!nfa_adj.mymap.containsKey(vertex))continue;//判断图中是否有该节点
				Set<NFA_EdgeNode> nfa_set=nfa_adj.mymap.get(vertex);
				
				Iterator<NFA_EdgeNode> it_nfa_set=nfa_set.iterator();
				
				while(it_nfa_set.hasNext()){
					NFA_EdgeNode tmp=it_nfa_set.next();
					if(tmp.dis.equals("#")){//如果该点是通过‘#’边的点，则拓展
						queue.add(tmp.end);
					}
				}
			}
		}
		return visited;
	}
	public Set<Integer> e_closure_via(Set<Integer> I,String via){//一个集合通过via这个字符到达的边的e-闭包
		Iterator<Integer> it=I.iterator();
		Set<Integer> visited=new HashSet<Integer>();
		while(it.hasNext()){
			int tmp=it.next();
			if(!nfa_adj.mymap.containsKey(tmp))continue;
			Set<NFA_EdgeNode> tmp_set=nfa_adj.mymap.get(tmp);
			//获取边集
			Iterator<NFA_EdgeNode> tmp_it=tmp_set.iterator();
			while(tmp_it.hasNext()){
				NFA_EdgeNode edgenode=tmp_it.next();
				if(edgenode.dis.equals(via)){//如果该边的值为via，把这个点的e-闭包加入
					visited.addAll(e_closure(edgenode.end));
				}
			}
		}
		return visited;
		
	}
	public String Set_to_String(Set<Integer> set){//Set_to_string
		String ret="";
		Iterator<Integer> it=set.iterator();
		while(it.hasNext()){
			ret+=it.next()+",";
		}
		return ret.substring(0, ret.length()-1);
	}
	public Set<Integer> String_to_Set(String str){//string_to_set
		String[] tmp=str.split(",");
		Set<Integer> set=new HashSet<Integer>();
		for(int i=0;i<tmp.length;i++){
			set.add(Integer.parseInt(tmp[i]));
		}
		return set;
	}
	
	public void revise_index(String str,String str_change){//修改下标
		Set<String> set=dfa_adj.mymap.keySet();
		for(String ss:set){
			Set<DFA_EdgeNode> set_node=dfa_adj.mymap.get(ss);//取边表
			
			Vector<DFA_EdgeNode> vec_del=new Vector<DFA_EdgeNode>();
			Vector<DFA_EdgeNode> vec_add=new Vector<DFA_EdgeNode>();
			
			for(DFA_EdgeNode node:set_node){
				if(node.end.equals(str)){
					vec_add.add(new DFA_EdgeNode(str_change,node.dis));
					vec_del.add(node);
				}
			}//不能在迭代器中作插入与删除
			Iterator<DFA_EdgeNode> it=vec_del.iterator();
			while(it.hasNext()){
				set_node.remove(it.next());
			}
			it=vec_add.iterator();
			while(it.hasNext()){
				set_node.add(it.next());
			}
			
			dfa_adj.mymap.put(ss, set_node);
		}
		if(dfa_adj.mymap.containsKey(str)){//如果顶点表中有，也修改
			Set<DFA_EdgeNode> set_node=dfa_adj.mymap.get(str);
			dfa_adj.mymap.put(str_change, set_node);
			dfa_adj.mymap.remove(str);
		}
	}
	public void NFA_TO_DFA(){
		Set<String> visited=new HashSet<String>();//广搜判重
		Set<String> tmp_end_state=new HashSet<String>();
		Queue<String> dfa_state_queue=new ArrayDeque<String>();//dfa状态队列
		
		String tmp_begin_state=Set_to_String(e_closure(nfa_begin));//将nfa_begin的闭包变为string
		dfa_state_queue.add(tmp_begin_state);//dfa队列初始化
		//System.out.println(tmp_begin_state);
		visited.add(dfa_state_queue.peek());
		while(!dfa_state_queue.isEmpty()){//状态队列不为空
			Set<Integer> nfa_set=String_to_Set(dfa_state_queue.peek());//解析string的nfa状态变为set
			Iterator<String> it=via_dis_set.iterator();//可能通过的边的集合eg.abcd
			while(it.hasNext()){//遍历边的集合
				String tmp_char=it.next();
				if(e_closure_via(nfa_set,tmp_char).isEmpty())continue;//假如集合通过该条边的集合为空跳过
				String new_dfa_state=Set_to_String(e_closure_via(nfa_set,tmp_char));//新状态为该集合变成的string
				if(!visited.contains(new_dfa_state)){
					visited.add(new_dfa_state);
					dfa_state_queue.add(new_dfa_state);
					if(new_dfa_state.contains(nfa_end.toString())){
						tmp_end_state.add(new_dfa_state);
					}
				}
				
				String tmp_begin=dfa_state_queue.peek();
				dfa_adj.insert(tmp_begin, new_dfa_state, tmp_char);
			}
			dfa_state_queue.poll();
		}
		dfa_begin=0;//更换名字
		Integer node_num=-1;
		revise_index(tmp_begin_state,(++node_num).toString());
		visited.remove(tmp_begin_state);
		for(String ss:visited){
			if(tmp_end_state.contains(ss)){
				dfa_end.add(node_num+1);
			}
			revise_index(ss,(++node_num).toString());
		}
		for(int i=0;i<=node_num;i++){
			dfa_state.add(i);
		}
	}
	public DFA_Adjtable getDfa_adj() {
		return dfa_adj;
	}
}
