package com.peternwerner.iagogame;

public class LevelListFiller {

	public void fillList() {
		
		// how many levels are in n index?
		int[] lengths = {0, 0, 18, 96, 96, 48, 0, 0, 0, 0};
		
		// tell maingame the size of each level list
		for(int i = 0; i < MainGame.levelListSize.length; i++) {
			
			MainGame.levelListSize[i] = lengths[i];
		}
		
		// populate maingame level lists
		for(int i = 0; i < MainGame.levelList.length; i++) {
			for(int j = 0; j < lengths[i]; j++) {
				
				String name;
				
				if(j < 10)
					name = "Level 0" + j + " (" + i + "x" + i + ")";
				else
					name = "Level " +  j + " (" + i + "x" + i + ")";					
				
				MainGame.levelList[i][j] = name;
			}
		}
				
	}
}
