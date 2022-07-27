package com.lmcteam.helpxmltranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmcteam.helpxmltranslate.adapter.XmlTranslateAdapter;
import com.lmcteam.helpxmltranslate.beans.TranslateDataBean;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    XmlTranslateAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_xmlcode_activity);
        ClipBoardUtil.context = this;
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new XmlTranslateAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(200000);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        showChooseXmlDialog(false);
    }

    void showErrorDialog(Throwable err) {
        AlertDialog.Builder a = new AlertDialog.Builder(this);
        a.setTitle(R.string.dialog_error);
        a.setMessage(err.getMessage());
        a.setPositiveButton(R.string.dialog_choose_ok, null);
        a.show();
    }


    void showChooseXmlDialog(boolean cancleable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_choose_dialog_title);
        EditText editText = new EditText(this);
        editText.setHint(R.string.dialog_choose_msg);
        builder.setView(editText);

        builder.setCancelable(cancleable);
        builder.setPositiveButton("ryt6h", null);
        builder.setNeutralButton("ljinlki", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setText(R.string.dialog_choose_ok);
        b.setOnClickListener(v -> {
            if (editText.getText().toString().endsWith(".xml")) {
                loadProject(editText.getText().toString(), alertDialog, OpenType.TYPE_XML);
                return;
            }
            loadProject(editText.getText().toString(), alertDialog, OpenType.TYPE_JSON);

        });
        Button bb = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        bb.setText(R.string.dialog_loadcode);
        bb.setOnClickListener((v) -> {
            loadCode(ClipBoardUtil.paste());
            alertDialog.dismiss();
        });
    }

    @SuppressLint("SdCardPath")
    void loadCode(String code) {
        isTemp = true;
        Objects.requireNonNull(getSupportActionBar()).setSubtitle("/sdcard/temp.xml");
        List<XmlTranslateAdapter.DataHolder> allDatas = new ArrayList<>();
        if (code.contains("<string name")) {
            Document document = Jsoup.parse(code);
            for (Element e :
                    document.getElementsByTag("string")) {
                XmlTranslateAdapter.DataHolder da = new XmlTranslateAdapter.DataHolder();
                da.setOldString(e.text());
                da.setNewString("");
                da.setStringID(e.attr("name"));
                allDatas.add(da);
            }

        } else {
            try {
                allDatas = JSON.parseObject(code, TranslateDataBean.class).getDatas();
            }catch (Throwable r){
                showErrorDialog(r);
            }
        }

        adapter.setData(allDatas);
        recyclerView.setAdapter(adapter);
    }

    public static class ClipBoardUtil {

        public static Context context;
        public static String paste() {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (manager != null) {
                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                    CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                    String addedTextString = String.valueOf(addedText);
                    if (!addedTextString.isEmpty()) {
                        return addedTextString;
                    }
                }
            }
            return "";
        }
        public static void clear() {
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (manager != null) {
                try {
                    manager.setPrimaryClip(manager.getPrimaryClip());
                    manager.setPrimaryClip(ClipData.newPlainText("", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    void loadProject(String path, AlertDialog alertDialog, OpenType type) {
        isTemp = false;
        File f = new File(path);
        if (f.exists()) {
            List<XmlTranslateAdapter.DataHolder> allData = null;
            if (type == OpenType.TYPE_XML) {
                allData = new ArrayList<>();
                try {
                    String xmlCode = FileUtils.readFileToString(f, "UTF-8");
                    Document xmlDocument = org.jsoup.Jsoup.parse(xmlCode);
                    Elements elements = xmlDocument.getElementsByTag("string");
                    for (Element element : elements) {
                        XmlTranslateAdapter.DataHolder dataHolder = new XmlTranslateAdapter.DataHolder();
                        dataHolder.setStringID(element.attr("name"));
                        dataHolder.setOldString(element.text());
                        dataHolder.setNewString("");
                        allData.add(dataHolder);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            } else {
                try {
                    String json = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
                    TranslateDataBean bean = JSON.parseObject(json, TranslateDataBean.class);
                    allData = bean.getDatas();
                } catch (Throwable e) {
                    e.printStackTrace();
                    showErrorDialog(e);
                }
            }


            adapter.setData(allData);
            recyclerView.setAdapter(adapter);
            if (alertDialog != null)
                alertDialog.dismiss();
            getSupportActionBar().setSubtitle(f.getAbsolutePath());
        } else {
            Toast.makeText(this, R.string.dialog_choose_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_as_xml:
                adapter.saveAsXML(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getSubtitle()).toString().replace(".xml", "_translated.xml"));
                break;
            case R.id.menu_save_as_json:
                adapter.saveAsJSON(Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar()).getSubtitle()).toString().replace(".xml", "_translated.json"));
                break;
            case R.id.reopen:
                showChooseXmlDialog(true);
                break;
            case R.id.reload:
                showReloadDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isTemp;

    void showReloadDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.menu_reload);
        ab.setMessage(R.string.dialog_reload_msg);
        ab.setPositiveButton(R.string.dialog_btn_saveandreload, (a, b) -> {
            if (getSupportActionBar().getSubtitle().toString().endsWith(".xml")) {
                adapter.saveAsXML(getSupportActionBar().getSubtitle().toString());
            } else {
                adapter.saveAsJSON(getSupportActionBar().getSubtitle().toString());
            }
            loadProject(getSupportActionBar().getSubtitle().toString(), null, getSupportActionBar().getSubtitle().toString().endsWith(".xml") ? OpenType.TYPE_XML : OpenType.TYPE_JSON);
        });

        ab.setNegativeButton(R.string.dialog_btn_reload, (a, b) -> loadProject(getSupportActionBar().getSubtitle().toString(), null, getSupportActionBar().getSubtitle().toString().endsWith(".xml") ? OpenType.TYPE_XML : OpenType.TYPE_JSON));
        ab.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isTemp)
            menu.getItem(3).setEnabled(false);
        else
            menu.getItem(3).setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    enum OpenType {
        TYPE_XML,
        TYPE_JSON
    }
}