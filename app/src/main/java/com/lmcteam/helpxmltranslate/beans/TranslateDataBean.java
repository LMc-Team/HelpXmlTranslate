package com.lmcteam.helpxmltranslate.beans;

import com.lmcteam.helpxmltranslate.adapter.XmlTranslateAdapter;

import java.util.List;

public class TranslateDataBean {
    public List<XmlTranslateAdapter.DataHolder> datas;

    public List<XmlTranslateAdapter.DataHolder> getDatas() {
        return datas;
    }

    public void setDatas(List<XmlTranslateAdapter.DataHolder> datas) {
        this.datas = datas;
    }
}
