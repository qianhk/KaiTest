package com.njnu.kai.test.support;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author hanik.lz
 * @version 1.0.0
 * @param <D> result绑定的数据泛型
 */
public class DataListResult<D> extends BaseResult {
	@SerializedName("data")
    private ArrayList<D> mDataList = new ArrayList<D>();

    /**
     * 获取数据列表
     * @return 数据列表
     */
    public ArrayList<D> getDataList() {
        return mDataList;
    }

    /**
     * 是否为空
     * @return 如果为空返回true
     */
    public boolean isDataListEmpty() {
        return mDataList.size() == 0;
    }
}
