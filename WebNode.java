package com.example.boogle.keywordsearching;

import java.io.IOException;
import java.util.ArrayList;

import com.example.boogle.model.Keyword;

public class WebNode {
	public WebNode parent;
	public ArrayList<WebNode> children;
	public WebPage webPage;
	public double nodeScore;
	private static final double CHILD_WEIGHT = 0.6; // 子節點分數權重

	public WebNode(WebPage webPage) {
		this.webPage = webPage;
		this.children = new ArrayList<>();
	}

	public void setNodeScore(ArrayList<Keyword> keywords) throws IOException {
		// 先算自己這個頁面的分數（裡面會印 keyword 次數與 selfScore）
		webPage.setScore(keywords);
		this.nodeScore = webPage.score;

		// 再把子節點的 nodeScore 乘上權重加上來
		if (!children.isEmpty()) {
			for (WebNode child : children) {
				this.nodeScore += child.nodeScore * CHILD_WEIGHT; // 子節點分數乘上 0.6
			}
		}

		// 印出「包含子頁面後」的 nodeScore
		System.out.printf("[NodeScore] url=%s, selfScore=%.4f, nodeScore(with children)=%.4f%n",
				webPage.url, webPage.score, this.nodeScore);
	}

	public void addChild(WebNode child) {
		this.children.add(child);
		child.parent = this;
	}

	public boolean isTheLastChild() {
		if (this.parent == null)
			return true;
		ArrayList<WebNode> siblings = this.parent.children;
		return this.equals(siblings.get(siblings.size() - 1));
	}

	public int getDepth() {
		int depth = 1;
		WebNode curr = this;
		while (curr.parent != null) {
			depth++;
			curr = curr.parent;
		}
		return depth;
	}
}