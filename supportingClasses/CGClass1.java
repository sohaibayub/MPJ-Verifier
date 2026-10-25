package gov.nasa.jpf.supportingClasses;

import java.util.ArrayList;

import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.ThreadChoiceFromSet;

public class CGClass1 {
	static public ChoiceGenerator<?> CG = null ;
	
	static public void setCG(ChoiceGenerator<?> _CG) {
		CG = _CG ;	
	}
	
	static public ChoiceGenerator<?> getCG() {
		return CG ;
	}
	
	static public boolean isEqual(ChoiceGenerator<?> _CG) {
		if(CG.getId() == _CG.getId()) 
			return true ;
		else 
			return false ;
	}
	
	public void p(String str) {	
  		//System.out.println(str) ;
	} 
}
