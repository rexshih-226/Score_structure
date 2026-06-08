package com.example.boogle.keywordsearching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.boogle.model.Keyword;

public class WebTree {
	public WebNode root;
	private static final double CHILD_WEIGHT = 0.6; // 子頁面分數權重
	private static final int MAX_DEPTH = 1; // 最大遞迴深度
	private final java.util.Set<String> visited = new java.util.HashSet<>();

	public WebTree(WebPage rootPage) {
		this.root = new WebNode(rootPage);
	}

	public void setPostOrderScore(ArrayList<Keyword> keywords) throws IOException {
		setPostOrderScore(root, keywords);
	}

	private void setPostOrderScore(WebNode startNode, ArrayList<Keyword> keywords) throws IOException {
		if (startNode.children.size() > 0) {
			for (WebNode child : startNode.children) {
				setPostOrderScore(child, keywords);
			}
		}
		startNode.setNodeScore(keywords);
	}

	public void eularPrintTree() {
		eularPrintTree(root);
	}

	private void eularPrintTree(WebNode startNode) {
		String space = repeat("\t", startNode.getDepth() - 1);
		if (startNode.children.size() == 0) {
			System.out.println(space + "(" + startNode.webPage.url + "," + startNode.nodeScore + ")");
		} else {
			System.out.println(space + "(" + startNode.webPage.url + "," + startNode.nodeScore);
			for (WebNode child : startNode.children) {
				eularPrintTree(child);
			}
			System.out.println(space + ")");
		}
	}

	private String repeat(String str, int repeat) {
		StringBuilder retVal = new StringBuilder();
		for (int i = 0; i < repeat; i++) {
			retVal.append(str);
		}
		return retVal.toString();
	}

	// 新增：自動建立子節點，遞迴探索 up to depth
	private void autoBuild(WebNode node, int currentDepth) throws IOException {
		if (currentDepth >= MAX_DEPTH)
			return;

		String url = node.webPage.url;
		// 已經處理過這個 URL 就直接跳過，不再抓、也不再展開子節點
		if (visited.contains(url)) {
			return;
		}
		visited.add(url);

		String html = node.webPage.counter.getContent();
		if (html == null || html.isEmpty())
			return;

		List<String> links = WordCounter.extractLinks(html);
		for (String link : links) {
			WebPage subPage = new WebPage(link, link);
			WebNode childNode = new WebNode(subPage);
			node.addChild(childNode);
			autoBuild(childNode, currentDepth + 1);
		}
	}

	// 新增：啟動自動建樹（從 root 開始）
	public void buildAutomatically() throws IOException {
		visited.clear(); // 每棵樹重建時清空
		autoBuild(root, 0);
	}
}
