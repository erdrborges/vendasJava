package com.example.vendasjava.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vendasjava.R;
import com.example.vendasjava.barcode.BarcodeCaptureActivity;
import com.example.vendasjava.model.Produto;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProdutoAdminActivity extends AppCompatActivity {
    private static final String TAG = "produtoAdminActivity";
    private static final int RC_BARCODE_CAPTURE = 1, RC_GALERIA_IMAGE_PICK = 2;
    private EditText etCodigoDeBarras, etNome, etDescricao, etValor, etQuantidade;
    private Button btSalvar;
    private Produto produto;
    private FirebaseDatabase database;

    //define se será realizado um INSERT ou um UPDATE no banco de dados
    private boolean flagInsertOrUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_admin);

        //ativa a funcionalidade do botão home na actionBar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();

        produto = new Produto();

        etCodigoDeBarras = findViewById(R.id.etCodigoProduto);
        etNome = findViewById(R.id.etNomeProdutoAdmin);
        etDescricao = findViewById(R.id.etDescProdutoAdmin);
        etValor = findViewById(R.id.etValorProdutoAdmin);
        etQuantidade = findViewById(R.id.etQuantProdutoAdmin);
        btSalvar = findViewById(R.id.btSalvarProdutoAdmin);

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCodigoDeBarras.getText().toString().isEmpty() && !etNome.getText().toString().isEmpty() &&
                !etValor.getText().toString().isEmpty() && !etQuantidade.getText().toString().isEmpty()){
                    produto.setCodigoDeBarras(Long.valueOf(etCodigoDeBarras.getText().toString()));
                    produto.setNome(etNome.getText().toString());
                    produto.setDescricao(etDescricao.getText().toString());
                    produto.setValor(Double.valueOf(etValor.getText().toString().replace(',', '.')));
                    produto.setQuantidade(Integer.valueOf(etQuantidade.getText().toString()));
                    produto.setSituacao(true);

                    Log.d("TAG", "Objeto de produto: "+produto);

                    saveProduct();
                }else{
                    Snackbar.make(findViewById(R.id.container_activity_produtoadmin), R.string.snack_preencher_todos_campos, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_produto_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuitem_list_users:{
                // launch barcode activity.
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //true liga a funcionalidade autofoco
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //true liga a lanterna (fash)
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                break;
            }

            case R.id.menuitem_limparform_admin:{
                cleanForm();
                break;
            }
            case android.R.id.home:{
                finish();
                break;
            }

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    etCodigoDeBarras.setText(barcode.displayValue);

                    searchDb(Long.valueOf(etCodigoDeBarras.getText().toString()));
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void searchDb(Long codigoDeBarras){
        DatabaseReference myRef = database.getReference("produtos/");
        Log.d("TAG", "Barcode: "+codigoDeBarras);

        Query query = myRef.orderByChild("codigoDeBarras").equalTo(codigoDeBarras).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot = " + dataSnapshot.getValue());
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        produto = ds.getValue(Produto.class);
                        produto.setKey(ds.getKey());
                    }
                    flagInsertOrUpdate = false;

                    loadView();
                }else{
                    Toast.makeText(ProdutoAdminActivity.this, getString(R.string.toast_produto_nao_cadastrado), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //providenciar retorno em caso de erro
            }
        });
    }

    private void loadView(){
        etCodigoDeBarras.setText(produto.getCodigoDeBarras().toString());
        etCodigoDeBarras.setEnabled(false);
        etNome.setText(produto.getNome());
        etDescricao.setText(produto.getDescricao());
        etValor.setText(String.format("%.2f", produto.getValor()));
        etQuantidade.setText(produto.getQuantidade().toString());
    }

    private void cleanForm(){
        produto = new Produto();

        etCodigoDeBarras.setEnabled(true);
        etCodigoDeBarras.setText(null);
        etNome.setText(null);
        etDescricao.setText(null);
        etValor.setText(null);
        etQuantidade.setText(null);
    }

    private void saveProduct(){
        //parametriza as ações para um INSERT
        if(flagInsertOrUpdate){
            final DatabaseReference myRef = database.getReference("produtos/");

            Log.d("TAG", "Barcode is: "+produto.getCodigoDeBarras());

            Query query = myRef.orderByChild("codigoDeBarras").equalTo(produto.getCodigoDeBarras()).limitToFirst(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        Toast.makeText(ProdutoAdminActivity.this, R.string.toast_codigo_barras_ja_cadastrado, Toast.LENGTH_SHORT).show();
                    }else{
                        produto.setKey(myRef.push().getKey());
                        myRef.child(produto.getKey()).setValue(produto).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProdutoAdminActivity.this, getString(R.string.toast_produto_salvo), Toast.LENGTH_SHORT).show();
                                cleanForm();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(R.id.container_activity_produtoadmin), R.string.snack_operacao_falhou, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //apresentar mensagem na ocorrência de erro
                }
            });
        //parametriza as ações para um UPDATE
        }else{
            flagInsertOrUpdate = true;

            DatabaseReference myRef = database.getReference("produtos/" + produto.getKey());
            Log.d("TAG", "Product id: " + produto.getKey());

            myRef.setValue(produto).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProdutoAdminActivity.this, getString(R.string.toast_produto_atualizado), Toast.LENGTH_SHORT).show();
                    cleanForm();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProdutoAdminActivity.this, getString(R.string.toast_produto_nao_att), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
