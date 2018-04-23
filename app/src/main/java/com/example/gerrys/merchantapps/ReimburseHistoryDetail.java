package com.example.gerrys.merchantapps;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gerrys.merchantapps.Model.Confirmation;
import com.example.gerrys.merchantapps.Model.ReimburseReq;
import com.example.gerrys.merchantapps.ViewHolder.ConfirmationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

public class ReimburseHistoryDetail extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    FirebaseDatabase database;
    DatabaseReference reimburseReq,category;
    String ID;
    private StorageTask mUploadTask;
    private ProgressBar mProgressBar;
    FirebaseRecyclerAdapter<Confirmation, ConfirmationViewHolder> adapter;
    TextView merchId,AN,bankID,amount,status;
    ImageView image;
    CardView ConfirmButton;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_reimburse_detail);
        ID = getIntent().getStringExtra("ConfirmationId");
        merchId = (TextView)findViewById(R.id.Merchant);
        bankID = (TextView) findViewById(R.id.Ovoid);
        AN = (TextView)findViewById(R.id.AN);
        status = (TextView)findViewById(R.id.StatusReimburse);
        image = (ImageView)findViewById(R.id.bukti);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        amount = (TextView)findViewById(R.id.ReimburseAmount);
        ConfirmButton = (CardView)findViewById(R.id.conf);

        database = FirebaseDatabase.getInstance();
        reimburseReq = database.getReference("ReimbureseReq");
        category = database.getReference("Category");

        reimburseReq.child(ID.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ReimburseReq confirmss = dataSnapshot.getValue(ReimburseReq.class);
                if(confirmss.getImage().equals(" ")){
                    image.setImageDrawable(getDrawable(R.drawable.noimage));
                }else{
                   new DownLoadImageTask(image).execute(confirmss.getImage().toString());
                }
                merchId.setText("Merchant ID : "+confirmss.getMerchantId().toString());
                bankID.setText("Bank ID : "+confirmss.getBankId().toString());
                AN.setText("Atas Nama : "+confirmss.getAn().toString());
                amount.setText("Jumlah Reimburse : "+confirmss.getAmount().toString());
                status.setText("Reimburse Status : "+confirmss.getStatus());

                //new DownLoadImageTask(image).execute(confirmss.getImage());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ReimburseHistoryDetail.this,ReimburseHistoryDetail.class);
                intent.putExtra("phoneId",ID);
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
