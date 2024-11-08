package com.dennis_brink.android.mymaththingy.gamecore;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "result"
})
@Generated("jsonschema2pojo")
public class RankSet {

    @JsonProperty("result")
    private List<Rank> rankList;

    public RankSet() {
    }

    public RankSet(List<Rank> result) {
        super();
        this.rankList = result;
    }

    @JsonProperty("result")
    public List<Rank> getResult() {
        return rankList;
    }

    @JsonProperty("result")
    public void setResult(List<Rank> result) {
        this.rankList = result;
    }

    @Override
    public String toString() {
        return "RankSet{" +
                "result=" + rankList +
                '}';
    }
}