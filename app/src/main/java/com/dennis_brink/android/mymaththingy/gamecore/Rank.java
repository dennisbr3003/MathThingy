package com.dennis_brink.android.mymaththingy.gamecore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "gl_rank",
        "id"
})
@Generated("jsonschema2pojo")
public class Rank {

    @JsonProperty("gl_rank")
    private Integer glRank;
    @JsonProperty("id")
    private String id;

    public Rank() {
    }

    public Rank(Integer glRank, String id) {
        super();
        this.glRank = glRank;
        this.id = id;
    }

    @JsonProperty("gl_rank")
    public Integer getGlRank() {
        return glRank;
    }

    @JsonProperty("gl_rank")
    public void setGlRank(Integer glRank) {
        this.glRank = glRank;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "glRank=" + glRank +
                ", id='" + id + '\'' +
                '}';
    }
}