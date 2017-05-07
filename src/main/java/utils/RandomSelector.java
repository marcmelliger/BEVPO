package utils;

import java.util.ArrayList;
import java.util.Random;


public class RandomSelector {

	private ArrayList<ProbabilityItem> items = new ArrayList<>();
    private Random rand = dataAnalysisUtils.random();
    private int totalSum = 0;

    public RandomSelector(ArrayList<ProbabilityItem> items) {
    	this.items = items;

    	resetCustomTotalSum();
    }
    
    public void setCustomTotalSum(int max){
    	
    	this.totalSum = max;
    	
    }
    
    public void resetCustomTotalSum(){
        for(ProbabilityItem item : items) {
            totalSum = totalSum + item.getRelativeProb();
        }
    }

    public ProbabilityItem getRandom() {

        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i=0;
        
        while(sum < index ) {
        	 if(i < items.size()){
        		 sum = sum + items.get(i++).getRelativeProb();
        	 } else {
        		 return null;
        	 }
        }
        return items.get(Math.max(0,i-1));
    }
    
    
}
