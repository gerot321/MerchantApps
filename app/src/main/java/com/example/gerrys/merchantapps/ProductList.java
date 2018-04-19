package com.example.gerrys.merchantapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gerrys.merchantapps.Common.Common;
import com.example.gerrys.merchantapps.Interface.ItemClickListener;
import com.example.gerrys.merchantapps.Model.Product;
import com.example.gerrys.merchantapps.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProductList extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference confirm,prodReq;
    String mercId;
    FirebaseRecyclerAdapter<Product, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_status);
        mercId = getIntent().getStringExtra("merch");

        database = FirebaseDatabase.getInstance();
        confirm = database.getReference("Product");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }

    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Product, MenuViewHolder>(
                Product.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                confirm.orderByChild("MerchantId").equalTo(mercId)
        ){

            protected void populateViewHolder(final MenuViewHolder viewHolder, Product model, final int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongCLick) {
                        Intent intent = new Intent(ProductList.this, EditProduct.class);
                        intent.putExtra("id", adapter.getRef(position).getKey());
                        intent.putExtra("merch",mercId);
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

}
