package com.example.boogle.keywordsearching;

import java.io.IOException;
import java.util.ArrayList;

import com.example.boogle.model.Keyword;

public class WebPage {
	public String url;
	public String name;
	public WordCounter counter;
	public double score;

	public WebPage(String url, String name) {
		this.url = url;
		this.name = name;
		this.counter = new WordCounter(url);
	}

	public void setScore(ArrayList<Keyword> keywords) throws IOException {
		double rawScore = 0.0;

		for (Keyword k : keywords) {
			try {
				int number = counter.countKeyword(k.name);

				System.out.printf("[KeywordCount] page=%s, keyword=%s, count=%d%n",
						this.url, k.name, number);

				rawScore += number * k.weight;
			} catch (IOException e) {
				System.err.println("Count keyword failed. url=" + url
						+ ", keyword=" + k.name + ", msg=" + e.getMessage());
			}
		}

		double tokensP = counter.getTotalTokenCount() / 100.0;
		this.score = (tokensP == 0) ? 0.0 : (rawScore / tokensP);

		// 印出此頁面的總分（只算自己，不含子頁面）
		System.out.printf("[PageScore] page=%s, selfScore=%.4f%n", this.url, this.score);
	}

}