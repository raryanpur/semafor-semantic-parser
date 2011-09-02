package edu.cmu.cs.lti.ark.fn.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphSpans {
	public String[] sortedSpans = null;
	public String[] sortedFEs = null;
	public float[][] smoothedGraph = null;
	public GraphSpans(String spansFile, 
					  String feFile,
					  String smoothedFile) {
		readSpansFile(spansFile);
		readFEFile(feFile);
		readSmoothedFile(smoothedFile);
	}
	
	public void readSpansFile(String spansFile) {
		int count = 0;
		System.out.println("Reading spans file...");
		String line = null;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(spansFile));
			while ((line = bReader.readLine()) != null) {
				count++;
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("Could not read file: " + spansFile);
			System.exit(-1);
		}
		System.out.println("Total number of spans: " + count);
		sortedSpans = new String[count];
		count = 0;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(spansFile));
			while ((line = bReader.readLine()) != null) {
				line = line.trim();
				sortedSpans[count] = line;
				count++;
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("Could not read file: " + spansFile);
			System.exit(-1);
		}
		System.out.println("Stored spans.");
	}
	
	public void readFEFile(String feFile) {
		int count = 0;
		System.out.println("Reading fe file...");
		String line = null;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(feFile));
			while ((line = bReader.readLine()) != null) {
				count++;
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("Could not read file: " + feFile);
			System.exit(-1);
		}
		System.out.println("Total number of fes: " + count);
		sortedFEs = new String[count];
		count = 0;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(feFile));
			while ((line = bReader.readLine()) != null) {
				line = line.trim();
				sortedFEs[count] = line;
				count++;
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("Could not read file: " + feFile);
			System.exit(-1);
		}
		System.out.println("Stored FEs.");
	}
	
	public void readSmoothedFile(String smoothedFile) {
		smoothedGraph = new float[sortedSpans.length][sortedFEs.length];
		String line = null;
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(smoothedFile));
			int count = 0;
			while ((line = bReader.readLine()) != null) {
				String[] toks = line.trim().split("\t");
				int index = new Integer(toks[0]);
				for (int i = 1; i < toks.length; i++) {
					smoothedGraph[index][i-1] = new Float(toks[i]);
				}
				count++;
				if (count >= 1) {
					break;
				}
			}
			bReader.close();
		} catch (IOException e) {
			System.out.println("Could not read smoothed graph.");
			e.printStackTrace();
			System.exit(-1);
		}
	} 
	
	public static void main(String[] args) {
		String spansFile = 
			"/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/NAACL2012/all.spans.sorted";
		String feFile = 
			"/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/NAACL2012/fes.sorted";
		String smoothedFile =
			"/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/NAACL2012/smoothed/lp.mu.0.5.nu.0.1.10";
		GraphSpans gs = new GraphSpans(spansFile, feFile, smoothedFile);
	}
}
