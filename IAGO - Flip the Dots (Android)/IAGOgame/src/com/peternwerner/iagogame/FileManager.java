package com.peternwerner.iagogame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class FileManager {

	// class variables
	Dots dotsObj;	Connectors connectorsObj;
	
	
	// run when the object is first instantiated
	public void init(Dots _dotsObj, Connectors _connectorsObj) {
		
		dotsObj = _dotsObj;
		connectorsObj = _connectorsObj;
	}
	
	
	// write level into a text file with specified filename
	public boolean writeFile(File file) { 
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			
			for(int i = 0; i < dotsObj.matrix.length; i++) {
				for(int j = 0; j < dotsObj.matrix[0].length; j++) {
					writer.println(dotsObj.matrix[i][j]);
				}
			}
			for(int i = 0; i < connectorsObj.connectorsHorizontal.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsHorizontal[0].length; j++) {
					writer.println(connectorsObj.connectorsHorizontal[i][j]);
				}
			}
			for(int i = 0; i < connectorsObj.connectorsVertical.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsVertical[0].length; j++) {
					writer.println(connectorsObj.connectorsVertical[i][j]);
				}
			}
			for(int i = 0; i < connectorsObj.connectorsDiagDown.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsDiagDown[0].length; j++) {
					writer.println(connectorsObj.connectorsDiagDown[i][j]);
				}
			}
			for(int i = 0; i < connectorsObj.connectorsDiagUp.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsDiagUp[0].length; j++) {
					writer.println(connectorsObj.connectorsDiagUp[i][j]);
				}
			}
			
			writer.close();
			return true;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	
	// load level from a text file at specified file location
	@SuppressWarnings("finally")
	public boolean readFile(InputStream inStream) {
		
		/*
		// set default score variables 
		MainGame.scoreRating[2] = MainGame.n + 1;
		MainGame.scoreRating[1] = 2 * MainGame.n + 1;
		MainGame.scoreRating[0] = 4 * MainGame.n;
		*/
		
		BufferedReader reader = null;
		
		try {
		    reader = new BufferedReader(new InputStreamReader(inStream));
		    
		    // set score variables
			MainGame.scoreRating[2] = Integer.parseInt(reader.readLine()) + 1;
			if(MainGame.scoreRating[2] <= 3)		// get rid of the 1 point cushion for the easiest levels
				MainGame.scoreRating[2]--;
			MainGame.scoreRating[1] = 2 * MainGame.scoreRating[2];
			MainGame.scoreRating[0] = 99999;
		    
		    // read ints from file line by line, and put them into the dots and connector arrays
		    for(int i = 0; i < dotsObj.matrix.length; i++) {
				for(int j = 0; j < dotsObj.matrix[0].length; j++) {
					(dotsObj.matrix[i][j]) = Integer.parseInt(reader.readLine());
				}
			}
			for(int i = 0; i < connectorsObj.connectorsHorizontal.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsHorizontal[0].length; j++) {
					(connectorsObj.connectorsHorizontal[i][j]) = Integer.parseInt(reader.readLine());
				}
			}
			for(int i = 0; i < connectorsObj.connectorsVertical.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsVertical[0].length; j++) {
					(connectorsObj.connectorsVertical[i][j]) = Integer.parseInt(reader.readLine());
				}
			}
			for(int i = 0; i < connectorsObj.connectorsDiagDown.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsDiagDown[0].length; j++) {
					(connectorsObj.connectorsDiagDown[i][j]) = Integer.parseInt(reader.readLine());
				}
			}
			for(int i = 0; i < connectorsObj.connectorsDiagUp.length; i++) {
				for(int j = 0; j < connectorsObj.connectorsDiagUp[0].length; j++) {
					(connectorsObj.connectorsDiagUp[i][j]) = Integer.parseInt(reader.readLine());
				}
			}
			
			MainGame.scoreNow = 0;
			
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return false;
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		        return true;
		    } catch (IOException e) {
		    	return false;
		    }
		}
		
	}
	

}
