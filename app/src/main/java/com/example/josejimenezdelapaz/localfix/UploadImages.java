package com.example.josejimenezdelapaz.localfix;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadImages extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton mSelectBtn;
    private RecyclerView mUploadList;

    private List<String> fileNameList;
    private List<String> fileDoneList;

    private UploadListAdapter uploadListAdapter;
    private final static int code = 1000;
    private StorageReference mStorage;

    private ImageView iv_uploadImg;

    private ArrayList<String> listaURLs=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectBtn = (ImageButton) findViewById(R.id.iv_uploadimg);
        mUploadList = (RecyclerView) findViewById(R.id.upload_list);
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);
        //RecyclerView
        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar imagen"), RESULT_LOAD_IMAGE);

            }
        });

        iv_uploadImg = (ImageView)findViewById(R.id.iv_uploadimg);

        Resources res = getResources();
        Drawable img_uploadImg = res.getDrawable(R.drawable.img_uploadimg);
        iv_uploadImg.setImageDrawable(img_uploadImg);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){

            if(data.getClipData() != null){

                int totalItemsSelected = data.getClipData().getItemCount();
                if(totalItemsSelected <= 4) {
                    for (int i = 0; i < totalItemsSelected; i++) {

                        Uri fileUri = data.getClipData().getItemAt(i).getUri();

                        String fileName = getFileName(fileUri);

                        fileNameList.add(fileName);
                        fileDoneList.add("uploading");
                        uploadListAdapter.notifyDataSetChanged();

                        final StorageReference fileToUpload = mStorage.child("Images").child(fileName);

                        final int finalI = i;
                        fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                fileDoneList.remove(finalI);
                                fileDoneList.add(finalI, "done");

                                uploadListAdapter.notifyDataSetChanged();

                                fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {


                                        Toast.makeText(UploadImages.this, "onSuccess: uri= " + uri.toString(), Toast.LENGTH_SHORT).show();
                                        listaURLs.add(uri.toString());
                                    }
                                });

                            }
                        });

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Selecciona máximo 4 imágenes", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(MainActivity.this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();

            } else if (data.getData() != null){

                Toast.makeText(this, "Seleccione como mínimo 2 imágenes", Toast.LENGTH_SHORT).show();

            }

        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //Pulsar boton de volver
    public void volver(View view){
       devolverURLS();
    }
    //Pulsar botón de atrás
    @Override
    public void onBackPressed()
    {
       devolverURLS();
    }
    private void devolverURLS(){
        //Starting the previous Intent
        Intent previousScreen = new Intent(getApplicationContext(), NuevoDesperfecto.class);
        //Sending the data to Activity_A
        //previousScreen.putExtra("urls",listaURLs);
        previousScreen.putStringArrayListExtra("urls",listaURLs);
        setResult(code, previousScreen);
        finish();
    }
}
