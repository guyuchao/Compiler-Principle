package demo;

import java.util.HashMap;
import java.util.Stack;

public class SLR_parsing {
	private HashMap<Integer,HashMap<String,String>> action=new HashMap<Integer,HashMap<String,String>>();
	private HashMap<Integer,HashMap<String,Integer>> go_to=new HashMap<Integer,HashMap<String,Integer>>();
	private String code;
	private HashMap<Integer,String> grammar;
	public SLR_parsing(SLR_table lrt,String code) {
		this.action=lrt.getAction();
		this.go_to=lrt.getGo_to();
		this.code=code;
		this.grammar=lrt.getGrammar_raw();
		Parsing(code);
	}
	private void Parsing(String code) {
		String[] code_arr=code.split(" ");
		Stack<String> token_s=new Stack<String>();
		token_s.push("$");
		Stack<String> ana_s=new Stack<String>();
		for(int i=code_arr.length-1;i>=0;i--)token_s.push(code_arr[i]);
		ana_s.push("0");
		while(true) {
			if(action.containsKey(Integer.parseInt(ana_s.peek()))) {
				String com=action.get(Integer.parseInt(ana_s.peek())).get(token_s.peek());
				if(com==null) {
					System.out.println("有语法错误");
					return;
				}
				if(com.equals("accept")) {
					System.out.println("没有语法错误");
					return;
				}
				else{
					if(com.charAt(0)=='s'){
						ana_s.push(token_s.pop());
						ana_s.push(com.substring(1));
					}
					else {
						int index=Integer.parseInt(com.substring(1));
						int len_pop=grammar.get(index).split("->")[1].split(" ").length;
						while(len_pop-->0) {
							ana_s.pop();
							ana_s.pop();
						}
						Integer now_peek=Integer.parseInt(ana_s.peek());
						ana_s.push(grammar.get(index).split("->")[0]);
						ana_s.push(go_to.get(now_peek).get(ana_s.peek()).toString());
					}
					
				}
			}
			else {
				System.out.println("error");
				return;
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str[]=new String[3];
		str[0]="E->E + T|T";
		str[1]="T->T * F|F";
		str[2]="F->( E )|id";		;
		new SLR_parsing(new SLR_table(new SLR_Init(str,"E")),"id * id + id");
	}

}
