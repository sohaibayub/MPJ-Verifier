package gov.nasa.jpf.supportingClasses;

import gov.nasa.jpf.vm.ChoiceGenerator;

import java.util.ArrayList;

class CG_Struct {
	public int Thread_Id ;
	public String Choice_Generater_Id ;
	public int Total_Choices ;
	public int Choices_Processed ;
	public ChoiceGenerator<?> Choice_Generator ;
	
	CG_Struct(int _Thread_Id, 
			  ChoiceGenerator<?> _Choice_Generator, 
			  String _Choice_Generater_Id, 
			  int _Total_Choices, 
			  int _Choices_Processed) {
		
		Thread_Id = _Thread_Id ;
		Choice_Generator = _Choice_Generator ;
		Choice_Generater_Id = _Choice_Generater_Id ;
		Total_Choices = _Total_Choices ;
		Choices_Processed = _Choices_Processed ;
		
	}
}

public class CGClass3 {

	ArrayList<CG_Struct> List = new ArrayList<CG_Struct>() ;
	
	public CGClass3() {
		
	}
	
	public void Add_CG(int _Thread_Id, ChoiceGenerator<?> _Choice_Generator) {
		List.add(new CG_Struct(_Thread_Id, 
							   _Choice_Generator, 
							   _Choice_Generator.getId(), 
							   _Choice_Generator.getTotalNumberOfChoices(), 
							   _Choice_Generator.getProcessedNumberOfChoices())) ;
		
	}
	
	public void update_record(int _Thread_Id, String _Choice_Generater_Id, int _Choices_Processed) {
		for(int i = 0 ; i < List.size(); i++) {
			if(List.get(i).Thread_Id == _Thread_Id && List.get(i).Choice_Generater_Id == _Choice_Generater_Id) {
				List.get(i).Choices_Processed = _Choices_Processed ;
				break ;
			}
		}
	}
	
}
