package game;

public class ElementComparator {

	public static boolean canMerge(Element base, Element comp){
		if(base == null || comp == null){
			return false;
		}
		
		if(base.isOperator() || comp.isOperator()){
			return false;
		}
		
		if(base.getVariable() == comp.getVariable()){
			if(base.getVariable() == 2){
				return (base.getPower() == comp.getPower());
			}
			return true;
		}
		return false;
	}
}
