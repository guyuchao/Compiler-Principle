package demo2;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
//import java.util.TreeMap;
//import java.util.TreeSet;

public class DFA_Minimize {
	Set<String> via_dis;
	Set<Integer> new_dfa_state=new HashSet<Integer>();//存放新状态,除了end中的状态
	private int dfa_begin;//dfa_begin状态
	private Set<Integer> dfa_end;//dfa_end状态
	DFA_Adjtable dfa_adj;
	Map<Integer,Set<Integer>> min_dfa=new HashMap<Integer,Set<Integer>>();//区号-顶点
	private Map<Integer,Set<DFA_EdgeNode>> final_min_dfa=new HashMap<Integer,Set<DFA_EdgeNode>>();//区号代替顶点号
	int min_dfa_begin;//最小化dfa的begin
	Set<Integer> min_dfa_end=new HashSet<Integer>();//最小化dfa的end
	private int node_quhao,node_quhao_begin;
	
	public int getNode_quhao() {
		return node_quhao;
	}
	public DFA_Minimize(DFA dfa,int quhao_begin){
		/*
		this.via_dis=new HashSet<String>();
		via_dis.add("a");
		via_dis.add("b");
		this.dfa_begin=1;
		this.dfa_end=new HashSet<Integer>();
		dfa_end.add(5);
		dfa_end.add(6);
		dfa_end.add(7);
		this.dfa_adj=new DFA_Adjtable();
		Set<DFA_EdgeNode> set1=new HashSet<DFA_EdgeNode>();
		set1.add(new DFA_EdgeNode("6","a"));
		set1.add(new DFA_EdgeNode("3","b"));
		dfa_adj.mymap.put("1",set1);
		
		Set<DFA_EdgeNode> set2=new HashSet<DFA_EdgeNode>();
		set2.add(new DFA_EdgeNode("7","a"));
		set2.add(new DFA_EdgeNode("3","b"));
		dfa_adj.mymap.put("2",set2);
		
		Set<DFA_EdgeNode> set3=new HashSet<DFA_EdgeNode>();
		set3.add(new DFA_EdgeNode("1","a"));
		set3.add(new DFA_EdgeNode("5","b"));
		dfa_adj.mymap.put("3",set3);
		
		Set<DFA_EdgeNode> set4=new HashSet<DFA_EdgeNode>();
		set4.add(new DFA_EdgeNode("4","a"));
		set4.add(new DFA_EdgeNode("6","b"));
		dfa_adj.mymap.put("4",set4);
		
		Set<DFA_EdgeNode> set5=new HashSet<DFA_EdgeNode>();
		set5.add(new DFA_EdgeNode("7","a"));
		set5.add(new DFA_EdgeNode("3","b"));
		dfa_adj.mymap.put("5",set5);
		
		Set<DFA_EdgeNode> set6=new HashSet<DFA_EdgeNode>();
		set6.add(new DFA_EdgeNode("4","a"));
		set6.add(new DFA_EdgeNode("1","b"));
		dfa_adj.mymap.put("6",set6);
		
		Set<DFA_EdgeNode> set7=new HashSet<DFA_EdgeNode>();
		set7.add(new DFA_EdgeNode("4","a"));
		set7.add(new DFA_EdgeNode("2","b"));
		dfa_adj.mymap.put("7",set7);
		
		new_dfa_state.add(1);
		new_dfa_state.add(2);
		new_dfa_state.add(3);
		new_dfa_state.add(4);
		new_dfa_state.add(5);
		new_dfa_state.add(6);
		new_dfa_state.add(7);
		*/
		
		this.dfa_begin=dfa.dfa_begin;
		this.dfa_end=dfa.dfa_end;
		this.dfa_adj=dfa.getDfa_adj();
		this.via_dis=dfa.getVia_dis_set();
		this.new_dfa_state=dfa.getDfa_state();
		this.node_quhao=quhao_begin;
		this.node_quhao_begin=quhao_begin;
		//初始化设置
		
		//最小化
		this.minimize();
		
		
		
	}
	public Map<Integer, Set<DFA_EdgeNode>> getFinal_min_dfa() {
		return final_min_dfa;
	}
	public Integer find_quhao(Integer num){//找到一个点的区号
		for(Integer i:min_dfa.keySet()){
			if(min_dfa.get(i).contains(num)){
				return i;
			}
		}
		return null;
	}
	public void get_dfa_state_except_end(){//获取除了end状态集合中的dfa状态
		for(Integer i:dfa_end){
			if(new_dfa_state.contains(i)){
				new_dfa_state.remove(i);
			}
		}
	}
	public boolean judge_equal(int i,int j){//判断两个点是否等价（若通过所有边到达相同状态，等价
		for(String ch:via_dis){
			if(find_quhao(num_via_ch(i,ch))!=find_quhao(num_via_ch(j,ch))){
				return false;
			}
		}
		return true;
	}
	public boolean judge_change(int quhao){
		Set<Integer> changeset=new HashSet<Integer>();//新增的set
		
		Set<Integer> node_set=min_dfa.get(quhao);//这个区号中所有点的set
		for(Integer i:node_set){
			for(Integer j:node_set){//循环，点不等的时候判断
				if(i.equals(j)){
					continue;
				}
				else{
					if(!judge_equal(i,j)){//两个顶点边集不等
						changeset.add(i);//记录i到带改变集合 循环查找与i等价的一起改变
						for(Integer k:node_set){
							if(k.equals(i))continue;
							else{
								if(judge_equal(i,k)){
									changeset.add(k);
								}
							}
						}
						for(Integer h:changeset){
							node_set.remove(h);//从原来的顶点set中移除
						}
						min_dfa.remove(quhao);//重置key
						min_dfa.put(quhao, node_set);
						min_dfa.put(++node_quhao, changeset);//新增区
						return true;
					}
				}
			}
		}
		return false;
	}
	public Integer num_via_ch(Integer num,String ch){//一个点在dfa图中经过ch到达的点
		if(!dfa_adj.mymap.containsKey(num.toString())){
			return -1;
		}
		Set<DFA_EdgeNode> set=dfa_adj.mymap.get(num.toString());
		for(DFA_EdgeNode node:set){
			if(node.dis.equals(ch)){
				return Integer.parseInt(node.end);
			}
		}
		return -1;
	}
	
