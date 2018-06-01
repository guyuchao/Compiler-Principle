package demo1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Parsing_LL1 {
	private HashMap<String,HashMap<String,Integer>> LL1_table;
	private Map<Integer,String> index_of_generation;
	private String startsymbol;
	private Set<String> non_terminal_set;
	public Parsing_LL1(First_Follow_Gen ffg) {
		LL1_table=ffg.getLL1();
		non_terminal_set=ffg.getNon_terminal_set();
		startsymbol=ffg.getStart_symbol();
		index_of_generation=ffg.getIndex_of_generation();
		String code="if ( 0 ) other else other";
		if(parsing_code(code)==true) {
			System.out.println("没有语法错误");
		}
		else{
			System.out.println("出现语法错误");
		}
	}
	private void init(Stack<String> analysis_s,Stack<String> input_s,String code) {
		analysis_s.add("$");
		input_s.add("$");
		analysis_s.add(startsymbol);
		String[] tmp_str=code.split(" ");
		for(int i=tmp_str.length-1;i>=0;i--) {
			input_s.add(tmp_str[i]);
		}
	}
	private boolean parsing_code(String code) {
		Stack<String> analysis_s=new Stack<String>();
		Stack<String> input_s=new Stack<String>();
		init(analysis_s,input_s,code);
		while(!(analysis_s.peek().equals("$")&&input_s.peek().equals("$"))) {
			if(!non_terminal_set.contains(analysis_s.peek())&&analysis_s.peek().equals(input_s.peek())) {
				analysis_s.pop();
				input_s.pop();
			}
			else {
				int tmp_index;
				if(non_terminal_set.contains(analysis_s.peek())&&(LL1_table.get(analysis_s.peek()).containsKey(input_s.peek()))) {
					tmp_index=LL1_table.get(analysis_s.peek()).get(input_s.peek());
					String[] tmp_str=index_of_generation.get(tmp_index).split("->")[1].split(" ");
					analysis_s.pop();
					for(int i=tmp_str.length-1;i>=0;i--) {
						analysis_s.add(tmp_str[i]);
					}
					
				}
				else {
					return false;
				}
			}
		}
		if(analysis_s.peek().equals("$")&&input_s.peek().equals("$")) {
			return true;
		}
		else {
			return false;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] str=new String[4];
		str[0]="statement->if_stmt|other";
		str[1]="if_stmt->if ( exp ) statement else_part";
		str[2]="else_part->else statement|#";
		str[3]="exp->0|1";
		new Parsing_LL1(new First_Follow_Gen(new Left_recursion_removal(str),"statement"));
	}

}
