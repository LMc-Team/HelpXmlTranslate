package com.lmcteam.helpxmltranslate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lmcteam.helpxmltranslate.adapter.XmlTranslateAdapter;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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

    void showChooseXmlDialog(boolean cancleable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_choose_dialog_title);
        EditText editText = new EditText(this);
        editText.setHint(R.string.dialog_choose_msg);
        builder.setView(editText);

        builder.setCancelable(cancleable);
        builder.setPositiveButton("ryt6h", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setText(R.string.dialog_choose_ok);
        b.setOnClickListener(v -> {
            File f = new File(editText.getText().toString());
            if (f.exists()) {
                List<XmlTranslateAdapter.DataHolder> allData = new ArrayList<XmlTranslateAdapter.DataHolder>();
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
                }
                adapter.setData(allData);
                recyclerView.setAdapter(adapter);
                alertDialog.dismiss();
                getSupportActionBar().setSubtitle(f.getAbsolutePath());
            } else {
                Toast.makeText(this, R.string.dialog_choose_error, Toast.LENGTH_SHORT).show();
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }
}