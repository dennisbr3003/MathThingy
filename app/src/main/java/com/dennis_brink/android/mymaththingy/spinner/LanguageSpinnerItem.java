package com.dennis_brink.android.mymaththingy.spinner;

public class LanguageSpinnerItem {

    private String language, isoCode;
    private int flag;

    public LanguageSpinnerItem(int flag, String language, String isoCode) {
        this.language = language;
        this.flag = flag;
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "LanguageSpinnerItem{" +
                "language='" + language + '\'' +
                ", isoCode='" + isoCode + '\'' +
                ", flag=" + flag +
                '}';
    }
}
