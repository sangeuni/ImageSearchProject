package com.example.myrecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button searchButton;
    EditText searchText;
    List<String> tagsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        searchButton = (Button) findViewById(R.id.search_btn);
        searchText = (EditText) findViewById(R.id.search_text);
        tagsList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        final PhotoAdapter adapter = new PhotoAdapter(getApplicationContext());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebData();
                adapter.notifyItemChanged(0, "click");
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void getWebData() {
        final String keyword = searchText.getText().toString();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String jsonData = null;
                    String url = "https://pixabay.com/api/?key=17828481-17c071c7f8eadf406822fada3&q=" + keyword + "&image_type=photo";
                    jsonData = Jsoup.connect(url).timeout(4000)
                            .userAgent("Mozilla").ignoreContentType(true).execute().body();
                    parseJsonData(jsonData);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void parseJsonData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("hits");
        addTagsList(jsonArray);
    }

    public void addTagsList(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            tagsList.add(object.get("tags").toString());
        }
    }
}