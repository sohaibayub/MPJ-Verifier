package gov.nasa.jpf.supportingClasses;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.choice.ThreadChoiceFromSet;

import java.util.ArrayList;

class CGStructure {
	public ChoiceGenerator<?> CG ;
	public int ListIndex ;
	CGStructure(ChoiceGenerator<?> _CG, int _ListIndex) {
		this.CG = _CG ;
		this.ListIndex = _ListIndex ;
	}
}

public class CGClass2 {

public static ArrayList<CGStructure> CGList = new ArrayList<CGStructure>() ;
	
	public static void addCG(ChoiceGenerator<?> _CG, int _ListIndex) {
		CGList.add(new CGStructure(_CG, _ListIndex)) ; 
	}

	public static boolean IsCGInListB(ChoiceGenerator<?> _CG) {
		for(int i = 0 ; i< CGList.size(); i++) {
			if(CGList.get(i).CG.getId().equals(_CG.getId())) {
				return true ;
			}
		}
		return false ;
	}
	
	public static int IsCGInListI(ChoiceGenerator<?> _CG) 
	{
		for(int i = 0 ; i< CGList.size(); i++) {
			if(CGList.get(i).CG.getId().equals(_CG.getId())) {
				return i ;
			}
		}
		return -1 ;
	}
	
	public static int getIndex(int _index) {
		return CGList.get(_index).ListIndex ;
	}
	
	public static void p(String str) {	
  		//System.out.println(str) ;
	} 
	
}
