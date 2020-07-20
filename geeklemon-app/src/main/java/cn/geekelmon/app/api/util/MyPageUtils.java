package cn.geekelmon.app.api.util;

public class MyPageUtils {

	public static int getStart(int pageSize, int pageNum) {
		pageSize = pageSize > 0 ? pageSize : 2;
		pageNum = pageNum > 0 ? pageNum : 1;
		int start = pageSize * (pageNum - 1);
		return start;
	}

	public static int getEndNum(int pageSize, int pageNum) {
		pageSize = pageSize > 0 ? pageSize : 2;
		pageNum = pageNum > 0 ? pageNum : 1;
		int end = (pageSize * pageNum - 1) + pageSize;
		return end;

	}
}
