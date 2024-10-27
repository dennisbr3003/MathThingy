package com.dennis_brink.android.mymaththingy.gamecore;

import java.io.Serializable;

public abstract class DataStructure implements Serializable {
    private final StructureType type;

    public DataStructure(StructureType type) {
        this.type = type;
    }

    public StructureType getType() {
        return type;
    }

}
