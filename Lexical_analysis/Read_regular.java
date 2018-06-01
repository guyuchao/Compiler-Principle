package demo2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Read_regular {
	private Map<String,String> InputMap=new HashMap<String,String>();
	private Map<Integer,Set<DFA_EdgeNode>> dfa_final=null;
	private Integer dfa_final_begin;
	private Map<Integer,String> Acceptable =new HashMap<Integer,String>();
	private int quhao_begin=0;
	private void read_regular_type(String str) {//read 0|1|2..... to name a few
		String tmp_name="";
		int cnt=0;
		while(str.charAt(cnt)!='=') {
			tmp_name+=str.charAt(cnt++);
		}
		str=str.substring(cnt+1,str.length()-1);
		String[] tmp_split=str.split("\\|");
		for(int j=0;j<tmp_split.length;j++) {
			InputMap.put(tmp_split[j], tmp_name);
		}
	}
	public Map<String, String> getInputMap() {
		return InputMap;
	}
	private void read_regular_expression(String str) {
		NFA nfa=new NFA();
		String end_str="";
		int cnt=1;
		while(str.charAt(cnt)!='=') {
			end_str+=str.charAt(cnt++);
		}
		str=str.substring(cnt+1,str.length()-1);
		nfa.regular_expression_to_nfa(str);
		DFA dfa=new DFA(nfa);
		dfa.NFA_TO_DFA();
		DFA_Minimize dfa_min=new DFA_Minimize(dfa,quhao_begin);
		//dfa_min.transvers();
		quhao_begin=dfa_min.getNode_quhao();
		for(Integer i : dfa_min.min_dfa_end) {
			Acceptable.put(i, end_str);
		}
		if(dfa_final==null) {
			dfa_final=dfa_min.getFinal_min_dfa();
			dfa_final_begin=dfa_min.min_dfa_begin;
		}
		else {
			Map<Integer,Set<DFA_EdgeNode>> tmp_dfa_adj=dfa_min.getFinal_min_dfa();
			Integer begin=dfa_min.min_dfa_begin;
			Set<DFA_EdgeNode> tmp_set=dfa_final.get(dfa_final_begin);
			tmp_set.addAll(tmp_dfa_adj.get(begin));
			dfa_final.put(dfa_final_begin, tmp_set);
			tmp_dfa_adj.remove(begin);
			dfa_final.putAll(tmp_dfa_adj);
		}
	}
	public Map<Integer, Set<DFA_EdgeNode>> getDfa_final() {
		return dfa_final;
	}
	public Integer getDfa_final_begin() {
		return dfa_final_begin;
	}
	public Map<Integer, String> getAcceptable() {
		return Acceptable;
	}
	public Read_regular() {
		String filename="E:\\regular.txt";
		FileIOstream fio=new FileIOstream();
		String[] text=fio.InputFile(filename).split("\n");
		//System.out.println(text.length);
		
		for(int i=0;i<text.length;i++) {
			if(text[i].charAt(0)=='\r') {
				continue;
			}
			if(text[i].charAt(0)!='@') {
				read_regular_type(text[i]);
			}
			else {
				read_regular_expression(text[i]);
				//break;
			}
		}
	}
	public void transvers() {
		for(Integer i:dfa_final.keySet()) {
			System.out.print(i+"  :");
			for(DFA_EdgeNode j:dfa_final.get(i)) {
				System.out.print(j.end+"("+j.dis+")");
			}
			System.out.println("");
		}
		System.out.println(dfa_final_begin);/*
		for(Integer i:Acceptable.keySet()) {
			System.out.println(i+"   "+Acceptable.get(i));
		}/*
		for(String str:InputMap.keySet()) {
			System.out.println(str+"   "+InputMap.get(str));
		}*/
	}
}
