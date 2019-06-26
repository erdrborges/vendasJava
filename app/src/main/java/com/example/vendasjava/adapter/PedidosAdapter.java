package com.example.vendasjava.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vendasjava.R;
import com.example.vendasjava.model.Pedido;

import java.text.NumberFormat;
import java.util.List;

public class PedidosAdapter extends ArrayAdapter<Pedido> {
    private final Context context;

    public PedidosAdapter(Context context, List<Pedido> pedido) {
        super(context, 0, pedido);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pedido_adapter, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //bindview
        final Pedido itemPedido = getItem(position);

        holder.nomeProduto.setText(itemPedido.getCliente().getNome());
        holder.quantidade.setText(itemPedido.getDataCriacao().toString());
        holder.totalDoItem.setText(NumberFormat.getCurrencyInstance().format(itemPedido.getTotalPedido()));

        return convertView;
    }

    private class ViewHolder {
        TextView nomeProduto;
        TextView quantidade;
        TextView totalDoItem;

        public ViewHolder(View convertView) {
            //mapeia os componentes da UI para vincular os dados do objeto de modelo
            nomeProduto = convertView.findViewById(R.id.tvNomeProdutoCarrinhoAdapter);
            quantidade = convertView.findViewById(R.id.tvQuantidadeDeProdutoCarrinhoAdapater);
            totalDoItem = convertView.findViewById(R.id.tvTotalItemCarrinhoAdapter);
        }
    }
}