	public void minimize(){
		get_dfa_state_except_end();//除end外集合

		Set<Integer> tmp_set=new HashSet<Integer>();
		for(Integer i:new_dfa_state){
			tmp_set.add(i);
			//System.out.println(i);
		}
		min_dfa.put(++node_quhao,tmp_set);//区号1对应的set
		
		tmp_set=new HashSet<Integer>();
		for(Integer i:dfa_end){
			tmp_set.add(i);
			//System.out.println("j"+i);
		}
		min_dfa.put(++node_quhao, tmp_set);//区号2对应可接受状态集
		
		boolean is_change=false;//标记是否有修改过
		do{
			for(int i=node_quhao_begin+1;i<=node_quhao;i++){
				if(judge_change(i)==true){
					is_change=true;
					break;
				}
				else{
					is_change=false;
				}
			}
		}while(is_change==true);
		
		//for(Integer item:min_dfa.keySet()) {
			
			//System.out.println(min_dfa.get(item).toString());
		//}
		for(int i=node_quhao_begin+1;i<=node_quhao;i++){
			final_min_dfa.put(i, change_value(i));//把node里的点改为区号
		}
	}
	public Set<DFA_EdgeNode> change_value(Integer quhao){
		Set<Integer> tmp=min_dfa.get(quhao);
		for(Integer i:tmp) {
			if(dfa_end.contains(i)) {
				min_dfa_end.add(quhao);
			}
			if(i==dfa_begin) {
				min_dfa_begin=quhao;
			}
		}
		Set<DFA_EdgeNode> ret_set=new HashSet<DFA_EdgeNode>();
		for(Integer i:tmp){
			for(String ch:via_dis){
				if(find_quhao(num_via_ch(i,ch))!=null) {
					ret_set.add(new DFA_EdgeNode(find_quhao(num_via_ch(i,ch)).toString(),ch));
				}
				//System.out.println(ch);
			}
			break; 
		}
		return ret_set;
	}
	public void transvers(){
		for(Integer i:final_min_dfa.keySet()){
			System.out.printf("%d    :   ",i);
			Set<DFA_EdgeNode> set=final_min_dfa.get(i);
			for(DFA_EdgeNode j:set){
				System.out.print(j.end+"("+j.dis+")");
			}
			System.out.println("");
		}
	}
	public void test_transvers(){
		for(int i=node_quhao_begin+1 ;i<=node_quhao;i++){
			System.out.printf("%d    :   ",i);
			Set<Integer> set=min_dfa.get(i);
			for(Integer j:set){
				System.out.print(j+" ");
			}
			System.out.println();
		}
	}
}
