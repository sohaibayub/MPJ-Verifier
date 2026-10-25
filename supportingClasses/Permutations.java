package gov.nasa.jpf.supportingClasses;

import java.util.ArrayList;

class List {
	private ArrayList<Integer> list  = new ArrayList<Integer>() ;
	List(String _str) {
		for(int i = 0 ; i < _str.length() ; i++) {
			list.add(Integer.valueOf(_str.charAt(i))) ;
		}
	}
}
public class Permutations {
	
	private ArrayList<String> stringlist = new ArrayList<String>() ;
	public ArrayList<List> ListOFLists = new ArrayList<List>() ;
	
	public Permutations(int _NoOfMessages) {
		String str = "" ;
		for(int i = 0 ; i < _NoOfMessages ; i++) 
			str = str + String.valueOf(i) ;
		permutation("", str) ;
		for(int i = 0 ; i < stringlist.size() ; i++) 
			ListOFLists.add(new List(stringlist.remove(i))) ;
	}
	
	private void permutation(String prefix, String str) {
		int n = str.length() ;
		if (n == 0)
			stringlist.add(prefix) ;
		else
			for (int i = 0; i < n; i++) 
				permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1)) ; 
	}
	
	private void p(String s) {
		//System.out.println(s) ;
	}
}
