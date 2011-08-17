package edu.cmu.cs.lti.ark.fn.parsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;

import edu.cmu.cs.lti.ark.fn.clusters.ScrapTest;
import edu.cmu.cs.lti.ark.fn.data.prep.ParsePreparation;
import edu.cmu.cs.lti.ark.util.SerializedObjects;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse;

import gnu.trove.THashMap;
import gnu.trove.THashSet;

public class ScanPotentialSpans {
	//public static final String DATA_DIR = "/home/dipanjan/work/summer2011/ArgID/data";
	public static final String DATA_DIR = "/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/NAACL2012";
	
	public static final String INFIX = "train";
	
	public static void main(String[] args) {
		// generateFEStats();
		generateSpans();
	}
	
	public static void generateFEStats() {
		String feMap = "" +
				"/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/ACLSplits/5/framenet.frame.element.map";
		Set<String> fes = new THashSet<String>();
		THashMap<String,THashSet<String>>fedict = 
			(THashMap<String,THashSet<String>>)SerializedObjects.readSerializedObject(feMap);
		Collection<THashSet<String>> col = fedict.values();
		for (THashSet<String> set: col) {
			fes.addAll(set);
		}
		System.out.println("Total number of unique fes: " + fes.size());
	}
	
	public static void generateSpans() {
		String[] labeledProcessedFiles = 
		{DATA_DIR + "/cv.train.sentences.all.lemma.tags",
		 DATA_DIR + "/cv.dev.sentences.all.lemma.tags"};
		String unlabeledProcessedFile = 
			"/mal2/dipanjan/experiments/FramenetParsing/fndata-1.5/uData/AP_1m.all.lemma.tags";
		Set<String> spans = new THashSet<String>();
		String spanFile = DATA_DIR + "/all.spans.sorted";
		
		for (int i = 0; i < labeledProcessedFiles.length; i++) {
			String file = labeledProcessedFiles[i];
			ArrayList<String> parses = ParsePreparation.readSentencesFromFile(file);
			for (int j = 0; j < parses.size(); j++) {
				StringTokenizer st = new StringTokenizer(parses.get(j),"\t");
				int tokensInFirstSent = new Integer(st.nextToken());
				String[][] data = new String[5][tokensInFirstSent];
				for(int k = 0; k < 5; k ++)
				{
					data[k]=new String[tokensInFirstSent];
					for(int l = 0; l < tokensInFirstSent; l ++)
					{
						data[k][l]=""+st.nextToken().trim();
					}
				}	
				DependencyParse parseS = DependencyParse.processFN(data, 0.0);
				DependencyParse[] sortedNodes = DependencyParse.getIndexSortedListOfNodes(parseS);
				boolean[][] spanMat = new boolean[sortedNodes.length][sortedNodes.length];
				int[][] heads = new int[sortedNodes.length][sortedNodes.length];
				ScrapTest.findSpans(spanMat,heads,sortedNodes);
				for (int m = 0; m < sortedNodes.length; m++) {
					for (int n = 0; n < sortedNodes.length; n++) {
						if (spanMat[m][n]) {
							String span = "";
							for (int z = m; z<= n; z++) {
								span += data[0][z].toLowerCase() + " ";
							}
							span = span.trim();
							spans.add(span);
						}
					}
				}
				if (j % 100 == 0) {
					System.out.print(j+" ");
				}
			}
			System.out.println();
		}
		System.out.println("Number of unique spans in the labeled data:" + spans.size());
		String uFile = unlabeledProcessedFile;
		ArrayList<String> parses = ParsePreparation.readSentencesFromFile(uFile);
		int j = 0;
		for (j = 0; j < parses.size(); j++) {
			StringTokenizer st = new StringTokenizer(parses.get(j),"\t");
			int tokensInFirstSent = new Integer(st.nextToken());
			String[][] data = new String[5][tokensInFirstSent];
			for(int k = 0; k < 5; k ++)
			{
				data[k]=new String[tokensInFirstSent];
				for(int l = 0; l < tokensInFirstSent; l ++)
				{
					data[k][l]=""+st.nextToken().trim();
				}
			}	
			DependencyParse parseS = DependencyParse.processFN(data, 0.0);
			DependencyParse[] sortedNodes = DependencyParse.getIndexSortedListOfNodes(parseS);
			boolean[][] spanMat = new boolean[sortedNodes.length][sortedNodes.length];
			int[][] heads = new int[sortedNodes.length][sortedNodes.length];
			ScrapTest.findSpans(spanMat,heads,sortedNodes);
			for (int m = 0; m < sortedNodes.length; m++) {
				for (int n = 0; n < sortedNodes.length; n++) {
					if (spanMat[m][n]) {
						String span = "";
						for (int z = m; z<= n; z++) {
							span += data[0][z].toLowerCase() + " ";
						}
						span = span.trim();
						spans.add(span);
					}
				}
			}
			if (spans.size() >= 500000) {
				break;
			}
			if (j % 100 == 0) {
				System.out.print(j+" ");
			}
			if (j % 10000 == 0) {
				System.out.println();
			}
		}
		System.out.println();
		System.out.println("Number of scanned unlabeled sentences: " + j);
		System.out.println("Number of total spans:" + spans.size());
		String[] spanArray = new String[spans.size()];
		spans.toArray(spanArray);
		Arrays.sort(spanArray);
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(spanFile));
			for (String span: spanArray) {
				bWriter.write(span);
			}
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}