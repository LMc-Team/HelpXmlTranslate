package com.lmcteam.helpxmltranslate.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmcteam.helpxmltranslate.R;
import com.lmcteam.helpxmltranslate.beans.TranslateDataBean;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlTranslateAdapter extends RecyclerView.Adapter<XmlTranslateAdapter.XmlViewHolder> implements TextWatcher {
    private List<DataHolder> data;
    Context context;

    @NonNull
    @Override
    public XmlTranslateAdapter.XmlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ip = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new XmlViewHolder(ip);
    }

    public void setData(List<DataHolder> data) {
        this.data = data;
//        notifyDataSetChanged();
    }

    public void saveAsXML(String p) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<resources>\n");
        for (DataHolder d : data) {
            if (d.newString.isEmpty() || d.newString.equals(d.oldString)) {

                sb.append("    <string name=\"" + d.stringID + "\">" + d.oldString + "</string>\n");
            } else {
                sb.append("    <string name=\"" + d.stringID + "\">" + d.newString + "</string>\n");
            }
        }
        sb.append("</resources>\n");
        try {
            FileUtils.writeStringToFile(new File(p), sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "保存在："+p, Toast.LENGTH_SHORT).show();
    }

    public void saveAsJSON(String p) {
        TranslateDataBean bean = new TranslateDataBean();
        bean.setDatas(data);
        try {
            FileUtils.writeStringToFile(new File(p), JSON.toJSONString(bean, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteNullListAsEmpty));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "保存在："+p, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(@NonNull XmlTranslateAdapter.XmlViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (context==null)
            context = holder.itemView.getContext();
        DataHolder dataHolder = data.get(position);
        holder.stringId.setText(dataHolder.getStringID());
        holder.old.setText(dataHolder.getOldString());
        if (!dataHolder.newString.isEmpty()) {
            holder.new_.setText(dataHolder.newString);
        }
        holder.new_.setTextColor(Color.BLACK);
        holder.old.setTextColor(Color.BLACK);
        holder.new_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!dataHolder.oldString.equals(s.toString())) {
                    holder.new_.setTextColor(Color.GREEN);
                    data.get(position).newString = s.toString();
                } else {
                    holder.new_.setTextColor(Color.BLACK);
                }
            }
        });

    }

    public List<DataHolder> getCurrentData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public static class DataHolder {
        String stringID;

        public String getStringID() {
            return stringID;
        }

        public void setStringID(String stringID) {
            this.stringID = stringID;
        }

        public String getOldString() {
            return oldString;
        }

        public void setOldString(String oldString) {
            this.oldString = oldString;
        }

        public String getNewString() {
            return newString;
        }

        public void setNewString(String newString) {
            this.newString = newString;
        }

        String oldString;
        String newString;

    }

    static class XmlViewHolder extends RecyclerView.ViewHolder {

        public TextView old;
        public EditText new_;
        public TextView stringId;

        public XmlViewHolder(@NonNull View itemView) {
            super(itemView);
            old = itemView.findViewById(R.id.text_old);
            new_ = itemView.findViewById(R.id.text_new);
            stringId = itemView.findViewById(R.id.string_id);
        }
    }
}
