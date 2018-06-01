package demo2;

import java.util.HashMap;
import java.util.Stack;


public class NFA {
	private int nfa_begin,nfa_end;//nfa起点与终点状态
	private HashMap<Character,Integer> priority=new HashMap<Character,Integer>();//优先级表
	private NFA_Adjtable nFA_Adjtable=new NFA_Adjtable();//NFA邻接表
	private int node_num=0; //NFA状态总数
	public NFA_Adjtable getAdjtable() {//封装
		return nFA_Adjtable;
	}
	public void transvers(){//对外接口：遍历nfa图
		System.out.printf("起点%d  终点%d\n",nfa_begin,nfa_end);
		nFA_Adjtable.transvers();
	}
	
	public NFA(){//设置优先级
		priority.put('(', 0);
		priority.put('*',3);
		priority.put('-', 2);
		priority.put('|', 1);
	}
	private int level(char a){//获取优先级
		return priority.get(a);
	}
	private  boolean isCharacter(char a){//判断是否为字母
		if((a>='a'&&a<='z')||(a>='A'&&a<='Z'))return true;
		else{
			return false;
		}
	}
	private  String AddNotation(String tmp){//将传入的表达式添加省略的连接号‘-’
		String expr="";
		for(int i=0;i<tmp.length()-1;i++){
			if(((isCharacter(tmp.charAt(i)))&&(tmp.charAt(i+1)=='('))||(tmp.charAt(i)=='*'&&isCharacter(tmp.charAt(i+1))||(tmp.charAt(i)==')'&&isCharacter(tmp.charAt(i+1))))){
				expr+=tmp.charAt(i);
				expr+='-';
			}
			else{
				expr+=tmp.charAt(i);
			}
		}
		expr+=tmp.charAt(tmp.length()-1);
		return expr;
	}
	public  Node regular_expression_to_nfa(String tmp){//nfa构建，参考中缀表达式
		Stack<Character> notation=new Stack<Character>();//符号栈
		Stack<Node> alphbet=new Stack<Node>();//节点栈，节点存放临时图的起点与终点
		
		String expr=AddNotation(tmp);//添加连接号
		
		for(int i=0;i<expr.length();i++){
			if(isCharacter(expr.charAt(i))){//是操作数就压栈
				String tmp_str="";
				tmp_str+=expr.charAt(i);
				while((i+1<expr.length())&&isCharacter(expr.charAt(i+1))){
					i++;
					tmp_str+=expr.charAt(i);
				}
				
				alphbet.push(new Node(tmp_str));
			}
			else{
				Character ch=expr.charAt(i);//当前字符
				if(ch=='*') {
					int begin1,end1;
					int begin2,end2;
					Node tmp_node=alphbet.pop();
					begin1=tmp_node.begin_id;
					end1=tmp_node.end_id;
					if(tmp_node.begin_id==-1&&tmp_node.end_id==-1) {
						begin1=++node_num;
						end1=++node_num;
						nFA_Adjtable.insert(begin1, end1, tmp_node.alph);
					}
					begin2=++node_num;
					end2=++node_num;
					nFA_Adjtable.insert(begin2, begin1, "#");
					nFA_Adjtable.insert(end1, end2, "#");
					nFA_Adjtable.insert(begin2, end2, "#");
					nFA_Adjtable.insert(end1, begin1, "#");
					alphbet.push(new Node(begin2,end2,"$"));
					//nFA_Adjtable.transvers();
					continue;
				}
				if(ch=='('||notation.isEmpty()){//左括号就压栈，直到遇到右括号取出
					notation.push(ch);
					continue;
				}
				
				if(ch==')'){//遇到右括号开始递归大法
					
					Character judge=notation.pop();
					alphbet.pop();//清空操作栈与符号栈括号间的内容
					while(judge!='('){
						alphbet.pop();
						judge=notation.pop();
					}
					
					int end=i;//获得原字符串中左括号位置
					int begin=0;
					for(int j=end;j>=0;j--){
						if(expr.charAt(j)=='('){
							begin=j;
							break;
						}
					}
					alphbet.push(regular_expression_to_nfa(expr.substring(begin+1, end)));//截取括号中字符串开始递归
					//递归大法好
					//System.out.println(expr2);
				}
				else{
					Character tmp_note=notation.peek();//不是括号
					if(level(ch)<=level(tmp_note)){//当前操作符优先级不大于栈顶优先级，先做栈顶运算，再压栈
						int begin1,end1;
						int begin2,end2;
						int begin3,end3;
						Node tmp_right;
						Node tmp_left;
						tmp_note=notation.pop();
						switch(tmp_note){
						case '|':
							tmp_right=alphbet.pop();
							tmp_left=alphbet.pop();
							begin1=++node_num;
							end1=++node_num;
							begin2=++node_num;
							end2=++node_num;
							nFA_Adjtable.insert(begin1,end1 ,tmp_left.alph);
							nFA_Adjtable.insert(begin2,end2, tmp_right.alph);
							begin3=++node_num;
							end3=++node_num;
							nFA_Adjtable.insert(begin3, begin1, "#");
							nFA_Adjtable.insert(begin3, begin2, "#");
							nFA_Adjtable.insert(end1, end3, "#");
							nFA_Adjtable.insert(end2, end3, "#");
							alphbet.push(new Node(begin3,end3,"$"));
							break;
						case '-':
							tmp_right=alphbet.pop();
							tmp_left=alphbet.pop();
							if(tmp_left.alph.equals("$")&&(!tmp_right.alph.equals("$"))){
								end1=tmp_left.end_id;
								begin1=tmp_left.begin_id;
								end2=++node_num;
								nFA_Adjtable.insert(end1, end2, tmp_right.alph);
								alphbet.push(new Node(begin1,end2,"$"));
							}
							else{
								if(tmp_right.alph.equals("$")&&(!tmp_left.alph.equals("$"))){
									begin1=tmp_right.begin_id;
									begin2=++node_num;
									end1=tmp_right.end_id;
									nFA_Adjtable.insert(begin2, begin1, tmp_left.alph);
									alphbet.push(new Node(begin2,end1,"$"));
								}
								else{
									begin1=++node_num;
									end1=++node_num;
									end2=++node_num;
									nFA_Adjtable.insert(begin1, end1, tmp_left.alph);
									nFA_Adjtable.insert(end1, end2, tmp_right.alph);
									alphbet.push(new Node(begin1,end2,"$"));
								}
							}
							break;
						}
					}
					notation.push(ch);//当前运算符进栈
				}
			}
			
		}
		while(!notation.isEmpty()){//符号栈若不为空，说明都是相同优先级。
			Character tmp_note=notation.pop();
			int begin1,end1;
			int begin2,end2;
			int begin3,end3;
			Node tmp_right;
			Node tmp_left;
			switch(tmp_note){
			case '|':
				tmp_right=alphbet.pop();
				tmp_left=alphbet.pop();
				begin1=++node_num;
				end1=++node_num;
				begin2=++node_num;
				end2=++node_num;
				nFA_Adjtable.insert(begin1,end1 ,tmp_left.alph);
				nFA_Adjtable.insert(begin2,end2, tmp_right.alph);
				begin3=++node_num;
				end3=++node_num;
				nFA_Adjtable.insert(begin3, begin1, "#");
				nFA_Adjtable.insert(begin3, begin2, "#");
				nFA_Adjtable.insert(end1, end3,"#");
				nFA_Adjtable.insert(end2, end3, "#");
				alphbet.push(new Node(begin3,end3,"$"));
				break;
			case '-':
				tmp_right=alphbet.pop();
				tmp_left=alphbet.pop();
				
				if(tmp_left.alph.equals("$")&&(!tmp_right.alph.equals("$"))){
					end1=tmp_left.end_id;
					begin1=tmp_left.begin_id;
					end2=++node_num;
					nFA_Adjtable.insert(end1, end2, tmp_right.alph);
					alphbet.push(new Node(begin1,end2,"$"));
				}
				else{
					if(tmp_right.alph.equals("$")&&(!tmp_left.alph.equals("$"))){
						begin1=tmp_right.begin_id;
						begin2=++node_num;
						end1=tmp_right.end_id;
						//nFA_Adjtable.transvers();
						nFA_Adjtable.insert(begin2, begin1, tmp_left.alph);
						alphbet.push(new Node(begin2,end1,"$"));
						
						//System.out.println("");
						//nFA_Adjtable.transvers();
					}
					else{
						begin1=++node_num;
						end1=++node_num;
						end2=++node_num;
						nFA_Adjtable.insert(begin1, end1, tmp_left.alph);
						nFA_Adjtable.insert(end1, end2, tmp_right.alph);
						alphbet.push(new Node(begin1,end2,"$"));
					}
				}
				break;
			}
		}
		nfa_begin=alphbet.peek().begin_id;//节点栈剩下的就是图的始末节点
		nfa_end=alphbet.peek().end_id;
		return alphbet.pop();
	}
	public int getNfa_begin() {
		return nfa_begin;
	}
	public Integer getNfa_end() {
		return nfa_end;
	}
}
class Node {
	int begin_id;
	int end_id;
	String alph;
	public Node(){
		begin_id=-1;
		end_id=-1;
		alph="$";//$代表没有任何线，#代表空集
	}
	public Node(String a){
		begin_id=-1;
		end_id=-1;
		alph=a;
	}
	public Node(int a,int b,String c){
		begin_id=a;
		end_id=b;
		alph=c;
	}
}