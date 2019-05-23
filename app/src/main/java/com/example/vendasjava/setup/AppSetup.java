package com.example.vendasjava.setup;

import java.util.ArrayList;
import java.util.List;

import com.example.vendasjava.model.Cliente;
import com.example.vendasjava.model.ItemPedido;
import com.example.vendasjava.model.Produto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AppSetup {
    public static List<Produto> produtos = new ArrayList<>();
    public static Cliente cliente = null;
    public static List<ItemPedido> carrinho = new ArrayList<>();

    public static DatabaseReference myRef = null;

    public static DatabaseReference getInstance(){
        if(myRef == null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference("vendasjava");

            return myRef;
        }

        return myRef;
    }
}
