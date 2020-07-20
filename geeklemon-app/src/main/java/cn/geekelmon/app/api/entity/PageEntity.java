/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.geekelmon.app.api.entity;

import java.io.Serializable;
import java.util.List;

public class PageEntity<T> implements Serializable {
    /**
     * 当前页面
     */
    private int pageNum;

    /**
     * 总页数
     */
    private int pageCount;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 列表
     */
    private List<T> objects;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "PageEntity{" +
                "size" + objects.size() +
                "pageNum=" + pageNum +
                ", pageCount=" + pageCount +
                ", hasNext=" + hasNext +
                '}';
    }
}
