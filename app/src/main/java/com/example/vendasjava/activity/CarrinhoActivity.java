package com.example.vendasjava.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.vendasjava.R;
import com.example.vendasjava.adapter.CarrinhoAdapter;
import com.example.vendasjava.model.ItemPedido;
import com.example.vendasjava.model.Pedido;
import com.example.vendasjava.model.Produto;
import com.example.vendasjava.setup.AppSetup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CarrinhoActivity extends AppCompatActivity {
    private ListView lv_carrinho;
    private TextView tvClienteCarrinho;
    private TextView tvTotalPedidoCarrinho;
    public int i;
    private Double valorTotal = new Double(0);
    private List<ItemPedido> itens;
    private Produto produto;
    private ItemPedido itemPedido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carrinho);

        lv_carrinho = findViewById(R.id.lv_carrinho);

        tvClienteCarrinho = findViewById(R.id.tvClienteCarrinho);
        tvTotalPedidoCarrinho = findViewById(R.id.tvTotalPedidoCarrinho);
        tvClienteCarrinho.setText(String.valueOf("Cliente: " + AppSetup.cliente.getNome().concat(" " + AppSetup.cliente.getSobrenome())));

        //tvClienteCarrinho.setText("Cliente: " + AppSetup.cliente.getNome() + " " + AppSetup.cliente.getSobrenome());

        lv_carrinho.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adExcluirItem("Atenção", "Você deseja excluir esse item?", position);

                //Toast.makeText(CarrinhoActivity.this, "Click para remover", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        lv_carrinho.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editaItem(position, "Editar item?", "Você deseja editar o item?");

                //Toast.makeText(CarrinhoActivity.this, "Click para editar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_carrinho, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_salvar:
                adSalvarPedido("Processando...", "\nTotal = " + NumberFormat.getCurrencyInstance().format(valorTotal) + ". Confirmar?");
                break;
            case R.id.menuitem_cancelar:
                adCancelarPedido("Cancelamento de Pedido", "Você realmente deseja cancelar o pedido?");
                break;
        }
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menuitem_salvar:
//                if(AppSetup.carrinho.isEmpty()){
//                    Toast.makeText(this, "Carrinho vazio.", Toast.LENGTH_SHORT).show();
//                }else{
//                    adSalvarPedido("Processando...", "\nTotal = " + NumberFormat.getCurrencyInstance().format(valorTotal) + ". Confirmar?");
//                }
//
//                //Toast.makeText(this, "Salvar no banco", Toast.LENGTH_SHORT).show();
//
//                break;
//
//            case R.id.menuitem_cancelar:
//                if(AppSetup.carrinho.isEmpty()){
//                    adCancelarPedido("Cancelamento de Pedido", "Você realmente deseja cancelar o pedido?");
//                }else{
//                    Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();
//                }
//
//                //Toast.makeText(this, "Excluir carrinho", Toast.LENGTH_SHORT).show();
//
//                break;
//        }
//
//        return true;
//    }

    private void editaItem(final int position, String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertdialog_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                atualizaEstoque(position);

                Intent intent = new Intent(CarrinhoActivity.this, ProdutoDetalheActivity.class);
                int index = AppSetup.carrinho.get(position).getProduto().getIndex();
                //Log.d("OUTPUT: ", "Valor do index: " +  index);

                intent.putExtra("position", index);

                startActivity(intent);

                AppSetup.carrinho.remove(position);

                //Toast.makeText(CarrinhoActivity.this, "Click para atualizar", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        builder.setNegativeButton(R.string.alertdialog_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //por enquanto nada
            }
        });

        builder.show();
    }

    private void atualizaView(){
        lv_carrinho.setAdapter(new CarrinhoAdapter(CarrinhoActivity.this, AppSetup.carrinho));

        valorTotal = new Double(0);

        for(ItemPedido itemPedido : AppSetup.carrinho){
            valorTotal += itemPedido.getTotalItem();
        }

        tvTotalPedidoCarrinho.setText(NumberFormat.getCurrencyInstance().format(valorTotal));
    }

    private void atualizaEstoque(final int position){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("produtos/").child(itens.get(position).getProduto().getKey()).child("quantidade");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //referencia da posição do estoque
                long quantidade = (long) dataSnapshot.getValue();

                myRef.setValue(itens.get(position).getQuantidade() + quantidade);

                atualizaView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void adSalvarPedido(String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertdialog_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //referencia do banco
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("pedidos/");

                Pedido pedido = new Pedido();

                pedido.setFormaDePagamento("dinheiro");
                pedido.setEstado("aberto");
                pedido.setDataCriacao(Calendar.getInstance().getTime());
                pedido.setDataModificacao(Calendar.getInstance().getTime());
                pedido.setTotalPedido(valorTotal);
                pedido.setSituacao(true);
                pedido.setItens(AppSetup.carrinho);
                pedido.setCliente(AppSetup.cliente);

                //salva no db
                myRef.push().setValue(pedido);

                AppSetup.carrinho.clear();
                AppSetup.cliente = null;

                Toast.makeText(CarrinhoActivity.this, "Vendido com sucesso!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

        builder.setNegativeButton(R.string.alertdialog_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CarrinhoActivity.this, "Venda cancelada!", Toast.LENGTH_SHORT).show();
            }
        });


        builder.show();
    }

    private void adCancelarPedido(String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertdialog_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int j = 0; j < itens.size(); j++){
                    atualizaEstoque(i);
                }

                AppSetup.carrinho.clear();
                AppSetup.cliente = null;

                Toast.makeText(CarrinhoActivity.this, "Pedido cancelado!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton(R.string.alertdialog_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CarrinhoActivity.this, "Operação cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void adExcluirItem(String titulo, String mensagem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.alertdialog_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppSetup.carrinho.remove(position);
                Toast.makeText(CarrinhoActivity.this, "Produto removido!", Toast.LENGTH_SHORT).show();

                atualizaView();
                atualizaEstoque(position);
            }
        });

        builder.show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!AppSetup.carrinho.isEmpty()){
            atualizaView();
        }

        //copia o carrinho para usar na att do estoque
        itens = new ArrayList<>();
        itens.addAll(AppSetup.carrinho);
    }
}
