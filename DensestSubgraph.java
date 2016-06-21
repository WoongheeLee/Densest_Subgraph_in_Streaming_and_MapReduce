package algorithm;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class DensestSubgraph {
	public static void main(String[] args) {

		// 원본 데이터의 통계적 명세
		System.out.println("원본 데이터의 통계적 명세");
		try {
			FileReader fr0 = new FileReader("Dataset statistics.txt");
			BufferedReader br = new BufferedReader(fr0);
			
			String line = "";
			for(int i = 1; (line = br.readLine()) != null; i++) {
				System.out.println(line);
			}
			System.out.println();
		} catch ( IOException e) {}
		System.out.println();
		// 원본 데이터의 통계적 명세를 보였음
		
		// 페이스북의 엣지를 저장하기 위한 해시맵
		// adjacency list 형태로 저장
		HashMap<String, ArrayList<String>> faceBook = new HashMap<String, ArrayList<String>>();
		
		// 데이터의 엣지를 읽어온다. 무방향 그래프임
		try {
			FileReader fr = new FileReader("facebook edges.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			for(int i = 1; (line = br.readLine()) != null; i++) {
				//System.out.println(line);
				StringTokenizer st = new StringTokenizer(line);
				String A = st.nextToken();
				String B = st.nextToken();
				
				ArrayList<String> tempArray1 = new ArrayList<String>();
				if(faceBook.get(A) != null) {
					tempArray1 = faceBook.get(A);
				}
				tempArray1.add(B);
				faceBook.put(A, tempArray1);
				
				ArrayList<String> tempArray2 = new ArrayList<String>();
				if(faceBook.get(B) != null) {
					tempArray2 = faceBook.get(B);
				}
				tempArray2.add(A);
				faceBook.put(B, tempArray2);
			}
			br.close();
		} catch (IOException e) {}
		// 여기까지 데이터 읽어와서 faceBook에 저장
		
		// JGraphT에 저장
		UndirectedGraph<String, DefaultEdge> G = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		
		// 먼저 vertex를 저장한다.
		Iterator<String> itFb = faceBook.keySet().iterator();
		while(itFb.hasNext()) {
			String tempKey = itFb.next();
			G.addVertex(tempKey);
		}
		
		// 엣지를 저장한다.
		itFb = faceBook.keySet().iterator();
		while(itFb.hasNext()) {
			String tempKey = itFb.next();
			for(int i = 0; i < faceBook.get(tempKey).size(); i++) {
				G.addEdge(tempKey, faceBook.get(tempKey).get(i));
			}
		}
		
		// 저장된 JGraphT를 확인한다.
		//System.out.println(G.toString());
		
		// 그래프의 노드 갯수.
		System.out.println("그래프 G의 노드 개수:\t" + G.vertexSet().size());
		// 그래프의 엣지의 개수.
		System.out.println("그래프 G의 엣지 개수:\t" + G.edgeSet().size());
		// 그래프의 본래 덴서티
		double densityG = (double)G.edgeSet().size() / (double)G.vertexSet().size();
		System.out.println("그래프 G의 덴서티:\t" + densityG);
		
		
		/*
		 *  Densest Subgraph 논문 알고리즘 1번 구현.
		 */
		// 알고리즘에서 돌아가는 와중에 쓸 그래프 S
		UndirectedSubgraph<String, DefaultEdge> S = new UndirectedSubgraph(G, G.vertexSet(), G.edgeSet());
		// 알고리즘에서 리턴할 approximation subgraph tS
		UndirectedSubgraph<String, DefaultEdge> tS = new UndirectedSubgraph(G, G.vertexSet(), G.edgeSet());
		
		
		double epsilon = 0.001d; // 0보다 큰 알고리즘 파라미터
		
		double densityS = (double)S.edgeSet().size() / (double)S.vertexSet().size();
		double densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
		
		System.out.println("\n처음 덴서티 => S:" + densityS + " tS: " + densityTS);
		System.out.println();
		
		while(S.vertexSet().size() > 0) {
			
			UndirectedGraph<String, DefaultEdge> A = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
			
			Iterator<String> itS = S.vertexSet().iterator();
			while(itS.hasNext()) {
				String tempVertex = itS.next();
				if((double)S.degreeOf(tempVertex) <= densityS*2d*(1+epsilon)) {
					//System.out.println("노드번호: " + tempVertex + "\t디그리: " + S.degreeOf(tempVertex) + "\t덴서티파라미터: " + densityS*2d*epsilon + "\t노드개수: " + S.vertexSet().size());
					A.addVertex(tempVertex);
				}
			}
			
			Iterator<String> itA = A.vertexSet().iterator();
			while(itA.hasNext()) {
				String tempVertex = itA.next();
				S.removeVertex(tempVertex);

			}
			
			System.out.println("노드 개수 S, tS, G: " + S.vertexSet().size() + " " + tS.vertexSet().size() + " " + G.vertexSet().size());
			
			if(S.vertexSet().size() != 0) {
				densityS = (double)S.edgeSet().size() / (double)S.vertexSet().size();
				densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
				
				System.out.println("중간 덴서티 => S:" + densityS + " tS: " + densityTS);
				
				if(densityS > densityTS && S.vertexSet().size() != 0) {
					itA = A.vertexSet().iterator();
					while(itA.hasNext()) {
						String tempV = itA.next();
						tS.removeVertex(tempV);
					}
					System.out.println("덴서티 업데이트 확인: " + densityS + " " + densityTS);
				}
				System.out.println();
			}
			
		} // while Denest Subgraph 논문 알고리즘 1
		
		densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
		
		System.out.println("\n최종 노드");
		Iterator<String> itTS = tS.vertexSet().iterator();
		while(itTS.hasNext()) {
			String vertex = itTS.next();
			//System.out.println(vertex + "\t" + tS.edgesOf(vertex));
		}
		
		System.out.println("\n최종 서브 그래프 tS의 노드 개수: " + tS.vertexSet().size() + " 엣지의 개수: " + tS.edgeSet().size());
		System.out.println("최종 서브 그래프 tS의 덴서티 => tS: " + densityTS);	
	}
}
