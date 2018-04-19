package com.example.gerrys.merchantapps;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gerrys.merchantapps.Model.Confirmation;
import com.example.gerrys.merchantapps.Model.Product;
import com.example.gerrys.merchantapps.Model.productRequest;
import com.example.gerrys.merchantapps.ViewHolder.ConfirmationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

public class EditProduct extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private StorageTask mUploadTask;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorageRef;
    private Uri mImageUri;
    FirebaseDatabase database;
    DatabaseReference confirm,request,prodReq,prod;
    String ID,merchId;

    productRequest prodss;
    FirebaseRecyclerAdapter<Confirmation, ConfirmationViewHolder> adapter;
    TextView reqId,prodId,prodName,prodQty,prodAddrs,prodPrice;
    ImageView image;
    Button ConfirmButton,Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ID = getIntent().getStringExtra("id");
  ;
        prodName = (TextView)findViewById(R.id.prodName);
        prodQty = (TextView)findViewById(R.id.prodQty);
        prodPrice = (TextView)findViewById(R.id.prodPrice);
        merchId = getIntent().getStringExtra("merch");
        image = (ImageView)findViewById(R.id.image_view);

        ConfirmButton = (Button)findViewById(R.id.Change);
        Cancel = (Button)findViewById(R.id.Cancel);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        database = FirebaseDatabase.getInstance();
        prod = database.getReference("Product");

        prod.child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product prod = dataSnapshot.getValue(Product.class);
                Picasso.with(getBaseContext()).load(prod.getImage()).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageUri != null) {
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));

                    mUploadTask = fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setProgress(0);
                                        }
                                    }, 0);
                                    Toast.makeText(EditProduct.this, "Upload successful", Toast.LENGTH_SHORT).show();

                                    prod.child("Image").setValue(taskSnapshot.getDownloadUrl().toString());
                                    prod.child("Name").setValue(prodName.getText().toString());
                                    prod.child("Price").setValue(prodPrice.getText().toString());
                                    prod.child("Stock").setValue(prodQty.getText().toString());
                                    final Intent intent = new Intent(EditProduct.this,ProductList.class);
                                    intent.putExtra("merch",merchId);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProduct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    mProgressBar.setProgress((int) progress);
                                }
                            });
                } else {

                    Toast.makeText(EditProduct.this, "No file selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProduct.this,ConfirmationMerchant.class);
                intent.putExtra("merch",merchId);
                startActivity(intent);
            }
        });
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(image);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
