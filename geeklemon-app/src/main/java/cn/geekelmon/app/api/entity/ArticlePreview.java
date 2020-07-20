
package cn.geekelmon.app.api.entity;

import java.io.Serializable;

public class ArticlePreview implements Serializable {
	/**
	 * 作者名称
	 */
	private String authorName;
	/**
	 * 作者logo的url
	 */
	private String authorLogo;

	private String authorOpenId;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 描述
	 */
	private String summary;

	/**
	 * 被点赞数
	 */
	private int starNum;
	/**
	 * 被收藏数
	 */
	private int collectionNum;

	/**
	 * 分类
	 */
	private String subject;

	private int id;

	private String previewImageUrl;

	public ArticlePreview() {
	}

	private ArticlePreview(Builder builder) {
		setAuthorName(builder.authorName);
		setAuthorLogo(builder.authorLogo);
		setTitle(builder.title);
		setSummary(builder.summary);
		setStarNum(builder.starNum);
		setCollectionNum(builder.collectionNum);
		setSubject(builder.subject);
		setId(builder.id);
		setPreviewImageUrl(builder.previewImageUrl);
	}

	public String getPreviewImageUrl() {
		return previewImageUrl;
	}

	public void setPreviewImageUrl(String previewImageUrl) {
		this.previewImageUrl = previewImageUrl;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorLogo() {
		return authorLogo;
	}

	public void setAuthorLogo(String authorLogo) {
		this.authorLogo = authorLogo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getStarNum() {
		return starNum;
	}

	public void setStarNum(int starNum) {
		this.starNum = starNum;
	}

	public int getCollectionNum() {
		return collectionNum;
	}

	public void setCollectionNum(int collectionNum) {
		this.collectionNum = collectionNum;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthorOpenId() {
		return authorOpenId;
	}

	public ArticlePreview setAuthorOpenId(String authorOpenId) {
		this.authorOpenId = authorOpenId;
		return this;
	}

	public static final class Builder {
		private String authorName;
		private String authorLogo;
		private String title;
		private String summary;
		private int starNum;
		private int collectionNum;
		private String subject;
		private int id;
		private String previewImageUrl;

		public Builder() {
		}

		public Builder authorName(String val) {
			authorName = val;
			return this;
		}

		public Builder authorLogo(String val) {
			authorLogo = val;
			return this;
		}

		public Builder title(String val) {
			title = val;
			return this;
		}

		public Builder summary(String val) {
			summary = val;
			return this;
		}

		public Builder starNum(int val) {
			starNum = val;
			return this;
		}

		public Builder collectionNum(int val) {
			collectionNum = val;
			return this;
		}

		public Builder subject(String val) {
			subject = val;
			return this;
		}

		public Builder id(int val) {
			id = val;
			return this;
		}

		public Builder previewImageUrl(String val) {
			previewImageUrl = val;
			return this;
		}

		public ArticlePreview build() {
			return new ArticlePreview(this);
		}
	}

	@Override
	public String toString() {
		return "ArticlePreview{" + "authorName='" + authorName + '\'' + ", title='" + title + '\'' + ", subject='"
				+ subject + '\'' + ", id=" + id + '}';
	}
}
