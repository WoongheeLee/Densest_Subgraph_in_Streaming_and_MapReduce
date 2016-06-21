package algorithm;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class DensestSubgraph {
	public static void main(String[] args) {

		// ���� �������� ����� ��
		System.out.println("���� �������� ����� ��");
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
		// ���� �������� ����� ���� ������
		
		// ���̽����� ������ �����ϱ� ���� �ؽø�
		// adjacency list ���·� ����
		HashMap<String, ArrayList<String>> faceBook = new HashMap<String, ArrayList<String>>();
		
		// �������� ������ �о�´�. ������ �׷�����
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
		// ������� ������ �о�ͼ� faceBook�� ����
		
		// JGraphT�� ����
		UndirectedGraph<String, DefaultEdge> G = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		
		// ���� vertex�� �����Ѵ�.
		Iterator<String> itFb = faceBook.keySet().iterator();
		while(itFb.hasNext()) {
			String tempKey = itFb.next();
			G.addVertex(tempKey);
		}
		
		// ������ �����Ѵ�.
		itFb = faceBook.keySet().iterator();
		while(itFb.hasNext()) {
			String tempKey = itFb.next();
			for(int i = 0; i < faceBook.get(tempKey).size(); i++) {
				G.addEdge(tempKey, faceBook.get(tempKey).get(i));
			}
		}
		
		// ����� JGraphT�� Ȯ���Ѵ�.
		//System.out.println(G.toString());
		
		// �׷����� ��� ����.
		System.out.println("�׷��� G�� ��� ����:\t" + G.vertexSet().size());
		// �׷����� ������ ����.
		System.out.println("�׷��� G�� ���� ����:\t" + G.edgeSet().size());
		// �׷����� ���� ����Ƽ
		double densityG = (double)G.edgeSet().size() / (double)G.vertexSet().size();
		System.out.println("�׷��� G�� ����Ƽ:\t" + densityG);
		
		
		/*
		 *  Densest Subgraph �� �˰��� 1�� ����.
		 */
		// �˰��򿡼� ���ư��� ���߿� �� �׷��� S
		UndirectedSubgraph<String, DefaultEdge> S = new UndirectedSubgraph(G, G.vertexSet(), G.edgeSet());
		// �˰��򿡼� ������ approximation subgraph tS
		UndirectedSubgraph<String, DefaultEdge> tS = new UndirectedSubgraph(G, G.vertexSet(), G.edgeSet());
		
		
		double epsilon = 0.001d; // 0���� ū �˰��� �Ķ����
		
		double densityS = (double)S.edgeSet().size() / (double)S.vertexSet().size();
		double densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
		
		System.out.println("\nó�� ����Ƽ => S:" + densityS + " tS: " + densityTS);
		System.out.println();
		
		while(S.vertexSet().size() > 0) {
			
			UndirectedGraph<String, DefaultEdge> A = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
			
			Iterator<String> itS = S.vertexSet().iterator();
			while(itS.hasNext()) {
				String tempVertex = itS.next();
				if((double)S.degreeOf(tempVertex) <= densityS*2d*(1+epsilon)) {
					//System.out.println("����ȣ: " + tempVertex + "\t��׸�: " + S.degreeOf(tempVertex) + "\t����Ƽ�Ķ����: " + densityS*2d*epsilon + "\t��尳��: " + S.vertexSet().size());
					A.addVertex(tempVertex);
				}
			}
			
			Iterator<String> itA = A.vertexSet().iterator();
			while(itA.hasNext()) {
				String tempVertex = itA.next();
				S.removeVertex(tempVertex);

			}
			
			System.out.println("��� ���� S, tS, G: " + S.vertexSet().size() + " " + tS.vertexSet().size() + " " + G.vertexSet().size());
			
			if(S.vertexSet().size() != 0) {
				densityS = (double)S.edgeSet().size() / (double)S.vertexSet().size();
				densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
				
				System.out.println("�߰� ����Ƽ => S:" + densityS + " tS: " + densityTS);
				
				if(densityS > densityTS && S.vertexSet().size() != 0) {
					itA = A.vertexSet().iterator();
					while(itA.hasNext()) {
						String tempV = itA.next();
						tS.removeVertex(tempV);
					}
					System.out.println("����Ƽ ������Ʈ Ȯ��: " + densityS + " " + densityTS);
				}
				System.out.println();
			}
			
		} // while Denest Subgraph �� �˰��� 1
		
		densityTS = (double)tS.edgeSet().size() / (double)tS.vertexSet().size();
		
		System.out.println("\n���� ���");
		Iterator<String> itTS = tS.vertexSet().iterator();
		while(itTS.hasNext()) {
			String vertex = itTS.next();
			//System.out.println(vertex + "\t" + tS.edgesOf(vertex));
		}
		
		System.out.println("\n���� ���� �׷��� tS�� ��� ����: " + tS.vertexSet().size() + " ������ ����: " + tS.edgeSet().size());
		System.out.println("���� ���� �׷��� tS�� ����Ƽ => tS: " + densityTS);	
	}
}
