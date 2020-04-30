package com.teamcool.touristum.data.model;

import androidx.annotation.Nullable;

public class Filter implements Comparable<Filter>{

    String type,filter;
    boolean isSearch;

    public Filter(String type, String filter,boolean isSearch) {
        this.type = type;
        this.filter = filter;
        this.isSearch = isSearch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }

    @Override
    public int compareTo(Filter o) {
        return this.type.compareTo(o.type);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Filter filter = (Filter) obj;
        return this.getType().equals(filter.getType()) && this.getFilter().equals(filter.getFilter()) && this.isSearch == filter.isSearch;
    }
}
