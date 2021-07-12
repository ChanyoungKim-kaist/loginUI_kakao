package com.example.loginui_kakao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.loginui_kakao.data.Categories;
import com.example.loginui_kakao.network.RetrofitClient;
import com.example.loginui_kakao.network.ServiceApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity implements RecyclerAdapter.OnNoteListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private RecyclerView.LayoutManager layoutManager;
    private Categories categoriesList;
    private RecyclerAdapter adapter;
    private ServiceApi service;
    private SearchView search;
    private List<PostItem> posts;
    private int type;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_category);
        fab = (FloatingActionButton) findViewById(R.id.fab_list);
        search = (SearchView) findViewById(R.id.search_post);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getInt("category");
            token = getIntent().getStringExtra("token");
            fetchInformation(type);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListActivity.this, NewPostActivity.class);
                    intent.putExtra("category", type);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
            });

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Call<Categories> call = service.getSearch(newText);
                    call.enqueue(new Callback<Categories>() {
                        @Override
                        public void onResponse(Call<Categories> call, Response<Categories> response) {
                            //Toast.makeText(ListActivity.this, "클", Toast.LENGTH_SHORT).show();
                            categoriesList = response.body();
                            if (categoriesList.getOk()) {
                                posts = categoriesList.getPosts();
                                adapter = new RecyclerAdapter(posts, ListActivity.this, ListActivity.this);
                                recyclerView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<Categories> call, Throwable t) {
                            Log.e("로그인 에러 발생", t.getMessage());
                            Toast.makeText(ListActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return false;
                }
            });
        }
    }

    public void fetchInformation(int type){
        service = RetrofitClient.getClient().create(ServiceApi.class);

        Call<Categories> call = service.category(type);
        call.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //Toast.makeText(ListActivity.this, "클", Toast.LENGTH_SHORT).show();
                categoriesList = response.body();
                if (categoriesList.getOk()) {
                    posts = categoriesList.getPosts();
                    adapter = new RecyclerAdapter(posts, ListActivity.this, ListActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                Log.e("로그인 에러 발생", t.getMessage());
                Toast.makeText(ListActivity.this, "실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onNoteClick(int position) {
        posts.get(position);
        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("postId", posts.get(position).getId());
        intent.putExtra("token", token);
        intent.putExtra("title", posts.get(position).getTitle());
        intent.putExtra("contents", posts.get(position).getSubtitle());
        intent.putExtra("likes", posts.get(position).getLikes());
        startActivity(intent);
    }
}