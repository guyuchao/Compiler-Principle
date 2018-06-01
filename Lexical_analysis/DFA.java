package demo2;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class DFA {
	
	private DFA_Adjtable dfa_adj=new DFA_Adjtable();//dfaͼ���ڽӱ�
	
	
	Integer dfa_begin;//dfa�ĳ�̬
	Set<Integer> dfa_end=new HashSet<Integer>();//dfa����̬����
	
	private Set<Integer> dfa_state=new HashSet<Integer>();//�������dfa״̬��
	
	private NFA_Adjtable nfa_adj;//nfaͼ
	private Integer nfa_begin,nfa_end;//nfa������յ�
	private Set<String> via_dis_set;//ͨ���ߵ�·��ֵ
	
	public DFA(NFA nfa){//���캯��������nfa
		nfa_adj=nfa.getAdjtable();
		nfa_begin=nfa.getNfa_begin();
		nfa_end=nfa.getNfa_end();
		via_dis_set=get_nfa_dis();//��ȡ�߼�
	}
	public Set<Integer> getDfa_state() {
		return dfa_state;
	}
	public Set<String> getVia_dis_set() {
		return via_dis_set;
	}
	private Set<String> get_nfa_dis(){
		Set<String> ret_set=new HashSet<String>();
		Queue<Integer> queue=new ArrayDeque<Integer>();//���Ѷ���
		Set<Integer> visited=new HashSet<Integer>();//��������set
		queue.add(nfa_begin);//���ѳ�ʼ��
		
		while(!queue.isEmpty()){//���в���
			if(visited.contains(queue.peek())){//�ڵ��Ѿ���չ��������
				queue.poll();
				continue;
			}
			else{//δ��չ�����뼯��
				visited.add(queue.peek());
			}
			
			int vertex=queue.poll();//ȡ���ڵ�
			
			if(nfa_adj.mymap.containsKey(vertex)){
				Set<NFA_EdgeNode> nfa_set=nfa_adj.mymap.get(vertex);//ȡ���ڽӱ��иýڵ�ı߼�
				Iterator<NFA_EdgeNode> it_nfa_set=nfa_set.iterator();
				while(it_nfa_set.hasNext()){
					NFA_EdgeNode tmp=it_nfa_set.next();
					queue.add(tmp.end);//�ڵ����
					if(!tmp.dis.equals("#")){
						ret_set.add(tmp.dis);//�ߵ�ֵ��Ϊ�ռ��뼯��
					}
				}
			}
		}
		return ret_set;
	}
	
	public Set<Integer> e_closure(int begin){//e-�հ������ض��ڵ�����ıհ�
		Set<Integer> tmp_set=new HashSet<Integer>();
		tmp_set.add(begin);
		return e_closure(tmp_set);//���ü��ϵ�e-�հ�
	}
	public Set<Integer> e_closure(Set<Integer> I){//���ϵ�e-�հ�
		Iterator<Integer> it=I.iterator();
		Set<Integer> visited=new HashSet<Integer>();//��Ϊ�������صļ��ϣ�ͬʱ�������е�Ԫ��Ҳ��e-�հ���Ҫ���ص�
		
		while(it.hasNext()){//���ڼ���ÿ��������հ�
			Queue<Integer> queue=new ArrayDeque<Integer>();//����
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
				
				if(!nfa_adj.mymap.containsKey(vertex))continue;//�ж�ͼ���Ƿ��иýڵ�
				Set<NFA_EdgeNode> nfa_set=nfa_adj.mymap.get(vertex);
				
				Iterator<NFA_EdgeNode> it_nfa_set=nfa_set.iterator();
				
				while(it_nfa_set.hasNext()){
					NFA_EdgeNode tmp=it_nfa_set.next();
					if(tmp.dis.equals("#")){//����õ���ͨ����#���ߵĵ㣬����չ
						queue.add(tmp.end);
					}
				}
			}
		}
		return visited;
	}
	public Set<Integer> e_closure_via(Set<Integer> I,String via){//һ������ͨ��via����ַ�����ıߵ�e-�հ�
		Iterator<Integer> it=I.iterator();
		Set<Integer> visited=new HashSet<Integer>();
		while(it.hasNext()){
			int tmp=it.next();
			if(!nfa_adj.mymap.containsKey(tmp))continue;
			Set<NFA_EdgeNode> tmp_set=nfa_adj.mymap.get(tmp);
			//��ȡ�߼�
			Iterator<NFA_EdgeNode> tmp_it=tmp_set.iterator();
			while(tmp_it.hasNext()){
				NFA_EdgeNode edgenode=tmp_it.next();
				if(edgenode.dis.equals(via)){//����ñߵ�ֵΪvia����������e-�հ�����
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
	
	public void revise_index(String str,String str_change){//�޸��±�
		Set<String> set=dfa_adj.mymap.keySet();
		for(String ss:set){
			Set<DFA_EdgeNode> set_node=dfa_adj.mymap.get(ss);//ȡ�߱�
			
			Vector<DFA_EdgeNode> vec_del=new Vector<DFA_EdgeNode>();
			Vector<DFA_EdgeNode> vec_add=new Vector<DFA_EdgeNode>();
			
			for(DFA_EdgeNode node:set_node){
				if(node.end.equals(str)){
					vec_add.add(new DFA_EdgeNode(str_change,node.dis));
					vec_del.add(node);
				}
			}//�����ڵ���������������ɾ��
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
		if(dfa_adj.mymap.containsKey(str)){//�����������У�Ҳ�޸�
			Set<DFA_EdgeNode> set_node=dfa_adj.mymap.get(str);
			dfa_adj.mymap.put(str_change, set_node);
			dfa_adj.mymap.remove(str);
		}
	}
	public void NFA_TO_DFA(){
		Set<String> visited=new HashSet<String>();//��������
		Set<String> tmp_end_state=new HashSet<String>();
		Queue<String> dfa_state_queue=new ArrayDeque<String>();//dfa״̬����
		
		String tmp_begin_state=Set_to_String(e_closure(nfa_begin));//��nfa_begin�ıհ���Ϊstring
		dfa_state_queue.add(tmp_begin_state);//dfa���г�ʼ��
		//System.out.println(tmp_begin_state);
		visited.add(dfa_state_queue.peek());
		while(!dfa_state_queue.isEmpty()){//״̬���в�Ϊ��
			Set<Integer> nfa_set=String_to_Set(dfa_state_queue.peek());//����string��nfa״̬��Ϊset
			Iterator<String> it=via_dis_set.iterator();//����ͨ���ıߵļ���eg.abcd
			while(it.hasNext()){//�����ߵļ���
				String tmp_char=it.next();
				if(e_closure_via(nfa_set,tmp_char).isEmpty())continue;//���缯��ͨ�������ߵļ���Ϊ������
				String new_dfa_state=Set_to_String(e_closure_via(nfa_set,tmp_char));//��״̬Ϊ�ü��ϱ�ɵ�string
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
		dfa_begin=0;//��������
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
