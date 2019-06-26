package com.example.vendasjava.setup;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.vendasjava.model.Cliente;
import com.example.vendasjava.model.ItemPedido;
import com.example.vendasjava.model.Pedido;
import com.example.vendasjava.model.Produto;
import com.example.vendasjava.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//public class AppSetup {
//    public static List<Produto> produtos = new ArrayList<>();
//    public static Cliente cliente = null;
//    public static List<ItemPedido> carrinho = new ArrayList<>();
//    public static FirebaseUser usuario = null;
//    public static FirebaseAuth mAuth = null;
//
//    //mappings para permitir o cache das imagens de produtos e imagens de clientes
//    public static Map<String, Bitmap> cacheProdutos = new HashMap<>();
//    public static Map<String, Bitmap> cacheClientes = new HashMap<>();
//
//
//    public static DatabaseReference myRef = null;
//
//    public static DatabaseReference getInstance(){
//        if(myRef == null){
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            myRef = database.getReference("vendasjava");
//
//            return myRef;
//        }
//
//        return myRef;
//    }
//}

public class AppSetup {
    public static List<Produto> produtos = new ArrayList<>();
    public static List<Cliente> clientes = new ArrayList<>();
    public static List<ItemPedido> carrinho = new ArrayList<>();
    public static List<Pedido> pedidos = new ArrayList<>();
    public static List<ItemPedido> itemPedidos = new ArrayList<>();
    public static Cliente cliente = null;
    public static Pedido pedido = null;
    public static Usuario user = null;
    public static FirebaseAuth mAuth = null;

    public static Map<String, Bitmap> cacheProdutos = new HashMap<>();
    public static Map<String, Bitmap> cacheClientes = new HashMap<>();
}
